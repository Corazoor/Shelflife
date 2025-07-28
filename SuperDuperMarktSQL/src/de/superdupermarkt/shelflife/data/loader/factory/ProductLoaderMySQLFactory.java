package de.superdupermarkt.shelflife.data.loader.factory;

import de.superdupermarkt.shelflife.data.loader.ProductLoader;
import de.superdupermarkt.shelflife.data.loader.ProductLoaderMySQL;
import de.superdupermarkt.shelflife.data.view.ErrorReporter;

public class ProductLoaderMySQLFactory implements ProductLoaderRegistry.LoaderFactory {
    @Override
    public ProductLoader fromConfigString(String config, ErrorReporter errorReporter) {
        return new ProductLoaderMySQL(
                errorReporter,
                config
        );
    }
}
