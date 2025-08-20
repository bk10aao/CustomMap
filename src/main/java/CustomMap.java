import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CustomMap<K, V> implements Map<K, V> {

    private ArrayList<Node>[] map;
    private final Class<K> key;
    private final Class<V> value;

    private int mapSize = primes[0];
    private int size = 0;
    private static final double LOAD_FACTOR = 0.75f;
    private int primesIndex = 0;

    public CustomMap(final Class<K> key, final Class<V> value) {
        if(key == null || value == null)
            throw new IllegalArgumentException();
        this.map = new ArrayList[primes[0]];
        this.key = key;
        this.value = value;
    }

    private CustomMap(final Class<K> key, final Class<V> value, final int mapSize) {
        if(key == null || value == null)
            throw new IllegalArgumentException();
        this.mapSize = getClosestPrime(mapSize);
        this.map = new ArrayList[mapSize];
        this.key = key;
        this.value = value;
    }

    public void clear() {
        this.primesIndex = 0;
        this.map = new ArrayList[primes[primesIndex]];
        this.mapSize = primes[primesIndex];
        this.size = 0;
    }

    public boolean containsKey(final Object key) {
        if (key == null)
            throw new NullPointerException();
        ArrayList<Node> bucket = map[hash(key)];
        if (bucket == null)
            return false;
        for (Node entry : bucket)
            if (entry.key.equals(key))
                return true;
        return false;
    }

    public boolean containsValue(final Object value) {
        for (ArrayList<Node> mapEntries : map)
            if (mapEntries != null)
                for (Node entry : mapEntries)
                    if (Objects.equals(entry.value, value))
                        return true;
        return false;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof CustomMap<?, ?> other))
            return false;
        if (size != other.size())
            return false;
        if (!key.equals(other.key) || !value.equals(other.value))
            return false;
        for (CustomMap.Entry<K, V> entry : entrySet()) {
            if (!other.containsKey(entry.getKey()))
                return false;
            if (!Objects.equals(entry.getValue(), other.get(entry.getKey())))
                return false;
        }
        return true;
    }

    public V get(final Object key) {
        return getOrDefault(key, null);
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public Set<CustomMap.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> set = new HashSet<>();
        for(ArrayList<Node> mapEntries : map)
            if (mapEntries != null)
                for (Node node : mapEntries)
                    set.add(new AbstractMap.SimpleEntry<>(node.key, node.value));
        return set;
    }

    public void putAll(final Map<? extends K, ? extends V> m) {
        if(m == null)
            throw new NullPointerException();
        int newSize = size + m.size();
        if ((double) newSize / mapSize > LOAD_FACTOR) {
            mapSize = getClosestPrime((int) (newSize / LOAD_FACTOR));
            expand();
        }
        for(CustomMap.Entry<? extends K, ? extends V> node : m.entrySet())
            put(node.getKey(), node.getValue());
    }

    public V compute(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        if (key == null || remappingFunction == null)
            throw new NullPointerException();
        if (!this.key.isInstance(key))
            throw new IllegalArgumentException();
        V previous = get(key);
        V newValue = remappingFunction.apply(key, previous);
        if (newValue != null && !this.value.isInstance(newValue))
            throw new IllegalArgumentException();
        if (newValue == null && previous != null) {
            remove(key);
            return null;
        }
        if(newValue != null)
            put(key, newValue);
        return newValue;
    }

    public V computeIfAbsent(final K key, final Function<? super K, ? extends V> mappingFunction) {
        if (key == null || mappingFunction == null)
            throw new NullPointerException();
        if (!this.key.isInstance(key))
            throw new IllegalArgumentException();
        if (!containsKey(key)) {
            V newValue = mappingFunction.apply(key);
            if (newValue != null) {
                if (!this.value.isInstance(newValue))
                    throw new IllegalArgumentException();
                put(key, newValue);
            }
            return newValue;
        }
        return get(key);
    }

    public V computeIfPresent(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        if (key == null || remappingFunction == null)
            throw new NullPointerException();
        if (!this.key.isInstance(key))
            throw new IllegalArgumentException();
        if (containsKey(key)) {
            V oldValue = get(key);
            V newValue = remappingFunction.apply(key, oldValue);
            if (newValue != null && !this.value.isInstance(newValue))
                throw new IllegalArgumentException();
            if (newValue == null) {
                remove(key);
                return null;
            }
            put(key, newValue);
            return newValue;
        }
        return null;
    }

    public void forEach(final BiConsumer<? super K, ? super V> action) {
        if (action == null)
            throw new NullPointerException();
        for (ArrayList<Node> mapEntries : map)
            if (mapEntries != null)
                for (Node node : mapEntries)
                    action.accept(node.key, node.value);
    }

    public V merge(final K key, final V value, final BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        if (key == null || value == null || remappingFunction == null)
            throw new NullPointerException();
        if (!this.key.isInstance(key) || !this.value.isInstance(value))
            throw new IllegalArgumentException();
        V previous = get(key);
        if (previous == null)
            return put(key, value);
        V newValue = remappingFunction.apply(previous, value);
        if (newValue == null) {
            remove(key);
            return null;
        }
        if (!this.value.isInstance(newValue))
            throw new IllegalArgumentException();
        return put(key, newValue);
    }

    public V replace(final K key, final V value) {
        if (key == null || value == null)
            throw new NullPointerException();
        if (!this.key.isInstance(key) || !this.value.isInstance(value))
            throw new IllegalArgumentException();
        return containsKey(key) ? put(key, value) : null;
    }

    public void replaceAll(final BiFunction<? super K, ? super V, ? extends V> function) {
        if (function == null)
            throw new IllegalArgumentException();
        for (ArrayList<Node> mapEntries : map) {
            if (mapEntries != null) {
                for (Node node : mapEntries) {
                    V newValue = function.apply(node.key, node.value);
                    if (newValue != null && !this.value.isInstance(newValue))
                        throw new IllegalArgumentException();
                    node.value = newValue;
                }
            }
        }
    }

    public V getOrDefault(final Object key, final V defaultValue) {
        if (key == null)
            throw new NullPointerException();
        ArrayList<Node> bucket = map[hash(key)];
        if (bucket == null)
            return defaultValue;
        for (Node entry : bucket)
            if (entry.key.equals(key))
                return entry.value;
        return defaultValue;
    }

    @Override
    public int hashCode() {
        int result = 0;
        for (CustomMap.Entry<K, V> entry : entrySet()) {
            result += Objects.hashCode(entry.getKey()) ^ Objects.hashCode(entry.getValue());
        }
        return result;
    }

    public Set<K> keySet() {
        Set<K> set = new HashSet<>();
        for (ArrayList<Node> mapEntries : map)
            if (mapEntries != null)
                for (Node entry : mapEntries)
                    set.add(entry.key);
        return set;
    }

    public V put(final K key, final V value) {
        if (!this.key.isInstance(key) || value != null && !this.value.isInstance(value))
            throw new IllegalArgumentException();
        int index = hash(key);
        ArrayList<Node> bucket = map[index];
        V result = null;
        if (bucket == null)
            addNewIndexEntry(key, value, index);
        else
            result = updateExistingArrayList(index, key, value);
        if ((double) size / (double) mapSize > LOAD_FACTOR)
            expand();
        return result;
    }

    public V putIfAbsent(final K key, final V value) {
        if(key == null || value == null)
            throw new NullPointerException();
        return !containsKey(key) ? put(key, value) : getOrDefault(key, null);
    }

    public V remove(final Object key) {
        if(key == null)
            throw new NullPointerException();
        ArrayList<Node> entryArrayList = map[hash(key)];
        if(entryArrayList != null)
            for (int i = 0; i < entryArrayList.size(); i++)
                if(entryArrayList.get(i).key.equals(key))
                    return removeItem(entryArrayList, i);
        return null;
    }

    public boolean remove(final Object key, final Object value) {
        if (key == null || value == null)
            throw new NullPointerException();
        if (containsKey(key)) {
            ArrayList<Node> bucket = map[hash(key)];
            Iterator<Node> iterator = bucket.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().value.equals(value)) {
                    iterator.remove();
                    size--;
                    if (mapSize > 17 && size <= mapSize / 4)
                        reduce();
                    return true;
                }
            }
        }
        return false;
    }

    public boolean replace(final K key, final V oldValue, final V newValue) {
        if (key == null || oldValue == null || newValue == null)
            throw new NullPointerException();
        if (!this.key.isInstance(key) || !this.value.isInstance(newValue))
            throw new IllegalArgumentException();
        ArrayList<Node> bucket = map[hash(key)];
        if (bucket != null) {
            for (Node node : bucket) {
                if (node.key.equals(key) && Objects.equals(node.value, oldValue)) {
                    node.value = newValue;
                    return true;
                }
            }
        }
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
        for(ArrayList<Node> entryArrayList : map)
            if(entryArrayList != null)
                for (Node entry : entryArrayList) {
                    if (!first)
                        stringBuilder.append(", ");
                    stringBuilder.append(entry.key).append("=").append(entry.value);
                    first = false;
            }
        return stringBuilder.append("}").toString();
    }

    public Collection<V> values() {
        List<V> list = new ArrayList<>();
        for (ArrayList<Node> mapEntries : map)
            if (mapEntries != null)
                for (Node mapEntry : mapEntries)
                    list.add(mapEntry.value);
        return list;
    }

    private void addNewIndexEntry(final K key, final V value, final int index) {
        ArrayList<Node> entryIndex = new ArrayList<>(4);
        entryIndex.add(new Node(key, value));
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

    private int getClosestPrime(final int mapSize) {
        for(int i = 0; i < primes.length; i++)
            if(primes[i] >= mapSize)
                return primes[primesIndex = i];
        return primes[0];
    }

    private int hash(final Object key) {
        if(key == null)
            throw new NullPointerException();
        int h = key.hashCode();
        return ((h ^ (h >>> 16)) % mapSize + mapSize) % mapSize;
    }

    private void reduce() {
        int newMapSize = getClosestPrime((int) (size / LOAD_FACTOR));
        if(newMapSize < mapSize) {
            mapSize = newMapSize;
            CustomMap newMap = new CustomMap<>(key, value, mapSize);
            Arrays.stream(map)
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .forEach(item -> newMap.put(item.key, item.value));
            map = newMap.map;
        }
    }

    private V removeItem(final ArrayList<Node> entryArrayList, final int index) {
        Node removed = entryArrayList.remove(index);
        size--;
        if(mapSize > 17 && size <= mapSize / 4)
            reduce();
        return removed.value;
    }

    private V updateExistingArrayList(final int index, final K key, final V value) {
        ArrayList<Node> currentEntries = map[index];
        for (int i = 0; i <  currentEntries.size(); i++) {
            Node currentEntry = currentEntries.get(i);
            if (currentEntry.key.equals(key)) {
                V previousValue = currentEntry.value;
                currentEntries.get(i).value = value;
                return previousValue;
            }
        }
        map[index].add(new Node(key, value));
        size++;
        return null;
    }

    //this method is really just for testing LOAD_FACTOR
    public final int getMapSize() {
        return this.mapSize;
    }

    protected static final int[] primes = { 17, 23, 29, 37, 47, 59, 71, 89, 107, 131, 163, 197, 239, 293, 353, 431, 521, 631, 761, 919,
                                            1103, 1327, 1597, 1931, 2333, 2801, 3371, 4049, 4861, 5839, 7013, 8419, 10103, 12143, 14591,
                                            17519, 21023, 25229, 30293, 36353, 43627, 52361, 62851, 75431, 90523, 108631, 130363, 156437,
                                            187751, 225307, 270371, 324449, 389357, 467237, 560689, 672827, 807403, 968897, 1162687, 1395263,
                                            1674319, 2009191, 2411033, 2893249, 3471899, 4166287, 4999559, 5999471, 7199369 };

    public class Node {
        final K key;
        V value;

        Node(final K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}