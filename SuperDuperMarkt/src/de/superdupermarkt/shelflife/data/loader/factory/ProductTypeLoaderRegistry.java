package de.superdupermarkt.shelflife.data.loader.factory;

import de.superdupermarkt.shelflife.data.view.ErrorReporter;
import de.superdupermarkt.shelflife.data.loader.ProductTypeLoader;
import de.superdupermarkt.shelflife.data.loader.ProductTypeLoaderCSVFile;
import de.superdupermarkt.shelflife.data.loader.ProductTypeLoaderCSVPipeInput;

import java.util.HashMap;

/**
* Utility class to associate LoaderFactories with a name, and produce instances from these factories via an oblique String based mechanism.
*/
public class ProductTypeLoaderRegistry {
    public interface LoaderFactory {
        /**
         * Responsible for converting a given configuration string to the appropriate constructor.
         * *
         * FactoryMethod pattern.
         * Used in the LoaderRegistry to handle configuring Loaders with differing constructors, thus providing a uniform interface.
         *
         * @param config The supplied configuration in string format. The actual format depends on the implementing class.
         * @return The Loader Object constructed with the given configuration
         */
        ProductTypeLoader fromConfigString(String config, ClassLoader classLoader, ErrorReporter errorReporter);
    }

    private final HashMap<String, LoaderFactory> factories = new HashMap<>();

    public ProductTypeLoaderRegistry() {
        //register all known loaders here. This could also be done from outside via various initialization and configuration mechanisms.
        this.registerLoaderFactory("CSVFile", new ProductTypeLoaderCSVFile.LoaderFactory());
        this.registerLoaderFactory("CSVPipeInput", new ProductTypeLoaderCSVPipeInput.LoaderFactory());
    }

    /**
     * Registers a concrete ProductTypeLoaderFactory implementation with a name.
     * If the given name is already in use, the old entry with this name will be replaced.
     *
     * @param name The name to associate with the given Factory
     * @param factory A concrete LoaderFactory instance to make accessible via this name
     */
    public void registerLoaderFactory(String name, LoaderFactory factory) {
        factories.put(name, factory);
    }

    /**
     * Provides a uniform way to produce an instance from a LoaderFactory registered with a name.
     * The Format is "registeredName:config", whereas the part before the colon is the name of the LoaderFactory to use,
     * and the part after the colon is the String passed to that Factories fromConfigString method.
     *
     * @param config The config string containing both the registered name of the LoaderFactory to use and the config to pass to that factory.
     * @param errorReporter The errorReporter to be used by the Loader
     * @return An instance of a ProductTypeLoader produced by the requested ProductLoaderFactory using the supplied config.
     */
    public ProductTypeLoader fromConfigString(String config, ClassLoader classLoader, ErrorReporter errorReporter) {
        String[] split = config.split(":", 2);
        if(split.length != 2) { return null; }

        LoaderFactory factory = factories.get(split[0]);
        return factory == null ? null : factory.fromConfigString(split[1], classLoader, errorReporter);
    }
}
