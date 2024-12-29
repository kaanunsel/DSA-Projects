import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

/**
 * This program is a fastest delivery route calculator with the help
 * of brute force and ant colony optimization algorithm
 * @author Mehmet Kaan Ünsel
 * @since 08.05.2024
 */
public class App {
    static int N = 100; // Number of iterations in ant colony optimization
    static int M = 50; // Number of ants per iteration
    static double degradationConstant = 0.7; // Rate at which pheromones evaporate
    static double alpha = 0.8; // Influence of pheromone on direction
    static double beta = 1.5; // Influence of heuristic distance on direction
    static double initialPheromone = 0.1; // Initial amount of pheromone on paths
    static double Q = 0.0001; // Pheromone left by ants is inversely proportional to the distance

    /**
     * Main function of the program.
     * @param args
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        // List to hold all nodes (including Migros as the first node)
        ArrayList<Node> nodes = new ArrayList<>();
        nodes.add(null); // Adding null as the first element to use 1-based indexing

        // Setup to read nodes from a file
        File f = new File("Ant Colony Optimization/inputs/input01.txt");
        Scanner scanner = new Scanner(f);

        // Read nodes from file and add to the list
        while (scanner.hasNextLine()) {
            String s = scanner.nextLine();
            String[] split = s.split(",");
            nodes.add(new Node(Double.parseDouble(split[0]), Double.parseDouble(split[1])));
        }

        // Mark the first node as Migros for identification
        nodes.get(1).isMigros = true;
        // Array to store the initial route considering all nodes
        Integer[] firstRoute = new Integer[nodes.size() - 1];

        // Initialize route with consecutive integers (1-based indexing)
        for (int i = 1; i < firstRoute.length + 1; i++) {
            firstRoute[i - 1] = i;
        }

        // Variable to choose the method of solving: 1 for brute-force, 2 for ant colony
        int chosenMethod = 2; // Change this value to choose the method

        // Call the chosen method to solve the problem
        if (chosenMethod == 1) {
            bruteForce(firstRoute, nodes);
        } else if (chosenMethod == 2) {
            antColony(firstRoute, nodes);
        }

        scanner.close(); // Close the scanner
    }

    /**
     * Main method to run ant colony algorithm
     * @param route
     * @param nodes
     */
    private static void antColony(Integer[] route, ArrayList<Node> nodes) {
        long startTime = System.currentTimeMillis(); // Start timing the operation
        // Initialize pheromone levels between all pairs of nodes
        double[][] pheromones = new double[nodes.size()][nodes.size()];
        for (double[] row : pheromones)
            Arrays.fill(row, initialPheromone);

        double minDistance = Double.MAX_VALUE; // Store the minimum distance found
        Integer[] bestRoute = route.clone(); // Best route found by ants

        // Run the ant colony simulation
        for (int iter = 0; iter < N; iter++) {
            for (int ant = 0; ant < M; ant++) {
                ArrayList<Integer> visited = new ArrayList<>(); // Track visited nodes for one ant
                visited.add(1); // Start at Migros
                Integer current = 1; // Current node is Migros

                // Perform the traversal for one ant
                while (visited.size() < nodes.size()) {
                    Integer next = selectNextNode(current, visited, nodes, pheromones); // Select the next node to visit
                    visited.add(next); // Mark next node as visited
                    current = next; // Move to the next node
                }

                // Calculate the total distance of the route found by the current ant
                double distance = calculateRouteDistance(visited.toArray(new Integer[0]), nodes);
                if (distance < minDistance) {
                    minDistance = distance; // Update the shortest distance found
                    bestRoute = visited.toArray(new Integer[0]); // Update the best route found
                }

                // Update pheromone levels based on the route found
                updatePheromones(pheromones, visited, distance);
            }
            // Evaporate pheromones at the end of each iteration
            evaporatePheromones(pheromones);
        }

        long endTime = System.currentTimeMillis(); // End timing the operation
        double elapsedTime = (endTime - startTime) / 1000.0; // Convert time to seconds

        // Output results of the ant colony optimization
        System.out.println("Method: Ant Colony Optimization");
        System.out.println("Shortest Distance: " + minDistance);
        System.out.println("Shortest Path: " + Arrays.toString(bestRoute));
        System.out.println("Time it takes to find the shortest path: " + elapsedTime + " seconds.");

        // Draw the route and pheromone graph
        int plotType = 1;
        if(plotType == 1) {
            drawRoute(bestRoute, nodes);
        }
        else{
            drawPheromoneGraph(pheromones, nodes);
        }

    }

    /**
     * This method updates the pheromone levels for the visited nodes.
     * @param pheromones
     * @param visited
     * @param distance
     */
    private static void updatePheromones(double[][] pheromones, ArrayList<Integer> visited, double distance) {
        double delta = Q / distance; // Calculate pheromone addition factor
        for (int i = 0; i < visited.size() - 1; i++) {
            int from = visited.get(i); // Current node
            int to = visited.get(i + 1); // Next node
            // Update pheromone levels for both directions
            pheromones[from][to] += delta;
            pheromones[to][from] += delta;
        }
    }

    /**
     * This method makes pheromones evaporate by multiplying them with a degradation constant
     * @param pheromones
     */
    private static void evaporatePheromones(double[][] pheromones) {
        // Reduce pheromone levels on all paths
        for (int i = 1; i < pheromones.length; i++) {
            for (int j = 1; j < pheromones[i].length; j++) {
                pheromones[i][j] *= degradationConstant;
            }
        }
    }

    /**
     * This method statistically calculates which node the ant will choose to travel.
     * @param current
     * @param visited
     * @param nodes
     * @param pheromones
     * @return next node that the ant will choose in ACO algorithm
     */
    private static Integer selectNextNode(Integer current, ArrayList<Integer> visited, ArrayList<Node> nodes, double[][] pheromones) {
        ArrayList<Double> probabilities = new ArrayList<>(); // Probabilities of moving to each node
        double sum = 0.0; // Sum of all probabilities for normalization
        // Calculate the attractiveness of each unvisited node
        for (int j = 1; j < nodes.size(); j++) {
            if (!visited.contains(j)) {
                double edgeValue = Math.pow(pheromones[current][j], alpha) / Math.pow(calculateDistance(nodes.get(current), nodes.get(j)), beta);
                probabilities.add(edgeValue);
                sum += edgeValue;
            } else {
                probabilities.add(0.0);
            }
        }

        double random = new Random().nextDouble() * sum; // Generate a random number for selection
        double cumulative = 0.0; // Cumulative probability for selecting a node

        // Select the next node based on the computed probabilities
        for (int j = 1; j < nodes.size(); j++) {
            cumulative += probabilities.get(j - 1);
            if (cumulative > random) {
                return j; // Return the selected node
            }
        }
        return 1; // Default to returning to Migros if something fails
    }

    /**
     * Calculates distance between two nodes
     * @param a
     * @param b
     * @return
     */
    private static double calculateDistance(Node a, Node b) {
        // Calculate Euclidean distance between two nodes
        return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }

    /**
     * Main brute force algorithm method to initialize it
     * @param route
     * @param nodes
     */
    private static void bruteForce(Integer[] route, ArrayList<Node> nodes) {
        long startTime = System.currentTimeMillis(); // Start timing the brute-force method

        double minDistance = Double.MAX_VALUE; // Initialize minimum distance
        Integer[] shortestPath = route.clone(); // Initialize shortest path

        // Copy route to newRoute starting from the second element
        Integer[] newRoute = new Integer[route.length - 1];
        for (int i = 0; i < route.length - 1; i++) {
            newRoute[i] = route[i + 1];
        }
        route = newRoute;

        // Calculate the distance of the initial route configuration
        double distance = calculateRouteDistance(route, nodes);
        if (distance < minDistance) {
            minDistance = distance;
            shortestPath = addMigros(route).clone();
        }

        // c array to control permutations
        int[] c = new int[route.length];

        int i = 0; // Initialize control variable for the loop
        // Loop to generate all permutations using Heap's algorithm
        while (i < route.length) {  // Continue until all permutations are explored
            if (c[i] < i) {  // Check if we can still swap at position i based on the count in c
                // Perform a swap based on the parity of i; if i is even, swap the first and i-th elements; if odd, swap elements at positions c[i] and i
                swap(route, (i % 2 == 0) ? 0 : c[i], i);

                // Calculate the total distance of the new route after the swap
                double newDistance = calculateRouteDistance(route, nodes);
                if (newDistance < minDistance) {  // Check if the new route distance is the shortest found so far
                    minDistance = newDistance;  // Update the minimum distance
                    shortestPath = addMigros(route).clone();  // Update the shortest path found
                }

                c[i]++;  // Increment the count of swaps at position i
                i = 0;  // Reset i to 0 to generate the next permutation sequence from the beginning
            } else {
                c[i] = 0;  // Reset the swap count for this position once it reaches i
                i++;  // Move to the next position
            }
        }

        long endTime = System.currentTimeMillis(); // End timing the operation
        double elapsedTime = (endTime - startTime) / 1000.0; // Convert time to seconds

        // Format the shortest path for output
        String bestPath = "[";
        for (int k = 0; k < shortestPath.length; k++) {
            bestPath += shortestPath[k] + ", ";
        }
        bestPath += "1]";

        // Output results of the brute-force method
        System.out.println("Method: Brute-Force Method");
        System.out.println("Shortest Distance: " + minDistance);
        System.out.println("Shortest Path: " + bestPath);
        System.out.println("Time it takes to find the shortest path: " + elapsedTime + " seconds.");

        drawRoute(shortestPath, nodes); // Draw the best route found
    }

    /**
     * Method to swap the integer array
     * @param array
     * @param i
     * @param j
     */
    private static void swap(Integer[] array, int i, int j) {
        Integer temp = array[i]; // Temporary variable to store the ith element
        array[i] = array[j]; // Swap the ith and jth elements
        array[j] = temp; // Complete the swap
    }

    /**
     * This method adds Migros as the starting node at the start of each node
     * @param route
     * @return
     */
    private static Integer[] addMigros(Integer[] route) {
        // Create a new route including Migros at the start
        Integer[] newRoute = new Integer[route.length + 1];
        newRoute[0] = 1; // Set the first element as Migros
        for (int i = 0; i < route.length; i++) {
            newRoute[i + 1] = route[i];
        }
        return newRoute; // Return the new route
    }

    /**
     * This method calculates the route distance by summing the distances between the nodes in the route.
     * @param route
     * @param locations
     * @return total distance traveled by an ant for a given route.
     */
    private static double calculateRouteDistance(Integer[] route, ArrayList<Node> locations) {
        // Calculate the total distance of a given route
        Integer[] newRoute = addMigros(route); // Add Migros to the route for calculation
        double distance = 0;
        int prevIdx = 1; // Start with Migros
        for (int locationIdx : newRoute) {
            // Calculate distance between consecutive nodes
            distance += Math.sqrt(Math.pow(locations.get(locationIdx).x - locations.get(prevIdx).x, 2) +
                    Math.pow(locations.get(locationIdx).y - locations.get(prevIdx).y, 2));
            prevIdx = locationIdx; // Move to the next node
        }
        // Add the distance to return to Migros at the end of the route
        distance += Math.sqrt(Math.pow(locations.get(1).x - locations.get(prevIdx).x, 2) +
                Math.pow(locations.get(1).y - locations.get(prevIdx).y, 2));
        return distance; // Return the total distance
    }

    /**
     * This method draws the optimal route by using StdDraw
     * @param route
     * @param nodes
     */
    private static void drawRoute(Integer[] route, ArrayList<Node> nodes) {
        // Setup canvas for drawing the route
        StdDraw.setCanvasSize(800, 800);
        StdDraw.setXscale(0, 1);
        StdDraw.setYscale(0, 1);
        StdDraw.setPenRadius(0.005);
        StdDraw.enableDoubleBuffering();

        double prev_x = nodes.get(1).x; // Start at Migros
        double prev_y = nodes.get(1).y;
        StdDraw.setPenRadius(0.004);
        StdDraw.setPenColor(Color.black);
        // Draw lines between nodes in the route
        for (int index : route) {
            double x = nodes.get(index).x;
            double y = nodes.get(index).y;
            StdDraw.line(prev_x, prev_y, x, y); // Draw line from previous node to current node
            prev_x = x;
            prev_y = y;
        }
        // Draw a line returning to Migros to complete the route
        StdDraw.line(prev_x, prev_y, nodes.get(1).x, nodes.get(1).y);
        // Draw circles for each node in the route
        for (int index : route) {
            double x = nodes.get(index).x;
            double y = nodes.get(index).y;
            if (nodes.get(index).isMigros) {
                StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE); // Draw Migros in orange
            } else {
                StdDraw.setPenColor(Color.LIGHT_GRAY); // Draw houses in gray
            }
            StdDraw.filledCircle(x, y, 0.023); // Draw filled circle for node
            StdDraw.setPenColor(Color.black);
            StdDraw.text(x, y, String.valueOf(index)); // Label node with its index
        }

        StdDraw.show(); // Display the drawing
    }

    /**
     * This method visualize the pheromone density using StdDraw
     * @param pheromones
     * @param nodes
     */
    private static void drawPheromoneGraph(double[][] pheromones, ArrayList<Node> nodes) {
        // Setup canvas for drawing pheromone levels
        StdDraw.setCanvasSize(800, 800);
        StdDraw.setXscale(0, 1);
        StdDraw.setYscale(0, 1);
        StdDraw.clear();
        StdDraw.enableDoubleBuffering();

        double maxPheromone = findMaxPheromone(pheromones); // Determine the maximum pheromone level for scaling

        // Draw lines representing pheromone levels between nodes
        for (int i = 1; i < nodes.size(); i++) {
            for (int j = 1; j < nodes.size(); j++) {
                if (i != j) {
                    double intensity = pheromones[i][j] / maxPheromone; // Calculate normalized intensity
                    StdDraw.setPenRadius(0.015 * Math.pow(intensity, 5)); // Set line thickness based on intensity
                    StdDraw.setPenColor(Color.BLACK); // Set line color to black
                    StdDraw.line(nodes.get(i).x, nodes.get(i).y, nodes.get(j).x, nodes.get(j).y); // Draw line
                }
            }
        }

        // Draw circles for each node on top of the lines
        for (int index = 1; index < nodes.size(); index++) {
            double x = nodes.get(index).x;
            double y = nodes.get(index).y;
            StdDraw.setPenColor(Color.LIGHT_GRAY); // Set node color
            StdDraw.filledCircle(x, y, 0.021); // Draw filled circle for node
            StdDraw.setPenColor(Color.black);
            StdDraw.text(x, y, String.valueOf(index)); // Label node with its index
        }

        StdDraw.show(); // Display the drawing
    }

    /**
     * Helper method to find the maximum pheromone level
     * @param pheromones
     * @return maximum phereomone level
     */
    private static double findMaxPheromone(double[][] pheromones) {
        double max = 0;
        for (int i = 1; i < pheromones.length; i++) {
            for (int j = 1; j < pheromones[i].length; j++) {
                if (pheromones[i][j] > max) {
                    max = pheromones[i][j]; // Update the maximum pheromone level found
                }
            }
        }
        return max; // Return the maximum pheromone level
    }
}

/**
 * This node class represents a stop-point, its instance can be a house or a Migros.
 * @author Mehmet Kaan Ünsel
 * @since 08.05.2024
 */
class Node {

    public double x; // x coordinate of the node
    public double y; // y coordinate of the node

    public boolean isMigros = false; // type of this node, a house or a Migros

    /**
     * Constructor with two parameters
     * @param x
     * @param y
     */
    Node(double x, double y){
        this.x = x;
        this.y = y;
    }
}
