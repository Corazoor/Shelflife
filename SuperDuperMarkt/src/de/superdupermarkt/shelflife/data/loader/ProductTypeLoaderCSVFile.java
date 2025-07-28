package de.superdupermarkt.shelflife.data.loader;

import de.superdupermarkt.shelflife.data.loader.factory.ProductTypeLoaderRegistry;
import de.superdupermarkt.shelflife.data.loader.exception.DataLoaderAccessException;
import de.superdupermarkt.shelflife.data.view.ErrorReporter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Concrete ProductTypeLoaderCSV implementation for loading productTypes in CSV format from a file.
 */
public class ProductTypeLoaderCSVFile extends ProductTypeLoaderCSV {
    /**
     * Factory implementation used to create an instance of ProductTypeLoaderCSVFile from a given configuration string.
     * <p>
     * Declared inline since it is tightly coupled to this class and fairly small.
     */
    public static class LoaderFactory implements ProductTypeLoaderRegistry.LoaderFactory {
        @Override
        public ProductTypeLoader fromConfigString(String config, ClassLoader classLoader, ErrorReporter errorReporter) {
            return new ProductTypeLoaderCSVFile(
                    Path.of(config),
                    classLoader,
                    errorReporter
            );
        }
    }

    private final Path file;

    public ProductTypeLoaderCSVFile(Path file, ClassLoader classLoader, ErrorReporter errorReporter) {
        super(errorReporter, classLoader);
        this.file = file;
    }

    @Override
    protected Stream<String> loadLines() throws DataLoaderAccessException {
        try {
            return Files.lines(this.file, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new DataLoaderAccessException("Could not load ProductType CSV: "+ex.getMessage());
        }
    }
}
