package searcher;

import java.util.Scanner;

// TODO: SAVE FOR LATER - https://stackoverflow.com/questions/215497/what-is-the-difference-between-public-protected-package-private-and-private-in#:~:text=public%20%3A%20accessible%20from%20everywhere.,classes%20of%20the%20same%20package.
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
        graph.printGraph();
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
                        resp = capitalize(resp);
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
