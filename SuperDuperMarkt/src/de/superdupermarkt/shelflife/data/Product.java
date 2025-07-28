package de.superdupermarkt.shelflife.data;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Data class holding information for exactly one Product of a certain type.
 * <p>
 * Implemented as a mutable type with an update method.
 * <p>
 * It could also be implemented as an immutable pattern as a (Java 17) record type (without the price and unshelf flag).
 * In that case the ProductRules need to work with an additional "initialDate" to correctly determine the number of passed days,
 * and for each daily calculation the results of the rules have to be used instead of the properties of this object.
 * This would have the advantage that days can be easily skipped (say weekends) since calculating the result for any day is just one function call.
 * <p>
 * But extending the rules like that could also be done with this mutable style, while using the current rule style with records
 * would result in a lot more allocations, so ultimately the mutable way is more flexible.
 */
public class Product {
    private final ProductType type; //strategy pattern, attached to an abstract class. @see ProductType

    private final String name;
    private final double basePrice;
    private final LocalDate dueDate;

    private int quality;
    private double price;
    private boolean unshelf;

    public Product(ProductType type, String name, int quality, double basePrice, LocalDate dueDate, LocalDate initialDate) {
        this.type = type;
        this.name = name;
        this.basePrice = basePrice;
        this.dueDate = dueDate;

        this.quality = quality;
        this.price = type.calculatePrice(this, initialDate);
        this.unshelf = this.type.shouldUnshelf(this, initialDate);
    }

    //Getters are not strictly necessary for public final fields,
    //but they provide a possible extension point if the values or the structure change in the future

    public ProductType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getQuality() {
        return quality;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public double getPrice() {
        return this.price;
    }

    public boolean shouldUnshelf() {
        return unshelf;
    }

    //no setter, properties should only be modified via rules

    public void update(LocalDate day) {
        this.quality = this.type.calculateQuality(this, day);
        this.price = this.type.calculatePrice(this, day);
        this.unshelf = this.type.shouldUnshelf(this, day);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Double.compare(basePrice, product.basePrice) == 0
                && quality == product.quality
                && Double.compare(price, product.price) == 0
                && unshelf == product.unshelf
                && Objects.equals(type, product.type)
                && Objects.equals(name, product.name)
                && Objects.equals(dueDate, product.dueDate);
    }

    @Override
    public String toString() {
        return "Product{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", basePrice=" + basePrice +
                ", dueDate=" + dueDate +
                ", quality=" + quality +
                ", price=" + price +
                ", unshelf=" + unshelf +
                '}';
    }
}
