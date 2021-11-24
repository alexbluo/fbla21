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
            resp = sc.nextLine();
            System.out.println("");

            switch (resp) {
                case "\\h":
                    // TODO help menu??
                    System.out.println("For the most accurate results provide a city or county, use plural nouns where appropriate, and capitalize each word in multi-word terms");
                    System.out.println("Example: ");
                    System.out.println("        Baltimore");
                    System.out.println("        Sea Creatures");
                    System.out.println("        Education");
                    System.out.println("        Animals");
                    System.out.println("        Fish");

                    System.out.println("");
                    break;
                case "\\e":
                    System.exit(0);
                    break;
                default:
                    resp = resp.trim();
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
