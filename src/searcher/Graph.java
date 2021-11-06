package searcher;

import java.util.*;
import java.sql.*;

public class Graph {
    // Nodes within Graph to store attractions and attributes
    // TODO: might need to store predecessor? not implemented for now
    private class Node {
        String dest;
        int weight;
        public Node(String dest, int weight) {
            this.dest = dest;
            this.weight = weight;
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

    // total number of nodes in graph object
    int size;
    // Graph of relationships between attributes and attributes/attractions
    HashMap<String, LinkedList<Node>> attRelationships;
    // Stores the shortest distances from each attribute being searched for to each attraction
    // TODO: handle when new hashmap not created?
    HashMap<String, HashMap<String, Integer>> attDistances = new HashMap<>();


    // Weighted directed adjacency list (attRelationships) representing relationships between attributes and attributes as well as between attributes and attractions
    // A Node pointing to nothing is an attraction
    public Graph() {
        attRelationships = new HashMap<>();
        size = 0;
    }

    // Points attribute source to attribute/attraction dest
    protected void addEdge(String source, String dest, int weight) {
        attRelationships.get("test").add(new Node("t", 2));

        // TODO handle situations where LinkedList is not created yet

        size++;
    }

    // Manually connects related attributes/attractions with weights to represent degree of relation
    protected void formGraph() {
        // TODO: do tedious stuff... aka yelp + addEdge x 50+
    }

    // TODO FIGURE BELOW OUT LOL
    // Runs Dijkstra's algorithm on each attribute in searchFor, storing their distances to each attraction in attDistances
    protected void dijkstra(List<String> searchFor) {





        // TODO: update attDistances within this method
    }


}
