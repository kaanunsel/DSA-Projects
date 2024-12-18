import java.io.*;
import java.util.ArrayList;

/**
 * The Main class reads input data for nodes, edges, and objectives from files,
 * calculates the shortest path while managing visibility (revealed/unrevealed lands),
 * and outputs the path followed along with selected objectives to an output file.
 */
public class Main {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws IOException, FileNotFoundException {
        
        // Input and output file paths received as command-line arguments
        String input_nodes = args[0];
        String input_edges = args[1];
        String input_objectives = args[2];
        String output_file = args[3];

        // Basic variables
        int x_size;  // Grid width
        int y_size;  // Grid height
        int radius;  // Visibility radius
        Land startLand;  // Starting position on the grid
        Land[][] land_matrix;  // 2D matrix representing the grid of lands
        ArrayList<Object[]> objectives = new ArrayList<>();  // List of objectives
        MyHashTable<Integer, Boolean> revealedColors = new MyHashTable<>();  // Tracks revealed colors

        // === STEP 1: Reading "nodes" file ===
        BufferedReader reader1 = new BufferedReader(new FileReader(input_nodes));
        String[] parts1 = reader1.readLine().split(" ");
        x_size = Integer.parseInt(parts1[0]);  // Grid width
        y_size = Integer.parseInt(parts1[1]);  // Grid height

        land_matrix = new Land[y_size][x_size];  // Initialize grid matrix
        String line1;

        // Parse each node's coordinates and type, and populate the grid
        while ((line1 = reader1.readLine()) != null) {
            String[] coordStrings = line1.split(" ");
            int x = Integer.parseInt(coordStrings[0]);
            int y = Integer.parseInt(coordStrings[1]);
            int type = Integer.parseInt(coordStrings[2]);

            // Initialize revealed status based on type
            if (type == 0) {
                revealedColors.put(type, true);  // Type 0 is revealed
            } else {
                revealedColors.put(type, false);
            }

            Land land = new Land(x, y, type);
            land_matrix[y][x] = land;  // Add land to grid
        }
        reader1.close();

        // === STEP 2: Reading "edges" file ===
        BufferedReader reader2 = new BufferedReader(new FileReader(input_edges));
        String line2;

        // Parse edges connecting two lands
        while ((line2 = reader2.readLine()) != null) {
            String[] parts2 = line2.split(" ");
            String[] nodes = parts2[0].split(",");

            // Parse coordinates of connected nodes
            String[] node1_coords = nodes[0].split("-");
            int x1 = Integer.parseInt(node1_coords[0]);
            int y1 = Integer.parseInt(node1_coords[1]);

            String[] node2_coords = nodes[1].split("-");
            int x2 = Integer.parseInt(node2_coords[0]);
            int y2 = Integer.parseInt(node2_coords[1]);

            Double time = Double.parseDouble(parts2[1]);  // Time cost between nodes

            // Get corresponding lands
            Land land1 = land_matrix[y1][x1];
            Land land2 = land_matrix[y2][x2];

            // Skip edges involving type 1 (impassable)
            if (land1.type == 1 || land2.type == 1) {
                continue;
            }

            // Create bidirectional edges
            Edge edge1 = new Edge(land1, land2, time);
            Edge edge2 = new Edge(land2, land1, time);

            // Add edges to respective lands
            land1.edges.add(edge1);
            land2.edges.add(edge2);
        }
        reader2.close();

        // === STEP 3: Reading "objectives" file ===
        BufferedReader reader3 = new BufferedReader(new FileReader(input_objectives));
        radius = Integer.parseInt(reader3.readLine());  // Read visibility radius

        // Starting land position
        String[] parts3 = reader3.readLine().split(" ");
        startLand = land_matrix[Integer.parseInt(parts3[1])][Integer.parseInt(parts3[0])];

        String line3;

        // Read objectives and possible options
        while ((line3 = reader3.readLine()) != null) {
            String[] partsObjective = line3.split(" ");
            Object[] objective = new Object[2];
            ArrayList<Integer> choices = new ArrayList<>();

            // Set target land for objective
            objective[0] = land_matrix[Integer.parseInt(partsObjective[1])][Integer.parseInt(partsObjective[0])];

            // If options are provided
            if (partsObjective.length == 2) {
                objective[1] = null;
            } else {
                for (int i = 2; i < partsObjective.length; i++) {
                    choices.add(Integer.parseInt(partsObjective[i]));
                }
                objective[1] = choices;
            }
            objectives.add(objective);
        }
        reader3.close();

        // === STEP 4: Reveal initial visibility circle ===
        double radiusSquared = radius * radius;

        for (int i = startLand.x - radius; i <= startLand.x + radius; i++) {
            if (i < 0 || i >= x_size) continue;

            for (int j = startLand.y - radius; j <= startLand.y + radius; j++) {
                if (j < 0 || j >= y_size) continue;

                int distanceSquared = (i - startLand.x) * (i - startLand.x) + (j - startLand.y) * (j - startLand.y);
                if (distanceSquared <= radiusSquared) {
                    land_matrix[j][i].revealed = true;
                }
            }
        }

        // === STEP 5: Process objectives and find routes ===
        PrintWriter writer = new PrintWriter(new FileWriter(output_file));
        Land currentLand = startLand;

        for (int k = 0; k < objectives.size(); k++) {
            Land targetLand = (Land) objectives.get(k)[0];

            // Find the route to the target land
            ArrayList<Land> newRoute = (ArrayList<Land>) findRoute(currentLand, targetLand, revealedColors, land_matrix)[0];

            while (currentLand != targetLand) {
                currentLand = newRoute.getLast();
                writer.println("Moving to " + currentLand.x + "-" + currentLand.y);

                newRoute.removeLast();

                // Reveal new lands in visibility circle
                for (int i = currentLand.x - radius; i <= currentLand.x + radius; i++) {
                    if (i < 0 || i >= x_size) continue;

                    for (int j = currentLand.y - radius; j <= currentLand.y + radius; j++) {
                        if (j < 0 || j >= y_size) continue;

                        int distanceSquared = (i - currentLand.x) * (i - currentLand.x) + (j - currentLand.y) * (j - currentLand.y);
                        if (distanceSquared <= radiusSquared) {
                            land_matrix[j][i].revealed = true;
                        }
                    }
                }

                // Handle impassable paths and recompute route if necessary
                for (int i = newRoute.size() - 1; i >= 0; i--) {
                    Land tempLand = newRoute.get(i);
                    if (tempLand.revealed && !revealedColors.get(tempLand.type)) {
                        writer.println("Path is impassable!");
                        newRoute = (ArrayList<Land>) findRoute(currentLand, targetLand, revealedColors, land_matrix)[0];
                        break;
                    }
                }
            }
            writer.println("Objective " + (k + 1) + " reached!");

            // Handle choices for objectives
            if (objectives.get(k)[1] != null) {
                ArrayList<Integer> options = (ArrayList<Integer>) objectives.get(k)[1];
                int bestChoice = 0;
                Double bestPath = Double.MAX_VALUE;

                for (int a : options) {
                    if (revealedColors.get(a)) continue;

                    revealedColors.put(a, true);
                    Double time = (Double) findRoute(targetLand, (Land) objectives.get(k + 1)[0], revealedColors, land_matrix)[1];

                    if (time < bestPath) {
                        bestPath = time;
                        bestChoice = a;
                    }
                    revealedColors.put(a, false);
                }

                writer.println("Number " + bestChoice + " is chosen!");
                revealedColors.put(bestChoice, true);
            }
        }
        writer.close();
    }

    /**
     * Finds the shortest route from start to end using Dijkstra-like approach.
     */
    public static Object[] findRoute(Land start, Land end, MyHashTable<Integer, Boolean> revealedColors, Land[][] land_matrix) {
        // Reset grid distances, visited status, and previous nodes
        for (Land[] row : land_matrix) {
            for (Land land : row) {
                land.distance = Double.MAX_VALUE;
                land.prevLand = null;
                land.visited = false;
            }
        }

        // Initialize route and heap
        ArrayList<Land> route = new ArrayList<>();
        MinHeap<Edge> heap = new MinHeap<>();
        start.distance = 0.0;

        // Add edges of start node to heap
        for (Edge e : start.edges) {
            if (revealedColors.get(e.to.type)) {
                heap.add(e);
            }
        }

        while (heap.size() > 0) {
            Edge temp_edge = heap.extractMin();
            Land from = temp_edge.from;
            Land to = temp_edge.to;

            if (to.revealed && !revealedColors.get(to.type)) continue;

            double time = temp_edge.time;

            if (time >= to.distance) continue;

            to.visited = true;
            to.prevLand = from;
            to.distance = time;

            // Explore neighbors
            for (Edge e : to.edges) {
                if (!e.to.visited) {
                    heap.add(new Edge(e.from, e.to, time + e.time));
                }
            }
        }

        // Backtrack to reconstruct the route
        Land current = end;
        while (current != start) {
            route.add(current);
            current = current.prevLand;
        }

        return new Object[] { route, end.distance };
    }
}