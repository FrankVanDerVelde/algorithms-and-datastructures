package maze_escape;

import java.sql.Array;
import java.util.*;
import java.util.function.BiFunction;

public abstract class AbstractGraph<V> {

    /**
     * Graph representation:
     * this class implements graph search algorithms on a graph with abstract vertex type V
     * for every vertex in the graph, its neighbours can be found by use of abstract method getNeighbours(fromVertex)
     * this abstraction can be used for both directed and undirected graphs
     **/

    public AbstractGraph() {
    }

    /**
     * retrieves all neighbours of the given fromVertex
     * if the graph is directed, the implementation of this method shall follow the outgoing edges of fromVertex
     *
     * @param fromVertex
     * @return
     */
    public abstract Set<V> getNeighbours(V fromVertex);

    /**
     * retrieves all vertices that can be reached directly or indirectly from the given firstVertex
     * if the graph is directed, only outgoing edges shall be traversed
     * firstVertex shall be included in the result as well
     * if the graph is connected, all vertices shall be found
     *
     * @param firstVertex the start vertex for the retrieval
     * @return
     */
    public Set<V> getAllVertices(V firstVertex) {
        // calculate recursively the set of all connected vertices that can be reached from the given start vertex
        //  hint: reuse getNeighbours()
        Set<V> vertices = new HashSet<>();
        return getAllVerticesRecursively(firstVertex, vertices);
    }

    private Set<V> getAllVerticesRecursively(V vertex, Set<V> visited) {
        visited.add(vertex);
        for (V neighbour : getNeighbours(vertex)) {
            if (!visited.contains(neighbour)) {
                getAllVerticesRecursively(neighbour, visited);
            }
        }
        return visited;
    }

    /**
     * Formats the adjacency list of the subgraph starting at the given firstVertex
     * according to the format:
     * vertex1: [neighbour11,neighbour12,…]
     * vertex2: [neighbour21,neighbour22,…]
     * …
     * Uses a pre-order traversal of a spanning tree of the sub-graph starting with firstVertex as the root
     * if the graph is directed, only outgoing edges shall be traversed
     * , and using the getNeighbours() method to retrieve the roots of the child subtrees.
     *
     * @param firstVertex
     * @return
     */
    public String formatAdjacencyList(V firstVertex) {
        StringBuilder stringBuilder = new StringBuilder("Graph adjacency list:\n");

        // recursively build the adjacency list including all vertices that can be reached from firstVertex
        //  following a recursive pre-order traversal of a spanning tree
        //  using the above stringBuilder to format the list
        //  hint: use the getNeighbours() method to retrieve the roots of the child subtrees.

        // return the result
        formatAdjacencyListRecursively(firstVertex, stringBuilder, new HashSet<>());
        System.out.println(stringBuilder.toString());
        return stringBuilder.toString();
    }

    private void formatAdjacencyListRecursively(V vertex, StringBuilder sb, Set<V> visited) {
        if(visited.contains(vertex)) return;
        visited.add(vertex);

        Set<V> neighbours = getNeighbours(vertex);
        sb.append(vertex).append(": [");

        final StringJoiner sj = new StringJoiner(",");
        for (V neighbour : neighbours) {
            sj.add(neighbour.toString());
        }

        sb.append(sj).append("]\n");

        for (V neighbour : neighbours) {
            formatAdjacencyListRecursively(neighbour, sb, visited);
        }
    }


    /**
     * represents a directed path of connected vertices in the graph
     */
    public class GPath {
        private Deque<V> vertices = new LinkedList<>();
        private double totalWeight = 0.0;
        private Set<V> visited = new HashSet<>();

        /**
         * representation invariants:
         * 1. vertices contains a sequence of vertices that are neighbours in the graph,
         * i.e. FOR ALL i: 1 < i < vertices.length: getNeighbours(vertices[i-1]).contains(vertices[i])
         * 2. a path with one vertex equal start and target vertex
         * 3. a path without vertices is empty, does not have a start nor a target
         * totalWeight is a helper attribute to capture total path length from a function on two neighbouring vertices
         * visited is a helper set to be able to track visited vertices in searches, only for analysis purposes
         **/
        private static final int DISPLAY_CUT = 10;

        public GPath() {
        }

        public GPath(Deque<V> shortestPath, double weightSumTo) {
            this.vertices = shortestPath;
            this.totalWeight = weightSumTo;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(
                    String.format("Weight=%.2f Length=%d visited=%d (",
                            this.totalWeight, this.vertices.size(), this.visited.size()));
            String separator = "";
            int count = 0;
            final int tailCut = this.vertices.size() - 1 - DISPLAY_CUT;
            for (V v : this.vertices) {
                // limit the length of the text representation for long paths.
                if (count < DISPLAY_CUT || count > tailCut) {
                    sb.append(separator + v.toString());
                    separator = ", ";
                } else if (count == DISPLAY_CUT) {
                    sb.append(separator + "...");
                }
                count++;
            }
            sb.append(")");
            return sb.toString();
        }

        /**
         * recalculates the total weight of the path from a given weightMapper that calculates the weight of
         * the path segment between two neighbouring vertices.
         *
         * @param weightMapper
         */
        public void reCalculateTotalWeight(BiFunction<V, V, Double> weightMapper) {
            this.totalWeight = 0.0;
            V previous = null;
            for (V v : this.vertices) {
                // the first vertex of the iterator has no predecessor and hence no weight contribution
                if (previous != null) this.totalWeight += weightMapper.apply(previous, v);
                previous = v;
            }
        }

        public Queue<V> getVertices() {
            return this.vertices;
        }

        public double getTotalWeight() {
            return this.totalWeight;
        }

        public Set<V> getVisited() {
            return this.visited;
        }
    }

    /**
     * Uses a depth-first search algorithm to find a path from the startVertex to targetVertex in the subgraph
     * All vertices that are being visited by the search should also be registered in path.visited
     *
     * @param startVertex
     * @param targetVertex
     * @return the path from startVertex to targetVertex
     * or null if target cannot be matched with a vertex in the sub-graph from startVertex
     */
    public GPath depthFirstSearch(V startVertex, V targetVertex) {
        if (startVertex == null || targetVertex == null) return null;

        return depthFirstSearch(startVertex, targetVertex, new GPath());
    }

    public GPath depthFirstSearch(V startVertex, V targetVertex, GPath path) {
        // calculate the path from start to target by recursive depth-first-search
        if (path.visited.contains(startVertex)) return null;
        path.visited.add(startVertex);

        // checking if the start vertex is already the target vertex, if so, return the path
        if (startVertex.equals(targetVertex)) {
            path.vertices.add(targetVertex);
            return path;
        }

        // getting the neighbours of the start vertex
        for (V neighbour : getNeighbours(startVertex)) {
            GPath path2 = depthFirstSearch(neighbour, targetVertex, path);
            if (path2 != null) {
                path2.vertices.addFirst(startVertex);
                return path2;
            }
        }

        return null;
    }


    /**
     * Uses a breadth-first search algorithm to find a path from the startVertex to targetVertex in the subgraph
     * All vertices that are being visited by the search should also be registered in path.visited
     *
     * @param startVertex
     * @param targetVertex
     * @return the path from startVertex to targetVertex
     * or null if target cannot be matched with a vertex in the sub-graph from startVertex
     */
    public GPath breadthFirstSearch(V startVertex, V targetVertex) {
        if (startVertex == null || targetVertex == null) return null;

        // TODO calculate the path from start to target by breadth-first-search

        GPath path = new GPath();
        path.vertices.addLast(targetVertex);

        // checking if the start vertex is already the target vertex, if so, return the path
        if (startVertex.equals(targetVertex)) {
            path.visited.add(startVertex);
            return path;
        }

        Queue<V> fifoQueue = new LinkedList<>();
        Map<V, V> visitedFrom = new HashMap<>();

        // initializing the queue with the starting vertex. Marking the start vertex as visited without a predecessor (null).
        fifoQueue.offer(startVertex);
        visitedFrom.put(startVertex, null);

        // Retrieve and remove the head element
        V current;
        while ((current = fifoQueue.poll()) != null) {
            for (V neighbour : this.getNeighbours(current)) {
                if (neighbour.equals(targetVertex)) {
                    while (current != null) {
                        path.vertices.addFirst(current);
                        current = visitedFrom.get(current);
                    }
                    return path;
                } else if (!visitedFrom.containsKey(neighbour)) {
                    fifoQueue.offer(neighbour);
                    visitedFrom.put(neighbour, current);
                    path.visited.add(neighbour);
                }
            }
        }

        return null;    // replace by a proper outcome, if any
    }


    // helper class to build the spanning tree of visited vertices in dijkstra's shortest path algorithm
    // your may change this class or delete it altogether follow a different approach in your implementation
    private class MSTNode implements Comparable<MSTNode> {
        protected V vertex;                // the graph vertex that is concerned with this MSTNode
        protected V parentVertex = null;     // the parent's node vertex that has an edge towards this node's vertex
        protected boolean marked = false;  // indicates DSP processing has been marked complete for this vertex
        protected double weightSumTo = Double.MAX_VALUE;   // sum of weights of current shortest path towards this node's vertex

        private MSTNode(V vertex) {
            this.vertex = vertex;
        }

        // comparable interface helps to find a node with the shortest current path, sofar
        @Override
        public int compareTo(MSTNode otherMSTNode) {
            return Double.compare(weightSumTo, otherMSTNode.weightSumTo);
        }
    }

    /**
     * Calculates the edge-weighted shortest path from the startVertex to targetVertex in the subgraph
     * according to Dijkstra's algorithm of a minimum spanning tree
     *
     * @param startVertex
     * @param targetVertex
     * @param weightMapper provides a function(v1,v2) by which the weight of an edge from v1 to v2
     *                     can be retrieved or calculated
     * @return the shortest path from startVertex to targetVertex
     * or null if target cannot be matched with a vertex in the sub-graph from startVertex
     */
    public GPath dijkstraShortestPath(V startVertex, V targetVertex,
                                      BiFunction<V, V, Double> weightMapper) {

        if (startVertex == null || targetVertex == null) return null;
        GPath path = new GPath();

        PriorityQueue<MSTNode> priorityQueue = new PriorityQueue<>();
        Map<V, MSTNode> vertexToMSTNode = new HashMap<>();
        for (V vertex : getAllVertices(startVertex)) {
            MSTNode mstNode = new MSTNode(vertex);
            vertexToMSTNode.put(vertex, mstNode);
            if (vertex.equals(startVertex)) {
                mstNode.weightSumTo = 0;
                priorityQueue.offer(mstNode);
            }
        }
        while (!priorityQueue.isEmpty()) {
            MSTNode currentNode = priorityQueue.poll();
            currentNode.marked = true;
            for (V neighbor : getNeighbours(currentNode.vertex)) {
                MSTNode neighborNode = vertexToMSTNode.get(neighbor);
                double weight = weightMapper.apply(currentNode.vertex, neighbor);
                if (!neighborNode.marked && currentNode.weightSumTo + weight < neighborNode.weightSumTo) {
                    neighborNode.weightSumTo = currentNode.weightSumTo + weight;
                    neighborNode.parentVertex = currentNode.vertex;
                    priorityQueue.offer(neighborNode);
                }
            }
        }
        Deque<V> shortestPath = new ArrayDeque<>();
        MSTNode targetNode = vertexToMSTNode.get(targetVertex);
        while (targetNode != null) {
            path.vertices.offerFirst(targetNode.vertex);
            path.totalWeight += vertexToMSTNode.get(targetNode.parentVertex).weightSumTo;
            targetNode = vertexToMSTNode.get(targetNode.parentVertex);
        }

        return path;
        
//        // initialise the result path of the search
//        GPath path = new GPath();
//        path.visited.add(startVertex);
//
//        // easy target
//        if (startVertex.equals(targetVertex)) {
//            path.vertices.add(startVertex);
//            return path;
//        }
//
//        // a minimum spanning tree which tracks for every visited vertex:
//        //   a) its (parent) predecessor in the currently shortest path towards this visited vertex
//        //   b) the total weight of the currently shortest path towards this visited vertex
//        //   c) a mark, indicating whether the current path towards this visited vertex is the final shortest.
//        // (you may choose a different approach of tracking the MST of the algorithm, if you wish)
//        Map<V, MSTNode> minimumSpanningTree = new HashMap<>();
//
//        // initialise the minimum spanning tree with the startVertex
//        MSTNode nearestMSTNode = new MSTNode(startVertex);
//        nearestMSTNode.weightSumTo = 0.0;
//        minimumSpanningTree.put(startVertex, nearestMSTNode);
//
//        // TODO maybe more helper variables or data structures, if needed
////        Set<V> settledNodes = new HashSet<>();
////        Set<V> unsettledNodes = new HashSet<>();
////
////        unsettledNodes.add(startVertex);
//
//
//        while (nearestMSTNode != null) {
//
//            // TODO continue Dijkstra's algorithm to process nearestMSTNode
//            //  mark nodes as you find their current shortest path to be final
//            //  if you hit the target: complete the path and bail out !!!
//            //  register all visited vertices for statistical purposes
//
//
//            // TODO find the next nearest MSTNode that is not marked yet
//            nearestMSTNode = null;      // replace by a proper selection
//        }
//
//
//        return null;        // replace by a proper outcome, if any
    }
}
