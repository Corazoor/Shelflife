package de.superdupermarkt.shelflife.data.loader;

import de.superdupermarkt.shelflife.data.Product;
import de.superdupermarkt.shelflife.data.ProductType;
import de.superdupermarkt.shelflife.data.loader.exception.DataLoaderAccessException;
import de.superdupermarkt.shelflife.data.loader.factory.ProductLoaderRegistry;
import de.superdupermarkt.shelflife.data.loader.factory.ProductTypeLoaderRegistry;
import de.superdupermarkt.shelflife.data.view.ErrorReporter;
import de.superdupermarkt.shelflife.sql.SQLProductAndType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductLoaderMySQL extends ProductLoader {
    private final String connStr;

    public ProductLoaderMySQL(ErrorReporter errorReporter, String connStr) {
        super(errorReporter);
        this.connStr = connStr;
    }

    @Override
    public List<Product> fetchProducts(LocalDate startDate, Map<String, ProductType> productTypes) throws DataLoaderAccessException {
        //this pattern works best with a connection pool, since it is very expensive to get a new connection each method call
        try(Connection conn = DriverManager.getConnection(this.connStr)) {
            List<Product> products = new ArrayList<>();

            SQLProductAndType.fetchProducts(conn, startDate, (name, type, quality, basePrice, dueDate) -> {
                ProductType productType = productTypes.get(type);
                if(productType == null) {
                    errorReporter.warning(new ProductTypeNotFoundError(type));
                    return; //continue operation but ignore this row
                }

                products.add(new Product(
                    productType,
                    name,
                    quality,
                    basePrice,
                    dueDate,
                    startDate
                ));
            });

            return products;
        } catch (SQLException ex) {
            throw new DataLoaderAccessException(ex.toString());
        }
    }

    public static class ProductTypeNotFoundError implements ErrorReporter.Error {
        private final String producTypeName;

        public ProductTypeNotFoundError(String producTypeName) {
            this.producTypeName = producTypeName;
        }

        @Override
        public String getMessage() {
            return "Error while loading Product from Database. Given productType "+ this.producTypeName +" not found.";
        }
    }
}
