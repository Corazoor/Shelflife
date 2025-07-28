package de.superdupermarkt.shelflife.sql;

import de.superdupermarkt.shelflife.sql.helper.Query;
import de.superdupermarkt.shelflife.sql.schema.ShelflifeTables;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

import static de.superdupermarkt.shelflife.sql.helper.Query.mysqlDateFormat;

/**
 * Container class for actual queries and their semantics.
 *
 * This is a simple yet effective style to alleviate most issues with manually constructed sql queries.
 *
 * Each method represents a query with a certain style, and are grouped together by putting them into appropriate
 * container classes, separated by domain. Since this project is small, it suffices to put them together here,
 * but in larger project there could be many more classes like this for the various parts of the system.
 *
 * The provided interface for each method ensures type safety for individual fields, while still being decoupled from
 * actual objects. This increases code flexibility at the use side and decreases allocation cost without being too loose.
 *
 * Arguments for prepared statements are provided directly via the method call, which helps prevent usage errors.
 *
 * The use of constants for table names helps during later refactoring. The same could (and arguable should) be done for
 * field names, but this can get unwieldy and unreadable very quickly. Field names rarely change in practice anyway, and with
 * proper testing and organization, refactoring field names is still a comparatively straightforward task despite having
 * to edit many strings directly.
 */
public class SQLProductAndType {
    public interface fetchProductTypesConsumer{ void accept(String name, String className) throws SQLException; }
    public static void fetchProductTypes(Connection conn, fetchProductTypesConsumer consumer) throws SQLException {
        //Concatenating strings like this for each query execution might seem wasteful, but the overhead is negligible
        //compared to the network round trip each query usually incurs.
        Query.fetchSimple(
            conn,
            "SELECT pt.name, pt.class FROM "+ ShelflifeTables.ProductType +" pt;",
            rs -> {
                //since the query and its consumer are always close to each other, it is usually fine to get values by index
                //this is measurably faster for very large datasets, but can still become confusing for large queries with many columns
                consumer.accept(rs.getString(1), rs.getString(2));
            });
    }

    public interface fetchProductsConsumer{ void accept(String name, String type, int quality, double basePrice, LocalDate dueDate) throws SQLException; }
    public static void fetchProducts(Connection conn, LocalDate atDay, fetchProductsConsumer consumer) throws SQLException {
        Query.fetchPrepared(
            conn,
            "SELECT p.name, IFNULL(pt.name, '') as type, p.quality, p.basePrice, p.dueDate " +
            "FROM "+ ShelflifeTables.Product +" p " +
            "LEFT JOIN "+ ShelflifeTables.ProductType +" pt ON pt.id = p.productType_id " +
            "WHERE p.`day` = ?",
            ps -> {
                ps.setString(1, atDay.format(mysqlDateFormat));
            },
            rs -> {
                //accessing values by field name is a bit slower
                //but much more readable and less prone to errors when the column order changes in the query above
                consumer.accept(
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getInt("quality"),
                        rs.getDouble("basePrice"),
                        LocalDate.parse(rs.getString("dueDate"), mysqlDateFormat));
            });
    }
}
