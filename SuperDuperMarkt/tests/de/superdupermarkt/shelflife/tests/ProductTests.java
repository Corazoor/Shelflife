package de.superdupermarkt.shelflife.tests;

import de.superdupermarkt.shelflife.data.Product;
import de.superdupermarkt.shelflife.data.ProductType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ProductTests {
    /**
     * Mock class with fixed implementation to keep these tests stable, no matter how the actual Type classes change.
     * The actual implementations are tested separately
     */
    private final ProductType mockProductType = new ProductType("MockProductType")  {
        @Override
        public int calculateQuality(Product product, LocalDate day) {
            return product.getQuality() + 1;
        }

        @Override
        public double calculatePrice(Product product, LocalDate day) {
            return product.getBasePrice() * product.getQuality();
        }

        @Override
        public boolean shouldUnshelf(Product product, LocalDate day) {
            return day.isAfter(product.getDueDate());
        }
    };

    @Test
    @DisplayName("Constructor and getter")
    void constructorAndGetter() {
        String name = "TestProduct";
        int quality = 5;
        double basePrice = 1.1;
        LocalDate dueDate = LocalDate.of(2025, 1, 1);

        Product product = new Product(
            mockProductType,
            name,
            quality,
            basePrice,
            dueDate,
            dueDate
        );

        //basic properties
        assertEquals(mockProductType, product.getType(), "ProductType error"); //tests for referntial equality, which is exactly what we want in this case
        assertEquals(name, product.getName(), "Name error");
        assertEquals(quality, product.getQuality(), "Quality error");
        assertEquals(basePrice, product.getBasePrice(), "BasePrice error");
        assertTrue(dueDate.isEqual(product.getDueDate()), "DueDate error");

        //generated properties, initialized via calculation
        assertEquals(basePrice*quality, product.getPrice(), "Calculated price error");
        assertFalse(product.shouldUnshelf(), "Unshelf Flag error"); //since dueDate == initialDate
    }

    @Test
    @DisplayName("Unshelf initialization with overdue dueDate")
    void initialUnshelfUpdate() {
        LocalDate dueDate = LocalDate.of(2025, 1, 1);

        Product product = new Product(
            mockProductType,
            "TestProduct",
            5,
            1.1,
            dueDate,
            dueDate.plusDays(1)
        );

        assertTrue(product.shouldUnshelf()); //since dueDate < initialDate
    }

    @Test
    @DisplayName("Update method")
    void updateMethod() {
        LocalDate initialDate = LocalDate.of(2025, 1, 1);

        Product product = new Product(
                mockProductType,
                "TestProduct",
                1,
                1.1,
                initialDate.plusDays(3),
                initialDate
        );

        for(int i = 1; i < 5; i++) {
            assertEquals(i, product.getQuality(), "Quality not properly updated at step"+i);
            assertEquals(i*1.1, product.getPrice(), "Price not properly updated at step"+i);
            assertFalse(product.shouldUnshelf(), "Unshelf wrong at step "+i); //since dueDate >= initialDate

            product.update(initialDate.plusDays(i));
        }

        assertEquals(5, product.getQuality(), "Quality not properly updated at end of test");
        assertEquals(5*1.1, product.getPrice(), "Price not properly updated at end of test");
        assertTrue(product.shouldUnshelf(), "Unshelf wrong at end of thest"); //since after last update dueDate < initialDate
    }

    //@todo equals method!
}
