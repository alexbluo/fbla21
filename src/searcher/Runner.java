package searcher;

import java.util.*;
import java.io.*;
import java.sql.*;

// https://stackoverflow.com/questions/2839321/connect-java-to-a-mysql-database

public class Runner {
    public static void main(String[] args) throws IOException {
        String url = "jdbc:mysql://localhost:3306/mdcp";
        String username = "luo";
        String password = "luoMySQL123";

        System.out.println("Connecting database...");

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Database connected!");
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }

        ArrayList<String> searchFor = new ArrayList<>();
        Graph graph = new Graph();
        graph.formGraph();

        // Prompt user for as many attributes as desired until they enter search, with optional help
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = null;
        String resp = "";

        while (!resp.equals("s")) {
            System.out.print("Please enter an attribute you would like to search for, 'h' for help menu or 's' to search: ");
            st = new StringTokenizer(br.readLine());
            resp = st.nextToken();
            System.out.println("");
            switch (resp) {
                case "h":
                    // TODO help menu??
                    System.out.println("help.");
                    break;
                case "s":
                    graph.dijkstra(searchFor);
                    // TODO: somehow store and stuff and black magic boom boom spit out attraction
                    break;
                default:
                    searchFor.add(resp);
                    break;
            }
        }
    }
}
