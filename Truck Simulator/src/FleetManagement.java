import java.util.ArrayList;

public class FleetManagement {

    private ParkingAvlTree ParkingLots;

    // Constructor initializes the AVL tree for managing parking lots
    public FleetManagement(){
        this.ParkingLots = new ParkingAvlTree(this);
    }

    // Creates a parking lot with the specified capacity constraint and truck limit
    public void createLot(int capacity, int truck_limit){
        ParkingLots.insert(new ParkingLot(capacity, truck_limit));
    }

    // Removes the parking lot with the specified capacity constraint
    public void deleteLot(int capacity){
        ParkingLots.remove(capacity);
    }

    // Adds a truck to an appropriate parking lot based on its capacity
    public int addTruck(int id, int capacity, int maxCapacity){
        ParkingAvlTree.AvlNode tempNode = ParkingLots.searchNode(capacity);

        // If no exact capacity match, add to the largest lot with smaller capacity
        if(tempNode == null){
            return ParkingLots.traverseLessThanAndAddTruck(capacity, id);
        }

        ParkingLot temp = tempNode.data;

        // Add the truck if the lot has space, otherwise look for a smaller capacity lot
        if(temp != null && !temp.isFull()){
            temp.addTruck(id, capacity);
            return temp.capacity;
        } else {
            return ParkingLots.traverseLessThanAndAddTruck(capacity, id);
        }
    }

    // Overloaded method to add a Truck object to the best-suited parking lot
    public int addTruck(Truck truck){
        int remainingCapacity = truck.max_capacity - truck.loadedAmount;
        ParkingAvlTree.AvlNode tempNode = ParkingLots.searchNode(remainingCapacity);

        // If no exact capacity match, add to the largest lot with smaller capacity
        if(tempNode == null){
            return ParkingLots.traverseLessThanAndAddTruck(remainingCapacity, truck);
        }

        ParkingLot temp = tempNode.data;

        // Add truck if space is available; otherwise, look for a smaller capacity lot
        if(temp != null && !temp.isFull()){
            return temp.addTruck(truck);
        } else {
            return ParkingLots.traverseLessThanAndAddTruck(remainingCapacity, truck);
        }
    }

    // Moves a truck from the waiting section to the ready section in the specified parking lot
    public int[] ready(int capacity){
        ParkingAvlTree.AvlNode tempNode = ParkingLots.searchNode(capacity);

        // If no exact capacity match, check larger lots for available trucks
        if(tempNode == null){
            ParkingLot tempLot = ParkingLots.traverseGreaterThanAndReady(capacity);
            return tempLot != null ? tempLot.ready() : new int[]{-1};
        }

        ParkingLot temp = tempNode.data;

        // Move the earliest waiting truck to the ready section if available
        if(temp != null && !temp.isWaitingEmpty()){
            return temp.ready();
        } else {
            ParkingLot tempLot = ParkingLots.traverseGreaterThanAndReady(capacity);
            return tempLot != null ? tempLot.ready() : new int[]{-1};
        }
    }

    // Distributes the load to trucks in the parking lot based on their capacity
    public ArrayList<int[]> load(int loadCapacity, int loadAmount){
        ArrayList<int[]> results = new ArrayList<>();
        int remainingLoad = loadAmount;
        ParkingAvlTree.AvlNode tempNode = ParkingLots.searchNode(loadCapacity);

        if(tempNode == null){
            // If no exact match, distribute load to trucks in larger lots
            results = ParkingLots.TraverseGreaterThanAndLoad(loadCapacity, remainingLoad);
        } else {
            ParkingLot tempLot = tempNode.data;

            // Load trucks in the ready section until the load is exhausted or trucks are full
            while(!tempLot.isReadyEmpty() && remainingLoad > 0){
                int loaded = Math.min(tempLot.capacity, remainingLoad);
                int[] result = tempLot.loadTruck(loaded, this);
                results.add(result);
                remainingLoad -= loaded;
            }

            // If load remains, continue distribution in larger lots
            if(remainingLoad > 0){
                results.addAll(ParkingLots.TraverseGreaterThanAndLoad(loadCapacity, remainingLoad));
            }
        }
        return results;
    }

    // Counts trucks in lots with capacity constraints greater than the specified value
    public int count(int countCapacity){
        return ParkingLots.countGreaterThan(countCapacity);
    }

    // Prints parking lots with capacity constraints greater than the specified value
    public void printGreaterThan(int capacity){
        System.out.print("Lots Greater Than " + capacity + ": ");
        ParkingLots.traverseGreaterThan(capacity);
    }

    // Prints parking lots with capacity constraints less than the specified value
    public void printLessThan(int capacity){
        System.out.print("Lots Less Than " + capacity + ": ");
        ParkingLots.traverseLessThan(capacity);
    }

    // Prints all parking lots in ascending order by their capacity constraint
    public void printInOrder(){
        System.out.print("Lots In Order: ");
        ParkingLots.inOrderTraversal();
    }
}