package searcher;

import java.sql.*;
import java.util.*;

public class Graph {
    // Nodes within Graph to store attractions, indicated by isAttraction, and attributes
    // TODO: might need to store predecessor? not implemented for now
    private static class Node implements Comparable<Node> {
        private String dest;
        private int weight;
        private final boolean isAttraction;

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
            TODO put some hashing algo or something idk
        }*/
    }

    // TODO Total number of nodes in graph object EDIT: probably not needed at all and if updated like below its not even accurate lol
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
    }

    // connects SOURCE and DEST together
    // all attractions should be passed in as SOURCE
    private void addEdge(String source, String dest, int weight, boolean isAttraction) {
        // TODO: prob just set all weights to 1 by default and allow user to change with output report (zz)

        // TODO handle situations where LinkedList is not created yet -
        //  containsKey() else put(key, new LinkedList<Node>) and add to LL
        //  if containsKey() then check if .get.contains() to check duplicates values for key alex what were u even thinking about when u wrote this /nvm
        if (!relationships.containsKey(source)) {
            relationships.put(source, new LinkedList<>());
        }
        if (!relationships.containsKey(dest)) {
            relationships.put(dest, new LinkedList<>());
        }

        if (isAttraction) {
            relationships.get(dest).add(new Node(source, weight, true));
        } else {
            relationships.get(dest).add(new Node(source, weight, false));
        }
        relationships.get(source).add(new Node(dest, weight, false));
        // needed? size++;
    }

    // TODO: describe adding process for each table
    protected void buildGraph() {
        try {
            ResultSet attractions = Database.getAttractionsRS();
            ResultSet counties = Database.getCountiesRS();
            ResultSet descriptions = Database.getDescriptionsRS();
            assert counties != null;
            ResultSetMetaData countiesMD = counties.getMetaData();
            assert descriptions != null;
            ResultSetMetaData descMD = descriptions.getMetaData();
            assert attractions != null;

            // add edges from the broadest attributes to the closest related attributes to attractions to ensure every attraction node is marked as an attraction
            while (attractions.next()) {
                counties.absolute(attractions.getInt("county_id"));
                descriptions.absolute(attractions.getInt("descriptions_id"));

                this.addEdge(attractions.getString("location_name"), attractions.getString("type"), 1, true);
                this.addEdge(attractions.getString("location_name"), attractions.getString("city"), 1, true);
                this.addEdge(attractions.getString("location_name"), counties.getString("county"), 1, true);

                for (int i = 1; i <= descMD.getColumnCount() - 1; i++) {
                    if (descriptions.getString("desc" + i) != null) {
                        this.addEdge(attractions.getString("location_name"), descriptions.getString("desc" + i), 1, true);
                    }
                }

                for (int i = 1; i <= countiesMD.getColumnCount() - 2; i++) {
                    if (counties.getString("nc" + i) != null) {
                        this.addEdge(counties.getString("county"), counties.getString("nc" + i), 1, false);
                    }
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    PriorityQueue<Node> pq;
    Set<Node> marked;
    HashMap<String, Integer> sourceDists;
    // Runs Dijkstra's algorithm from source, updating attDistances accordingly
    protected void dijkstra(String source) {
        // equalsIgnoreCase will be helpful yw
        pq = new PriorityQueue<>();
        marked = new HashSet<>();
        sourceDists = new HashMap<>();

        for (String s : relationships.keySet()) {
            sourceDists.put(s, Integer.MAX_VALUE);
        }

        sourceDists.replace(source, 0);

        // maybe review this part?
        pq.add(new Node(source, 0, false));

        while (!pq.isEmpty()) {
            Node r = pq.poll();
            relax(r);
        }
    }

    private void relax(Node currentVisitNode) {
        for (Node relaxingNode : relationships.get(currentVisitNode.dest)) {

            if (!marked.contains(relaxingNode)) {
                int initial;
                int potential;

                initial = sourceDists.get(relaxingNode.dest);
                potential = sourceDists.get(currentVisitNode.dest) + relaxingNode.weight;

                if (potential < initial) {
                    // TODO need to somehow find center of ALL search attributes
                    sourceDists.replace(relaxingNode.dest, potential);
                    // TODO why is it ! instead of inverse??? lol
                    if (relaxingNode.isAttraction) {
                        attDistances.put(relaxingNode.dest, potential);
                    }
                }
                pq.add(relaxingNode);
            }
        }
        marked.add(currentVisitNode);
        // TODO: update attDistances within this method if node.isAttraction put() or something
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

    protected boolean validSearch(String resp) {
        return relationships.containsKey(resp);
    }

    // TODO: add get output or some similar method to get and print... outputs...
    // add all lowest distance Strings from attDistances to a hashSet and do below for each
    // note... print link by first retrieving ResultSet of just the row from attractions table with query PreparedStatement
    // then just System.out.println(RS.getString("website_link")); but probably a bit more complicated... lol
    protected void printOutput() {
        for (Map.Entry<String, Integer> entry : attDistances.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    public static void main(String[] args) {
        Graph g = new Graph();
        g.buildGraph();
        g.dijkstra("Food");
        g.printOutput();

    }
}
