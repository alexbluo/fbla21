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
    // Graph of relationships between attributes and attributes/attractions
    HashMap<String, LinkedList<Node>> attRelationships;
    // Stores the shortest distances from each attribute being searched for to each attraction
    // TODO: handle creating inner hashMaps later in dijkstra method when temporarily storing
    HashMap<String, HashMap<String, Integer>> attDistances = new HashMap<>();


    // Weighted directed adjacency list (attRelationships) representing relationships between attributes and attributes as well as between attributes and attractions
    // A Node pointing to nothing is an attraction
    public Graph() {
        attRelationships = new HashMap<>();
        size = 0;

        // establishes and initializes Connection con

    }

    // Points attribute source to attribute/attraction dest
    private void addEdge(String source, String dest, int weight, boolean isAttraction) {
        // TODO: prob just set all weights to 1 by default and allow user to change with output report (zz)
        attRelationships.get(source).add(new Node(dest, weight, isAttraction));

        // TODO handle situations where LinkedList is not created yet

        size++;
    }

    // Manually connects related attributes/attractions with weights to represent degree of relation
    protected void buildGraph() {
        // TODO: do tedious stuff... aka yelp + google sheets + mysql X 50 hf lol

        // TODO: possible to actually write sql in here and wrap using smthn

    }

    // TODO FIGURE BELOW OUT LOL
    // Runs Dijkstra's algorithm on each attribute in searchFor, storing their distances to each attraction in attDistances
    protected void dijkstra(List<String> searchFor) {





        // TODO: update attDistances within this method
    }


}
