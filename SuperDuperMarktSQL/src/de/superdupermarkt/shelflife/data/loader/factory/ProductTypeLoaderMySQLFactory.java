package de.superdupermarkt.shelflife.data.loader.factory;

import de.superdupermarkt.shelflife.data.loader.ProductTypeLoader;
import de.superdupermarkt.shelflife.data.loader.ProductTypeLoaderMySQL;
import de.superdupermarkt.shelflife.data.view.ErrorReporter;

public class ProductTypeLoaderMySQLFactory implements ProductTypeLoaderRegistry.LoaderFactory {
    @Override
    public ProductTypeLoader fromConfigString(String config, ClassLoader classLoader, ErrorReporter errorReporter) {
        return new ProductTypeLoaderMySQL(
                errorReporter,
                classLoader,
                config
        );
    }
}
