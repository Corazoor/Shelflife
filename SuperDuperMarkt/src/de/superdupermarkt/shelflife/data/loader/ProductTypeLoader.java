package de.superdupermarkt.shelflife.data.loader;

import de.superdupermarkt.shelflife.data.ProductType;
import de.superdupermarkt.shelflife.data.loader.exception.DataLoaderAccessException;
import de.superdupermarkt.shelflife.data.view.ErrorReporter;

import java.util.Map;

/**
 * Responsible for loading a Map of ProductTypes identified by their name.
 * <p>
 * This could be designed as an implicit part of product loading, but this way enables loading types and products
 * from different sources if desired.
 * <p>
 * Abstract class instead of an interface to provide default handling of the required ErrorReporter.
 */
public abstract class ProductTypeLoader {
    protected final ErrorReporter errorReporter;
    protected final ClassLoader classLoader;

    public ProductTypeLoader(ErrorReporter errorReporter, ClassLoader classLoader) {
        this.errorReporter = errorReporter;
        this.classLoader = classLoader;
    }

    /**
     * Produces a map of ProductTypes identified by their name loaded from the underlying data source.
     * Will always produce a list of all loadable lines, since lines with errors will be omitted from the list
     *
     * @return
     * @throws DataLoaderAccessException
     */
    public abstract Map<String, ProductType> fetchProductTypes() throws DataLoaderAccessException;
}
