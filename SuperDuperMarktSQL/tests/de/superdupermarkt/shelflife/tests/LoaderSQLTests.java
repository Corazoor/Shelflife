package de.superdupermarkt.shelflife.tests;

import de.superdupermarkt.shelflife.data.Product;
import de.superdupermarkt.shelflife.data.ProductType;
import de.superdupermarkt.shelflife.data.loader.ProductLoader;
import de.superdupermarkt.shelflife.data.loader.ProductLoaderMySQL;
import de.superdupermarkt.shelflife.data.loader.ProductTypeLoader;
import de.superdupermarkt.shelflife.data.loader.ProductTypeLoaderMySQL;
import de.superdupermarkt.shelflife.data.loader.exception.DataLoaderAccessException;
import de.superdupermarkt.shelflife.data.view.ErrorReporter;
import de.superdupermarkt.shelflife.rules.Cheese;
import de.superdupermarkt.shelflife.rules.GeneralProduct;
import de.superdupermarkt.shelflife.rules.Wine;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class LoaderSQLTests {
    private final static String testDbName = "testDb_shelflife";
    private final static String connStrErr = "jdbc:mysql://localhost/?user=notExists";
    private final static String connStr = "jdbc:mysql://localhost/?user=user&password=password";

    /**
     * Creating a complete database before executing tests can be a bit expensive,
     * but leads to very stable test behaviour. Keeping a proper test system can work as well.
     *
     * In any case, changing the used Database for the queries is necessary to switch to a test schema.
     */
    @BeforeAll
    static void beforeAll() throws SQLException, URISyntaxException, IOException {
        try(
            Connection conn = DriverManager.getConnection(connStr+"&allowMultiQueries=true");
            Statement st = conn.createStatement();
        ) {
            st.execute("CREATE DATABASE `"+ testDbName +"` CHARACTER SET utf8mb4; use `"+testDbName+"`");
            String script = Files.readString(Paths.get(LoaderSQLTests.class.getResource("resources/testDatabase.sql").toURI()), StandardCharsets.UTF_8);
            st.execute(script);

            System.setProperty("db.Shelflife", testDbName);
        }
    }

    @AfterAll
    static void afterAll() throws SQLException {
        try(
            Connection conn = DriverManager.getConnection(connStr+"&allowMultiQueries=true");
            Statement st = conn.createStatement();
        ) {
            st.execute("DROP DATABASE `"+ testDbName +"`");

            //@todo reset System.setProperty("db.Shelflife", testDbName); ?
        }
    }

    private static class MockErrorReporter implements ErrorReporter {
        public final List<Object> errors = new ArrayList<>();
        public final List<Object> warnings = new ArrayList<>();

        @Override
        public void error(Exception ex) {
            errors.add(ex);
        }

        @Override
        public void error(ErrorReporter.Error error) {
            errors.add(error);
        }

        @Override
        public void warning(Exception ex) {
            warnings.add(ex);
        }

        @Override
        public void warning(ErrorReporter.Error warning) {
            warnings.add(warning);
        }
    };

    @Test
    @DisplayName("Connection Error")
    void testSQLLoaderConnectionError() {
        ProductTypeLoader loader = new ProductTypeLoaderMySQL(
            new MockErrorReporter(),
            ClassLoader.getSystemClassLoader(),
                connStrErr);


        AtomicReference<Map<String, ProductType>> ref = new AtomicReference<>();
        assertThrows(DataLoaderAccessException.class, () -> {
            ref.set(loader.fetchProductTypes());
        }, "ProductType Loader should throw DataLoaderAccessException");
        Map<String, ProductType> productTypes = ref.get();

        ProductLoader productLoader = new ProductLoaderMySQL(
                new MockErrorReporter(),
                connStrErr);

        assertThrows(DataLoaderAccessException.class, () -> {
            productLoader.fetchProducts(LocalDate.of(2024, 12, 1), productTypes);
        }, "Product Loader should throw DataLoaderAccessException");
    }

    @Test
    @DisplayName("fetchProductTypes")
    void testSQLLoaders() {
        MockErrorReporter ptErrReporter = new MockErrorReporter();
        ProductTypeLoader loader = new ProductTypeLoaderMySQL(
                ptErrReporter,
                ClassLoader.getSystemClassLoader(),
                connStr);

        AtomicReference<Map<String, ProductType>> ref = new AtomicReference<>();
        assertDoesNotThrow(() -> {
            ref.set(loader.fetchProductTypes());
        });
        Map<String, ProductType> productTypes = ref.get();

        assertEquals(Map.of(
                        "general", new GeneralProduct("general"),
                        "cheese", new Cheese("cheese"),
                        "wine", new Wine("wine")),
                productTypes,
                "Generated ProductTypeMap is not correct");

        ProductLoader productLoader = new ProductLoaderMySQL(
                new MockErrorReporter(),
                connStr);

        LocalDate initialDate = LocalDate.of(2024, 12, 1);

        AtomicReference<List<Product>> ref2 = new AtomicReference<>();
        assertDoesNotThrow(() -> {
            ref2.set(productLoader.fetchProducts(initialDate, productTypes));
        }, "Product Loader should throw no Exception");
        List<Product> products = ref2.get();

        assertEquals(List.of(
                new Product(productTypes.get("general"), "Brot", 0, 1.15, LocalDate.of(2024, 12, 5), initialDate),
                new Product(productTypes.get("general"), "Brot", 0, 1.15, LocalDate.of(2024, 12, 9), initialDate),
                new Product(productTypes.get("general"), "Salami", 0, 1.70, LocalDate.of(2024, 12, 30), initialDate),
                new Product(productTypes.get("general"), "Salami", 2, 1.70, LocalDate.of(2024, 12, 30), initialDate),
                new Product(productTypes.get("cheese"), "Emmentaler", 35, 2.20, LocalDate.of(2024, 12, 22), initialDate),
                new Product(productTypes.get("cheese"), "Gouda", 100, 2.20, LocalDate.of(2024, 12, 22), initialDate),
                new Product(productTypes.get("cheese"), "Brie", 20, 2.50, LocalDate.of(2025, 1, 10), initialDate),
                new Product(productTypes.get("wine"), "Weisswein", 0, 5.00, LocalDate.of(2024, 12, 15), initialDate),
                new Product(productTypes.get("wine"), "Rotwein", 10, 5.00, LocalDate.of(2025, 1, 1), initialDate)),
                products,
                "Generated ProductList is not correct");
    }
}
