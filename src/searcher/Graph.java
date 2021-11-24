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
            if (o == this) {
                return true;
            }
            if (o instanceof Node) {
                Node toCompare = (Node) o;
                return toCompare.dest.equals(this.dest) && toCompare.weight == this.weight;
            }
            return false;
        }

        /*@Override
        // override hashcode because overriding equals
       /* public int hashCode() {
            int prime = 31;
            //hash =
        }*/
    }

    // Adjacency list of relationships between attributes and attributes/attractions
    HashMap<String, LinkedList<Node>> relationships;
    // The sums of the shortest distances from all search attributes to each attraction
    HashMap<String, Integer> attDistances;
    // Map of every attraction to their website links
    HashMap<String, String> attractionsAndLinks;
    // Set of all already searched attributes to ensure that duplicate searches are not weighted differently
    HashSet<String> searched;


    // Weighted undirected adjacency list (attRelationships) representing relationships between attributes and attributes as well as between attributes and attractions
    public Graph() {
        relationships = new HashMap<>();
        attDistances = new HashMap<>();
        attractionsAndLinks = new HashMap<>();
        searched = new HashSet<>();
        buildGraph();
    }

    // connects SOURCE and DEST together 
    private void addEdge(String source, String dest, int weight) {
        if (!relationships.containsKey(source)) {
            relationships.put(source, new LinkedList<>());
        }
        if (!relationships.containsKey(dest)) {
            relationships.put(dest, new LinkedList<>());
        }

        relationships.get(source).add(new Node(dest, weight));
        relationships.get(dest).add(new Node(source, weight));
    }

    // TODO: describe adding process for each table
    private void buildGraph() {
        try {
            ResultSet attractionsRS = Database.getAttractionsRS();
            ResultSet countiesRS = Database.getCountiesRS();
            ResultSet descriptionsRS = Database.getDescriptionsRS();
            assert attractionsAndLinks != null;
            assert countiesRS != null;
            assert descriptionsRS != null;
            ResultSetMetaData countiesMD = countiesRS.getMetaData();
            ResultSetMetaData descriptionsMD = descriptionsRS.getMetaData();

            // add edges from the broadest attributes to the closest related attributes to attractions to ensure every attraction node is marked as an attraction
            while (attractionsRS.next()) {
                countiesRS.absolute(attractionsRS.getInt("county_id"));
                descriptionsRS.absolute(attractionsRS.getInt("descriptions_id"));

                this.addEdge(attractionsRS.getString("location_name"), attractionsRS.getString("type"), 1);
                this.addEdge(attractionsRS.getString("location_name"), attractionsRS.getString("city"), 1);
                this.addEdge(attractionsRS.getString("location_name"), countiesRS.getString("county"), 1);
                this.attractionsAndLinks.put(attractionsRS.getString("location_name"), attractionsRS.getString("website_link"));

                for (int i = 1; i <= descriptionsMD.getColumnCount() - 1; i++) {
                    if (descriptionsRS.getString("desc" + i) != null) {
                        this.addEdge(attractionsRS.getString("location_name"), descriptionsRS.getString("desc" + i), 1);
                    }
                }

                for (int i = 1; i <= countiesMD.getColumnCount() - 2; i++) {
                    if (countiesRS.getString("nc" + i) != null) {
                        this.addEdge(countiesRS.getString("county"), countiesRS.getString("nc" + i), 1);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        for (String s : attractionsAndLinks.keySet()) {
            attDistances.put(s, 0);
        }
    }

    PriorityQueue<Node> pq;
    Set<Node> marked;
    HashMap<String, Integer> sourceDistances;
    // Runs Dijkstra's algorithm from source, updating attDistances accordingly
    protected void dijkstra(String source) {
        // equalsIgnoreCase will be helpful yw
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
                    // TODO need to figure out where and how to put this in (add to last search terms dists but how)
                }
                pq.add(tempVisitNode);
            }
        }

        marked.add(currentVisitNode);

    }

    // prints every attraction/attribute that every attraction/attribute is connected to
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

    protected boolean validSearch(String resp) { return relationships.containsKey(resp) && resp.length() != 0 && !searched.contains(resp); }

    // add all lowest distance Strings from attDistances to a hashSet and do below for each

    // note... print link by first retrieving ResultSet of just the row from attractions table with query PreparedStatement
    // then just System.out.println(RS.getString("website_link")); but probably a bit more complicated... lol
    protected void printOutput() {
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
