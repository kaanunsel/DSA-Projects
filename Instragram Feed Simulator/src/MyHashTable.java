import java.util.ArrayList;

/**
 * A custom implementation of a hash table using separate chaining for collision resolution.
 * @param <K> The type of keys maintained by this hash table.
 * @param <V> The type of mapped values.
 */
public class MyHashTable<K, V> {

    /**
     * Inner class to represent a key-value pair.
     * @param <Key>   The type of the key.
     * @param <Value> The type of the value.
     */
    private class Entry<Key, Value> {
        Key key;
        Value value;

        public Entry(Key key, Value value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;

            @SuppressWarnings("unchecked")
            Entry<K, V> entry = (Entry<K, V>) obj;

            return key.equals(entry.key);
        }
    }

    private ArrayList<MyLinkedList<Entry<K, V>>> buckets; // Array of buckets (chaining)
    private int size; // Number of key-value pairs in the table
    private static final int DEFAULT_CAPACITY = 16; // Default initial capacity
    private static final double LOAD_FACTOR = 0.75; // Threshold for resizing

    /**
     * Default constructor to initialize the hash table with the default capacity.
     */
    public MyHashTable() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Constructor to initialize the hash table with a specified initial capacity.
     * @param initialCapacity The initial number of buckets.
     */
    public MyHashTable(int initialCapacity) {
        buckets = new ArrayList<>();
        for (int i = 0; i < initialCapacity; i++) {
            buckets.add(new MyLinkedList<>());
        }
        size = 0;
    }

    /**
     * Computes the index of the bucket for a given key using its hash code.
     * @param key The key to hash.
     * @return The index of the bucket.
     */
    private int getBucketIndex(K key) {
        return Math.abs(key.hashCode()) % buckets.size();
    }

    /**
     * Adds or updates a key-value pair in the hash table.
     * @param key   The key.
     * @param value The value to associate with the key.
     */
    public void put(K key, V value) {
        int index = getBucketIndex(key);
        MyLinkedList<Entry<K, V>> bucket = buckets.get(index);

        // Check if the key already exists and update its value
        for (Entry<K, V> entry : bucket) {
            if (entry.key.equals(key)) {
                entry.value = value;
                return;
            }
        }

        // Add a new entry if the key doesn't exist
        bucket.add(new Entry<>(key, value));
        size++;

        // Resize the hash table if the load factor is exceeded
        if ((double) size / buckets.size() > LOAD_FACTOR) {
            resize();
        }
    }

    /**
     * Retrieves the value associated with a key.
     * @param key The key to search for.
     * @return The value associated with the key, or null if the key is not found.
     */
    public V get(K key) {
        int index = getBucketIndex(key);
        MyLinkedList<Entry<K, V>> bucket = buckets.get(index);

        for (Entry<K, V> entry : bucket) {
            if (entry.key.equals(key)) {
                return entry.value;
            }
        }

        return null; // Key not found
    }

    /**
     * Removes a key-value pair from the hash table by its key.
     * @param key The key to remove.
     * @return True if the pair was removed, false if the key was not found.
     */
    public boolean remove(K key) {
        int index = getBucketIndex(key);
        MyLinkedList<Entry<K, V>> bucket = buckets.get(index);

        for (Entry<K, V> entry : bucket) {
            if (entry.key.equals(key)) {
                bucket.removeByValue(entry); // Remove the entry from the bucket
                size--;
                return true;
            }
        }

        return false; // Key not found
    }

    /**
     * Checks if the hash table contains a specific key.
     * @param key The key to check.
     * @return True if the key exists, false otherwise.
     */
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    /**
     * Returns the number of key-value pairs in the hash table.
     * @return The size of the hash table.
     */
    public int size() {
        return size;
    }

    /**
     * Clears all key-value pairs from the hash table.
     */
    public void clear() {
        for (MyLinkedList<Entry<K, V>> bucket : buckets) {
            bucket.clear(); // Clear each bucket
        }
        size = 0;
    }

    /**
     * Resizes the hash table when the load factor is exceeded.
     * Rehashes all existing entries into the new buckets.
     */
    private void resize() {
        ArrayList<MyLinkedList<Entry<K, V>>> oldBuckets = buckets;
        buckets = new ArrayList<>();

        // Double the number of buckets
        for (int i = 0; i < oldBuckets.size() * 2; i++) {
            buckets.add(new MyLinkedList<>());
        }

        size = 0; // Reset size to reinsert entries

        // Reinsert all existing entries into the new buckets
        for (MyLinkedList<Entry<K, V>> bucket : oldBuckets) {
            for (Entry<K, V> entry : bucket) {
                put(entry.key, entry.value);
            }
        }
    }

    /**
     * Returns a string representation of the hash table.
     * @return A string showing all key-value pairs.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (MyLinkedList<Entry<K, V>> bucket : buckets) {
            for (Entry<K, V> entry : bucket) {
                sb.append(entry.key).append("=").append(entry.value).append(", ");
            }
        }
        if (size > 0) sb.setLength(sb.length() - 2); // Remove trailing comma
        sb.append("}");
        return sb.toString();
    }
}