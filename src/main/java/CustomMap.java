import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class CustomMap<K, V> implements CustomMapInterface<K, V> {

    private LinkedList<MapEntry>[] map;
    private final Class<K> key;
    private final Class<V> value;

    private int mapSize = primes[0];
    private int size = 0;
    private static final double LOAD_FACTOR = 0.75f;
    private int primesIndex = 0;

    public CustomMap(final Class<K> key, final Class<V> value) {
        this.map = new LinkedList[primes[0]];
        this.key = key;
        this.value = value;
    }

    private CustomMap(final Class<K> key, final Class<V> value, final int mapSize) {
        this.mapSize = getClosestPrime(mapSize);
        this.map = new LinkedList[mapSize];
        this.key = key;
        this.value = value;
    }

    public void clear() {
        this.primesIndex = 0;
        this.map = new LinkedList[primes[primesIndex]];
        this.mapSize = primes[primesIndex];
        this.size = 0;
    }

    public boolean containsKey(Object key) {
        if (key == null)
            throw new NullPointerException();
        LinkedList<MapEntry> indexedMapEntry = map[hash((K) key)];
        if (indexedMapEntry == null)
            return false;
        for (MapEntry entry : indexedMapEntry)
            if (entry.key.equals(key))
                return true;
        return false;
    }

    public boolean containsValue(final V value) {
        for (LinkedList<MapEntry> mapEntries : map)
            if (mapEntries != null)
                for (MapEntry entry : mapEntries)
                    if (Objects.equals(entry.value, value))
                        return true;
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof CustomMap<?, ?> other))
            return false;
        if (size != other.size())
            return false;
        if (!key.equals(other.key) || !value.equals(other.value))
            return false;
        for (K key : keySet()) {
            if (!other.containsKey(key))
                return false;
            if (!Objects.equals(get(key), other.getOrDefault(key, null)))
                return false;
        }
        return true;
    }

    public V get(final K key) {
        return getOrDefault(key, null);
    }

    public V getOrDefault(Object key, V defaultValue) {
        if (key == null)
            throw new NullPointerException();
        LinkedList<MapEntry> indexedMapEntry = map[hash((K) key)];
        if (indexedMapEntry == null)
            return defaultValue;
        for (MapEntry entry : indexedMapEntry)
            if (entry.key.equals(key))
                return entry.value;
        return defaultValue;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(key, value, size);
        for (LinkedList<MapEntry> entryLinkedList : map)
            if (entryLinkedList != null)
                for (MapEntry entry : entryLinkedList)
                    result = 31 * result + Objects.hash(entry.key, entry.value);
        return result;
    }

    public Set<K> keySet() {
        Set<K> set = new HashSet<>();
        for (LinkedList<MapEntry> mapEntries : map)
            if (mapEntries != null)
                for (MapEntry entry : mapEntries)
                    set.add(entry.key);
        return set;
    }

    public V put(final K key, final V value) {
        if (!this.key.isInstance(key))
            throw new IllegalArgumentException();
        if (value != null && !this.value.isInstance(value))
            throw new IllegalArgumentException();
        int index = hash(key);
        LinkedList<MapEntry> indexedEntry = map[index];
        V result = null;
        if (indexedEntry == null)
            addNewIndexEntry(key, value, index);
        else
            result = updateExistingLinkedList(index, key, value);
        if ((double) size / (double) mapSize > LOAD_FACTOR)
            expand();
        return result;
    }

    public V putIfAbsent(final K key, final V value) {
        if(key == null || value == null)
            throw new NullPointerException();
        return !containsKey(key) ? put(key, value) : getOrDefault(key, null);
    }

    public V remove(final K key) {
        if(key == null)
            throw new NullPointerException();
        LinkedList<MapEntry> entryLinkedList = map[hash(key)];
        if(entryLinkedList != null)
            for (int i = 0; i < entryLinkedList.size(); i++)
                if(entryLinkedList.get(i).key.equals(key))
                    return removeItem(entryLinkedList, i);
        return null;
    }

    public boolean remove(final K key, final V value) {
        if(key == null || value == null)
            throw new NullPointerException();
        if(containsKey(key)) {
            LinkedList<MapEntry> currentEntry = map[hash(key)];
            for(MapEntry entry : currentEntry)
                if (entry.value.equals(value)) {
                    currentEntry.remove(entry);
                    size--;
                    if (mapSize > 17 && size <= mapSize / 4)
                        reduce();
                    return true;
                }
        }
        return false;
    }

    public V replace(final K key, final V value) {
        if(key == null || value == null)
            throw new NullPointerException();
        return containsKey(key) ? put(key, value) : null;
    }

    public boolean replace(final K key, final V oldValue, final V newValue) {
        if(key == null || oldValue == null || newValue == null)
            throw new NullPointerException();
        if(containsKey(key))
            if(get(key).equals(oldValue))
                return put(key, newValue) != null;
        return false;
    }

    public int size() {
        return this.size;
    }

    @Override
    public String toString() {
        if(size == 0)
            return "{}";
        StringBuilder stringBuilder = new StringBuilder("{");
        boolean first = true;
        for(LinkedList<MapEntry> entryLinkedList : map)
            if(entryLinkedList != null)
                for (MapEntry entry : entryLinkedList) {
                    if (!first)
                        stringBuilder.append(", ");
                    stringBuilder.append("[").append(entry.key).append(", ").append(entry.value).append("]");
                    first = false;
            }
        return stringBuilder.append("}").toString();
    }

    public Collection<V> values() {
        List<V> list = new ArrayList<>();
        for (LinkedList<MapEntry> mapEntries : map)
            if (mapEntries != null)
                for (MapEntry mapEntry : mapEntries)
                    list.add(mapEntry.value);
        return list;
    }

    private void addNewIndexEntry(final K key, final V value, final int index) {
        LinkedList<MapEntry> entryIndex = new LinkedList<>();
        entryIndex.add(new MapEntry(key, value));
        map[index] = entryIndex;
        size++;
    }

    private void expand() {
        mapSize = primes[++primesIndex];
        CustomMap<K, V> newMap = new CustomMap<>(key, value, mapSize);
        Arrays.stream(map)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .forEach(entry -> newMap.put(entry.key, entry.value));
        map = newMap.map;
    }

    private int getClosestPrime(int mapSize) {
        for(int i = 0; i < primes.length; i++)
            if(primes[i] >= mapSize)
                return primes[primesIndex = i];
        return primes[0];
    }

    private int hash(K key) {
        int h;
        return (key == null) ? 0 : ((h = key.hashCode()) ^ (h >>> 16)) % mapSize;
    }

    private void reduce() {
        primesIndex = (primesIndex / 2);
        mapSize = primes[primesIndex];
        CustomMap newMap = new CustomMap<>(key, value, mapSize);
        Arrays.stream(map)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .forEach(item -> newMap.put(item.key, item.value));
        map = newMap.map;
    }

    private V removeItem(final LinkedList<MapEntry> entryLinkedList, final int index) {
        MapEntry removed = entryLinkedList.remove(index);
        size--;
        if(mapSize > 17 && size <= mapSize / 4)
            reduce();
        return removed.value;
    }

    private V updateExistingLinkedList(final int index, final K key, final V value) {
        LinkedList<MapEntry> currentEntries = map[index];
        for (int i = 0; i <  currentEntries.size(); i++) {
            MapEntry currentEntry = currentEntries.get(i);
            if (currentEntry.key.equals(key)) {
                V previousValue = currentEntry.value;
                map[index].get(i).value = value;
                return previousValue;
            }
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
        final K key;
        V value;

        MapEntry(final K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}