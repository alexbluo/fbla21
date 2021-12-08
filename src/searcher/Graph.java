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

    HashMap<String, LinkedList<Node>> relationships;    // Adjacency list of relationships between attributes and attributes/attractions
    HashMap<String, Integer> simpleSumPoints;           // An accumulation of the number of searched attributes directly connected to each attraction
    HashMap<String, Integer> dijkstraSumDistances;      // The sums of the shortest distances from all search attributes to each attraction
    HashMap<String, String> attractionsAndLinks;        // Map of every attraction to their website links, used in outputting links
    HashSet<String> searched;                           // Set of all already searched attributes to ensure that duplicate searches are not weighted extra

    /**
     * Constructs a weighted undirected graph represented by an adjacency list of relationships between attributes and other attributes as well as between attributes and attractions.
     * Connects Nodes as specified in buildGraph().
     */
    public Graph() {
        relationships = new HashMap<>();
        simpleSumPoints = new HashMap<>();
        dijkstraSumDistances = new HashMap<>();
        attractionsAndLinks = new HashMap<>();
        searched = new HashSet<>();
        buildGraph();
        // puts the name of every attraction into searchSumDistances Maps as a key, with the initial value set to 0 indicating the absence of search attributes
        for (String s : attractionsAndLinks.keySet()) {
            simpleSumPoints.put(s, 0);
            dijkstraSumDistances.put(s, 0);
        }
    }

    /**
     * Connects two attributes/attractions by adding them to the adjacency list, indicating a relationship.
     * @param source - the name of the attribute/attraction to be connected.
     * @param dest - the name of the second attribute/attraction to be connected.
     * @param weight - the weighting of the edge representing the degree of relation, set to 1 by default.
     */
    private void addEdge(String source, String dest, int weight) {
        if (!relationships.containsKey(source)) {
            relationships.put(source, new LinkedList<>());  // initializes a new LinkedList if one has not already been created for the key
        }
        if (!relationships.containsKey(dest)) {
            relationships.put(dest, new LinkedList<>());    // initializes a new LinkedList if one has not already been created for the key
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
    }

    PriorityQueue<Node> pq;
    Set<Node> marked;
    HashMap<String, Integer> sourceDistances;

    /**
     * Standard implementation of Dijkstra's algorithm with a PriorityQueue to determine the length of the shortest paths from the search attribute to every attraction.
     * @param source - the attribute which is being searched for
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

        for (Map.Entry<String, Integer> entry : sourceDistances.entrySet()) {                                                   // iterate through every node and their distance from source
            if (attractionsAndLinks.containsKey(entry.getKey())) {                                                              // if the node is an attraction
                dijkstraSumDistances.replace(entry.getKey(), dijkstraSumDistances.get(entry.getKey()) + entry.getValue());  // update distances based on previous values
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
     * Finds all the attractions that the source attribute is directly connected to and updates simpleSumPoints accordingly.
     * @param source - the attribute which is being searched for
     */
    void simpleSearch(String source) {
        for (Node n : relationships.get(source)) {             // iterate through every node that the source is directly connected to
            if (attractionsAndLinks.containsKey(n.dest)) {     // add a 'point' to the attraction if the node is an attraction, favoring those higher points as possible outputs
                String loc = n.dest;
                int prev = simpleSumPoints.get(loc);
                simpleSumPoints.put(loc, prev + 1);
            }
        }
        if (attractionsAndLinks.containsKey(source)) {  // handle cases where the source is an attraction by adding a point to that attraction
            int prev = simpleSumPoints.get(source);
            simpleSumPoints.put(source, prev + 1);
        }
    }

    /**
     * Tests the validity of the attribute based on whether the string is empty, contained in the database, or has already been searched for.
     * Failure for any of these cases returns false.
     * @param resp - the attribute being searched for.
     * @return the true if the search attribute is valid, false if not
     */
    boolean validSearch(String resp) { return relationships.containsKey(resp) && resp.length() != 0 && !searched.contains(resp); }

    /**
     * Prints the name and website link of the attraction related closest to all previously searched attributes.
     * Prints different results based on the toggle passed in.
     * In the event of ties, every tied attribute is printed.
     * @param dijkstraToggle - a boolean set to true if the user is expecting the output of the Dijkstra's searching method, and false if the user is expecting output from the simple search
     */
    void printOutput(boolean dijkstraToggle) {
        int searchVal = dijkstraToggle ? Collections.min(dijkstraSumDistances.values()) : Collections.max(simpleSumPoints.values());  // Dijkstra's favors the lowest sum distances, while simple favors the highest sum points
        for (Map.Entry<String, Integer> entry : dijkstraToggle ? dijkstraSumDistances.entrySet() : simpleSumPoints.entrySet()) {
            if (entry.getValue() == searchVal) {
                String output = entry.getKey();
                String link = attractionsAndLinks.get(output);
                System.out.println(output);
                System.out.println("    " + link);
            }
        }
    }

    /**
     * Sets up the graph class as if the program were being rerun or a new graph were being instantiated, except without redundant steps
     */
    public void resetGraph() {
        simpleSumPoints.clear();
        dijkstraSumDistances.clear();
        searched.clear();
        for (String s : attractionsAndLinks.keySet()) {
            simpleSumPoints.put(s, 0);
            dijkstraSumDistances.put(s, 0);
        }
    }

    /**
     * Prints every attraction/attribute that every attraction/attribute is connected to.
     * For development purposes only
     */
    private void printGraph() {
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
}