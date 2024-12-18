/**
 * The Edge class represents a connection between two Land objects (nodes) in a grid or graph.
 * Each edge has a time cost associated with traveling between the two Lands.
 */
public class Edge implements Comparable<Edge> {
    Land from;      // The starting Land (node) of the edge
    Land to;        // The ending Land (node) of the edge
    double time;    // Time cost or weight of the edge

    /**
     * Constructor for the Edge class.
     *
     * @param from The starting Land (node) of the edge
     * @param to   The ending Land (node) of the edge
     * @param time The time cost (weight) of traveling along this edge
     */
    Edge(Land from, Land to, double time) {
        this.from = from;
        this.to = to;
        this.time = time;
    }

    /**
     * Compares this edge to another edge based on their time costs.
     *
     * @param e The other Edge object to compare to.
     * @return 1 if this edge's time is greater, -1 if smaller, and 0 if equal.
     */
    @Override
    public int compareTo(Edge e) {
        if (this.time > e.time) {
            return 1;   // This edge has a higher time cost
        } else if (this.time < e.time) {
            return -1;  // This edge has a lower time cost
        } else {
            return 0;   // Both edges have the same time cost
        }
    }
}