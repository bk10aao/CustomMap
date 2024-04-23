import java.util.Arrays;
import java.util.LinkedList;

public class CustomMap<T> {

    private T[] map;
    private final T key;
    private final T type;

    private int mapSize = 16;
    private int size = 0;
    private static double LOAD_FACTOR = 0.75;
    private int primesIndex = 0;

    public CustomMap(T key, T value) {
        this.map = (T[]) new Object[mapSize];
        this.key = key;
        this.type = value;
    }

    public void clear() {
        mapSize = 16;
        size = 0;
        this.map = (T[]) new Object[mapSize];
    }

    public boolean containsValue(T value) {
        if(value.getClass() == type) return Arrays.stream(map).anyMatch(item -> item != null && item.equals(value));
        return false;
    }

    public T get(T key) {
        return key.getClass() == this.key ? map[Math.abs(key.hashCode()) % mapSize] : null;
    }

    public T put(T key, T value) {
        T previous = null;
        if(key.getClass() == this.key && value.getClass() == this.type) {
            int index = Math.abs(key.hashCode()) % mapSize;
            previous = map[index];
            if (previous == null) {
                size++;
            }
            map[index] = value;
        }
        if((double)size / (double)mapSize > LOAD_FACTOR)
            expand();
        return previous;
    }

    public T remove(T key) {
        T item = null;
        if(key.getClass() == this.key) {
            int index = Math.abs(key.hashCode()) % mapSize;
            item = map[index];
            if (item != null) {
                size--;
                map[index] = null;
            }
        }
        return item;
    }

    public int size() {
        return size;
    }

    private void expand() {
        mapSize = primes[++primesIndex];
        T[] newMap = (T[]) new Object[mapSize];

        for (T item : map)
            if (item != null) {
                int index = Math.abs(key.hashCode()) % mapSize;
                newMap[index] = item;
            }
        map = newMap;
    }

    protected static final int[] primes = { 17, 23, 29, 37, 47, 59, 71, 89, 107, 131, 163, 197, 239, 293, 353, 431, 521, 631, 761, 919,
            1103, 1327, 1597, 1931, 2333, 2801, 3371, 4049, 4861, 5839, 7013, 8419, 10103, 12143, 14591,
            17519, 21023, 25229, 30293, 36353, 43627, 52361, 62851, 75431, 90523, 108631, 130363, 156437,
            187751, 225307, 270371, 324449, 389357, 467237, 560689, 672827, 807403, 968897, 1162687, 1395263,
            1674319, 2009191, 2411033, 2893249, 3471899, 4166287, 4999559, 5999471, 7199369 };
}