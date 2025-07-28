package de.superdupermarkt.shelflife.data;

import de.superdupermarkt.shelflife.data.loader.exception.ProductTypeNotFoundException;
import de.superdupermarkt.shelflife.data.loader.exception.ProductTypeNotValidException;
import de.superdupermarkt.shelflife.rules.ProductRule;

import java.lang.reflect.InvocationTargetException;

/***
 * Strategy pattern combined with an abstract base class (which could be considered a flyweight pattern). Advantages:
 * Configurable during program load (e.g. a csv file) instead of being fixed via code.
 * Instances are reusable between Products.
 * The abstract class handles requirements for all strategies (in this case only a name for lookup during loading)
 */
public abstract class ProductType implements ProductRule {
    private final String name;

    public ProductType(String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    //no setter, object is immutable after construction

    /**
     * Creates an instance of a ProductType from the given className using the provided classLoader.
     * <p>
     * This enables dynamically adding classes at runtime without having to provide a newly compiled version of the base program.
     *
     * @param classLoader
     * @param className
     * @param productTypeName
     * @return
     * @throws ProductTypeNotValidException
     */
    public static ProductType fromClassName(ClassLoader classLoader, String className, String productTypeName) throws ProductTypeNotValidException {
        try {
            Class<?> clazz = classLoader.loadClass(ProductRule.class.getName().replaceFirst("(?<=\\.)[^.]+$", className));
            if (ProductType.class.isAssignableFrom(clazz)) {
                return (ProductType) clazz.getDeclaredConstructor(String.class).newInstance(productTypeName);
            } else {
                throw new ProductTypeNotValidException(className, productTypeName);
            }
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException |
                 IllegalAccessException | NoSuchMethodException ex) {
            throw new ProductTypeNotFoundException(className, productTypeName, ex);
        }
    }

    /**
     * This is not strictly necessary, but simplifies tests and provides proper equality semantics
     */
    @Override
    public boolean equals(Object obj) {
        return obj != null
                && obj.getClass().equals(this.getClass()) //this is an "instanceof" that checks for strict subclass equality
                && ((ProductType) obj).getName().equals(this.getName());
    }

    @Override
    public String toString() {
        return "ProductType{" +
                "name='" + name + '\'' +
                '}';
    }
}
