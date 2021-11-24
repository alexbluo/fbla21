package searcher;

// TODO: at very end remove wildcard imports on all classes
import java.util.*;
import java.io.*;

// wtf is hot backup
// wtf are output reports

public class Runner {
    public static void main(String[] args) throws IOException {
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
        // Prompts user for as many attributes as desired until they enter search, with optional help
        // TODO update/figure out \\o???
        while (true) {
            System.out.print("Please enter an attribute you would like to search for, '\\h' for help menu, '\\o' to view and edit the output report, or '\\e' to exit: ");
            resp = sc.nextLine();
            System.out.println("");

            switch (resp) {
                case "\\h":
                    // TODO help menu??
                    // ex: for more accurate results, use plural nouns where appropriate
                    System.out.println("For the most accurate results provide a city or county, use plural nouns where appropriate, and capitalize each word in multi-word terms");
                    System.out.println("Example: ");
                    System.out.println("        Baltimore");
                    System.out.println("        Sea Creatures");
                    System.out.println("        Education");
                    System.out.println("        Animals");
                    System.out.println("        Fish");

                    System.out.println("");
                    break;
                case "\\o":
                    // TODO: print semi interactive table of all the stuff? but what even is an output report first
                    // https://www.youtube.com/watch?v=r8Qiz9Bn1Ag maybe for later, gauge time after doing everything else
                    // TODO: make scuffed command line interface(?) to allow user to edit graph



                    System.out.println("");
                    break;
                case "\\e":
                    System.exit(0);
                    break;
                default:
                    if (resp.trim().length() != 0) {
                        resp = (resp.substring(0, 1).toUpperCase() + resp.substring(1).toLowerCase()).trim();
                    }
                    if (graph.validSearch(resp)) {
                        graph.dijkstra(resp);
                        graph.printOutput();
                    } else {
                        System.out.println("Not a valid search attribute, please enter something else");
                    }
                    System.out.println("");
                    break;
            }
        }
    }
}
