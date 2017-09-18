package random;

import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * @author Luis Hoderlein
 *
 * LuisMap is based on the idea of the HashMap.
 * It is written and maintaied by Luis Hoderlein.
 *
 * I give no warranty to it working in any way as intended.
 * If it breaks, you may in no way fault me.
 *
 * By using this software, you agree to the above terms.
 *
 * "I am innocent of this man's blood. 
 * The responsibility is yours!"
 * 
 * - Pontius Pilate
 *
 * @param <K> the key class, all keys must extend K
 * @param <V> the value class, all values must extend V
 */

public class LuisMap<K, V> {
    
    /**
     * Default values for the settings of the LuisMap
     */
    private static final int DEFAULT_MAX_CAPACITY = 6;
    private static final float DEFAULT_FILLED_CAPACITY_RATIO = 0.5f;
    private static final float DEFAULT_GROWTH_RATIO = 1.2f;

    /**
     * A class that represents a key-value pair 
     * @param <k> the key of the key-value pair
     * @param <v> the value of the key-value pair 
     */
    private class KVPair<k, v> {
        private k key;
        private v value;

        private KVPair(k key, v value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            if (key != null)
                return key + ": " + value;
            else
                return "Empty Pair";
        }
    }

    private final KVPair<K, V> emptyPair = new KVPair<>(null, null);

    private KVPair<K,V>[] kvPairs = new KVPair[1];

    private int capacity;
    private int filled;
    private float filledCapacityRatio;
    private float growthRatio;

    /**
     * Main Constructor
     *
     * @param capacity how much space the new LuisMap should start with
     * @param filledCapacityRatio the ratio of filled : capacity that will trigger grow()
     * @param growthRatio how much grow() should grow your LuisMap
     */
    public LuisMap(int capacity, float filledCapacityRatio, float growthRatio) {
        if (capacity <= 0)
            throw new IllegalArgumentException("Capacity too low");
        if (filledCapacityRatio < 0.001f)
            throw new IllegalArgumentException("Filled : Capacity ratio too low");
        if (growthRatio < 1.001f)
            throw new IllegalArgumentException("Growth Ratio too low");

        this.capacity = capacity;
        this.filled = 0;

        this.filledCapacityRatio = filledCapacityRatio;
        this.growthRatio = growthRatio;

        this.kvPairs = new KVPair[capacity];
    }

    /**
     * Default Constructor
     */
    public LuisMap() {this(DEFAULT_MAX_CAPACITY, DEFAULT_FILLED_CAPACITY_RATIO, DEFAULT_GROWTH_RATIO);}

    /**
     * Add a new key-value pair to the Luis Map
     *
     * @param key the key of the key-value pair
     * @param value the value of the key-value pair
     */
    public void add(K key, V value) {
        if (key == null)
            throw new IllegalArgumentException("Key may not be null!");

        int position = mod(key.hashCode(), this.capacity);

        while (kvPairs[position] != null && kvPairs[position] != emptyPair) {
            position = mod(position + 1, this.capacity);
        }

        kvPairs[position] = new KVPair<>(key, value);
        filled++;

        if (1.0f * filled / capacity >= filledCapacityRatio) {
            grow();
        }
    }

    /**
     * Grow the LuisMap as specified by growthRatio
     */
    private void grow() {
        KVPair<K, V>[] tmpKVPairs = new KVPair[(int)(capacity * growthRatio)];

        for (int i = 0; i < capacity; i++) {
            if (kvPairs[i] != null && kvPairs[i] != emptyPair) {
                int position = mod(kvPairs[i].key.hashCode(), tmpKVPairs.length);

                while (tmpKVPairs[position] != null) {
                    position = mod(position + 1, tmpKVPairs.length);
                }

                tmpKVPairs[position] = new KVPair<>(kvPairs[i].key, kvPairs[i].value);
            }
        }

        this.kvPairs = tmpKVPairs;
        this.capacity = tmpKVPairs.length;
    }

    /**
     * Get the value associated with a key
     * i.e. look up a key-value pair
     *
     * @param key A Key to look up
     * @return the value that that key refers to
     */
    public V get(K key) {
        if (key == null)
            throw new IllegalArgumentException("Key may not be null!");

        int position = mod(key.hashCode(), this.capacity);
        while (kvPairs[position] != null && !key.equals(kvPairs[position].key)) {
            System.out.println(position);
            position = mod(position + 1, this.capacity);

            if (position == mod(key.hashCode(), this.capacity))
                throw new NoSuchElementException("There exists no such key!");
        }

        if (kvPairs[position] == null)
            throw new NoSuchElementException("There exists no such key!");

        return kvPairs[position].value;
    }

    /**
     * Get the value associated with a key
     * Then remove it from the Luis Map
     *
     * @param key A Key to look up
     * @return The value that the key refers to
     */
    public V remove(K key) {
        if (key == null)
            throw new IllegalArgumentException("Key may not be null!");

        int position = mod(key.hashCode(), this.capacity);
        while (kvPairs[position] != null && !key.equals(kvPairs[position].key)) {
            position = mod(position + 1, this.capacity);

            if (position == mod(key.hashCode(), this.capacity))
                throw new IllegalArgumentException("There exists no such key!");
        }

        if (kvPairs[position] == null)
            throw new NoSuchElementException("There exists no such key! @ pos: " + position);

        V res = kvPairs[position].value;
        kvPairs[position] = emptyPair;

        filled--;
        return res;
    }

    /**
     * A getter for the length of the Luis Map
     * @return the number of key-value pairs stored
     */
    public int getLength() {
        return this.filled;
    }

    /**
     * A getter for the capacity of the Luis Map
     * @return The maximum capacity of key-value pairs able to be stored
     */
    public int getCapacity() {
        return this.capacity;
    }

    @Override
    public String toString() {return Arrays.toString(kvPairs);}

    private int mod(int a, int b) {
        if (a >= 0)
            return a % b;
        else
            return b + (a % b);
    }
}
