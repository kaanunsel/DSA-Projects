public class MyLinkedList<T> {
    
    // Node class representing each element in the linked list
    public class Node<E> {
        E data;
        Node<E> prev;
        Node<E> next;

        public Node(E data, Node<E> prev, Node<E> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }
    }
    
    Node<T> header;  // First node (dummy)
    Node<T> tail;    // Last node (dummy)
    int size;        // Number of elements

    // Initializes an empty list
    public MyLinkedList() {
        clear();
    }

    // Clears the list, resetting header and tail nodes
    public void clear() {
        header = new Node<>(null, null, null);
        tail = new Node<>(null, header, null);
        header.next = tail;
        size = 0;
    }

    // Returns the number of elements in the list
    public int getSize() { 
        return this.size; 
    }

    // Checks if the list is empty
    public boolean isEmpty() { 
        return this.size == 0; 
    }

    // Returns the node at the specified index
    public Node<T> getNode(int idx) {
        if (idx < 0 || idx >= size) {
            throw new IndexOutOfBoundsException();
        }

        Node<T> current;

        // Start from header if index is in the first half
        if (idx < size / 2) {
            current = header.next;
            for (int i = 0; i < idx; i++) {
                current = current.next;
            }
        } 
        // Start from tail if index is in the second half
        else {
            current = tail;
            for (int i = size; i > idx; i--) {
                current = current.prev;
            }
        }

        return current;
    }

    // Adds a new node with data `d` before the specified node `n`
    public void addBefore(Node<T> n, T d) {
        Node<T> newNode = new Node<>(d, n.prev, n);
        newNode.prev.next = newNode;
        n.prev = newNode;
        size++;
    }

    // Adds a new node with data `d` at the end of the list
    public void add(T d) {
        addBefore(tail, d);
    }

    // Adds a new node with data `d` at the specified index
    public void add(int idx, T d) {
        addBefore(getNode(idx), d);
    }
    
    // Replaces data at the specified index with new data `d`
    public T set(int idx, T d) {
        Node<T> n = getNode(idx);
        T oldValue = n.data;
        n.data = d;
        return oldValue;
    }

    // Removes node at the specified index and returns its data
    public T remove(int idx) {
        Node<T> n = getNode(idx);
        T removed_data = n.data;
        n.prev.next = n.next;
        n.next.prev = n.prev;
        size--;
        return removed_data;
    }

    // Returns a string representation of the list
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
}