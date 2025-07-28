package de.superdupermarkt.shelflife.data.view;

import de.superdupermarkt.shelflife.data.Product;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/*
 * DataView implementation that sends its output directly to the console.
 */
public class ConsoleOutput implements DataView {
    /**
     * A StringBuilder works best if it can be reused again and again for similar data, which is the case in this class.
     */
    private final StringBuilder sb = new StringBuilder();
    private final DecimalFormat priceFormat = new DecimalFormat("#0.00€");

    /** Implicit constructor would be the same, but it is cleaner to be explicit **/
    public ConsoleOutput() {}

    @Override
    public void outputInventory(List<Product> products) {
        System.out.println("=== Inventar ===");
        System.out.println("Produkt\tTyp\tBasisPreis\tQualität\tPreis\tVerfallsdatum\tAnzahl");
        System.out.println();

        for(Product product : products) {
            this.sb.setLength(0);
            this.sb.append(product.getName());
            this.sb.append(":\t");
            this.sb.append(product.getType().getName());
            this.sb.append("\t");
            this.sb.append(priceFormat.format(product.getBasePrice()));
            this.sb.append("\t");
            this.sb.append(product.getQuality());
            this.sb.append("\t");
            this.sb.append(priceFormat.format(product.getPrice()));
            this.sb.append("\t");
            this.sb.append(product.getDueDate().format(DateTimeFormatter.ISO_DATE));

            System.out.print(this.sb);

            if (product.shouldUnshelf()) {
                System.out.print("\t<- Aus Regal entfernen!");
            }

            System.out.println();
        }

        System.out.println();
    }

    @Override
    public void outputDay(LocalDate day, List<Product> products) {
        System.out.println("--- "+day.format(DateTimeFormatter.ISO_DATE)+" ---");
        System.out.println("Produkt\tPreis\tQualität");
        System.out.println();

        for(Product product : products) {
            this.sb.setLength(0);
            this.sb.append(product.getName());
            this.sb.append(":\t");
            this.sb.append(priceFormat.format(product.getPrice()));
            this.sb.append("\t");
            this.sb.append(product.getQuality());
            System.out.print(this.sb);

            if (product.shouldUnshelf()) {
                System.out.print("\t<- Aus Regal entfernen!");
            }

            System.out.println();
        }

        System.out.println();
    }

    @Override
    public void done() {
        System.out.println("Done.");
    }

    @Override
    public void error(Exception ex) {
        System.err.print("[Error] ");
        System.err.println(ex.toString());
    }

    public void error(Error error) {
        System.err.print("[Error] ");
        System.err.println(error.getMessage());
    }

    @Override
    public void warning(Exception ex) {
        System.err.print("[Warning] ");
        System.err.println(ex.toString());
    }

    public void warning(Error error) {
        System.err.print("[Warning] ");
        System.err.println(error.getMessage());
    }
}
