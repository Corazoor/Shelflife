package de.superdupermarkt.shelflife.sql.schema;

/**
 * Contains constants for tables in the respective Database.
 *
 * This is one way to encode a shema and provide configurability for potentiol name changes.
 * Doing it via enum entails a bit more boilerplate, but is still fairly straightforward and provides nice access during query construction.
 *
 * These names are not properly escaped (e.g. a name containing ` would result in an invalid query), but since they are
 * fixed at compile time this is not a big concern, error like these should surface during testing.
 */
public enum ShelflifeTables {
    ProductType("ProductType"),
    Product("Product");

    /**
     * Could also use enum.getName(), but that would couple the name in the code to the field name.
     * Providing an extra property decouples these, which allows for sensible divergence.
     * For example, many schema designs use underscores instead of camel case (product_type instead of ProductType),
     * which can look weird if translated directly to Java code.
     */
    private String name;
    ShelflifeTables(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        //using System.getProperty here enables configurability during runtime
        //this is not perfect, concurrent code with two different configurations would not work for example
        //but in many (if not most) projects this is enough to enable testing with mock schemas
        //or to run multiple program instances on different schemas on the same DB Server
        return "`"+ System.getProperty("db.Shelflife", "db_shelflife") +"`.`"+ this.name +"`";
    }
}
