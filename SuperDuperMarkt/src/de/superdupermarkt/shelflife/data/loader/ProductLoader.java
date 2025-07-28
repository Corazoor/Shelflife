package de.superdupermarkt.shelflife.data.loader;

import de.superdupermarkt.shelflife.data.Product;
import de.superdupermarkt.shelflife.data.ProductType;
import de.superdupermarkt.shelflife.data.loader.exception.DataLoaderAccessException;
import de.superdupermarkt.shelflife.data.view.ErrorReporter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Responsible for loading an actual List of Products from the datasource associated with the respective subclass.
 *
 * Abstract class instead of an interface to provide default handling of the required ErrorReporter.
 */
public abstract class ProductLoader {
    protected final ErrorReporter errorReporter;

    public ProductLoader(ErrorReporter errorReporter) {
        this.errorReporter = errorReporter;
    }

    /**
     * Produces a list of products loaded from the underlying data source.
     * Will always produce a list of all loadable lines, since lines with errors will be omitted from the list
     * The startDate is necessary for object initialization and may be used by the DataSource to determine which set of products to retrieve.
     *
     * @param startDate The date with which the products are initially loaded.
     * @param productTypes A map of ProductTypes identified by their given name.
     * @return The List of fully initialized product objects for the given startDate
     * @throws DataLoaderAccessException
     */
    public abstract List<Product> fetchProducts(LocalDate startDate, Map<String, ProductType> productTypes) throws DataLoaderAccessException;
}
