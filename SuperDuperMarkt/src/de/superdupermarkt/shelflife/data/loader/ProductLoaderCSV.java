package de.superdupermarkt.shelflife.data.loader;

import de.superdupermarkt.shelflife.data.Product;
import de.superdupermarkt.shelflife.data.ProductType;
import de.superdupermarkt.shelflife.data.loader.exception.DataLoaderAccessException;
import de.superdupermarkt.shelflife.data.view.ErrorReporter;
import de.superdupermarkt.shelflife.helper.CSV;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Template Method pattern. Implements a shared algorithm, but individual steps can be overwritten by subclasses.
 * <p>
 * In this case it decouples acquiring the individual csv lines from the code processing them.
 * This could be achieved in various other ways for such a simple task, but this allows for subclasses to have their own properties (like a file reference).
 * <p>
 * A composable pattern like a handler or strategy would be more appropriate to enable mix and match of loading method
 * and parsed format. Something like ProductLoaderCSV(FileReader()) and ProductLoaderXML(FileReader())
 * But this would complicate configuration via commandline/properties even more, therefore this pattern was chosen.
 */
public abstract class ProductLoaderCSV extends ProductLoader {
    public ProductLoaderCSV(ErrorReporter errorReporter) {
        super(errorReporter);
    }

    /**
     * Provides a stream of individual lines of the actual CSV Data.
     *
     * @return A Stream of the individual lines of the loaded CSV.
     * @throws DataLoaderAccessException
     */
    protected abstract Stream<String> loadLines() throws DataLoaderAccessException;

    @Override
    public List<Product> fetchProducts(LocalDate startDate, final Map<String, ProductType> productTypes) throws DataLoaderAccessException {
        try(Stream<String> lines = this.loadLines()) {
            return CSV.fetchListFromCSV(lines, fields -> {
                if(fields.length != 5) { //type, name, quality, basePrice, dueDate, quantity
                    this.errorReporter.warning(new FieldLengthError(fields.length, String.join(";", fields)));
                    return null;
                } else {
                    ProductType type = productTypes.get(fields[0]);
                    if(type == null) {
                        errorReporter.warning(new ProductTypeNotFoundError(fields[0], String.join(";", fields)));
                        return null; //continue operation but ignore this row
                    }

                    try {
                        return new Product(
                                type,
                                fields[1],
                                Integer.parseInt(fields[2]),
                                Double.parseDouble(fields[3]),
                                LocalDate.parse(fields[4], DateTimeFormatter.ISO_DATE),
                                startDate);
                    } catch (NumberFormatException ex) {
                        errorReporter.warning(new NotAValidNumberError(String.join(";", fields)));
                    } catch (DateTimeParseException ex) {
                        errorReporter.warning(new NotAValidDateError(String.join(";", fields)));
                    }

                    return null; //continue operation but ignore this row
                }
            });
        }
    }

    /**
     * The given csv line did not contain the expected amount of fields.
     */
    public static class FieldLengthError implements ErrorReporter.Error {
        private final int actualLength;
        private final String line;

        public FieldLengthError(int actualLength, String line) {
            this.actualLength = actualLength;
            this.line = line;
        }

        @Override
        public String getMessage() {
            return "Error while parsing product CSV. Expected exactly 5 fields but got "+this.actualLength+": "+line;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            FieldLengthError that = (FieldLengthError) o;
            return actualLength == that.actualLength;
        }
    }

    /**
     * The provided map of ProductTypes did not contain a key for the typename specified in the csv.
     */
    public static class ProductTypeNotFoundError implements ErrorReporter.Error {
        private final String producTypeName;
        private final String line;

        public ProductTypeNotFoundError(String producTypeName, String line) {
            this.producTypeName = producTypeName;
            this.line = line;
        }

        @Override
        public String getMessage() {
            return "Error while parsing product CSV. Given productType "+ this.producTypeName +" not found: "+line;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            ProductTypeNotFoundError that = (ProductTypeNotFoundError) o;
            return Objects.equals(producTypeName, that.producTypeName);
        }
    }

    /**
     * Tried to parse an Integer or Double, but the value was not in a valid format.
     */
    public static class NotAValidNumberError implements ErrorReporter.Error {
        private final String line;

        public NotAValidNumberError(String line) {
            this.line = line;
        }

        @Override
        public String getMessage() {
            return "Error while parsing product CSV. Not a valid number in line: "+ this.line;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            return true;
        }
    }

    /**
     * Tried to parse a Date, but the value was not in a valid format.
     */
    public static class NotAValidDateError implements ErrorReporter.Error {
        private final String line;

        public NotAValidDateError(String line) {
            this.line = line;
        }

        @Override
        public String getMessage() {
            return "Error while parsing product CSV. Not a valid date in line: "+ this.line;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            return true;
        }
    }
}
