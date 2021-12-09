package searcher;

import java.util.Scanner;

public class Runner {
    public static void main(String[] args) {
        try {
            System.out.println("Database connecting...");
            Database.createTables();
            Database.loadData();
            System.out.println("Database connected!");
        } catch (Exception ex) {
            throw new IllegalStateException("Cannot connect to database", ex);
        }
        Graph graph = new Graph();
        Scanner sc = new Scanner(System.in);
        boolean dijkstraToggle = false;

        // Prompts user for as many attributes as desired until they enter '\e', with optional help menu by entering '\h'
        // Program results update dynamically, and are outputted after each valid input
        while (true) {
            System.out.print("Enter an attribute you would like to search for, '\\t' to toggle to " + (dijkstraToggle ? "Simple" : "Dijkstra's") + ", '\\h' for help menu, '\\r' to start a fresh search, or '\\e' to exit: ");
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
                            System.out.println("    Simple (default) will output attractions which are directly related to the most of the searched attributes");
                            System.out.println("    Dijkstra's will produce results determined by both direct and indirect relationships, and results tend to be narrowed down compared to Simple");
                            System.out.println("Both run in the background with every search, which guarantees consistency regardless of the mode");
                            System.out.println("- - - - - - - - - - - -");
                        } else if (resp.equals("\\g")) {
                            System.out.println("For the best results ensure that spelling is accurate, provide a city and county (including the word 'county' or 'city' for Baltimore City), and use plural nouns where appropriate");
                            System.out.println("Capitalization and leading or trailing spaces do not matter");
                            System.out.println("Example: ");
                            System.out.println("         Baltimore");
                            System.out.println("         Sea Creatures");
                            System.out.println("         Baltimore City");
                            System.out.println("         Animals");
                            System.out.println("         Fish");
                            System.out.println("- - - - - - - - - - - -");
                        } else {
                            if (!resp.equals("\\c")) {
                                System.out.println("Not a command");
                            }
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
                case "\\r":
                    System.out.println("Restarted");
                    System.out.println("- - - - - - - - - - - -");
                    graph.reset();
                    dijkstraToggle = false;
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
                        graph.simpleSearch(resp);
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
     * @return the capitalized word(s)
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