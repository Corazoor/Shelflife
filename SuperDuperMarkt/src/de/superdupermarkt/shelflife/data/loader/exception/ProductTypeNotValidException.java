package de.superdupermarkt.shelflife.data.loader.exception;

/**
 * Used during classloading (mainly for modules) to denote that the given class from config is not of the proper type required.
 */
public class ProductTypeNotValidException extends Exception {
    public final String className;
    public final String typeName;

    public ProductTypeNotValidException(String className, String typeName) {
        super("Class '"+ className +"' is not a valid ProductType for '"+ typeName + "'");
        this.className = className;
        this.typeName = typeName;
    }

    public ProductTypeNotValidException(String message, String className, String typeName, Exception ex) {
        super(message +": "+ ex.getMessage(), ex);
        this.className = className;
        this.typeName = typeName;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null
            && obj.getClass().equals(this.getClass())
            && (this.className == null && ((ProductTypeNotValidException) obj).className == null
            || this.className != null && this.className.equals(((ProductTypeNotValidException) obj).className))
            && (this.typeName == null && ((ProductTypeNotValidException) obj).typeName == null
            || this.typeName != null && this.typeName.equals(((ProductTypeNotValidException) obj).typeName));
    }
}
