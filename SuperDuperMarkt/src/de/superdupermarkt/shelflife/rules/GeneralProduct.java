package de.superdupermarkt.shelflife.rules;

import de.superdupermarkt.shelflife.data.Product;
import de.superdupermarkt.shelflife.data.ProductType;

import java.time.LocalDate;

public class GeneralProduct extends ProductType {
    public GeneralProduct(String name) {
        super(name);
    }

    /**
     * The specification did not contain any rule for changing quality on products other than cheese and wine.
     * Under the assumption that this is correct, we return the original quality.
     * @param product
     * @return
     */
    @Override
    public int calculateQuality(Product product, LocalDate day) {
        return product.getQuality();
    }

    @Override
    public double calculatePrice(Product product, LocalDate day) {
        return product.getBasePrice() + 0.1*product.getQuality();
    }

    @Override
    public boolean shouldUnshelf(Product product, LocalDate day) {
        return day.isAfter(product.getDueDate());
    }
}
