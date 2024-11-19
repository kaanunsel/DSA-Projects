import java.util.NoSuchElementException;
import java.util.ArrayList;

public class ParkingAvlTree {

    // AVL Tree node definition
    public static class AvlNode {
        ParkingLot data;
        AvlNode left;
        AvlNode right;
        int height;

        // Node with only data (left and right set to null)
        AvlNode(ParkingLot d) {
            this(d, null, null);
        }

        // Node with data, left and right children
        AvlNode(ParkingLot data, AvlNode l_tree, AvlNode r_tree) {
            this.data = data;
            this.left = l_tree;
            this.right = r_tree;
            this.height = 0;
        }
    }

    private AvlNode root;
    private int remainingLoad;
    private ArrayList<int[]> loadResult;
    private FleetManagement fleet;

    // Initializes the AVL tree and links to fleet management
    public ParkingAvlTree(FleetManagement fleet) {
        this.fleet = fleet;
        root = null;
    }

    // Returns the height of a node, or -1 if null
    private int height(AvlNode n) {
        return n == null ? -1 : n.height;
    }

    // Inserts a parking lot into the AVL tree
    public void insert(ParkingLot d) {
        root = insert(d, root);
    }

    // Recursive insert with balancing
    private AvlNode insert(ParkingLot d, AvlNode n) {
        if (n == null) return new AvlNode(d);

        int comparison = d.compareTo(n.data);
        if (comparison < 0) {
            n.left = insert(d, n.left);
        } else if (comparison > 0) {
            n.right = insert(d, n.right);
        }

        return balance(n);
    }

    // Removes a parking lot by object
    public void remove(ParkingLot d) {
        root = remove(d, root);
    }

    // Recursive remove with balancing
    private AvlNode remove(ParkingLot d, AvlNode n) {
        if (n == null) return n;

        int comparison = d.compareTo(n.data);
        if (comparison < 0) {
            n.left = remove(d, n.left);
        } else if (comparison > 0) {
            n.right = remove(d, n.right);
        } else if (n.left != null && n.right != null) {
            n.data = findMin(n.right).data;
            n.right = remove(n.data, n.right);
        } else {
            n = (n.left != null) ? n.left : n.right;
        }
        return balance(n);
    }

    // Removes a parking lot by capacity
    public void remove(Integer d) {
        root = remove(d, root);
    }

    // Recursive remove by capacity with balancing
    private AvlNode remove(Integer d, AvlNode n) {
        if (n == null) return n;

        int comparison = d.compareTo(n.data.capacity);
        if (comparison < 0) {
            n.left = remove(d, n.left);
        } else if (comparison > 0) {
            n.right = remove(d, n.right);
        } else if (n.left != null && n.right != null) {
            n.data = findMin(n.right).data;
            n.right = remove(n.data, n.right);
        } else {
            n = (n.left != null) ? n.left : n.right;
        }
        return balance(n);
    }

    // Checks if the tree is empty
    public boolean isEmpty() {
        return root == null;
    }

    // Empties the tree
    public void makeEmpty() {
        root = null;
    }

    // Finds the minimum parking lot by capacity
    public ParkingLot findMin() {
        if (isEmpty()) throw new NoSuchElementException("Tree is empty");
        return findMin(root).data;
    }

    private AvlNode findMin(AvlNode n) {
        while (n != null && n.left != null) {
            n = n.left;
        }
        return n;
    }

    // Finds the maximum parking lot by capacity
    public ParkingLot findMax() {
        if (isEmpty()) throw new NoSuchElementException("Tree is empty");
        return findMax(root).data;
    }

    private AvlNode findMax(AvlNode n) {
        while (n != null && n.right != null) {
            n = n.right;
        }
        return n;
    }

    // Balances the AVL tree to maintain height property
    private AvlNode balance(AvlNode n) {
        if (n == null) return null;

        int deltaHeight = height(n.left) - height(n.right);

        if (deltaHeight > 1) {
            n = (height(n.left.left) >= height(n.left.right)) ? leftSingleRotate(n) : leftDoubleRotate(n);
        } else if (deltaHeight < -1) {
            n = (height(n.right.right) >= height(n.right.left)) ? rightSingleRotate(n) : rightDoubleRotate(n);
        }

        n.height = Math.max(height(n.left), height(n.right)) + 1;
        return n;
    }

    // Single left rotation for balancing
    private AvlNode leftSingleRotate(AvlNode n) {
        AvlNode leftChild = n.left;
        n.left = leftChild.right;
        leftChild.right = n;

        n.height = Math.max(height(n.left), height(n.right)) + 1;
        leftChild.height = Math.max(height(leftChild.left), n.height) + 1;

        return leftChild;
    }

    // Single right rotation for balancing
    private AvlNode rightSingleRotate(AvlNode n) {
        AvlNode rightChild = n.right;
        n.right = rightChild.left;
        rightChild.left = n;

        n.height = Math.max(height(n.left), height(n.right)) + 1;
        rightChild.height = Math.max(height(rightChild.right), n.height) + 1;

        return rightChild;
    }

    // Double left-right rotation for balancing
    private AvlNode leftDoubleRotate(AvlNode n) {
        n.left = rightSingleRotate(n.left);
        return leftSingleRotate(n);
    }

    // Double right-left rotation for balancing
    private AvlNode rightDoubleRotate(AvlNode n) {
        n.right = leftSingleRotate(n.right);
        return rightSingleRotate(n);
    }

    public boolean contains(ParkingLot d) {
        return contains(d, root);
    }
    
    // Checks if tree contains a parking lot with specified data
    private boolean contains(ParkingLot d, AvlNode n) {
        while (n != null) {
            int comparison = d.compareTo(n.data);
            if (comparison < 0) n = n.left;
            else if (comparison > 0) n = n.right;
            else return true;
        }
        return false;
    }
    
    public boolean contains(Integer d) {
        return contains(d, root);
    }
    
    // Checks if tree contains a parking lot with specified capacity
    private boolean contains(Integer d, AvlNode n) {
        while (n != null) {
            int comparison = d.compareTo(n.data.capacity);
            if (comparison < 0) n = n.left;
            else if (comparison > 0) n = n.right;
            else return true;
        }
        return false;
    }
    
    // In-order traversal of the AVL tree (left-root-right)
    public void inOrderTraversal() {
        inOrderTraversal(root);
    }

    private void inOrderTraversal(AvlNode node) {
        if (node != null) {
            inOrderTraversal(node.left);
            System.out.print(node.data.capacity + " ");
            inOrderTraversal(node.right);
        }
    }

    // Searches and returns the node with the specified parking lot data
    public AvlNode searchNode(ParkingLot data) {
        return searchNode(data, root);
    }

    private AvlNode searchNode(ParkingLot data, AvlNode node) {
        while (node != null) {
            int comparison = data.compareTo(node.data);

            if (comparison < 0)
                node = node.left;
            else if (comparison > 0)
                node = node.right;
            else
                return node;
        }
        return node;
    }

    // Searches and returns the node with the specified capacity
    public AvlNode searchNode(Integer data) {
        return searchNode(data, root);
    }

    private AvlNode searchNode(Integer data, AvlNode node) {
        while (node != null) {
            int comparison = data.compareTo(node.data.capacity);

            if (comparison < 0)
                node = node.left;
            else if (comparison > 0)
                node = node.right;
            else
                return node;
        }
        return node;
    }

    // Counts nodes with a capacity greater than the specified value
    public int countGreaterThan(Integer data) {
        return countGreaterThan(root, data);
    }

    private int countGreaterThan(AvlNode node, Integer data) {
        if (node == null) return 0;

        int comparison = data.compareTo(node.data.capacity);

        if (comparison < 0) {
            return countGreaterThan(node.left, data) + node.data.truck_count + countGreaterThan(node.right, data);
        } else {
            return countGreaterThan(node.right, data);
        }
    }

    // Traverses and prints nodes with capacity greater than the specified value
    public void traverseGreaterThan(Integer data) {
        traverseGreaterThan(root, data);
    }

    private void traverseGreaterThan(AvlNode node, Integer data) {
        if (node == null) return;

        int comparison = data.compareTo(node.data.capacity);

        if (comparison < 0) {
            traverseGreaterThan(node.left, data);
            System.out.print(node.data.capacity + " ");
            traverseGreaterThan(node.right, data);
        } else {
            traverseGreaterThan(node.right, data);
        }
    }

    // Traverses and prints nodes with capacity less than the specified value
    public void traverseLessThan(Integer data) {
        traverseLessThan(root, data);
    }

    private void traverseLessThan(AvlNode node, Integer data) {
        if (node == null) return;

        int comparison = data.compareTo(node.data.capacity);

        if (comparison > 0) {
            traverseLessThan(node.right, data);
            System.out.print(node.data.capacity + " ");
            traverseLessThan(node.left, data);
        } else {
            traverseLessThan(node.left, data);
        }
    }

    // Attempts to add a truck to the closest smaller capacity lot if available
    public int traverseLessThanAndAddTruck(int capacity, int truckID) {
        return traverseLessThanAndAddTruck(root, capacity, truckID);
    }

    private int traverseLessThanAndAddTruck(AvlNode node, int capacity, int truckID) {
        if (node == null) return -1;

        int comparison = Integer.compare(capacity, node.data.capacity);

        if (comparison > 0) {
            int result = traverseLessThanAndAddTruck(node.right, capacity, truckID);

            if (result != -1) return result;

            if (!node.data.isFull()) {
                node.data.addTruck(truckID, capacity); 
                return node.data.capacity;
            }

            return traverseLessThanAndAddTruck(node.left, capacity, truckID);
        } else {
            return traverseLessThanAndAddTruck(node.left, capacity, truckID);
        }
    }

    public int traverseLessThanAndAddTruck(int capacity, Truck truck) {
        return traverseLessThanAndAddTruck(root, capacity, truck);
    }

    private int traverseLessThanAndAddTruck(AvlNode node, int capacity, Truck truck) {
        if (node == null) return -1;

        int comparison = Integer.compare(capacity, node.data.capacity);

        if (comparison > 0) {
            int result = traverseLessThanAndAddTruck(node.right, capacity, truck);

            if (result != -1) return result;

            if (!node.data.isFull()) {
                node.data.addTruck(truck); 
                return node.data.capacity;
            }

            return traverseLessThanAndAddTruck(node.left, capacity, truck);
        } else {
            return traverseLessThanAndAddTruck(node.left, capacity, truck);
        }
    }

    // Traverses and finds the next larger capacity lot with a waiting truck available
    public ParkingLot traverseGreaterThanAndReady(int capacity) {
        return traverseGreaterThanAndReady(root, capacity);
    }

    private ParkingLot traverseGreaterThanAndReady(AvlNode node, int capacity) {
        if (node == null) return null;

        int comparison = Integer.compare(capacity, node.data.capacity);

        if (comparison < 0) {
            ParkingLot result = traverseGreaterThanAndReady(node.left, capacity);

            if (result != null) return result;

            if (!node.data.isWaitingEmpty()) {
                return node.data;
            }

            return traverseGreaterThanAndReady(node.right, capacity);
        } else {
            return traverseGreaterThanAndReady(node.right, capacity);
        }
    }

    // Loads trucks in nodes with capacity greater than specified, starting with remaining load
    public ArrayList<int[]> TraverseGreaterThanAndLoad(int capacity, int remainingLoad) {
        this.remainingLoad = remainingLoad;
        this.loadResult = new ArrayList<>();
        TraverseGreaterThanAndLoad(root, capacity);

        if (loadResult.isEmpty()) {
            loadResult.add(new int[]{-1});
        }

        return loadResult;
    }

    // Recursive helper method for loading trucks in larger lots
    private void TraverseGreaterThanAndLoad(AvlNode node, int capacity) {
        if (node == null || this.remainingLoad == 0) return;

        int comparison = Integer.compare(capacity, node.data.capacity);
        ParkingLot tempLot = node.data;

        // If current node capacity is greater
        if (comparison < 0) {
            TraverseGreaterThanAndLoad(node.left, capacity);  // Traverse left subtree first

            // Distribute load if there's remaining load and trucks in ready section
            if (this.remainingLoad > 0 && !tempLot.isReadyEmpty()) {
                while (!tempLot.isReadyEmpty() && this.remainingLoad > 0) {
                    int loaded = Math.min(tempLot.capacity, this.remainingLoad); 
                    int[] temp_result = tempLot.loadTruck(loaded, fleet);
                    loadResult.add(temp_result);
                    this.remainingLoad -= loaded;
                }
            }

            // Continue to right subtree if load remains
            if (this.remainingLoad > 0) {
                TraverseGreaterThanAndLoad(node.right, capacity);
            }
        } else {
            TraverseGreaterThanAndLoad(node.right, capacity);
        }
    }
}