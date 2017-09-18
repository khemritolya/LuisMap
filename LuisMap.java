/**
 * LuisMap is a type of HashMap, written by Luis Hoderlein
 *
 * I give no warranty to it working in any way as intended.
 *
 * @author Luis Hoderlein
 *
 * @param <K> the key class, all keys must extend K
 * @param <V> the value class, all values must extend V
 */

public class LuisMap<K, V> {
    /**
     * Default values
     */
    private static final int DEFAULT_MAX_CAPACITY = 6;
    private static final float DEFAULT_FILLED_CAPACITY_RATIO = 0.5f;
    private static final float DEFAULT_GROWTH_RATIO = 1.2f;

    private class KVPair<K, V> {
        private K key;
        private V value;

        private KVPair(K key, V value) {
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

    public LuisMap(int capacity, float filledCapacityRatio, float growthRatio) {
        if (capacity <= 0)
            throw new IllegalArgumentException("Capacity too low");
        if (filledCapacityRatio < 0.001f)
            throw new IllegalArgumentException("Filled : Capacity ratio too low");
        if (growthRatio < 1.001f)
            throw new IllegalArgumentException("");

        this.capacity = capacity;
        this.filled = 0;

        this.filledCapacityRatio = filledCapacityRatio;
        this.growthRatio = growthRatio;

        this.kvPairs = new KVPair[capacity];
    }

    public LuisMap() {
        this(DEFAULT_MAX_CAPACITY, DEFAULT_FILLED_CAPACITY_RATIO, DEFAULT_GROWTH_RATIO);
    }

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

    public int getLength() {
        return this.filled;
    }

    public int getCapacity() {
        return this.capacity;
    }

    @Override
    public String toString() {
        return Arrays.toString(kvPairs);
    }

    private int mod(int a, int b) {
        if (a >= 0)
            return a % b;
        else
            return b + (a % b);
    }

    public static void main(String[] args) {
        LuisMap<String, Integer> test = new LuisMap<>();
        test.add(new String("This is test!!!"), new Integer(100));
        test.add(new String("This is also a test"), new Integer(210));
        System.out.println(test);
        System.out.println(test.get(new String("This is test!!!")));
        System.out.println(test.get(new String("This is also a test")));
        System.out.println(test.remove(new String("This is test!!!")));
        System.out.println(test);

        try{new Thread().sleep(1000);}catch(Exception e){}

        //System.out.println(test.get(new String("This is test")));
        //System.out.println(test.remove(new String("This is test")));
        test.add(new String("WELP"), new Integer(14));
        System.out.println(test);
        test.add(new String("OP"), new Integer(15));
        System.out.println(test);
        System.out.println(test.getCapacity());
        System.out.println(test.getLength());
        System.out.println(new String("OP").hashCode() % 6);

        try{new Thread().sleep(1000);}catch(Exception e){}
        test.remove(new String("OP"));
        System.out.println(test);
        test.add(new String("OP"), new Integer(15));
        System.out.println(test);
    }
}
