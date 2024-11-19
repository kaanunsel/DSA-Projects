import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A custom doubly linked list implementation.
 * Supports common operations such as adding, removing, and iterating through elements.
 * @param <T> The type of elements stored in the linked list.
 */
public class MyLinkedList<T> implements Iterable<T> {

    /**
     * Inner class representing a node in the doubly linked list.
     * @param <E> The type of data stored in the node.
     */
    public class Node<E> {
        E data; // Data held by the node
        Node<E> prev; // Reference to the previous node
        Node<E> next; // Reference to the next node

        /**
         * Constructor to create a new node.
         * @param data The data to store in the node.
         * @param prev Reference to the previous node.
         * @param next Reference to the next node.
         */
        public Node(E data, Node<E> prev, Node<E> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }
    }

    private Node<T> header; // Sentinel node at the beginning of the list
    private Node<T> tail; // Sentinel node at the end of the list
    private int size; // Number of elements in the list

    /**
     * Constructor to initialize an empty linked list.
     */
    public MyLinkedList() {
        clear();
    }

    /**
     * Clears the linked list, resetting it to an empty state.
     * Initializes the header and tail sentinel nodes.
     */
    public void clear() {
        header = new Node<>(null, null, null);
        tail = new Node<>(null, header, null);
        header.next = tail;
        size = 0;
    }

    /**
     * Returns the number of elements in the list.
     * @return The size of the list.
     */
    public int getSize() {
        return this.size;
    }

    /**
     * Checks if the list is empty.
     * @return True if the list is empty, false otherwise.
     */
    public boolean isEmpty() {
        return this.size == 0;
    }

    /**
     * Retrieves the node at a specified index.
     * @param idx The index of the node to retrieve.
     * @return The node at the specified index.
     * @throws IndexOutOfBoundsException If the index is invalid.
     */
    public Node<T> getNode(int idx) {
        if (idx < 0 || idx >= size) {
            throw new IndexOutOfBoundsException();
        }

        Node<T> current;

        // Traverse from header if index is in the first half, otherwise from tail
        if (idx < size / 2) {
            current = header.next;
            for (int i = 0; i < idx; i++) {
                current = current.next;
            }
        } else {
            current = tail;
            for (int i = size; i > idx; i--) {
                current = current.prev;
            }
        }

        return current;
    }

    /**
     * Adds a new node with the specified data before the given node.
     * @param n The node before which to add the new node.
     * @param d The data to add.
     */
    public void addBefore(Node<T> n, T d) {
        Node<T> newNode = new Node<>(d, n.prev, n);
        newNode.prev.next = newNode;
        n.prev = newNode;
        size++;
    }

    /**
     * Adds a new element to the end of the list.
     * @param d The element to add.
     */
    public void add(T d) {
        addBefore(tail, d);
    }

    /**
     * Adds a new element at a specified index.
     * @param idx The index at which to add the element.
     * @param d The element to add.
     */
    public void add(int idx, T d) {
        addBefore(getNode(idx), d);
    }

    /**
     * Updates the data at a specified index.
     * @param idx The index of the node to update.
     * @param d The new data to set.
     * @return The old data that was replaced.
     */
    public T set(int idx, T d) {
        Node<T> n = getNode(idx);
        T oldValue = n.data;
        n.data = d;
        return oldValue;
    }

    /**
     * Removes the node at a specified index.
     * @param idx The index of the node to remove.
     * @return The data of the removed node.
     */
    public T removeByIdx(int idx) {
        return removeByNode(getNode(idx));
    }

    /**
     * Removes a specific node from the list.
     * @param n The node to remove.
     * @return The data of the removed node.
     */
    public T removeByNode(Node<T> n) {
        T removedData = n.data;
        n.prev.next = n.next;
        n.next.prev = n.prev;
        size--;
        return removedData;
    }

    /**
     * Removes the first node containing the specified value.
     * @param value The value to remove.
     * @return True if a node was removed, false otherwise.
     */
    public boolean removeByValue(T value) {
        Node<T> current = header.next;

        // Traverse the list to find the node with the specified value
        while (current != tail) {
            if (current.data.equals(value)) {
                current.prev.next = current.next;
                current.next.prev = current.prev;
                size--;
                return true;
            }
            current = current.next;
        }

        return false;
    }

    /**
     * Checks if the list contains the specified element.
     * @param d The element to check for.
     * @return True if the element is found, false otherwise.
     */
    public boolean contains(T d) {
        for (T element : this) {
            if (element.equals(d)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a string representation of the list.
     * @return A string showing the list's elements.
     */
    public String toString() {
        StringBuilder string = new StringBuilder();
        Node<T> current = header.next;
        string.append("[");
        while (current != tail) {
            string.append(current.data);
            if (current.next != tail) {
                string.append(", ");
            }
            current = current.next;
        }
        string.append("]");
        return string.toString();
    }

    /**
     * Provides an iterator for the linked list.
     * @return An iterator for traversing the list.
     */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current = header.next;

            @Override
            public boolean hasNext() {
                return current != tail;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T data = current.data;
                current = current.next;
                return data;
            }
        };
    }
}