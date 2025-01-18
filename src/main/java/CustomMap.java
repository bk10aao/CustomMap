import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomMap<K, V> implements Map<K, V> {

    private LinkedList<MapEntry>[] map;
    private final K key;
    private final V type;

    private int mapSize;
    private int size = 0;
    private static final double LOAD_FACTOR = 0.75f;
    private int primesIndex = 0;

    public CustomMap(final K key, final V value) {
        this.map = new LinkedList[primes[primesIndex]];
        this.mapSize = primes[0];
        this.key = key;
        this.type = value;
    }

    private CustomMap(final K key, final V value, final int mapSize) {
        this.mapSize = getClosestPrime(mapSize);
        this.map = new LinkedList[mapSize];
        this.key = key;
        this.type = value;
    }

    public void clear() {
        this.primesIndex = 0;
        this.map = new LinkedList[primes[primesIndex]];
        this.mapSize = primes[0];
        this.size = 0;
    }

    public boolean containsKey(final K key) {
        if(key.equals(null))
            throw new NullPointerException();
        LinkedList<MapEntry> indexedMapEntry = map[Math.abs(key.hashCode()) % mapSize];
        if(indexedMapEntry == null)
            return false;
        return indexedMapEntry.stream().anyMatch(entry -> entry.key.equals(key));
    }

    public boolean containsValue(final V value) {
        return Arrays.stream(map)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .anyMatch(entry -> entry.value.equals(value));
    }

    @Override
    public boolean equals(final CustomMap<K, V> o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if(size != o.size)
            return false;
        V[] mapValues = (V[]) Arrays.stream(map).toArray();
        V[] map1Values = (V[]) Arrays.stream(o.map).toArray();
        for(int i = 0; i < mapValues.length; i++) {
            LinkedList<MapEntry> first = map[i];
            LinkedList<MapEntry> second = (LinkedList<MapEntry>) map1Values[i];
            if(first == null && second == null)
                continue;
            else if(first == null || second == null)
                return false;
            for (int x = 0; x < first.size(); x++)
                if (!first.get(x).key.equals(second.get(x).key) || !first.get(x).value.equals(second.get(x).value))
                    return false;
        }
        return Objects.equals(key, o.key) && Objects.equals(type, o.type);
    }

    public V get(final K key) {
        if(key == null)
            throw new NullPointerException();
        return getOrDefault(key, null);
    }

    public V getOrDefault(final K key, final V defaultValue) {
        if(key == null)
            throw new NullPointerException();
        LinkedList<MapEntry> indexedMapEntry = map[Math.abs(key.hashCode()) % mapSize];
        if(indexedMapEntry == null) {
            return defaultValue;
        }
        return indexedMapEntry.stream()
                                .filter(mapEntry -> mapEntry.key.equals(key))
                                .findFirst()
                                .map(mapEntry -> mapEntry.value)
                                .orElse(defaultValue);
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hash(key, type, size)+ Arrays.hashCode(map);
    }

    public Set<K> keySet() {
        return Arrays.stream(map)
                        .filter(Objects::nonNull)
                        .flatMap(Collection::stream)
                        .map(entry -> entry.key)
                        .collect(Collectors.toSet());
    }

    public V put(final K key, final V value) {
        if(!key.getClass().equals(this.key) || !value.getClass().equals(this.type))
            throw new IllegalArgumentException();
        if(key == null)
            throw new NullPointerException();
        int index = Math.abs(key.hashCode()) % mapSize;
        LinkedList<MapEntry> indexedEntry = map[index];
        V result = null;
        if(indexedEntry == null)
            addNewIndexEntry(key, value, index);
        else
            result = updateExistingLinkedList(index, key, value);
        if((double)size / (double)mapSize > LOAD_FACTOR)
            expand();
        return result;
    }

    public V putIfAbsent(final K key, final V value) {
        if(key == null || value == null)
            throw new NullPointerException();
        if(!containsKey(key))
            put(key, value);
        else
            return get(key);
        return null;
    }

    public V remove(final K key) {
        if(key == null)
            throw new NullPointerException();
        LinkedList<MapEntry> entryLinkedList = map[Math.abs(key.hashCode()) % mapSize];

        if(entryLinkedList != null)
            for (int i = 0; i < entryLinkedList.size(); i++)
                if (entryLinkedList.get(i).key.equals(key))
                    return removeItem(entryLinkedList, i);
        return null;
    }

    public boolean remove(final K key, final V value) {
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

    public V replace(final K key, final V value) {
        if(key == null || value == null)
            throw new NullPointerException();
        return containsKey(key) ? put(key, value) : null;
    }

    public boolean replace(final K key, final V oldValue, final V newValue) {
        if(key == null || oldValue == null || newValue == null)
            throw new NullPointerException();
        if(containsKey(key))
            if (get(key).equals(oldValue))
                return put(key, newValue) != null;
        return false;
    }

    public int size() {
        return this.size;
    }

    @Override
    public String toString() {
        if(size == 0) return "{ }";
        StringBuilder stringBuilder = new StringBuilder("{ ");
        for(K key : keySet()) {
            if(stringBuilder.length() > 2)
                stringBuilder.append(", ");
            stringBuilder.append("[")
                            .append(key)
                            .append(", ")
                            .append(get(key))
                            .append("]");
        }
        return stringBuilder.append(" }").toString();
    }

    public Collection<V> values() {
        Collection<V> values = new ArrayList<>();
        if(size == 0)
            return values;
        return Arrays.stream(map)
                        .filter(Objects::nonNull)
                        .flatMap(Collection::stream)
                        .map(mapEntry -> mapEntry.value)
                        .collect(Collectors.toList());
    }

    private void addNewIndexEntry(final K key, final V value, final int index) {
        map[index] = new LinkedList<>();
        LinkedList<MapEntry> entryIndex = map[index];
        MapEntry mapEntry = new MapEntry(key, value);
        entryIndex.add(mapEntry);
        map[index] = entryIndex;
        size++;
    }

    private void expand() {
        mapSize = primes[++primesIndex];
        CustomMap<K, V> newMap = new CustomMap(this.key, this.type, mapSize);
        Arrays.stream(map)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .forEach(entry -> newMap.put(entry.key, entry.value));
        map = newMap.map;
    }

    private int getClosestPrime(int mapSize) {
        for(int i = 0; i < primes.length; i++)
            if (primes[i] >= mapSize) {
                primesIndex = i;
                return primes[i];
            }
        return 0;
    }

    private void reduce() {
        primesIndex = (primesIndex / 2);
        mapSize = primes[primesIndex];
        CustomMap newMap = new CustomMap(this.key, this.type, mapSize);
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