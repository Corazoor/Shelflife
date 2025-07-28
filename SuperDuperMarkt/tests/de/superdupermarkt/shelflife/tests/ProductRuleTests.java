package de.superdupermarkt.shelflife.tests;

import de.superdupermarkt.shelflife.data.Product;
import de.superdupermarkt.shelflife.data.ProductType;
import de.superdupermarkt.shelflife.rules.Cheese;
import de.superdupermarkt.shelflife.rules.GeneralProduct;
import de.superdupermarkt.shelflife.rules.Wine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ProductRuleTests {
    private final LocalDate dueDate = LocalDate.of(2025, 2, 28);

    private Product createTestProduct(ProductType type, int quality) {
        return new Product(
            type,
            "TestProduct",
            quality,
            2.6,
            dueDate,
            dueDate
        );
    }

    @Test
    @DisplayName("GeneralProduct Type")
    void testGeneralProductType() {
        Product product = createTestProduct(new GeneralProduct("TestTypeGeneralProduct"), 5);

        assertEquals(3.1, product.getType().calculatePrice(product, dueDate), "Calculated Price is wrong");
        assertEquals(5, product.getType().calculateQuality(product, dueDate), "Calculated Quality is wrong, it should not change");

        assertFalse(product.getType().shouldUnshelf(product, dueDate.minusDays(1)), "Unshelf Flag is wrong, despite testDate before dueDate");
        assertFalse(product.getType().shouldUnshelf(product, dueDate), "Unshelf Flag is wrong, product should remain on shelf until AFTER dueDate");
        assertTrue(product.getType().shouldUnshelf(product, dueDate.plusDays(1)), "Unshelf Flag is wrong, despite being overdue");
    }

    @Test
    @DisplayName("Cheese Type")
    void testCheeseProductType() {
        Product product = createTestProduct(new Cheese("TestTypeCheese"), 30);

        assertEquals(5.6, product.getType().calculatePrice(product, dueDate), "Calculated Price is wrong");
        assertEquals(29, product.getType().calculateQuality(product, dueDate), "Calculated Quality is wrong, it should decrease by one");

        assertTrue(product.getType().shouldUnshelf(product, dueDate.minusDays(49)), "Unshelf Flag is wrong, -49 days");
        assertFalse(product.getType().shouldUnshelf(product, dueDate.minusDays(50)), "Unshelf Flag is wrong, -50 days");
        assertFalse(product.getType().shouldUnshelf(product, dueDate.minusDays(51)), "Unshelf Flag is wrong, -51 days");

        assertFalse(product.getType().shouldUnshelf(product, dueDate.minusDays(99)), "Unshelf Flag is wrong, -99 days");
        assertFalse(product.getType().shouldUnshelf(product, dueDate.minusDays(100)), "Unshelf Flag is wrong, -100 days");
        assertTrue(product.getType().shouldUnshelf(product, dueDate.minusDays(101)), "Unshelf Flag is wrong, -101 days");

        product = createTestProduct(new Cheese("TestTypeCheese"), 21);
        assertTrue(product.getType().shouldUnshelf(product, dueDate.minusDays(50)), "Unshelf Flag is wrong, -50 days");
        assertTrue(product.getType().shouldUnshelf(product, dueDate.minusDays(51)), "Unshelf Flag is wrong, -51 days");

        assertTrue(product.getType().shouldUnshelf(product, dueDate.minusDays(99)), "Unshelf Flag is wrong, -99 days");
        assertTrue(product.getType().shouldUnshelf(product, dueDate.minusDays(100)), "Unshelf Flag is wrong, -100 days");
    }

    @Test
    @DisplayName("Wine ProductType")
    void testWineProductType() {
        Product product = createTestProduct(new Wine("TestTypeWine"), 48);

        assertEquals(7.4, product.getType().calculatePrice(product, dueDate), "Calculated Price is wrong");

        assertEquals(48, product.getType().calculateQuality(product, dueDate), "Calculated Quality is wrong, it should not change");

        assertEquals(48, product.getType().calculateQuality(product, dueDate.plusDays(9)), "Calculated Quality is wrong +9 days");
        assertEquals(49, product.getType().calculateQuality(product, dueDate.plusDays(10)), "Calculated Quality is wrong +10 days");
        assertEquals(49, product.getType().calculateQuality(product, dueDate.plusDays(11)), "Calculated Quality is wrong +11 days");

        assertEquals(49, product.getType().calculateQuality(product, dueDate.plusDays(19)), "Calculated Quality is wrong +19 days");
        assertEquals(50, product.getType().calculateQuality(product, dueDate.plusDays(20)), "Calculated Quality is wrong +20 days");
        assertEquals(50, product.getType().calculateQuality(product, dueDate.plusDays(21)), "Calculated Quality is wrong +21 days");

        assertEquals(50, product.getType().calculateQuality(product, dueDate.plusDays(30)), "Calculated Quality is wrong +30 days");
        assertEquals(50, product.getType().calculateQuality(product, dueDate.plusDays(31)), "Calculated Quality is wrong +31 days, should net go above 50");
        assertEquals(50, product.getType().calculateQuality(product, dueDate.plusDays(32)), "Calculated Quality is wrong +32 days, should net go above 50");

        //test a reasonable range of dates
        for(int i = -110; i < 110; i++) {
            assertFalse(product.getType().shouldUnshelf(product, dueDate.plusDays(i)), "Unshelf Flag is wrong, should never change: "+i);
        }

        for(int i = 0; i < 110; i++) {
            product.update(dueDate.plusDays(i));
            assertEquals(7.4, product.getType().calculatePrice(product, dueDate), "Calculated Price got updated but should stay the same at "+i);
        }

        for(int i = -110; i < 110; i++) {
            product = createTestProduct(new Wine("TestTypeWine"), i);
            assertEquals(i<0, product.shouldUnshelf(), "Unshelf Flag is wrong, negative quality should not be accepted but all others should: "+i);
        }

        product = createTestProduct(new Wine("TestTypeWine"), -5);
        assertTrue(product.getType().shouldUnshelf(product, dueDate), "Negative quality should not be accepted");
    }
}
