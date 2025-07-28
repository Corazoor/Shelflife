package de.superdupermarkt.shelflife.data.loader;

import de.superdupermarkt.shelflife.data.loader.exception.DataLoaderAccessException;
import de.superdupermarkt.shelflife.data.loader.factory.ProductLoaderRegistry;
import de.superdupermarkt.shelflife.data.view.ErrorReporter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Concrete ProductLoaderCSV implementation for loading products in CSV format from a file.
 */
public class ProductLoaderCSVFile extends ProductLoaderCSV {
    /**
     * Factory implementation used to create an instance of ProductLoaderCSVFile from a given configuration string.
     * <p>
     * Declared inline since it is tightly coupled to this class and fairly small.
     */
    public static class LoaderFactory implements ProductLoaderRegistry.LoaderFactory {
        @Override
        public ProductLoader fromConfigString(String config, ErrorReporter errorReporter) {
            return new ProductLoaderCSVFile(
                    Path.of(config),
                    errorReporter
            );
        }
    }

    private final Path file;

    public ProductLoaderCSVFile(Path file, ErrorReporter errorReporter) {
        super(errorReporter);
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
