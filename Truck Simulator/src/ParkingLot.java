public class ParkingLot implements Comparable<ParkingLot> {
    int capacity;
    int truck_limit;
    int truck_count;
    MyQueue<Truck> waitSection;
    MyQueue<Truck> readySection;

    // Constructor with only capacity
    public ParkingLot(int capacity) {
        this.capacity = capacity;
    }

    // Constructor with capacity and truck limit
    public ParkingLot(int capacity, int truck_limit) {
        this.capacity = capacity;
        this.truck_limit = truck_limit;
        this.truck_count = 0;
        this.waitSection = new MyQueue<>();
        this.readySection = new MyQueue<>();
    }

    // Compare lots by capacity for AVL tree order
    @Override
    public int compareTo(ParkingLot other) {
        return Integer.compare(this.capacity, other.capacity);
    }

    // Adds a truck to the waiting section by ID and capacity
    public int addTruck(int id, int cap) {
        Truck newTruck = new Truck(id, cap);
        waitSection.enqueue(newTruck);
        truck_count++;
        return this.capacity;
    }

    // Adds an existing truck to the waiting section
    public int addTruck(Truck truck) {
        waitSection.enqueue(truck);
        truck_count++;
        return this.capacity;
    }

    // Moves the first truck from waiting to ready section
    public int[] ready() {
        Truck temp = waitSection.dequeue();
        readySection.enqueue(temp);
        return new int[]{temp.ID, capacity};
    }

    // Loads specified amount onto a truck in the ready section
    public int[] loadTruck(int loaded, FleetManagement fleet) {
        Truck tempTruck = readySection.dequeue();
        truck_count--;

        // Update truck load, reset if max capacity reached
        tempTruck.loadedAmount += loaded;
        if (tempTruck.loadedAmount == tempTruck.max_capacity) {
            tempTruck.loadedAmount = 0;
        }

        // Place truck in appropriate lot after loading
        int placement = fleet.addTruck(tempTruck);
        return new int[]{tempTruck.ID, placement};
    }

    // Checks if lot is at full truck capacity
    public boolean isFull() {
        return truck_count == truck_limit;
    }

    // Checks if waiting section is empty
    public boolean isWaitingEmpty() {
        return waitSection.isEmpty();
    }

    // Checks if ready section is empty
    public boolean isReadyEmpty() {
        return readySection.isEmpty();
    }
}