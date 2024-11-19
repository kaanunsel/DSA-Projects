import java.util.ArrayList;

/**
 * A generic MaxHeap implementation that supports elements of any type
 * implementing the Comparable interface. Ensures that the maximum element
 * is always at the root of the heap.
 * @param <T> The type of elements stored in the heap, must be Comparable.
 */
public class MaxHeap<T extends Comparable<T>> {
    private ArrayList<T> heap; // Internal list to store heap elements

    /**
     * Constructor to initialize the MaxHeap.
     */
    public MaxHeap() {
        heap = new ArrayList<>(); // Initialize the underlying array list
    }

    /**
     * Adds an element to the heap and restores the heap property.
     * @param element The element to be added.
     */
    public void add(T element) {
        heap.add(element); // Add the new element at the end
        heapifyUp(heap.size() - 1); // Restore the heap property by moving the element up
    }

    /**
     * Removes and returns the maximum element (root) of the heap.
     * @return The maximum element in the heap.
     * @throws IllegalStateException If the heap is empty.
     */
    public T extractMax() {
        if (heap.isEmpty()) {
            throw new IllegalStateException("Heap is empty");
        }
        T max = heap.get(0); // The root is the maximum element
        T lastElement = heap.remove(heap.size() - 1); // Remove the last element
        if (!heap.isEmpty()) {
            heap.set(0, lastElement); // Replace the root with the last element
            heapifyDown(0); // Restore the heap property by moving the element down
        }
        return max;
    }

    /**
     * Returns the maximum element without removing it.
     * @return The maximum element in the heap.
     * @throws IllegalStateException If the heap is empty.
     */
    public T peek() {
        if (heap.isEmpty()) {
            throw new IllegalStateException("Heap is empty");
        }
        return heap.get(0); // The root is the maximum element
    }

    /**
     * Returns the current size of the heap.
     * @return The number of elements in the heap.
     */
    public int size() {
        return heap.size();
    }

    /**
     * Restores the heap property by moving an element up the tree.
     * @param index The index of the element to be moved up.
     */
    private void heapifyUp(int index) {
        while (index > 0) {
            int parentIndex = (index - 1) / 2; // Calculate the parent's index
            if (heap.get(index).compareTo(heap.get(parentIndex)) > 0) {
                swap(index, parentIndex); // Swap with the parent if it's larger
                index = parentIndex; // Move up to the parent's position
            } else {
                break; // Heap property is satisfied
            }
        }
    }

    /**
     * Restores the heap property by moving an element down the tree.
     * @param index The index of the element to be moved down.
     */
    private void heapifyDown(int index) {
        int size = heap.size();
        while (index < size) {
            int leftChild = 2 * index + 1; // Calculate the index of the left child
            int rightChild = 2 * index + 2; // Calculate the index of the right child
            int largest = index; // Assume the current index holds the largest element

            // Check if the left child is larger than the current element
            if (leftChild < size && heap.get(leftChild).compareTo(heap.get(largest)) > 0) {
                largest = leftChild;
            }

            // Check if the right child is larger than the current largest
            if (rightChild < size && heap.get(rightChild).compareTo(heap.get(largest)) > 0) {
                largest = rightChild;
            }

            // If the largest element is not at the current index, swap and continue
            if (largest != index) {
                swap(index, largest);
                index = largest; // Move down to the position of the largest child
            } else {
                break; // Heap property is satisfied
            }
        }
    }

    /**
     * Swaps two elements in the heap.
     * @param i The index of the first element.
     * @param j The index of the second element.
     */
    private void swap(int i, int j) {
        T temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }
}