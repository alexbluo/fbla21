package searcher;

import java.util.Scanner;

public class Runner {
    public static void main(String[] args) {
        try {
            System.out.println("Database connecting...");
            Database.buildTables();
            Database.loadData();
            System.out.println("Database connected!");
        } catch (Exception ex) {
            throw new IllegalStateException("Cannot connect to database", ex);
        }
        Graph graph = new Graph();

        Scanner sc = new Scanner(System.in);
        String resp = "";

        // Prompts user for as many attributes as desired until they exit, with optional help menu
        // Program results update dynamically, and are outputted after each valid input
        while (true) {
            System.out.print("Please enter an attribute you would like to search for, '\\h' for help menu, or '\\e' to exit: ");
            resp = sc.nextLine().trim();
            System.out.println("");

            switch (resp) {
                case "\\h":
                    System.out.println("For the most accurate results ensure that spelling is accurate, provide a city and county (including the word 'county' or 'city' for Baltimore City), and use plural nouns where appropriate");
                    System.out.println("Example: ");
                    System.out.println("        Baltimore");
                    System.out.println("        Sea Creatures");
                    System.out.println("        Baltimore City");
                    System.out.println("        Animals");
                    System.out.println("        Fish");
                    System.out.println("");
                    break;
                case "\\e":
                    System.exit(0);
                    break;
                default:
                    if (resp.length() != 0) {
                        resp = resp.substring(0, 1).toUpperCase() + resp.substring(1).toLowerCase();
                    }
                    if (graph.validSearch(resp)) {
                        graph.dijkstra(resp);
                        graph.printOutput();
                    } else {
                        System.out.println("Search attribute has either already been searched for or is not recognized by database");
                    }
                    System.out.println("");
                    break;
            }
        }
    }
}
