package de.superdupermarkt.shelflife.data.loader;

import de.superdupermarkt.shelflife.data.ProductType;
import de.superdupermarkt.shelflife.data.loader.exception.DataLoaderAccessException;
import de.superdupermarkt.shelflife.data.loader.exception.ProductTypeNotValidException;
import de.superdupermarkt.shelflife.data.view.ErrorReporter;
import de.superdupermarkt.shelflife.helper.CSV;

import java.util.Map;
import java.util.stream.Stream;

/**
 * Template Method pattern. Implements a shared algorithm, but individual steps can be overwritten by subclasses.
 * <p>
 * In this case it decouples acquiring the individual csv lines from the code processing them.
 * This could be achieved in various other ways for such a simple task, but this allows for subclasses to have their own properties (like a file reference).
 */
public abstract class ProductTypeLoaderCSV extends ProductTypeLoader {
    public ProductTypeLoaderCSV(ErrorReporter errorReporter, ClassLoader classLoader) {
        super(errorReporter, classLoader);
    }

    protected abstract Stream<String> loadLines() throws DataLoaderAccessException;

    @Override
    public Map<String, ProductType> fetchProductTypes() throws DataLoaderAccessException {
        try(Stream<String> lines = loadLines()) {
            return CSV.fetchMapFromCSV(lines, this::createProductTypeFromFields, ProductType::getName);
        }
    }

    private ProductType createProductTypeFromFields(String[] fields) {
        if (fields.length != 2) { //name, class
            errorReporter.warning(new FieldLengthError(fields.length, String.join(";", fields)));
            return null; //continue operation but ignore this row
        }

        try {
            return ProductType.fromClassName(this.classLoader, fields[1], fields[0]);
        } catch (ProductTypeNotValidException ex) {
            errorReporter.warning(ex);
        }

        return null; //continue operation but ignore this row
    }

    /**
     * The given csv line did not contain the expected amount of fields.
     */
    public static class FieldLengthError implements ErrorReporter.Error {
        public final int actualLength;
        private final String line;

        public FieldLengthError(int actualLength, String line) {
            this.actualLength = actualLength;
            this.line = line;
        }

        @Override
        public String getMessage() {
            return "Error while parsing productType CSV. Expected exactly 2 fields but got "+this.actualLength+": "+line;
        }

        @Override
        public boolean equals(Object obj) {
            return obj != null
                && obj.getClass().equals(this.getClass())
                && ((FieldLengthError) obj).actualLength == this.actualLength;
        }
    }
}
