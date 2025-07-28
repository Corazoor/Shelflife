package de.superdupermarkt.shelflife.data.loader;

import de.superdupermarkt.shelflife.data.ProductType;
import de.superdupermarkt.shelflife.data.loader.exception.DataLoaderAccessException;
import de.superdupermarkt.shelflife.data.loader.exception.ProductTypeNotValidException;
import de.superdupermarkt.shelflife.data.loader.factory.ProductTypeLoaderRegistry;
import de.superdupermarkt.shelflife.data.view.ErrorReporter;
import de.superdupermarkt.shelflife.sql.SQLProductAndType;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ProductTypeLoaderMySQL extends ProductTypeLoader {
    private final String connStr;

    public ProductTypeLoaderMySQL(ErrorReporter errorReporter, ClassLoader classLoader, String connStr) {
        super(errorReporter, classLoader);
        this.connStr = connStr;
    }

    @Override
    public Map<String, ProductType> fetchProductTypes() throws DataLoaderAccessException {
        //this pattern works best with a connection pool, since it is very expensive to get a new connection each method call
        try(Connection conn = DriverManager.getConnection(this.connStr)) {
            Map<String, ProductType> productTypes = new HashMap<>();

            SQLProductAndType.fetchProductTypes(conn, (name, className) -> {
                try {
                    productTypes.put(
                            name,
                            ProductType.fromClassName(classLoader, className, name)
                    );
                } catch (ProductTypeNotValidException ex) {
                    errorReporter.warning(ex);
                }
            });

            return productTypes;
        } catch (SQLException ex) {
            throw new DataLoaderAccessException(ex.toString());
        }
    }
}
