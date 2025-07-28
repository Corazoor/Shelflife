package de.superdupermarkt.shelflife.data.loader;

import de.superdupermarkt.shelflife.data.loader.exception.DataLoaderAccessException;
import de.superdupermarkt.shelflife.data.loader.factory.ProductLoaderRegistry;
import de.superdupermarkt.shelflife.data.view.ErrorReporter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

/**
 * Concrete Loader implementation for loading Products in CSV format from the system input stream.
 *
 * Not part of the original task, but implemented to demonstrate the implementation flexibility
 */
public class ProductLoaderCSVPipeInput extends ProductLoaderCSV {
    /**
     * Factory implementation used to create an instance of ProductLoaderCSVPipeInput from a given configuration string.
     * <p>
     * Declared inline since it is tightly coupled to this class and fairly small.
     */
    public static class LoaderFactory implements ProductLoaderRegistry.LoaderFactory {
        @Override
        public ProductLoader fromConfigString(String config, ErrorReporter errorReporter) {
            //no config required, so it is ignored here
            //the config parameter could actually be used to select named pipes instead of just using System.in
            return new ProductLoaderCSVPipeInput(
                    errorReporter
            );
        }
    }

    public ProductLoaderCSVPipeInput(ErrorReporter errorReporter) {
        super(errorReporter);
    }

    @Override
    protected Stream<String> loadLines() throws DataLoaderAccessException {
        if(System.in == null) { throw new DataLoaderAccessException("Could not load ProductType CSV from System Input"); }
        return new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8)).lines();
    }
}
