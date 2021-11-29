package searcher;

import java.util.*;
import java.sql.*;

public class Graph {
    // Nodes within Graph to store names of attractions and attributes
    private static class Node implements Comparable<Node> {
        private String dest;
        private int weight;

        public Node(String dest, int weight) {
            this.dest = dest;
            this.weight = weight;
        }


        // override compareTo so that nodes in PriorityQueue can be sorted in dijkstra
        @Override
        public int compareTo(Node n) {
            if (this.weight > n.weight) {
                return 1;
            } else if (this.weight < n.weight) {
                return -1;
            }
            return 0;
        }

        // override equals so that it is possible to check if marked set contains a node in dijkstra
        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof Node) {
                Node toCompare = (Node) o;
                return toCompare.dest.equals(this.dest) && toCompare.weight == this.weight;
            }
            return false;
        }

        // override hashCode because equals was overridden
        @Override
        public int hashCode() {
            return this.dest.hashCode() * this.weight * 31;
        }
    }

    HashMap<String, LinkedList<Node>> relationships;  // Adjacency list of relationships between attributes and attributes/attractions
    HashMap<String, Integer> attDistances;            // The sums of the shortest distances from all search attributes to each attraction, used in determining outputs
    HashMap<String, String> attractionsAndLinks;      // Map of every attraction to their website links, used in outputting links
    HashSet<String> searched;                         // Set of all already searched attributes to ensure that duplicate searches are not weighted extra

    /**
     * Constructs a weighted undirected adjacency list representing relationships between attributes and other attributes as well as between attributes and attractions.
     * Connects Nodes as specified in buildGraph();
     */
    public Graph() {
        relationships = new HashMap<>();
        attDistances = new HashMap<>();
        attractionsAndLinks = new HashMap<>();
        searched = new HashSet<>();
        buildGraph();
    }

    /**
     * Connects two attributes/attractions by adding them to the adjacency list, indicating a relationship.
     *
     * @param source - the name of the attribute/attraction to be connected.
     * @param dest - the name of the second attribute/attraction to be connected.
     * @param weight - the weighting of the edge representing the degree of relation, set to 1 by default.
     */
    private void addEdge(String source, String dest, int weight) {
        if (!relationships.containsKey(source)) {
            relationships.put(source, new LinkedList<>());  // initializes a new LinkedList if one has not already been created for the key
        }
        if (!relationships.containsKey(dest)) {
            relationships.put(dest, new LinkedList<>());
        }

        relationships.get(source).add(new Node(dest, weight));
        relationships.get(dest).add(new Node(source, weight));
    }

    /**
     * Builds and connects the Nodes within the graph based on the database tables.
     */
    private void buildGraph() {
        try {
            ResultSet attractionsRS = Database.getAttractionsRS();
            ResultSet countiesRS = Database.getCountiesRS();
            ResultSet descriptionsRS = Database.getDescriptionsRS();
            assert attractionsRS != null;
            assert countiesRS != null;
            assert descriptionsRS != null;
            ResultSetMetaData countiesMD = countiesRS.getMetaData();
            ResultSetMetaData descriptionsMD = descriptionsRS.getMetaData();

            while (attractionsRS.next()) {
                // moves the cursors of the ResultSets to the row corresponding to the current location
                countiesRS.absolute(attractionsRS.getInt("county_id"));
                descriptionsRS.absolute(attractionsRS.getInt("descriptions_id"));

                // connects the name of type of attraction, city, and county to the attraction that the cursor currently points at
                this.addEdge(attractionsRS.getString("location_name"), attractionsRS.getString("type"), 1);
                this.addEdge(attractionsRS.getString("location_name"), attractionsRS.getString("city"), 1);
                this.addEdge(attractionsRS.getString("location_name"), countiesRS.getString("county"), 1);

                // adds the maps the attraction name to its link
                this.attractionsAndLinks.put(attractionsRS.getString("location_name"), attractionsRS.getString("website_link"));

                // connects every description with the corresponding attraction
                for (int i = 1; i <= descriptionsMD.getColumnCount() - 1; i++) {
                    if (descriptionsRS.getString("desc" + i) != null) {
                        this.addEdge(attractionsRS.getString("location_name"), descriptionsRS.getString("desc" + i), 1);
                    }
                }
                // connects every nearby county to the county that the attraction is located in
                for (int i = 1; i <= countiesMD.getColumnCount() - 2; i++) {
                    if (countiesRS.getString("nc" + i) != null) {
                        this.addEdge(countiesRS.getString("county"), countiesRS.getString("nc" + i), 1);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // puts the name of every attraction into attDistances as a key, with the initial value set to 0 indicating the absence of search attributes
        for (String s : attractionsAndLinks.keySet()) {
            attDistances.put(s, 0);
        }
    }

    PriorityQueue<Node> pq;
    Set<Node> marked;
    HashMap<String, Integer> sourceDistances;

    /**
     * Standard implementation of Dijkstra's algorithm with a PriorityQueue to determine the length of the shortest paths from the search attribute to each attraction.
     * @param source the attribute which is being searched for
     */
    void dijkstra(String source) {
        pq = new PriorityQueue<>();
        marked = new HashSet<>();
        sourceDistances = new HashMap<>();
        searched.add(source);

        for (String s : relationships.keySet()) {
            sourceDistances.put(s, Integer.MAX_VALUE);
        }
        sourceDistances.replace(source, 0);

        pq.add(new Node(source, 0));
        while (!pq.isEmpty()) {
            Node r = pq.poll();
            relax(r);
        }

        for (Map.Entry<String, Integer> entry : sourceDistances.entrySet()) {
            if (attractionsAndLinks.containsKey(entry.getKey())) {
                attDistances.replace(entry.getKey(), attDistances.get(entry.getKey()) + entry.getValue());
            }
        }
    }

    private void relax(Node currentVisitNode) {
        for (Node tempVisitNode : relationships.get(currentVisitNode.dest)) {
            if (!marked.contains(tempVisitNode)) {
                int initial;
                int potential;

                initial = sourceDistances.get(tempVisitNode.dest);
                potential = sourceDistances.get(currentVisitNode.dest) + tempVisitNode.weight;

                if (potential < initial) {
                    sourceDistances.replace(tempVisitNode.dest, potential);
                }
                pq.add(tempVisitNode);
            }
        }
        marked.add(currentVisitNode);
    }

    /**
     * Prints every attraction/attribute that every attraction/attribute is connected to.
     */
    void printGraph() {
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

    /**
     * Tests whether the string is empty, contained in the database, or has already been searched for.
     * Failure for any of these cases returns false.
     * @param resp - the attribute being searched for.
     * @return the validity of the search attribute, determined by whether the string is empty, contained in the database, or has already been searched for.
     */
    boolean validSearch(String resp) { return relationships.containsKey(resp) && resp.length() != 0 && !searched.contains(resp); }

    /*
     * Prints the name and website link of the attraction related closest to all previously searched attributes.
     * In the event of ties, every tied attribute is printed.
     */
    void printOutput() {
        int minDist = Collections.min(attDistances.values());

        for (Map.Entry<String, Integer> entry : attDistances.entrySet()) {
            if (entry.getValue() == minDist) {
                String output = entry.getKey();
                try {
                    String link = attractionsAndLinks.get(output);
                    System.out.println(output);
                    System.out.println("    " + link);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
