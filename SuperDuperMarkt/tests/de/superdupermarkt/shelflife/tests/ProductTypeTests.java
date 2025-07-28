package de.superdupermarkt.shelflife.tests;

import de.superdupermarkt.shelflife.data.ProductType;
import de.superdupermarkt.shelflife.data.loader.exception.ProductTypeNotFoundException;
import de.superdupermarkt.shelflife.data.loader.exception.ProductTypeNotValidException;
import de.superdupermarkt.shelflife.rules.Cheese;
import de.superdupermarkt.shelflife.rules.GeneralProduct;
import de.superdupermarkt.shelflife.rules.Wine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProductTypeTests {
    @Test
    @DisplayName("::fromClassName - Class exists but is not subtype of ProductType")
    void fromClassName_ExistsButNotSubtype() {
        ProductTypeNotValidException ex = assertThrows(ProductTypeNotValidException.class, () -> {
            ProductType.fromClassName(ClassLoader.getSystemClassLoader(), "ProductRule", "Test");
        });

        assertEquals(new ProductTypeNotValidException("ProductRule", "Test"), ex, "Unexpected Exception Message");
    }

    @Test
    @DisplayName("::fromClassName - Class does not exist")
    void fromClassName_NotExists() {
        ProductTypeNotFoundException ex = assertThrows(ProductTypeNotFoundException.class, () -> {
            ProductType.fromClassName(ClassLoader.getSystemClassLoader(), "XXX", "Test");
        });

        assertEquals(new ProductTypeNotFoundException("XXX", "Test", new Exception()), ex, "Unexpected Exception Message");
    }

    @Test
    @DisplayName("::fromClassName - Given Type and Name matches object")
    void fromClassName_ExistsAndNameMatches() {
        final String typeName = "TestName";
        ProductType productType = Assertions.assertDoesNotThrow(() ->
                ProductType.fromClassName(ClassLoader.getSystemClassLoader(), GeneralProduct.class.getSimpleName(), typeName));

        assertInstanceOf(GeneralProduct.class, productType);
        assertEquals(typeName, productType.getName());
    }

    @Test
    @DisplayName("equals method")
    void equalsMethod() {
        assertEquals(new GeneralProduct("test"), new GeneralProduct("test"), "GeneralProduct not equals");
        assertNotEquals(new GeneralProduct("test"), new GeneralProduct("otherTest"), "GeneralProduct with different name should not be equals");

        assertEquals(new Cheese("test"), new Cheese("test"), "GeneralProduct not equals");
        assertNotEquals(new Cheese("test"), new Cheese("otherTest"), "GeneralProduct with different name should not be equals");

        assertEquals(new Wine("test"), new Wine("test"), "GeneralProduct not equals");
        assertNotEquals(new Wine("test"), new Wine("otherTest"), "GeneralProduct with different name should not be equals");

        List<ProductType> testTypes = new ArrayList<>() {{
            add(new GeneralProduct("test"));
            add(new Cheese("test"));
            add(new Wine("test"));
        }};

        while(!testTypes.isEmpty()) {
            ProductType tstType = testTypes.removeFirst();
            for(ProductType tstAgainst : testTypes) {
                assertNotEquals(tstType, tstAgainst, "Different subclasses should not be equals");
            }
        }
    }
}
