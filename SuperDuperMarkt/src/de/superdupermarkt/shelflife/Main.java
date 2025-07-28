package de.superdupermarkt.shelflife;

import de.superdupermarkt.shelflife.data.loader.factory.ClassLoading;
import de.superdupermarkt.shelflife.data.loader.factory.ProductLoaderRegistry;
import de.superdupermarkt.shelflife.data.loader.factory.ProductTypeLoaderRegistry;
import de.superdupermarkt.shelflife.data.loader.*;
import de.superdupermarkt.shelflife.data.view.ConsoleOutput;
import de.superdupermarkt.shelflife.data.view.DataView;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws IOException {
        //Very simple argument parsing. Often this is enough, but more complex cases might require a getopt library.
        LocalDate startDate = (args.length > 1) //no sense in supplying a start date but using the default end date
            ? LocalDate.parse(args[0], DateTimeFormatter.BASIC_ISO_DATE)
            : LocalDate.of(2024, 12, 1);

        LocalDate endDate = (args.length > 1)
            ? LocalDate.parse(args[1], DateTimeFormatter.BASIC_ISO_DATE)
            : LocalDate.of(2025, 2, 15);

        //Use a properties file to add new Loaders from submodules.
        //It would also be possible to scan Jars for suitable classes, but this usually entails loading all classes from the given jars.
        Properties settings = new Properties();
        try(BufferedReader reader =  Files.newBufferedReader(Path.of("settings.properties"), StandardCharsets.UTF_8)) {
            settings.load(reader);
        }

        //providing config via arguments is not always a good idea, for example jdbc strings contain the password...
        //therefore, there is a way to provide them via settings
        String productTypeLoaderConfig = (args.length > 2)
                ? args[2]
                : settings.getProperty("ProductTypeLoaderConfig", "CSVFile:./productTypes.csv");

        String productLoaderConfig = (args.length > 3)
                ? args[3]
                : settings.getProperty("ProductLoaderConfig", "CSVFile:./products.csv");

        //Here we could also detect if a file was piped into this program and enable CSVPipeInput like so:
        //if(System.in.available() > 0) productLoaderConfig = "CSVPipeInput:"

        //Initialize DataView early to use it for error output
        //This could also be configurable via commandline
        DataView view = new ConsoleOutput();
        ProductTypeLoaderRegistry ptRegistry = new ProductTypeLoaderRegistry();
        ProductLoaderRegistry pRegistry = new ProductLoaderRegistry();

        //Get the path of the module directory
        Path modulesPath = Path.of(settings.getProperty("moduleDirectory", "./modules/"));

        try(URLClassLoader cl = createClassloaderForModules(modulesPath)) {
            settings.forEach((key, value) -> {
                String name = (String)key;
                String val = (String)value;
                try {
                    if(name.startsWith("ProductLoader.")) {
                        ProductLoaderRegistry.LoaderFactory factory = ClassLoading.fromClassName(cl, val, ProductLoaderRegistry.LoaderFactory.class);
                        pRegistry.registerLoaderFactory(name.replace("ProductLoader.", ""), factory);
                    } else if(name.startsWith("ProductTypeLoader.")) {
                        ProductTypeLoaderRegistry.LoaderFactory factory = ClassLoading.fromClassName(cl, val, ProductTypeLoaderRegistry.LoaderFactory.class);
                        ptRegistry.registerLoaderFactory(name.replace("ProductTypeLoader.", ""), factory);
                    }
                } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                         InstantiationException | IllegalAccessException ex) {
                    view.error(ex);
                }
            });

            //Now get the actual loaders, using the provided config strings
            ProductTypeLoader productTypeLoader =  ptRegistry.fromConfigString(productTypeLoaderConfig, cl, view);
            if(productTypeLoader == null) {
                view.error(() -> "ProductTypeLoader not found: "+productTypeLoaderConfig);
                System.exit(1);
            }

            ProductLoader productLoader = pRegistry.fromConfigString(productLoaderConfig, view);
            if(productLoader == null) {
                view.error(() -> "ProductLoader not found: "+productLoaderConfig);
                System.exit(1);
            }

            //Compose it all together via the Controller and run
            ShelfController controller = new ShelfController(productTypeLoader, productLoader, view);
            controller.execute(startDate, endDate);
        } catch (Exception ex) {
            view.error(ex);
            throw ex;
        }
    }

    /**
     * create a classloader which can be used to load additional .class files dynamically at runtime, providing a mechanism to add productTypes and loaders even while the program is running
     *
     * This could also be achieved by providing the module folder via classpath, but it's tricky to unload classes in that case which prevents reloads if those files are updated (classes are only garbage collected when their classloader is gcd)
     * this is potentially unsafe, because anyone with access to the module folder can add arbitrary code. In practice that rarely matters, since access to this folder implies access to the actual jar file in most cases
     * Plus, it is a nice showcase for a test project like this
     */
    private static URLClassLoader createClassloaderForModules(Path moduleDir) throws IOException {
        URL[] moduleJars;
        try(Stream<Path> paths = Files.list(moduleDir)) {
            moduleJars = paths
                    .filter(path -> path.toString().endsWith(".jar"))
                    .map(path -> {
                        try {
                            return path.toUri().toURL();
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toArray(URL[]::new);
        }

        return new URLClassLoader(moduleJars, ClassLoader.getSystemClassLoader());
    }
}
