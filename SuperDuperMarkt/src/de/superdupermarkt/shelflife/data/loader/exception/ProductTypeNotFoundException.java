package de.superdupermarkt.shelflife.data.loader.exception;

/**
 * Used during classloading (mainly for modules) to denote that the given class from config could not be found.
 */
public class ProductTypeNotFoundException extends ProductTypeNotValidException {
    public ProductTypeNotFoundException(String className, String typeName, Exception ex) {
        super("Cannot find or use class '"+ className +"' for ProductType '"+ typeName + "'", className, typeName, ex);
    }
}
