package searcher;

import java.util.*;
import java.io.*;
import java.sql.*;

// TODO for other computer download this sus jar thing and add to library from proj structure idk why just do it bc stackoverflow
// https://www.javaguides.net/2019/11/mysql-connector-java-maven-dependency.html
// https://mygeodata.cloud/data/download/osm/tourist-attractions/united-states-of-america--maryland??? what am i even doing
// https://www.visithowardcounty.com/ please send help
// https://dev-maryland.opendata.arcgis.com/apps/maryland::visit-maryland-interactive-map-tourism/explore why does this not even load
public class Runner {
    public static void main(String[] args) throws IOException {
        String url = "jdbc:mysql://localhost:3306/javabase";
        String username = "alexbluo";
        String password = "doGracie06";

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
