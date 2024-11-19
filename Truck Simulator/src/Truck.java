public class Truck {
    int ID;                // Unique truck ID
    int max_capacity;      // Maximum capacity of the truck
    int loadedAmount;      // Current load amount
    ParkingLot lot;        // Current parking lot of the truck

    // Initializes truck with ID and max capacity, starts with no load and no lot assigned
    public Truck(int ID, int max_capacity) {
        this.ID = ID;
        this.loadedAmount = 0;       // Initial load is zero
        this.max_capacity = max_capacity;
        this.lot = null;             // Initially not assigned to any lot
    }
}