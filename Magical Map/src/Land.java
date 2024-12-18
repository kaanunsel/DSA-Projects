import java.util.ArrayList;

/**
 * The Land class represents a node or position on a grid.
 * Each Land object stores its coordinates, type, distance for pathfinding,
 * edges (connections to other Lands), and additional properties like visited and revealed status.
 */
public class Land {
    int x;                        // X-coordinate of the land
    int y;                        // Y-coordinate of the land
    Land prevLand;                // Reference to the previous Land in the shortest path
    Double distance;              // Distance from the start node (used in pathfinding)
    int type;                     // Type of the land (e.g., 0 = revealed, 1 = impassable)
    ArrayList<Edge> edges;        // List of edges (connections to neighboring Lands)
    boolean visited;              // Marks whether this land has been visited in pathfinding
    boolean revealed;             // Marks whether this land has been revealed to the user

    /**
     * Constructor for the Land class.
     *
     * @param x    X-coordinate of the land
     * @param y    Y-coordinate of the land
     * @param type Type of the land (e.g., 0 = revealed, 1 = impassable)
     */
    Land(int x, int y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.prevLand = null;             // Default to null; no previous Land initially
        this.distance = Double.MAX_VALUE; // Default distance set to a very large value
        this.edges = new ArrayList<>(4);  // Initialize edges list; max neighbors typically 4 in a grid
        this.visited = false;             // Default as not visited
        this.revealed = false;            // Default as not revealed
    }

    /**
     * Generates a unique hash code for this Land object based on its coordinates.
     *
     * @return Hash code calculated using the x and y coordinates.
     */
    @Override
    public int hashCode() {
        return 31 * x + y;  // A simple hash code formula combining x and y
    }

    /**
     * Provides a string representation of this Land object.
     *
     * @return A string in the format "(x, y)" representing the land's coordinates.
     */
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    /**
     * Compares this Land object to another object for equality.
     * Two Lands are considered equal if their x and y coordinates are the same.
     *
     * @param obj The object to compare to this Land.
     * @return True if the objects are equal; false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;              // Check for same object reference
        if (obj == null || getClass() != obj.getClass()) return false; // Check for null or different class
        Land land = (Land) obj;
        return this.x == land.x && this.y == land.y; // Compare x and y coordinates
    }
}