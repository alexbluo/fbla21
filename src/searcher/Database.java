package searcher;

import java.sql.*;

final class Database {
    private static final Connection CON = getConnection();

    /**
     * Establishes a connection to the server with the specified URL, USERNAME, and PASSWORD.
     * @return a connection to the server with the specified URL, USERNAME, and PASSWORD.
     */
    private static Connection getConnection() {
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
     * Creates three tables with data to be loaded in from three different google sheets:
     *     'attractions' contains names of locations, links to their websites, their city, and foreign keys 'county_id' and 'descriptions_id' which refer to 'id'
     *          in the 'counties' and 'descriptions' table respectively
     *     'counties' contains the county that the attraction in located in, along with up to three nearby counties.
     *          For simplicity, only counties originally used for every attraction are used in the nearby counties
     *     'descriptions' contains up to seven descriptions of their respective attraction
     * Most locations in 'attractions' are taken from https://www.busytourist.com/fun-things-to-do-in-maryland/
     */
    static void createTables() {
        try {
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

            createCountiesTable.executeUpdate();
            createDescriptionsTable.executeUpdate();
            createAttractionsTable.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Loads locally downloaded csv files of aforementioned google sheets into tables.
     * Multiple runs of the program will not load in data multiple times.
     * Empty cells are loaded in as null.
     */
    static void loadData() {
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

    /**
     * Queries the database for every field in 'attractions'.
     * @return a ResultSet of every attraction.
     */
    static ResultSet getAttractionsRS() {
        try {
            PreparedStatement ps = CON.prepareStatement("SELECT * FROM attractions;");
            return ps.executeQuery();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Queries the database for every field in 'counties'.
     * @return a ResultSet of every county and up to three nearby counties.
     */
    static ResultSet getCountiesRS() {
        try {
            PreparedStatement ps = CON.prepareStatement("SELECT * FROM counties;");
            return ps.executeQuery();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Queries the database for every field in 'descriptions'.
     * @return a ResultSet of the descriptions.
     */
    static ResultSet getDescriptionsRS() {
        try {
            PreparedStatement ps = CON.prepareStatement("SELECT * FROM descriptions;");
            return ps.executeQuery();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}