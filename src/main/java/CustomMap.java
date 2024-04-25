import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;

public class CustomMap<T> {

    private LinkedList<MapEntry>[] map;
    private final T key;
    private final T type;

    private int mapSize;
    private int size = 0;
    private static final double LOAD_FACTOR = 0.75;
    private int primesIndex = 0;

    public CustomMap(final T key, final T value) {
        this.map = new LinkedList[primes[primesIndex]];
        this.mapSize = primes[0];
        this.key = key;
        this.type = value;
    }

    private CustomMap(final T key, final T value, final int mapSize) {
        this.map = new LinkedList[mapSize];
        this.mapSize = mapSize;
        this.key = key;
        this.type = value;
    }

    public void clear() {
        this.map = new LinkedList[primes[primesIndex]];
        this.mapSize = primes[0];
        this.primesIndex = 0;
        this.size = 0;
    }

    public boolean containsKey(T key) {
        LinkedList<MapEntry> indexedMapEntry = map[Math.abs(key.hashCode()) % mapSize];
        if(indexedMapEntry == null)
            return false;
        return indexedMapEntry.stream().anyMatch(entry -> entry.key.equals(key));
    }

    public boolean containsValue(final T value) {
        return Arrays.stream(map)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .anyMatch(entry -> entry.value.equals(value));
    }

    public T get(final T key) {
        if(key == null)
            throw new NullPointerException();
        LinkedList<MapEntry> indexedMapEntry = map[Math.abs(key.hashCode()) % mapSize];
        if(indexedMapEntry == null)
            return null;
        return indexedMapEntry.stream()
                                .filter(mapEntry -> mapEntry.key.equals(key))
                                .findFirst()
                                .map(mapEntry -> mapEntry.value)
                                .orElse(null);
    }

    public T getOrDefault(T key, T defaultValue) {
        if(key == null)
            throw new NullPointerException();
        LinkedList<MapEntry> indexedMapEntry = map[Math.abs(key.hashCode()) % mapSize];
        if(indexedMapEntry == null)
            return null;
        return indexedMapEntry.stream()
                .filter(mapEntry -> mapEntry.key.equals(key))
                .findFirst()
                .map(mapEntry -> mapEntry.value)
                .orElse(defaultValue);
    }

    public Set keySet() {
        Set<T> keySet = new HashSet<>();
        for(LinkedList<MapEntry> entryList : map) {
            if(entryList != null) {
                for(MapEntry entry: entryList) {
                    keySet.add(entry.key);
                }
            }
        }
        return keySet;
    }

    public T put(final T key, final T value) {
        if(!key.getClass().equals(this.key) || !value.getClass().equals(this.type))
            throw new IllegalArgumentException();
        if(key == null)
            throw new NullPointerException();
        int index = Math.abs(key.hashCode()) % mapSize;
        LinkedList<MapEntry> indexedEntry = map[index];
        T result = null;
        if(indexedEntry == null)
            addNewIndexEntry(key, value, index);
        else
            result = updateExistingLinkedList(index, key, value);
        if((double)size / (double)mapSize > LOAD_FACTOR)
            expand();
        return result;
    }

    public T putIfAbsent(final T key, final T value) {
        if(key == null || value == null)
            throw new NullPointerException();
        if(!containsKey(key))
            put(key, value);
        else
            return get(key);
        return null;
    }

    public T remove(final T key) {
        if(key == null)
            throw new NullPointerException();
        LinkedList<MapEntry> entryLinkedList = map[Math.abs(key.hashCode()) % mapSize];

        if(entryLinkedList != null)
            for (int i = 0; i < entryLinkedList.size(); i++) {
            MapEntry entry = entryLinkedList.get(i);
            if (entry.key.equals(key))
                return removeItem(entryLinkedList, i);
        }
        return null;
    }

    public boolean remove(final T key, final T value) {
        if(key == null || value == null)
            throw new NullPointerException();
        if(containsKey(key)) {
            if (map[key.hashCode() % mapSize].get(primesIndex).value.equals(value)) {
                map[key.hashCode() % mapSize].remove(key);
                return true;
            }
        }
        return false;
    }

    private T removeItem(final LinkedList<MapEntry> entryLinkedList, final int index) {
        MapEntry removed = entryLinkedList.remove(index);
        size--;
        if(mapSize > 17 && size <= mapSize / 4)
            reduce();
        return removed.value;
    }

    public int size() {
        return this.size;
    }

    private void addNewIndexEntry(final T key, final T value, final int index) {
        map[index] = new LinkedList<>();
        LinkedList<MapEntry> entryIndex = map[index];
        MapEntry mapEntry = new MapEntry(key, value);
        entryIndex.add(mapEntry);
        map[index] = entryIndex;
        size++;
    }

    private void expand() {
        mapSize = primes[++primesIndex];
        CustomMap<T> newMap = new CustomMap(this.key, this.type, mapSize);
        for (LinkedList<MapEntry> linkedList : map)
            if (linkedList != null)
                for (MapEntry entry : linkedList)
                    newMap.put(entry.key, entry.value);
        map = newMap.map;
    }

    private void reduce() {
        primesIndex = (primesIndex / 2);
        mapSize = primes[primesIndex];
        CustomMap newMap = new CustomMap(this.key, this.type, mapSize);
        for (LinkedList<MapEntry> mapEntries : map)
            if (mapEntries != null)
                for (MapEntry item : mapEntries)
                    newMap.put(item.key, item.value);
        map = newMap.map;
    }

    private T updateExistingLinkedList(final int index, final T key, final T value) {
        LinkedList<MapEntry> currentEntries = map[index];
        for (MapEntry currentEntry : currentEntries)
            if (currentEntry.key.equals(key)) {
                T previousValue = currentEntry.value;
                currentEntry.value = value;
                return previousValue;
            }
        map[index].add(new MapEntry(key, value));
        size++;
        return null;
    }

    //this method is really just for testing LOAD_FACTOR
    public int getMapSize() {
        return this.mapSize;
    }

    protected static final int[] primes = { 17, 23, 29, 37, 47, 59, 71, 89, 107, 131, 163, 197, 239, 293, 353, 431, 521, 631, 761, 919,
            1103, 1327, 1597, 1931, 2333, 2801, 3371, 4049, 4861, 5839, 7013, 8419, 10103, 12143, 14591,
            17519, 21023, 25229, 30293, 36353, 43627, 52361, 62851, 75431, 90523, 108631, 130363, 156437,
            187751, 225307, 270371, 324449, 389357, 467237, 560689, 672827, 807403, 968897, 1162687, 1395263,
            1674319, 2009191, 2411033, 2893249, 3471899, 4166287, 4999559, 5999471, 7199369 };

    public class MapEntry {
        T key;
        T value;

        MapEntry(final T key, T value) {
            this.key = key;
            this.value = value;
        }
    }
}