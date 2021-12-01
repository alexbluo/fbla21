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
        boolean dijkstraToggle = false;

        // Prompts user for as many attributes as desired until they exit, with optional help menu
        // Program results update dynamically, and are outputted after each valid input
        while (true) {
            System.out.print("Please enter an attribute you would like to search for, '\\t' to toggle " + (dijkstraToggle ? "Simple" : "Dijkstra's") + " as the search method, '\\h' for help menu, or '\\e' to exit: ");
            String resp = sc.nextLine().trim();
            System.out.println("");
            switch (resp) {
                case "\\h":
                    // help menu loop so user doesn't have to reenter '\h' to use both help commands
                    while (!resp.equals("\\c")) {
                        System.out.print("Enter '\\t' for an explanation of the \\t toggle, '\\g' for searching guidelines, or '\\c' to close the help menu at any time: ");
                        resp = sc.nextLine().trim();
                        System.out.println("");
                        if (resp.equals("\\t")) {
                            System.out.println("\\t toggles between two searching methods:");
                            System.out.println("    Simple (default) will output the attractions which simply have the most of the searched attributes");
                            System.out.println("    Dijkstra's will produce results abstracted by indirect relationships");
                            System.out.println("Both run in the background regardless of toggling, which guarantees consistency regardless of the mode");
                            System.out.println("- - - - - - - - - - - -");
                        } else if (resp.equals("\\g")) {
                            System.out.println("For the most accurate results ensure that spelling is accurate, provide a city and county (including the word 'county' or 'city' for Baltimore City), and use plural nouns where appropriate");
                            System.out.println("Capitalization and leading or trailing spaces do not matter");
                            System.out.println("Example: ");
                            System.out.println("         Baltimore");
                            System.out.println("         Sea Creatures");
                            System.out.println("         Baltimore City");
                            System.out.println("         Animals");
                            System.out.println("         Fish");
                            System.out.println("- - - - - - - - - - - -");
                        } else {
                            System.out.println("Not a command");
                            System.out.println("- - - - - - - - - - - -");
                        }
                    }
                    break;
                case "\\t":
                    dijkstraToggle = !dijkstraToggle;
                    System.out.println("Toggled to: " + (dijkstraToggle ? "Dijkstra's" : "Simple"));
                    graph.printOutput(dijkstraToggle);
                    System.out.println("- - - - - - - - - - - -");
                    break;
                case "\\e":
                    System.exit(0);
                    break;
                default:
                    if (resp.length() != 0) {
                        resp = capitalize(resp);
                    }
                    if (graph.validSearch(resp)) {
                        graph.dijkstra(resp);
                        graph.sparseSearch(resp);
                        graph.printOutput(dijkstraToggle);
                    } else {
                        System.out.println("Search attribute has either already been searched for or is not recognized by database");
                    }
                    System.out.println("- - - - - - - - - - - -");
                    break;
            }
        }
    }

    /**
     * Capitalizes the first letter of each word and makes everything else lowercase
     * @param word - the word(s) to be capitalized
     * @return the capitalized word
     */
    private static String capitalize(String word) {
        StringBuilder capitalizedWord = new StringBuilder();
        String[] words = word.split(" ");
        for (int i = 0; i < words.length; i++) {
            capitalizedWord.append(words[i].substring(0, 1).toUpperCase()).append(words[i].substring(1).toLowerCase());
            if (i != words.length - 1) {
                capitalizedWord.append(" ");
            }
        }
        return capitalizedWord.toString();
    }
}