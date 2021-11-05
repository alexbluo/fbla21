package searcher;

import java.util.*;
import java.io.*;

public class Runner {
    public static void main(String[] args) throws IOException{
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
                    System.out.println("");
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
