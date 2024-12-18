import java.util.ArrayList;

/**
 * A custom implementation of a hash table using separate chaining for collision resolution.
 *
 * @param <K> The type of keys maintained by this hash table.
 * @param <V> The type of mapped values associated with the keys.
 */
public class MyHashTable<K, V> {

    /**
     * Inner class to represent a key-value pair stored in the hash table.
     *
     * @param <Key>   The type of the key.
     * @param <Value> The type of the value.
     */
    private class Entry<Key, Value> {
        Key key;
        Value value;

        /**
         * Constructs an Entry object with a key and its associated value.
         *
         * @param key   The key of the entry.
         * @param value The value associated with the key.
         */
        public Entry(Key key, Value value) {
            this.key = key;
            this.value = value;
        }

        /**
         * Checks equality of two Entry objects based on their keys.
         *
         * @param obj The object to compare with this entry.
         * @return {@code true} if the keys are equal; {@code false} otherwise.
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;

            @SuppressWarnings("unchecked")
            Entry<K, V> entry = (Entry<K, V>) obj;

            return key.equals(entry.key);
        }
    }

    /** The buckets array, where each bucket is a linked list to handle collisions. */
    private ArrayList<MyLinkedList<Entry<K, V>>> buckets;

    /** The current number of key-value pairs stored in the hash table. */
    private int size;

    /** Default initial capacity of the hash table. */
    private static final int DEFAULT_CAPACITY = 16;

    /** The load factor threshold for resizing the hash table. */
    private static final double LOAD_FACTOR = 0.75;

    /**
     * Default constructor that initializes the hash table with the default capacity.
     */
    public MyHashTable() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Constructs a hash table with a specified initial capacity.
     *
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
     * Computes the bucket index for a given key based on its hash code.
     *
     * @param key The key for which to compute the bucket index.
     * @return The index of the bucket where the key-value pair is stored.
     */
    private int getBucketIndex(K key) {
        return Math.abs(key.hashCode()) % buckets.size();
    }

    /**
     * Adds or updates a key-value pair in the hash table.
     * If the key already exists, its associated value is updated.
     *
     * @param key   The key.
     * @param value The value to associate with the key.
     */
    public void put(K key, V value) {
        int index = getBucketIndex(key);
        MyLinkedList<Entry<K, V>> bucket = buckets.get(index);

        // Check for existing key and update value
        for (Entry<K, V> entry : bucket) {
            if (entry.key.equals(key)) {
                entry.value = value; // Update existing value
                return;
            }
        }

        // Add new entry if key does not exist
        bucket.add(new Entry<>(key, value));
        size++;

        // Resize table if load factor is exceeded
        if ((double) size / buckets.size() > LOAD_FACTOR) {
            resize();
        }
    }

    /**
     * Retrieves the value associated with a given key.
     *
     * @param key The key to search for.
     * @return The value associated with the key, or {@code null} if the key is not found.
     */
    public V get(K key) {
        int index = getBucketIndex(key);
        MyLinkedList<Entry<K, V>> bucket = buckets.get(index);

        // Search for the key in the bucket
        for (Entry<K, V> entry : bucket) {
            if (entry.key.equals(key)) {
                return entry.value;
            }
        }

        return null; // Key not found
    }

    /**
     * Removes a key-value pair from the hash table.
     *
     * @param key The key to remove.
     * @return {@code true} if the pair was removed, {@code false} if the key was not found.
     */
    public boolean remove(K key) {
        int index = getBucketIndex(key);
        MyLinkedList<Entry<K, V>> bucket = buckets.get(index);

        // Search for and remove the entry
        for (Entry<K, V> entry : bucket) {
            if (entry.key.equals(key)) {
                bucket.removeByValue(entry);
                size--;
                return true;
            }
        }

        return false; // Key not found
    }

    /**
     * Checks if the hash table contains a specific key.
     *
     * @param key The key to check.
     * @return {@code true} if the key exists, {@code false} otherwise.
     */
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    /**
     * Returns the number of key-value pairs in the hash table.
     *
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
     * Rehashes all entries into a new array of buckets with double the capacity.
     */
    private void resize() {
        ArrayList<MyLinkedList<Entry<K, V>>> oldBuckets = buckets;
        buckets = new ArrayList<>();

        // Double the number of buckets
        for (int i = 0; i < oldBuckets.size() * 2; i++) {
            buckets.add(new MyLinkedList<>());
        }

        size = 0; // Reset size to reinsert entries

        // Reinsert all entries into the new buckets
        for (MyLinkedList<Entry<K, V>> bucket : oldBuckets) {
            for (Entry<K, V> entry : bucket) {
                put(entry.key, entry.value);
            }
        }
    }

    /**
     * Returns a string representation of the hash table.
     *
     * @return A string showing all key-value pairs in the hash table.
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

    /**
     * Retrieves all keys present in the hash table.
     *
     * @return An ArrayList containing all keys.
     */
    public ArrayList<K> getKeys() {
        ArrayList<K> keys = new ArrayList<>();
        for (MyLinkedList<Entry<K, V>> bucket : buckets) {
            for (Entry<K, V> entry : bucket) {
                keys.add(entry.key);
            }
        }
        return keys;
    }
}