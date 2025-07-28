package de.superdupermarkt.shelflife.rules;

import de.superdupermarkt.shelflife.data.Product;

import java.time.LocalDate;

public class Cheese extends GeneralProduct {
    public Cheese(String name) {
        super(name);
    }

    @Override
    public int calculateQuality(Product product, LocalDate day) {
        return product.getQuality() - 1;
    }

    /*@Override
    public double calculatePrice(Product product) {
        return super.calculatePrice(product);
    }*/

    @Override
    public boolean shouldUnshelf(Product product, LocalDate day) {
        return product.getQuality() < 30
            || day.isAfter(product.getDueDate().minusDays(50)) //creating new objects could be very costly during tight loops on many objects, but is fine for most applications
            || day.isBefore(product.getDueDate().minusDays(100));
    }
}
