package de.superdupermarkt.shelflife.tests;

import de.superdupermarkt.shelflife.data.Product;
import de.superdupermarkt.shelflife.data.ProductType;
import de.superdupermarkt.shelflife.data.loader.*;
import de.superdupermarkt.shelflife.data.loader.exception.ProductTypeNotFoundException;
import de.superdupermarkt.shelflife.data.loader.exception.ProductTypeNotValidException;
import de.superdupermarkt.shelflife.data.view.ErrorReporter;
import de.superdupermarkt.shelflife.rules.Cheese;
import de.superdupermarkt.shelflife.rules.GeneralProduct;
import de.superdupermarkt.shelflife.rules.Wine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class ProductLoaderTests {
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

    private static final LocalDate dueDate = LocalDate.of(2024, 12, 1);
    private static final Map<String, ProductType> productTypes = Map.of(
            "general", new GeneralProduct("general"),
                    "cheese", new Cheese("cheese"),
                    "wine", new Wine("wine"));

    private static List<Product> runLoader(ProductLoader loader) {
        //using atomic reference to store result.
        //Captured variables in lambdas need to be final, this is an easy workaround to store references created within those
        AtomicReference<List<Product>> ref = new AtomicReference<>();
        assertDoesNotThrow(() -> {
            ref.set(loader.fetchProducts(dueDate, productTypes));
        });

        return ref.get();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("csvLoader")
    @DisplayName("CSV Product File Loader")
    void csvFileLoader(String testName, InputStream inputStream, BiConsumer<List<Product>, MockErrorReporter> asserts) {
        MockErrorReporter errReporter = new MockErrorReporter();

        List<Product> products = runLoader(
                new ProductLoaderCSVFile(
                        Helper.createTempFileFromInputStream("tstFile", ".csv", inputStream),
                        errReporter));

        asserts.accept(products, errReporter);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("csvLoader")
    @DisplayName("CSV Product PipeInput Loader")
    void csvPipeInputLoader(String testName, InputStream inputStream, BiConsumer<List<Product>, MockErrorReporter> asserts) {
        System.setIn(inputStream);

        MockErrorReporter errReporter = new MockErrorReporter();
        List<Product> products = runLoader(
                new ProductLoaderCSVPipeInput(
                        errReporter));

        asserts.accept(products, errReporter);
    }

    public static Stream<Arguments> csvLoader() {
        return Stream.of(
                arguments(
                    "Empty file",
                    InputStream.nullInputStream(),
                    (BiConsumer<List<Product>, MockErrorReporter>)(productTypes, errReporter) -> {
                        assertTrue(productTypes.isEmpty(), "Empty file should produce empty list");

                        assertTrue(errReporter.errors.isEmpty(), "Empty file should report no errors");
                        assertTrue(errReporter.warnings.isEmpty(), "Empty file should report no warnings");
                    }),

                arguments(
                    "tstProductType.csv",
                    ProductTypeLoaderTests.class.getResourceAsStream("resources/tstProducts.csv"),
                    (BiConsumer<List<Product>, MockErrorReporter>)(products, errReporter) -> {
                        assertEquals(List.of(
                                        new Product(productTypes.get("general"), "Brot", 0, 1.15, LocalDate.of(2024, 12, 5), dueDate),
                                        new Product(productTypes.get("general"), "Brot", 0, 1.15, LocalDate.of(2024, 12, 9), dueDate),
                                        new Product(productTypes.get("general"), "Salami", 0, 1.70, LocalDate.of(2024, 12, 30), dueDate),
                                        new Product(productTypes.get("general"), "Salami", 2, 1.70, LocalDate.of(2024, 12, 30), dueDate),
                                        new Product(productTypes.get("cheese"), "Emmentaler", 35, 2.20, LocalDate.of(2024, 12, 22), dueDate),
                                        new Product(productTypes.get("cheese"), "Gouda", 100, 2.20, LocalDate.of(2024, 12, 22), dueDate),
                                        new Product(productTypes.get("cheese"), "Brie", 20, 2.50, LocalDate.of(2025, 1, 10), dueDate),
                                        new Product(productTypes.get("wine"), "Weisswein", 0, 5.00, LocalDate.of(2024, 12, 15), dueDate),
                                        new Product(productTypes.get("wine"), "Rotwein", 10, 5.00, LocalDate.of(2025, 1, 1), dueDate)),
                                products,
                                "Generated Products List is not correct"
                        );

                        assertTrue(errReporter.errors.isEmpty(), "Test file should report no errors");
                        assertIterableEquals(List.of(
                                new ProductLoaderCSV.FieldLengthError(1, ""),
                                new ProductLoaderCSV.ProductTypeNotFoundError("noType", ""),
                                new ProductLoaderCSV.NotAValidNumberError(""),
                                new ProductLoaderCSV.NotAValidNumberError(""),
                                new ProductLoaderCSV.NotAValidDateError(""),
                                new ProductLoaderCSV.NotAValidDateError("")),
                            errReporter.warnings,
                            "Produced warnings were not as expected");
                    })
        );
    }
}
