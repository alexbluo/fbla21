package searcher;

import java.sql.*;

public class Database {
    private static final Connection CON = getConnection();

    // gets and returns a connection to the server with the specified URL, USERNAME, and PASSWORD
    private static Connection getConnection() {
        final String URL = "jdbc:mysql://127.0.0.1:3306/mdcp";
        final String USERNAME = "luo";
        final String PASSWORD = "luoMySQL123";
        Connection c;
        try {
            System.out.println("Connecting database...");
            c = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Database connected!");
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }
        return c;
    }

    // creates all tables and loads in data from csv files of three different google sheets:
    // TODO: update doc
    // 'attractions' contains names of locations as well as links to their websites
    // 'attributes' contains the city (or whatever is specified on the address) that the location is in,
    //     the type of attraction, one key description which is used as the foreign key of 'related_descriptions,'
    //     and two supporting descriptions
    // 'related_locations' contains the county
    // all tables except related_descriptions have a foreign key 'id' which refers to primary key 'id' of 'attractions'
    // most locations in 'attractions' from https://www.busytourist.com/fun-things-to-do-in-maryland/
    // nearby counties based on https://msa.maryland.gov/msa/mdmanual/36loc/html/02maps/seatb.html
    // TODO: revise and edit (ex: ON UPDATE/DELETE CASCADE necessary?)
    // TODO: maybe add indexing later?
    protected static void buildTables() {
        try {
            // https://stackoverflow.com/questions/3271249/difference-between-statement-and-preparedstatement
            // https://dev.mysql.com/doc/refman/8.0/en/mysql-indexes.html

            PreparedStatement createCountiesTable = CON.prepareStatement("CREATE TABLE IF NOT EXISTS nearby_counties (" +
                    "id int NOT NULL AUTO_INCREMENT, " +
                    "county varchar(32), " +
                    "nc1 varchar(32), " +
                    "nc2 varchar(32), " +
                    "nc3 varchar(32), " +
                    "PRIMARY KEY (id)) " +
                    "ENGINE=INNODB;");
            PreparedStatement createDescriptionsTable = CON.prepareStatement("CREATE TABLE IF NOT EXISTS descriptions (" +
                    "id int NOT NULL AUTO_INCREMENT, " +
                    "desc1 varchar(32), " +
                    "desc2 varchar(32), " +
                    "desc3 varchar(32), " +
                    "desc4 varchar(32), " +
                    "desc5 varchar(32), " +
                    "desc6 varchar(32), " +
                    "desc7 varchar(32), " +
                    "PRIMARY KEY (id))");
            PreparedStatement createAttractionsTable = CON.prepareStatement("CREATE TABLE IF NOT EXISTS attractions (" +
                    "id int NOT NULL AUTO_INCREMENT, " +
                    "location_name varchar(255) NOT NULL, " +
                    "website_link varchar(255) NOT NULL, " +
                    "type varchar(64) NOT NULL, " +
                    "city varchar(64) NOT NULL, " +
                    "county_id int NOT NULL, " +
                    "descriptions_id int NOT NULL, " +
                    "PRIMARY KEY (id), " +
                    "FOREIGN KEY (county_id) " +
                        "REFERENCES nearby_counties (id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (descriptions_id) " +
                        "REFERENCES descriptions (id) ON DELETE CASCADE) " +
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
    // TODO huh
    public static void loadData() {
        try {
            PreparedStatement loadAttractionsData = CON.prepareStatement("LOAD DATA INFILE \"C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/mdcp_attractions.csv\" IGNORE INTO TABLE mdcp.attractions " +
                    "FIELDS TERMINATED BY ',' ENCLOSED BY '\"\"' " +
                    "LINES TERMINATED BY '\\r\\n' " +
                    "IGNORE 1 LINES " +
                    "(id, location_name, website_link, type, city, county_id, descriptions_id)");
            loadAttractionsData.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
