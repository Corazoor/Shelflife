package de.superdupermarkt.shelflife.rules;

import de.superdupermarkt.shelflife.data.Product;

import java.time.LocalDate;

/***
 * Strategy pattern. Advantages:
 * Different productTypes (e.g. different cheeses) can share the same rule.
 * Configurable during program load (e.g. a csv file) instead of being fixed via code.
 * No inheritance restrictions for products themselves, only for these rules.
 */
public interface ProductRule {
    /**
     * Calculates a new quality for a given product.
     * Assumes that exactly one day has passed. (This could be fixed by adding another parameter specifying the amount of passed days since last update)
     *
     * @param product The product to calculate the new quality for
     * @param day The day for which the update occurs
     * @return The newly calculated quality for the given product
     */
    public int calculateQuality(Product product, LocalDate day);

    /**
     * Calculates the current price for a given product.
     * Will use the basePrice and depending on the implementation various other properties of the product.
     * <p>
     * Should be called AFTER the quality update.
     *
     * @param product The product to calculate the new quality for
     * @param day  The day at which the update occurs. Not used by current implementations, but exists for extensibility
     * @return The newly calculated price for the given product
     */
    public double calculatePrice(Product product, LocalDate day);

    /**
     * Determines wether a product should be removed from the shelf.
     * <p>
     * Should be called AFTER the quality update.
     *
     * @param product The product to determine shelf status for
     * @param day The day for which the update occurs
     * @return A boolean representing if the product should be removed (true) or can stay (false) on the shelf.
     */
    public boolean shouldUnshelf(Product product, LocalDate day);
}
