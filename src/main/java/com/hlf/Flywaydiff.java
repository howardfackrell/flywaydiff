package com.hlf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by howard.fackrell on 9/8/16.
 */
public class Flywaydiff {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RED = "\u001B[31m";

    private static JdbcTemplate getJdbcTemplateFromUrl(final String url) {
        return new JdbcTemplate(new DriverManagerDataSource(url));
    }

    public static void main(String ... args) throws Exception {

        Properties properties = new Properties();
        properties.load(Flywaydiff.class.getClassLoader().getResourceAsStream("env.properties"));

        String left = properties.getProperty(args[0]) != null ? properties.getProperty(args[0]) : args[0];
        String right = properties.getProperty(args[1]) != null ? properties.getProperty(args[1]) : args[1];

        List<Migration> leftMigrations = getMigrations(getJdbcTemplateFromUrl(left));
        Collections.sort(leftMigrations, new MigrationComparator());
        List<Migration> rightMigrations = getMigrations(getJdbcTemplateFromUrl(right));

        for (Migration migration : leftMigrations) {
            if (rightMigrations.contains(migration)) {
                System.out.print(ANSI_GREEN + "+");
            } else {
                System.out.print(ANSI_RED);
            }
            System.out.println(migration + ANSI_RESET);
        }
    }

    static List<Migration> getMigrations(JdbcTemplate db) {
        return db.query(
                "select \"version\", \"script\" from \"schema_version\" where \"version\" is not null order by \"version\"",

                new RowMapper<Migration>() {
                    public Migration mapRow(ResultSet resultSet, int i) throws SQLException {
                        return new Migration(resultSet.getString("version"), resultSet.getString("script"));
                    }
                });
    }
}

class Migration {
    final double version;
    final String script;
    public Migration(String version, String script) {
        this.version = Double.parseDouble(version);
        this.script = script;
    }
    public String toString() {
        return "\t" + version + "\t" + script;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Migration)) return false;

        Migration migration = (Migration) o;

        if (Double.compare(migration.version, version) != 0) return false;
        return script.equals(migration.script);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(version);
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + script.hashCode();
        return result;
    }
}

class MigrationComparator implements Comparator<Migration> {
    public int compare(Migration o1, Migration o2) {
        if (o1.version < o2.version)
            return -1;
        else if (o1.version > o2.version)
            return 1;
        else
            return o1.script.compareTo(o2.script);
    }
}
