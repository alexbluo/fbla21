package searcher;

import java.util.*;
import java.io.*;
import java.sql.*;

// GOAL: make big pool of stuff to search for by including everything related to the attributes
// EXAMPLE: a sushi place might be connected to the description "sushi" which is connected to "food" so that searching for either can yield the same result
// PROBLEM: this makes it so that the number of columns within a table of all the words related to an attribute cannot be predetermined
// QUESTION: is there an alternative to my current method of stuffing everything in the same column and parse
// TEMP SOLUTION: JUST DON'T IMPLEMENT THIS AT ALL LMAO ITS SO TEDIOUS
// cannot find a separate dataset related to "sushi" for every single 200 or so attributes
// either implement by manually stuffing and parse or don't do at all for now or find better way but unlikely

// wtf is hot backup

// https://stackoverflow.com/questions/2839321/connect-java-to-a-mysql-database

public class Runner {
    public static void main(String[] args) throws IOException {

        ArrayList<String> searchFor = new ArrayList<>();
        Graph graph = new Graph();
        graph.formGraph();

        // Prompt user for as many attributes as desired until they enter search, with optional help
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = null;
        String resp = "";

        while (!resp.equals("\\s") && !resp.equals("\\o")) {
            System.out.print("Please enter an attribute you would like to search for, 'h' for help menu, o to view and edit the output report, or 's' to begin search: ");
            st = new StringTokenizer(br.readLine());
            resp = st.nextToken();
            System.out.println("");

            switch (resp) {
                case "\\h":
                    // TODO help menu??
                    System.out.println("help.");
                    System.out.println("");
                    break;
                case "\\s":
                    graph.dijkstra(searchFor);
                    // TODO: somehow store and stuff and black magic boom boom spit out attraction

                    break;
                case "\\o":
                    // TODO: print semi interactive table of all the stuff

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
