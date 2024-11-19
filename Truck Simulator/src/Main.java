import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        // Input and output files, passed as arguments
        File inFile = new File(args[0]);
        File outFile = new File(args[1]);
        PrintStream outstream;

        // Attempt to open the output file for writing
        try { 
            outstream = new PrintStream(outFile); 
        } catch (FileNotFoundException e) { 
            e.printStackTrace(); 
            return; 
        }

        // Attempt to read the input file
        Scanner reader;
        try { 
            reader = new Scanner(inFile); 
        } catch (FileNotFoundException e) {
            System.out.println("Cannot find input file");
            outstream.close(); // Close output stream before exiting
            return;
        }        
        
        // Initialize the fleet management system to handle trucks and parking lots
        FleetManagement fleet = new FleetManagement();

        // Process each line in the input file
        while(reader.hasNextLine()) {
            String line = reader.nextLine();
            String[] tokens = line.trim().split("\\s+");
            String action = tokens[0]; // First word indicates the action type

            // Execute actions based on the command type
            switch(action) {
                case "create_parking_lot":
                    int capacityConstraint = Integer.parseInt(tokens[1]);
                    int truckLimit = Integer.parseInt(tokens[2]);
                    fleet.createLot(capacityConstraint, truckLimit); // Create a parking lot
                    break;

                case "delete_parking_lot":
                    int delCapacity = Integer.parseInt(tokens[1]);
                    fleet.deleteLot(delCapacity); // Delete a specified parking lot
                    break;

                case "add_truck":
                    int truckID = Integer.parseInt(tokens[1]);
                    int maxCapacity = Integer.parseInt(tokens[2]);
                    // Add a truck and print the parking lot capacity or -1 if it cannot be added
                    outstream.println(fleet.addTruck(truckID, maxCapacity, maxCapacity));
                    break;

                case "ready":
                    int readyCapacity = Integer.parseInt(tokens[1]);
                    int[] token = fleet.ready(readyCapacity);
                    // Output the truck ID and capacity, or -1 if no truck is available
                    outstream.println(token[0] == -1 ? token[0] : token[0] + " " + token[1]);
                    break;

                case "load":
                    int loadCapacity = Integer.parseInt(tokens[1]);
                    int loadAmount = Integer.parseInt(tokens[2]);
                    // Load the specified amount onto trucks and print results in required format
                    ArrayList<int[]> results = fleet.load(loadCapacity, loadAmount);

                    if (results.get(0)[0] == -1) {
                        outstream.println(-1); // Indicate unsuccessful load operation
                    } else {
                        // Print truck ID and load in "ID capacity - ID capacity" format
                        for (int i = 0; i < results.size(); i++) {
                            if (results.get(i)[0] == -1) break;
                            outstream.print(results.get(i)[0] + " " + results.get(i)[1]);
                            outstream.print(i == results.size() - 1 || results.get(i + 1)[0] == -1 ? "\n" : " - ");
                        }
                    }
                    break;

                case "count":
                    int countCapacity = Integer.parseInt(tokens[1]);
                    // Output the number of trucks with capacity greater than specified limit
                    outstream.println(fleet.count(countCapacity));
                    break;

                default:
                    // Handle unrecognized commands
                    System.out.println("Unknown command: " + action);
                    break;
            }
        }

        // Close input and output resources
        reader.close();
        outstream.close();

    }
}