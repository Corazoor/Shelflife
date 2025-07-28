package de.superdupermarkt.shelflife.tests;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class ProductTypeLoaderTests {
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

    private static Map<String, ProductType> runLoader(ProductTypeLoader loader) {
        //using atomic reference to store result.
        //Captured variables in lambdas need to be final, this is an easy workaround to store references created within those
        AtomicReference<Map<String, ProductType>> ref = new AtomicReference<>();
        assertDoesNotThrow(() -> {
            ref.set(loader.fetchProductTypes());
        });

        return ref.get();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("csvLoader")
    @DisplayName("CSV ProductType File Loader")
    void csvFileLoader(String testName, InputStream inputStream, BiConsumer<Map<String, ProductType>, MockErrorReporter> asserts) {
        MockErrorReporter errReporter = new MockErrorReporter();

        Map<String, ProductType> productTypes = runLoader(
            new ProductTypeLoaderCSVFile(
                Helper.createTempFileFromInputStream("tstFile", ".csv", inputStream),
                ClassLoader.getSystemClassLoader(),
                errReporter));

        asserts.accept(productTypes, errReporter);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("csvLoader")
    @DisplayName("CSV ProductType PipeInput Loader")
    void csvPipeInputLoader(String testName, InputStream inputStream, BiConsumer<Map<String, ProductType>, MockErrorReporter> asserts) {
        System.setIn(inputStream);

        MockErrorReporter errReporter = new MockErrorReporter();
        Map<String, ProductType> productTypes = runLoader(
                new ProductTypeLoaderCSVPipeInput(
                        ClassLoader.getSystemClassLoader(),
                        errReporter));

        asserts.accept(productTypes, errReporter);
    }

    public static Stream<Arguments> csvLoader() {
        return Stream.of(
            arguments(
                "Empty file",
                InputStream.nullInputStream(),
                (BiConsumer<Map<String, ProductType>, MockErrorReporter>)(productTypes, errReporter) -> {
                    assertTrue(productTypes.isEmpty(), "Empty file should produce empty list");

                    assertTrue(errReporter.errors.isEmpty(), "Empty file should report no errors");
                    assertTrue(errReporter.warnings.isEmpty(), "Empty file should report no warnings");
            }),

            arguments(
                "tstProductType.csv",
                ProductTypeLoaderTests.class.getResourceAsStream("resources/tstProductType.csv"),
                (BiConsumer<Map<String, ProductType>, MockErrorReporter>)(productTypes, errReporter) -> {
                    assertEquals(Map.of(
                        "general", new GeneralProduct("general"),
                        "cheese", new Cheese("cheese"),
                        "wine", new Wine("wine")),
                        productTypes,
                        "Generated ProductTypeMap is not correct"
                    );

                    //[Warning] Error while parsing productType CSV. Cannot find or use class 'X' for ProductType 'error': java.lang.ClassNotFoundException: de.superdupermarkt.shelflife.rules.X
                    //[Warning] Error while parsing productType CSV. Cannot find or use class 'Product' for ProductType 'error2': java.lang.ClassNotFoundException: de.superdupermarkt.shelflife.rules.Product
                    assertTrue(errReporter.errors.isEmpty(), "Test file should report no errors");
                    assertIterableEquals(List.of(
                            new ProductTypeNotFoundException("X", "error", new Exception()),
                            new ProductTypeNotFoundException("Product", "error2", new Exception()),
                            new ProductTypeLoaderCSV.FieldLengthError(1, ""),
                            new ProductTypeNotValidException("InvalidProductType", "error3")),
                        errReporter.warnings,
                        "Produced warnings were not as expected"
                    );
            })
        );
    }
}
