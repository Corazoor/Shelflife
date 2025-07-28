package de.superdupermarkt.shelflife.rules;

import de.superdupermarkt.shelflife.data.Product;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Wine extends GeneralProduct {
    public Wine(String name) {
        super(name);
    }

    @Override
    public int calculateQuality(Product product, LocalDate day) {
        int daysOverdue = (int)ChronoUnit.DAYS.between(product.getDueDate(), day);
        int qualityGain = Math.max(0, daysOverdue/10); //daysOverdue can be negative, thus we prevent it from falling below 0

        return Math.min(50, product.getQuality() + qualityGain);
    }

    /**
     * We use a price of 0 as a "sentinel value", i.e. if a product has a price of 0, it is not yet calculated
     * We can do this, because it's highly implausible to sell stuff for free
     *
     * @param product
     * @param day
     * @return
     */
    @Override
    public double calculatePrice(Product product, LocalDate day) {
        return product.getPrice() == 0 ? super.calculatePrice(product, day) : product.getPrice();
    }

    @Override
    public boolean shouldUnshelf(Product product, LocalDate day) {
        return product.getQuality() < 0;
    }
}
