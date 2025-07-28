package de.superdupermarkt.shelflife.data.loader.exception;

/**
 * A unified Exception to signal any error while accessing the underlying resource of a DataLoader,
 * like FileNotFoundException, IOException, Database Connection errors etc.
 */
public class DataLoaderAccessException extends Exception {
    public DataLoaderAccessException(String message) {
        super(message);
    }
}
