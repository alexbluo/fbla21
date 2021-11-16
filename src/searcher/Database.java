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
    // TODO: update
    // 'attractions' contains names of locations as well as links to their websites
    // 'attributes' contains the city (or whatever is specified on the address) that the location is in,
    //     the type of attraction, one key description which is used as the foreign key of 'related_descriptions,'
    //     and two supporting descriptions
    // 'related_locations' contains the county
    // all tables except related_descriptions have a foreign key 'id' which refers to primary key 'id' of 'attractions'
    // most locations in 'attractions' from https://www.busytourist.com/fun-things-to-do-in-maryland/
    // nearby counties based on https://msa.maryland.gov/msa/mdmanual/36loc/html/02maps/seatb.html
    protected static void buildDatabase() {
        try {
            // https://stackoverflow.com/questions/3271249/difference-between-statement-and-preparedstatement
            PreparedStatement createAttractionsTable = CON.prepareStatement("CREATE TABLE IF NOT EXISTS attractions (" +
                    "id int NOT NULL AUTO_INCREMENT, " +
                    "location_name varchar(255), " +
                    "website_link varchar(255), " +
                    "PRIMARY KEY(id))");
            PreparedStatement createAttributesTable = CON.prepareStatement("CREATE TABLE IF NOT EXISTS attributes (" +
                    "id int NOT NULL AUTO_INCREMENT, " +
                    "city varchar(64), " +
                    "type varchar(32), " +
                    "desc1 varchar(32), " +
                    "desc2 varchar(32), " +
                    "desc3 varchar(32), " +
                    "PRIMARY KEY (id), " +
                    "FOREIGN KEY (id) " +
                    "REFERENCES attractions (id) " +
                    "ON DELETE CASCADE)");
            // TODO: maybe create tables of words related to each attribute later and add to graph as well
            createAttractionsTable.executeUpdate();
            createAttributesTable.executeUpdate();
            /* either download excel file as csv and use below or look a little into alternative with xlsx
            OR use some kind of translator, which looks messy but allows non-local integration
            prob load csv like below because scaled integration isn't too important and for code readability
            --------------
            LOAD DATA LOCAL INFILE "/path/to/FILENAME.csv" INTO TABLE TABLENAME.DATABASENAME
            FIELDS TERMINATED BY ','
            LINES TERMINATED BY '\n'
            edit below
            IGNORE 1 LINES
            (id, name, type, owner_id, @datevar, rental_price)
             */
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
