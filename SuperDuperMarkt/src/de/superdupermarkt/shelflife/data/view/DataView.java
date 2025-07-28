package de.superdupermarkt.shelflife.data.view;

import de.superdupermarkt.shelflife.data.Product;

import java.time.LocalDate;
import java.util.List;

/**
 * Accesses the data from the model and converts it into a format suitable for output.
 * This interface directly sends the result to the output channels and includes ways to report errors.
 *
 * Normally, a View is only used to transform a model into various formats.
 * But it can also be used to directly create output, resulting in a flexible way to present results.
 * With some additional work, a composable solution can be created (e.g. Formatting in Format X and then Outputting to the console or a database)
 *
 * Additionally, one would not combine a View with an ErrorReporter.
 * But since we included output in the view, this is a natural extension.
 */
public interface DataView extends ErrorReporter {
    /**
     * Used to show the initial inventory directly after loading the list of products.
     * @param products List of products to display
     */
    void outputInventory(List<Product> products);

    /**
     * Used to display the result of each daily update.
     * @param day The concrete day for which the result was calculated
     * @param products THe list of updated Products for the given day
     */
    void outputDay(LocalDate day, List<Product> products);

    /**
     * Signifies the end of processing.
     */
    void done();
}
