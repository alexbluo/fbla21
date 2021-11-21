package searcher;

import java.sql.*;
import java.util.*;

public class Graph {
    // Nodes within Graph to store attractions, indicated by isAttraction, and attributes
    // TODO: might need to store predecessor? not implemented for now
    private static class Node implements Comparable<Node> {
        String dest;
        int weight;
        boolean isAttraction = false;

        public Node(String dest, int weight, boolean isAttraction) {
            this.dest = dest;
            this.weight = weight;
            this.isAttraction = isAttraction;
        }

        @Override
        // override compareTo so that nodes in priorityQueue can be sorted in dijkstra
        public int compareTo(Node n) {
            if (this.weight > n.weight) {
                return 1;
            } else if (this.weight < n.weight) {
                return -1;
            }
            return 0;
        }

        @Override
        // override equals so that it is possible to check if marked set contains a node in dijkstra
        public boolean equals(Object o) {
            if (o instanceof Node) {
                Node toCompare = (Node) o;
                return toCompare.dest.equals(this.dest) && toCompare.weight == this.weight && toCompare.isAttraction == this.isAttraction;
            }
            return false;
        }

        /*@Override
        // override hashcode because overriding equals
        public int hashCode() {
            TODO put some hashing algo or something
        }*/
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
        //  if containsKey() then check if .get.contains() to check duplicates values for key alex what were u even thinking about when u wrote this
        if (!relationships.containsKey(source)) {
            relationships.put(source, new LinkedList<>());
        }
        relationships.get(source).add(new Node(dest, weight, isAttraction));

        if (!relationships.containsKey(dest)) {
            relationships.put(dest, new LinkedList<>());
        }
        relationships.get(dest).add(new Node(source, weight, isAttraction));
        size++;
    }

    // TODO: describe adding process for each table
    protected void buildGraph() {
        try {
            ResultSet attractions = Database.getAttractionsRS();
            ResultSet counties = Database.getCountiesRS();
            ResultSet descriptions = Database.getDescriptionsRS();
            while (attractions.next()) {
                PreparedStatement getCountiesRow =
                // do stuff... remember if not null also why is this infinite looping lmao
                break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Runs Dijkstra's algorithm from source, updating attDistances accordingly
    protected void dijkstra(String source) {
        // equalsIgnoreCase will be helpful yw




        // TODO: update attDistances within this method
    }

    // TODO use to check after buildGraph and also add doc bc im too lazy to rn
    public void printGraph() {
        for (Map.Entry<String, LinkedList<Node>> entry : relationships.entrySet()) {
            if (entry.getValue().isEmpty()) {
                System.out.print(entry.getKey() + " is connected to nothing");
            } else {
                System.out.print(entry.getKey() + " is connected to: ");
            }
            for (int j = 0; j < entry.getValue().size(); j++) {
                if (j == entry.getValue().size() - 1) {
                    System.out.print(entry.getValue().get(j).dest + " with distance " + entry.getValue().get(j).weight);
                } else {
                    System.out.print(entry.getValue().get(j).dest + " with distance " + entry.getValue().get(j).weight + ", ");
                }
            }
            System.out.println();
        }
    }


    // TODO: add get output or some similar method to get and print... outputs...

    public static void main(String[] args) {
        Graph g = new Graph();
        g.buildGraph();

        g.printGraph();

    }
}
