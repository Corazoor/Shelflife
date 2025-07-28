package de.superdupermarkt.shelflife.data.loader;

import de.superdupermarkt.shelflife.data.loader.factory.ProductTypeLoaderRegistry;
import de.superdupermarkt.shelflife.data.loader.exception.DataLoaderAccessException;
import de.superdupermarkt.shelflife.data.view.ErrorReporter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

/**
 * Concrete Loader implementation for loading ProductTypes in CSV format from the system input stream.
 *
 * Not part of the original task, but implemented to demonstrate the implementation flexibility
 */
public class ProductTypeLoaderCSVPipeInput extends ProductTypeLoaderCSV {
    /**
     * Factory implementation used to create an instance of ProductTypeLoaderCSVPipeInput from a given configuration string.
     * <p>
     * Declared inline since it is tightly coupled to this class and fairly small.
     */
    public static class LoaderFactory implements ProductTypeLoaderRegistry.LoaderFactory {
        @Override
        public ProductTypeLoader fromConfigString(String config, ClassLoader classLoader, ErrorReporter errorReporter) {
            //np config required, so it is ignored here
            return new ProductTypeLoaderCSVPipeInput(
                    classLoader,
                    errorReporter
            );
        }
    }

    public ProductTypeLoaderCSVPipeInput(ClassLoader classLoader, ErrorReporter errorReporter) {
        super(errorReporter, classLoader);
    }

    @Override
    protected Stream<String> loadLines() throws DataLoaderAccessException {
        if(System.in == null) { throw new DataLoaderAccessException("Could not load ProductType CSV from System Input"); }
        return new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8)).lines();
    }
}
