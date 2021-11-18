package searcher;

import java.util.*;
import searcher.Database;

public class Graph {
    // Nodes within Graph to store attractions and attributes
    // TODO: might need to store predecessor? not implemented for now
    private class Node {
        String dest;
        int weight;
        boolean isAttraction = false;

        public Node(String dest, int weight, boolean isAttraction) {
            this.dest = dest;
            this.weight = weight;
            this.isAttraction = isAttraction;
        }

        // TODO: test if this infinite loops from calling equals inside
        @Override
        public boolean equals(Object o) {
            if (o instanceof Node) {
                Node toCompare = (Node) o;
                return toCompare.dest.equals(this.dest) && toCompare.weight == this.weight;
            }
            return false;
        }
    }

    // Total number of nodes in graph object
    private int size;
    // Adjacency list of relationships between attributes and attributes/attractions
    HashMap<String, LinkedList<Node>> relationships;
    // The sum of the shortest distances from all search attributes to each attraction
    // https://stackoverflow.com/questions/2776176/get-minvalue-of-a-mapkey-double use this to find output
    HashMap<String, Integer> attDistances = new HashMap<>();


    // Weighted undirected adjacency list (attRelationships) representing relationships between attributes and attributes as well as between attributes and attractions
    public Graph() {
        relationships = new HashMap<>();
        size = 0;

        // establishes and initializes Connection con

    }

    // Points attribute source to attribute/attraction dest
    private void addEdge(String source, String dest, int weight, boolean isAttraction) {
        // TODO: prob just set all weights to 1 by default and allow user to change with output report (zz)

        // TODO handle situations where LinkedList is not created yet -
        //  containsKey() else put(key, new LinkedList<Node>) and add to LL
        //  if containsKey() then check if .get.contains() to check duplicates values for key
        if (relationships.containsKey(source)) {
            relationships.get(source).add(new Node(dest, weight, isAttraction));
        } else {
            relationships.put(source, new LinkedList<>());
        }

        if (relationships.containsKey(dest)) {
            relationships.get(dest).add(new Node(dest, weight, isAttraction));
        } else {
            relationships.put(dest, new LinkedList<>());
        }
        size++;
    }

    // TODO: describe adding process for each table
    protected void buildGraph() {
        try {
            while (Database.getAttractionsRS().next()) {

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // TODO: loop query and add? also if (!=null) for variable num of columns like in related words table if that is even made
        // relate broad tables for city and type, just make a whole separate table for desc and pt each word and their related words in a new column

    }

    // Runs Dijkstra's algorithm from source, updating attDistances accordingly
    protected void dijkstra(String source) {
        // equalsIgnoreCase will be helpful yw




        // TODO: update attDistances within this method
    }

    // TODO: USE PRINTGRAPH() FROM PREVIOUS PROJ TO CHECK GRAPH AFTER DOING OTHER STUFF FIRST AND BEFORE MAKING DIJKSTRA
    public void printGraph() {
        for (Map.Entry<String, Object> entry : relationships) {
            if (relationships.get(i).isEmpty()) {
                System.out.print("Vertex " + i + " is connected to nothing");
            } else {
                System.out.print("Vertex " + i + " is connected to: ");
            }
            for (int j = 0; j < relationships.get(i).size(); j++) {
                if (j == relationships.get(i).size() - 1) {
                    System.out.print(relationships.get(i).get(j).dest + " with distance " + relationships.get(i).get(j).weight);
                } else {
                    System.out.print(relationships.get(i).get(j).dest + " with distance " + relationships.get(i).get(j).weight + ", ");
                }
            }
            System.out.println();
        }
    }


    // TODO: add get output or some similar method to get and print... outputs...
}
