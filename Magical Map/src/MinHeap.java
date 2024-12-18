import java.util.ArrayList;

/**
 * A generic MinHeap implementation that maintains a binary heap structure.
 * This heap ensures that the smallest element is always at the root.
 *
 * @param <T> The type of elements stored in the heap, which must implement {@link Comparable}.
 */
public class MinHeap<T extends Comparable<T>> {

    /** The internal list used to store heap elements. */
    public ArrayList<T> heap;

    /**
     * Constructs an empty MinHeap.
     */
    public MinHeap() {
        heap = new ArrayList<>();
    }

    /**
     * Adds an element to the heap and restores the heap property.
     *
     * @param element The element to add to the heap.
     */
    public void add(T element) {
        heap.add(element);           // Add the element to the end of the list
        heapifyUp(heap.size() - 1);  // Restore heap property from the last element upward
    }

    /**
     * Removes and returns the minimum element (root) of the heap.
     *
     * @return The smallest element in the heap.
     * @throws IllegalStateException if the heap is empty.
     */
    public T extractMin() {
        if (heap.isEmpty()) {
            throw new IllegalStateException("Heap is empty");
        }
        T min = heap.get(0);                      // Store the root element
        T lastElement = heap.remove(heap.size() - 1); // Remove the last element
        if (!heap.isEmpty()) {
            heap.set(0, lastElement);             // Replace the root with the last element
            heapifyDown(0);                       // Restore the heap property downward
        }
        return min;
    }

    /**
     * Returns the minimum element (root) without removing it.
     *
     * @return The smallest element in the heap.
     * @throws IllegalStateException if the heap is empty.
     */
    public T peek() {
        if (heap.isEmpty()) {
            throw new IllegalStateException("Heap is empty");
        }
        return heap.get(0);
    }

    /**
     * Returns the number of elements currently in the heap.
     *
     * @return The size of the heap.
     */
    public int size() {
        return heap.size();
    }

    /**
     * Restores the heap property by moving an element up the heap.
     *
     * @param index The index of the element to move up.
     */
    private void heapifyUp(int index) {
        while (index > 0) {
            int parentIndex = (index - 1) / 2;  // Calculate the parent index
            // Compare current element with its parent
            if (heap.get(index).compareTo(heap.get(parentIndex)) < 0) {
                swap(index, parentIndex);       // Swap if the current element is smaller
                index = parentIndex;            // Move up to the parent index
            } else {
                break;  // Stop when the heap property is satisfied
            }
        }
    }

    /**
     * Restores the heap property by moving an element down the heap.
     *
     * @param index The index of the element to move down.
     */
    private void heapifyDown(int index) {
        int size = heap.size();
        while (index < size) {
            int leftChild = 2 * index + 1;      // Left child index
            int rightChild = 2 * index + 2;     // Right child index
            int smallest = index;              // Assume current index is smallest

            // Compare with left child
            if (leftChild < size && heap.get(leftChild).compareTo(heap.get(smallest)) < 0) {
                smallest = leftChild;
            }
            // Compare with right child
            if (rightChild < size && heap.get(rightChild).compareTo(heap.get(smallest)) < 0) {
                smallest = rightChild;
            }

            // If the smallest element is not the current index, swap and continue
            if (smallest != index) {
                swap(index, smallest);
                index = smallest;
            } else {
                break;  // Stop when the heap property is satisfied
            }
        }
    }

    /**
     * Swaps two elements in the heap at the specified indices.
     *
     * @param i The index of the first element to swap.
     * @param j The index of the second element to swap.
     */
    private void swap(int i, int j) {
        T temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }
}