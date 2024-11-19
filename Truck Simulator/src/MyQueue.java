public class MyQueue<T> {

    private MyLinkedList<T> list;  // Linked list to store queue elements

    // Initializes an empty queue
    public MyQueue() {
        list = new MyLinkedList<>();
    }

    // Adds an element to the back of the queue
    public void enqueue(T data) {
        list.add(data);
    }

    // Removes and returns the front element of the queue
    public T dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }
        return list.remove(0);
    }

    // Returns the front element without removing it
    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }
        return list.getNode(0).data;
    }

    // Checks if the queue is empty
    public boolean isEmpty() {
        return list.isEmpty();
    }

    // Returns the number of elements in the queue
    public int size() {
        return list.getSize();
    }

    // Returns a string representation of the queue
    public String toString() {
        return list.toString();
    }
}