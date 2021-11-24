package searcher;

import java.sql.*;

// STATIC IS VERY SMALL PERFORMANCE COST IN INITIALIZING CON EVERY TIME BUT VERY SMALL MEMORY ADVANTAGE FOR NOT ALWAYS STORING STATES OF DATABASE,
// OVERALL DOESN'T REALLY MATTER IN THE FIRST PLACE BC ONLY CALLED AT BEGINNING
public class Database {
    private static final Connection CON = getConnection();

    // gets and returns a connection to the server with the specified URL, USERNAME, and PASSWORD
    protected static Connection getConnection() {
        final String URL = "jdbc:mysql://127.0.0.1:3306/mdcp";
        final String USERNAME = "luo";
        final String PASSWORD = "luoMySQL123";
        Connection c = null;
        try {
            c = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return c;
    }

    /** 
     * creates three tables with data loaded in from three different google sheets:
     *
     *
     *
     *
     *
     *
      */

    // 'attractions' contains names of locations as well as links to their websites
    // 'attributes' contains the city (or whatever is specified on the address) that the location is in,
    //     the type of attraction, one key description which is used as the foreign key of 'related_descriptions,'
    //     and two supporting descriptions
    // 'nearby_counties' contains the county as well as up to three nearby counties
    // all tables except related_descriptions have a foreign key 'id' which refers to primary key 'id' of 'attractions'
    // most locations in 'attractions' from https://www.busytourist.com/fun-things-to-do-in-maryland/
    // nearby counties based on https://msa.maryland.gov/msa/mdmanual/36loc/html/02maps/seatb.html
    // TODO: revise and edit (ex: ON UPDATE/DELETE CASCADE necessary?)
    // TODO: maybe add indexing later?
    protected static void buildTables() {
        try {
            // https://stackoverflow.com/questions/3271249/difference-between-statement-and-preparedstatement
            // https://dev.mysql.com/doc/refman/8.0/en/mysql-indexes.html

            PreparedStatement createCountiesTable = CON.prepareStatement("CREATE TABLE IF NOT EXISTS counties (" +
                    "id int NOT NULL AUTO_INCREMENT, " +
                    "county varchar(32) NOT NULL, " +
                    "nc1 varchar(32) DEFAULT NULL, " +
                    "nc2 varchar(32) DEFAULT NULL, " +
                    "nc3 varchar(32) DEFAULT NULL, " +
                    "PRIMARY KEY (id), " +
                    "INDEX (id)) " +
                    "ENGINE=INNODB;");
            PreparedStatement createDescriptionsTable = CON.prepareStatement("CREATE TABLE IF NOT EXISTS descriptions (" +
                    "id int NOT NULL AUTO_INCREMENT, " +
                    "desc1 varchar(32) DEFAULT NULL, " +
                    "desc2 varchar(32) DEFAULT NULL, " +
                    "desc3 varchar(32) DEFAULT NULL, " +
                    "desc4 varchar(32) DEFAULT NULL, " +
                    "desc5 varchar(32) DEFAULT NULL, " +
                    "desc6 varchar(32) DEFAULT NULL, " +
                    "desc7 varchar(32) DEFAULT NULL, " +
                    "INDEX (id)) " +
                    "ENGINE=INNODB;");
            PreparedStatement createAttractionsTable = CON.prepareStatement("CREATE TABLE IF NOT EXISTS attractions (" +
                    "id int NOT NULL AUTO_INCREMENT, " +
                    "location_name varchar(255) NOT NULL, " +
                    "website_link varchar(255) NOT NULL, " +
                    "type varchar(64) NOT NULL, " +
                    "city varchar(64) NOT NULL, " +
                    "county_id int NOT NULL, " +
                    "descriptions_id int NOT NULL, " +
                    "PRIMARY KEY (id), " +
                    "INDEX (county_id), " +
                    "INDEX (descriptions_id), " +
                    "FOREIGN KEY (county_id) " +
                        "REFERENCES counties (id) ON UPDATE CASCADE ON DELETE CASCADE, " +
                    "FOREIGN KEY (descriptions_id) " +
                        "REFERENCES descriptions (id) ON UPDATE CASCADE ON DELETE CASCADE) " +
                    "ENGINE=INNODB;");
            // TODO: maybe create tables of words related to each attribute later and add to graph as well

            createCountiesTable.executeUpdate();
            createDescriptionsTable.executeUpdate();
            createAttractionsTable.executeUpdate();


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // loads csv files into tables
    // ignore to prevent loading multiple times each run - https://dev.mysql.com/doc/refman/8.0/en/sql-mode.html#ignore-effect-on-execution basically INSERT IGNORE instead of just INSERT
    // https://stackoverflow.com/questions/2675323/mysql-load-null-values-from-csv-data all hail guy on the internet
    public static void loadData() {
        try {
            PreparedStatement loadCountiesData = CON.prepareStatement("LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/mdcp_counties.csv' IGNORE INTO TABLE mdcp.counties " +
                    "FIELDS TERMINATED BY ',' " +
                    "LINES TERMINATED BY '\\r\\n' " +
                    "IGNORE 1 LINES " +
                    "(id, county, @c1, @c2, @c3) " +
                    "SET " +
                    "nc1 = NULLIF(@c1, ''), " +
                    "nc2 = NULLIF(@c2, ''), " +
                    "nc3 = NULLIF(@c3, '');");
            PreparedStatement loadDescriptionsData = CON.prepareStatement("LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/mdcp_descriptions.csv' IGNORE INTO TABLE mdcp.descriptions " +
                    "FIELDS TERMINATED BY ',' " +
                    "LINES TERMINATED BY '\\r\\n' " +
                    "IGNORE 1 LINES " +
                    "(id, @d1, @d2, @d3, @d4, @d5, @d6, @d7)" +
                    "SET " +
                    "desc1 = NULLIF(@d1, ''), " +
                    "desc2 = NULLIF(@d2, ''), " +
                    "desc3 = NULLIF(@d3, ''), " +
                    "desc4 = NULLIF(@d4, ''), " +
                    "desc5 = NULLIF(@d5, ''), " +
                    "desc6 = NULLIF(@d6, ''), " +
                    "desc7 = NULLIF(@d7, '');");
            PreparedStatement loadAttractionsData = CON.prepareStatement("LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/mdcp_attractions.csv' IGNORE INTO TABLE mdcp.attractions " +
                    "FIELDS TERMINATED BY ',' " +
                    "LINES TERMINATED BY '\\r\\n' " +
                    "IGNORE 1 LINES " +
                    "(id, location_name, website_link, type, city, county_id, descriptions_id)");
            loadCountiesData.execute();
            loadDescriptionsData.execute();
            loadAttractionsData.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected static ResultSet getAttractionsRS() {
        try {
            PreparedStatement ps = CON.prepareStatement("SELECT * FROM attractions;");
            return ps.executeQuery();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    protected static ResultSet getCountiesRS() {
        try {
            PreparedStatement ps = CON.prepareStatement("SELECT * FROM counties;");
            return ps.executeQuery();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    protected static ResultSet getDescriptionsRS() {
        try {
            PreparedStatement ps = CON.prepareStatement("SELECT * FROM descriptions;");
            return ps.executeQuery();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
