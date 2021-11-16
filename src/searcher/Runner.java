package searcher;

// TODO: at very end remove wildcard imports on all classes
import java.util.*;
import java.io.*;
import searcher.Database;


// wtf is hot backup
// wtf are output reports

public class Runner {
    public static void main(String[] args) throws IOException {

        ArrayList<String> searchFor = new ArrayList<>();
        Graph graph = new Graph();
        graph.buildGraph();
        Database.buildDatabase();


        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = null;
        String resp = "";
        // Prompts user for as many attributes as desired until they enter search, with optional help
        while (!resp.equals("\\s") && !resp.equals("\\o")) {
            System.out.print("Please enter an attribute you would like to search for, 'h' for help menu, o to view and edit the output report, or 's' to begin search: ");
            st = new StringTokenizer(br.readLine());
            resp = st.nextToken();
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
                case "\\s":
                    graph.dijkstra(searchFor);
                    // TODO: reset attDistances in Graph

                    break;
                case "\\o":
                    // TODO: print semi interactive table of all the stuff? but what even is an output report first
                    // https://www.youtube.com/watch?v=r8Qiz9Bn1Ag maybe for later, gauge time after doing everything else
                    // TODO: make scuffed command line interface(?) to allow user to edit graph



                    System.out.println("");
                    break;
                default:
                    searchFor.add(resp);
                    break;
            }
        }
    }
}
