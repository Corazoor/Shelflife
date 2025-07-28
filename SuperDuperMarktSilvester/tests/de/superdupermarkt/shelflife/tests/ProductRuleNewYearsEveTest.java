package de.superdupermarkt.shelflife.tests;

import de.superdupermarkt.shelflife.data.Product;
import de.superdupermarkt.shelflife.data.ProductType;
import de.superdupermarkt.shelflife.rules.NewYearsEve;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

public class ProductRuleNewYearsEveTest {
    private final LocalDate dueDate = LocalDate.of(2025, 1, 15);

    @Test
    @DisplayName("NewYearsEve Type")
    void testSilvesterProductType() {
        Product product = new Product(
                new NewYearsEve("TestNewYearsEve"),
                "TestProduct",
                22,
                20,
                dueDate,
                dueDate
        );

        for(int i = 0; i < 110; i++) {
            product.update(dueDate.plusDays(i));
            assertEquals(22, product.getType().calculateQuality(product, dueDate), "Calculated Quality got updated but should stay the same at "+i);
        }

        assertFalse(product.getType().shouldUnshelf(product, dueDate.minusDays(1)), "Unshelf Flag is wrong, despite testDate before dueDate");
        assertFalse(product.getType().shouldUnshelf(product, dueDate), "Unshelf Flag is wrong, product should remain on shelf until AFTER dueDate");
        assertTrue(product.getType().shouldUnshelf(product, dueDate.plusDays(1)), "Unshelf Flag is wrong, despite being overdue");

        LocalDate compareDate = dueDate.minusYears(1);
        assertFalse(product.getType().shouldUnshelf(product, dueDate.withMonth(Month.JANUARY.getValue())), "Unshelf Flag is wrong, should sell in January");
        assertFalse(product.getType().shouldUnshelf(product, compareDate), "Unshelf Flag is wrong, should sell in December");
        for(int i = 2; i < 12; i++) {
            assertTrue(product.getType().shouldUnshelf(product, compareDate.withMonth(i)), "Unshelf Flag is wrong, should not sell outside of January and December at "+i);
        }

        LocalDate priceTstDate = LocalDate.of(2024, 12, 15);
        for(int i = 0; i < 16; i++) {
            assertEquals(20, product.getType().calculatePrice(product, priceTstDate.plusDays(i)), "Price changed but should stay the same at " + i);
        }
        assertEquals(22.2, product.getType().calculatePrice(product, priceTstDate.plusDays(16)), "quality surcharge at new years eve is wrong");

        for(int i = 1; i < 10; i++) {
            assertEquals(10-0.5*i, product.getType().calculatePrice(product, priceTstDate.plusDays(16 +i)), "Price after new years eve wront at "+i);
        }
    }
}
