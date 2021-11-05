package searcher;

import java.util.*;

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
    }

    HashMap<String, LinkedList<Node>> attRelationships;
    int size;

    // Weighted directed adjacency list (attRelationships) representing relationships between attributes and attributes as well as between attributes and attractions
    // A Node pointing to nothing is an attraction
    public Graph() {
        attRelationships = new HashMap<>();
        size = 0;
    }

    // Points attribute source to attribute/attraction dest
    protected void addEdge(String source, String dest, int weight) {
        attRelationships.size();

        size++;
    }

    // Manually connects related attributes/attractions with weights to represent degree of relation
    protected void formGraph() {

    }

    // TODO FIGURE BELOW OUT LOL
    // Runs Dijkatra's algorithm on each attribute, storing their distances to each attraction
    protected void dijkstra() {
        
    }
}
