package de.superdupermarkt.shelflife.rules;

import de.superdupermarkt.shelflife.data.Product;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;

public class NewYearsEve extends GeneralProduct {
    public NewYearsEve(String name) {
        super(name);
    }


    /**
     * Does not change quality.
     */
    @Override
    public int calculateQuality(Product product, LocalDate day) {
        return product.getQuality();
    }

    /**
     * Before new years eve, the basePrice is always used.
     * On NewYearsEve there is a quality surcharge.
     * After new years eve, the price is reduced by half and drops further.
     */
    @Override
    public double calculatePrice(Product product, LocalDate day) {
        LocalDate nearestSilvester = LocalDate.of(day.getMonth().getValue() <= 6 ? day.getYear() -1 : day.getYear(), 12, 31);
        int daysOverdue = (int) ChronoUnit.DAYS.between(day, nearestSilvester);

        return daysOverdue == 0
            ? product.getBasePrice() + product.getQuality()*0.1
            : (daysOverdue < 0
                ? product.getBasePrice()*0.5 + 0.5*daysOverdue
                : product.getBasePrice());
    }

    /**
     * Only allowed to sell in December and January
     */
    @Override
    public boolean shouldUnshelf(Product product, LocalDate day) {
        return day.isAfter(product.getDueDate())
            || !(day.getMonth().equals(Month.DECEMBER) || day.getMonth().equals(Month.JANUARY));
    }
}
