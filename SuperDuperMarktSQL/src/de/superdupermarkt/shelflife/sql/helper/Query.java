package de.superdupermarkt.shelflife.sql.helper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class Query {
    public final static DateTimeFormatter mysqlDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void fetchSimple(Connection conn, String query, SQLConsumer<ResultSet> consumer) throws SQLException {
        try(PreparedStatement ps = conn.prepareStatement(query)) {
            try(ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    consumer.accept(rs);
                }
            }
        }
    }

    public static void fetchPrepared(Connection conn, String query, SQLConsumer<PreparedStatement> prepare, SQLConsumer<ResultSet> consumer) throws SQLException {
        try(PreparedStatement ps = conn.prepareStatement(query)) {
            prepare.accept(ps);
            try(ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    consumer.accept(rs);
                }
            }
        }
    }

    public interface SQLConsumer<T> {
        void accept(T t) throws SQLException;
    }
}
