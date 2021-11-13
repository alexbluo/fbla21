package searcher;

import java.sql.*;

// TODO: static or nonstatic methods?
public class Database {
    private static final Connection CON = getConnection();

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

    // TODO add doc after figuring out wtf im doing
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
