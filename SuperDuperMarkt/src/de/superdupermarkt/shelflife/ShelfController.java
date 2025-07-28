package de.superdupermarkt.shelflife;

import de.superdupermarkt.shelflife.data.Product;
import de.superdupermarkt.shelflife.data.ProductType;
import de.superdupermarkt.shelflife.data.loader.ProductLoader;
import de.superdupermarkt.shelflife.data.loader.exception.DataLoaderAccessException;
import de.superdupermarkt.shelflife.data.loader.ProductTypeLoader;
import de.superdupermarkt.shelflife.data.view.DataView;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * MVC (Model View Controller) architecture, with a simplified Model (List<Product>).
 * Slightly overkill for this relatively simple case, but it still offers several advantages:
 * The view and model can be easily tested independently of each other.
 * It is very easy to implement a different logic (e.g. only increment by one day and return a CSV)
 * The view could also be changed to store the result into a database instead of Outputting to console.
 * The DataLoader allows switching between data sources (e.g. CSV or SQL) without changing the controller logic.
 *
 * The controller is NOT implemented via interface, since the actual run method could take different parameters depending on the controller logic.
 */
public class ShelfController {
    private final ProductTypeLoader productTypeLoader;
    private final ProductLoader productLoader; //Strategy pattern in canonical form
    private final DataView view;

    public ShelfController(ProductTypeLoader productTypeLoader, ProductLoader productLoader, DataView view) {
        this.productTypeLoader = productTypeLoader;
        this.productLoader = productLoader;
        this.view = view;
    }

    /**
     * Executes the actual logic of the program.
     * <p>
     * Loads the ProductTypes and Products from the supplied loaders, lists the initial inventory, then loops in 1 day increments
     * and shows the updated Product list after each day until the endDate is reached.
     *
     * @param startDate Initial Date, the inventory will be shown for this day.
     * @param endDate The last day to produce a result for (inclusive).
     */
    public void execute(LocalDate startDate, LocalDate endDate) {
        try {
            Map<String, ProductType> productTypes = this.productTypeLoader.fetchProductTypes();
            List<Product> products = this.productLoader.fetchProducts(startDate, productTypes);

            this.view.outputInventory(products);

            for(LocalDate day = startDate; !day.isAfter(endDate); day = day.plusDays(1)) {
                final LocalDate updateDay = day;
                products = products.stream()
                        .filter(product -> !product.shouldUnshelf()) //filters out all products which should be removed this day
                        .peek(product -> product.update(updateDay)) //lambdas in intermediary operations should be side effect free. fine in this case, since no short-circuit optimization is possible
                                                                            //for a "pure" solution, either a separate iteration with forEach or a map operation to create new Objects should be used
                        .toList(); //typical functional style with immutable lists. A new list is allocated each time, but it's fine for small lists like these

                /* the alternative without the list reallocation
                for(Iterator<Product> iter = products.iterator(); iter.hasNext();) {
                    Product product = iter.next();
                    if(product.shouldUnshelf()) {
                        iter.remove();
                    } else {
                        product.update(day);
                    }
                }
                */

                this.view.outputDay(day, products);
            }

            this.view.done();
        } catch (DataLoaderAccessException ex) {
            this.view.error(ex);
        }
    }
}
