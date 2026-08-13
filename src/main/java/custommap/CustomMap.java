package custommap;

import java.util.AbstractCollection;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * A hash table-based implementation of the {@link Map} interface, using an array
 * to handle collisions via chaining. This map does not permit null keys and enforces type constraints
 * on keys and values using the provided {@code Class<K>} and {@code Class<V>}. Null values are permitted
 * in some operations (e.g., {@code put}) but not others (e.g., {@code replace}). The map resizes
 * automatically based on a load factor of 0.75, using power-of-two capacity sizing and bitwise masking.
 * <p>
 * This implementation is not synchronized and does not guarantee detection of concurrent modifications.
 * Methods like {@code compute} and {@code forEach} may produce undefined behavior if the map is modified
 * during iteration.
 * <p>
 * @author Benjamin Kane
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @see <a href="https://www.linkedin.com/in/benjamin-kane-81149482/">LinkedIn</a>
 * @see <a href="https://github.com/bk10aao">GitHub account bk10aao</a>
 * @see <a href="https://github.com/bk10aao/CustomMap">Repository</a>
 */
public class CustomMap<K, V> implements Map<K, V> {

    private Node<K, V>[] map;

    private int mapSize;
    private int size = 0;
    private static final double LOAD_FACTOR = 0.75f;
    private static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * Constructs an empty {@code CustomMap} with an initial capacity of 16 buckets.
     */
    public CustomMap() {
        this(16);
    }

    /**
     * Constructs an empty {@code CustomMap} with the specified initial capacity.
     * The capacity is automatically rounded up to the nearest power of two.
     *
     * @param initialCapacity the initial capacity
     * @throws IllegalArgumentException if the initial capacity is negative
     */
    public CustomMap(int initialCapacity) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Initial capacity must not be negative: " + initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        int capacity = 1;
        while (capacity < initialCapacity && capacity < MAXIMUM_CAPACITY)
            capacity <<= 1;
        if (capacity > MAXIMUM_CAPACITY)
            capacity = MAXIMUM_CAPACITY;
        this.mapSize = capacity;
        this.map = new Node[Math.min(capacity, 1 << 24)];
    }

    /**
     * Constructs a new {@code CustomMap} with the same mappings as the specified map.
     *
     * @param m the map whose mappings are to be placed in this map
     */
    public CustomMap(final Map<? extends K, ? extends V> m) {
        this((int) ((m.size() / LOAD_FACTOR) + 1));
        putAll(m);
    }

    /**
     * Removes all mappings from this map (optional operation). The map will be empty after this call,
     * with its internal array reset to the initial capacity (16 buckets).
     *
     * @implSpec
     * This implementation creates a new array of size equal to the initial capacity (16), and resets the size to 0.
     * (<a href="{@docRoot}/java.base/java/util/Map.html#optional-restrictions">optional</a>)
     */
    public void clear() {
        Node<K, V>[] tab = map;
        if (tab != null && size > 0) {
            size = 0;
            Arrays.fill(tab, null);
        }
        if (mapSize > 16) {
            this.map = (Node<K, V>[]) new Node[16];
            this.mapSize = 16;
        }
    }

    /**
     * Computes a new mapping for the specified key using the given remapping function. The function
     * receives the key and its current value (or {@code null} if no mapping exists). If the function
     * returns {@code null}, the mapping is removed (or remains absent). If the function throws an
     * exception, it is rethrown, and the mapping is unchanged.
     *
     * <p>The remapping function should not modify this map during computation, as this may lead to
     * undefined behavior since this implementation does not detect concurrent modifications.
     *
     * @param key the key whose value is to be computed
     * @param remappingFunction the function to compute the new value
     * @return the new value associated with the key, or {@code null} if none
     * @throws NullPointerException if the key or remappingFunction is null
     * @throws IllegalArgumentException if the key or computed value is not an instance of the key or value
     *         type specified at construction
     */
    public V compute(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        requireNonNull(key, "Key value must not be null.");
        requireNonNull(remappingFunction, "Remapping function must not be null.");
        Node<K, V>[] tab = map;
        int hash = hash(key);
        int index = hash & (tab.length - 1);
        Node<K, V> previous = null;
        Node<K, V> current = tab[index];
        while (current != null) {
            if (current.key.equals(key))
                break;
            previous = current;
            current = current.next;
        }
        V newValue = remappingFunction.apply(key, (current == null) ? null : current.value);
        if (newValue == null) {
            if (current != null)
                remove(previous, index, current);
            return null;
        }
        if (current != null)
            current.value = newValue;
        else {
            map[index] = new Node<>(hash, key, newValue, tab[index]);
            size++;
            if (size > (mapSize - (mapSize >>> 2)))
                expand();
        }
        return newValue;
    }

    /**
     * If the specified key is not already associated with a value, computes a new value using the
     * given mapping function and associates it with the key. If the function returns {@code null},
     * no mapping is created. The map may resize if the load factor (0.75) is exceeded or shrink if
     * the size falls below one-quarter of the current capacity and the capacity exceeds 16.
     *
     * @param key the key whose value is to be computed if absent
     * @param mappingFunction the function to compute a value
     * @return the current (existing or computed) value associated with the key, or {@code null} if none
     * @throws NullPointerException if the key or mappingFunction is null
     * @throws IllegalArgumentException if the key or computed value is not an instance of the key or value
     *         type specified at construction
     */
    public V computeIfAbsent(final K key, final Function<? super K, ? extends V> mappingFunction) {
        requireNonNull(key, "Key must not be null.");
        requireNonNull(mappingFunction, "Mapping function must not be null.");
        Node<K, V>[] tab = map;
        int hash = hash(key);
        int index = hash & (tab.length - 1);
        for (Node<K, V> e = tab[index]; e != null; e = e.next)
            if (e.key.equals(key))
                return e.value;
        V newValue = mappingFunction.apply(key);
        if (newValue != null) {
            tab[index] = new Node<>(hash, key, newValue, tab[index]);
            size++;
            if (size > (mapSize - (mapSize >>> 2)))
                expand();
        }
        return newValue;
    }

    /**
     * If the specified key is associated with a value, computes a new value using the given remapping
     * function and the current value. If the function returns {@code null}, the mapping is removed.
     *
     * @param key the key whose value is to be computed
     * @param remappingFunction the function to compute a new value
     * @return the new value associated with the key, or {@code null} if none
     * @throws NullPointerException if the key or remappingFunction is null
     * @throws IllegalArgumentException if the key or computed value is not an instance of the key or value
     *         type specified at construction
     */
    public V computeIfPresent(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        requireNonNull(key, "Key value must not be null.");
        requireNonNull(remappingFunction, "Remapping function must not be null.");
        Node<K, V>[] tab = map;
        int index = hash(key) & (tab.length - 1);
        Node<K, V> current = tab[index];
        Node<K, V> previous = null;
        while (current != null) {
            if (current.key.equals(key)) {
                V oldValue = current.value;
                V newValue = remappingFunction.apply(key, oldValue);
                if (newValue == null) {
                    remove(previous, index, current);
                    return null;
                }
                current.value = newValue;
                return newValue;
            }
            previous = current;
            current = current.next;
        }
        return null;
    }

    /**
     * Returns {@code true} if this map contains a mapping for the specified key. More formally,
     * returns {@code true} if and only if this map contains a mapping for a key {@code k} such that
     * {@code Objects.equals(key, k)}. (There can be at most one such mapping.)
     *
     * @param key the key whose presence in this map is to be tested
     * @return {@code true} if this map contains a mapping for the specified key
     * @throws NullPointerException if the specified key is null
     */
    public boolean containsKey(final Object key) {
        requireNonNull(key, "Key value must not be null.");
        Node<K, V>[] tab = map;
        int index = hash(key) & (tab.length - 1);
        for (Node<K, V> node = tab[index]; node != null; node = node.next)
            if (node.key.equals(key))
                return true;
        return false;
    }

    /**
     * Returns {@code true} if this map maps one or more keys to the specified value. More formally,
     * returns {@code true} if and only if this map contains at least one mapping to a value {@code v}
     * such that {@code Objects.equals(value, v)}. This operation requires time linear in the map size.
     *
     * @param value the value whose presence in this map is to be tested
     * @return {@code true} if this map maps one or more keys to the specified value
     */
    public boolean containsValue(final Object value) {
        Node<K, V>[] tab = map;
        for (int i = 0; i < tab.length; i++)
            for (Node<K, V> node = tab[i]; node != null; node = node.next)
                if (Objects.equals(node.value, value))
                    return true;
        return false;
    }

    /**
     * Returns a new {@link Set} containing all key-value mappings in this map. The set contains
     * {@link java.util.Map.Entry} objects and is not backed by the map, so changes to the set do not
     * affect the map, and vice versa.
     * The set contains {@link SimpleEntry} objects
     *
     * @return a new set containing all key-value mappings in this map
     */
    public Set<Map.Entry<K, V>> entrySet() {
        return new EntrySetView();
    }

    /**
     * Compares the specified object with this map for equality. Returns {@code true} if the given object
     * is a {@code CustomMap} with the same key and value types (as specified by {@code Class<K>} and
     * {@code Class<V>}), the same size, and identical key-value mappings. Two mappings are considered
     * identical if their keys are equal (via {@code Objects.equals}) and their values are equal (via
     * {@code Objects.equals}). This ensures that {@code map1.equals(map2)} implies
     * {@code map1.hashCode() == map2.hashCode()}, as required by the general contract of
     * {@link Object#equals}.
     * <p>
     * The order of entries does not affect the comparison.
     *
     * @param o the object to be compared for equality with this map
     * @return {@code true} if the specified object is equal to this map
     * @see #hashCode()
     * @see Objects#equals(Object, Object)
     */
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Map<?, ?> otherMap))
            return false;
        if (size() != otherMap.size())
            return false;
        Node<K, V>[] tab = map;
        for (Node<K, V> head : tab) {
            for (Node<K, V> node = head; node != null; node = node.next) {
                K key = node.key;
                V value = node.value;
                try {
                    Object otherValue = otherMap.get(key);
                    if (value == null) {
                        if (otherValue != null || !otherMap.containsKey(key))
                            return false;
                    } else if (!value.equals(otherValue))
                        return false;
                } catch (ClassCastException | NullPointerException e) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Performs the given action for each key-value mapping in this map until all entries have been
     * processed. The action should not modify this map, as this may lead to undefined behavior since
     * this implementation does not detect concurrent modifications.
     *
     * @param action the action to be performed for each key-value pair
     * @throws NullPointerException if the action is null
     */
    public void forEach(final BiConsumer<? super K, ? super V> action) {
        requireNonNull(action, "BiConsumer must not be null.");
        Node<K, V>[] tab = map;
        for (Node<K, V> node : tab)
            for (Node<K, V> n = node; n != null; n = n.next)
                action.accept(n.key, n.value);
    }

    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     *
     * <p>More formally, if this map contains a mapping from a key
     * {@code k} to a value {@code v} such that
     * {@code Objects.equals(key, k)},
     * then this method returns {@code v}; otherwise
     * it returns {@code null}.  (There can be at most one such mapping.)
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or
     *         {@code null} if this map contains no mapping for the key
     * @throws NullPointerException if the specified key is null
     * @throws ClassCastException if the key is not an instance of the key type specified at construction
     */
    public V get(final Object key) {
        return getOrDefault(key, null);
    }

    /**
     * Returns the value to which the specified key is mapped, or the specified default value if
     * no mapping exists for the key.
     *
     * @param key the key whose associated value is to be returned
     * @param defaultValue the value to return if no mapping exists
     * @return the value associated with the key, or {@code defaultValue} if none
     * @throws NullPointerException if the key is null
     * @throws ClassCastException if the key is not an instance of the key type specified at construction
     */
    public V getOrDefault(final Object key, final V defaultValue) {
        requireNonNull(key, "Key value must not be null.");
        Node<K, V>[] tab = map;
        int index = hash(key) & (tab.length - 1);
        for (Node<K, V> entry = tab[index]; entry != null; entry = entry.next)
            if (entry.key.equals(key))
                return entry.value;
        return defaultValue;
    }

    /**
     * Returns the hash code value for this map. The hash code is computed as the sum of the bitwise
     * XOR of the hash codes of each key and value in the map, as returned by {@link Objects#hashCode}.
     * This ensures that {@code map1.equals(map2)} implies {@code map1.hashCode() == map2.hashCode()}
     * for any two maps, as required by the general contract of {@link Object#hashCode}.
     * <p>
     * The hash code depends on the map's key-value mappings, and the order of entries does not affect
     * the result. If the map is empty, the hash code is 0.
     *
     * @return the hash code value for this map
     * @see #equals(Object)
     * @see Objects#hashCode(Object)
     */
    public int hashCode() {
        int result = 0;
        Node<K, V>[] tab = map;
        for (Node<K, V> node : tab)
            for (Node<K, V> e = node; e != null; e = e.next)
                result += Objects.hashCode(e.key) ^ Objects.hashCode(e.value);
        return result;
    }

    /**
     * Returns {@code true} if this map contains no key-value mappings.
     *
     * @return {@code true} if this map contains no key-value mappings
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns a new {@link Set} containing all keys in this map. The set is not backed by the map,
     * so changes to the set do not affect the map, and vice versa.
     *
     * @return a new set containing all keys in this map
     */
    public Set<K> keySet() {
        return new KeySetView();
    }

    /**
     * If the specified key is not associated with a value, associates it with the given value.
     * Otherwise, replaces the current value with the result of applying the remapping function to
     * the current value and the given value (optional operation). If the remapping function returns
     * {@code null}, the mapping is removed. The map may resize if the load factor (0.75) is exceeded
     * or shrink if the size falls below one-quarter of the current capacity and the capacity exceeds 16.
     *
     * <p>The remapping function should not modify this map during computation, as this may lead to
     * undefined behavior since this implementation does not detect concurrent modifications.
     *
     * @param key the key with which the value is to be associated
     * @param value the value to use if the key is not mapped
     * @param remappingFunction the function to compute a new value if the key is mapped
     * @return the new value associated with the key, or {@code null} if none
     * @throws NullPointerException if the key, value, or remappingFunction is null
     * @throws IllegalArgumentException if the key, value, or computed value is not an instance of the
     *         key or value type specified at construction
     * (<a href="{@docRoot}/java.base/java/util/Map.html#optional-restrictions">optional</a>)
     */
    public V merge(final K key, final V value, final BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        requireNonNull(key, "Key value must not be null.");
        requireNonNull(value, "Value must not be null.");
        requireNonNull(remappingFunction, "Remapping BiFunction must not be null.");
        Node<K, V>[] tab = map;
        int hash = hash(key);
        int index = hash & (tab.length - 1);
        Node<K, V> previous = null;
        Node<K, V> current = tab[index];
        while (current != null) {
            if (current.key.equals(key))
                break;
            previous = current;
            current = current.next;
        }
        if (current == null) {
            tab[index] = new Node<>(hash, key, value, tab[index]);
            size++;
            if (size > (mapSize - (mapSize >>> 2)))
                expand();
            return value;
        }

        V newValue = remappingFunction.apply(current.value, value);
        if (newValue == null) {
            remove(previous, index, current);
            return null;
        }
        current.value = newValue;
        return newValue;
    }

    /**
     * Associates the specified value with the specified key in this map (optional operation). If the map
     * previously contained a mapping for the key, the old value is replaced. The map may resize if the
     * load factor (0.75) is exceeded.
     *
     * @param key the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key (maybe null)
     * @return the previous value associated with the key, or {@code null} if none
     * @throws NullPointerException if the key is null
     * (<a href="{@docRoot}/java.base/java/util/Map.html#optional-restrictions">optional</a>)
     */
    public V put(final K key, final V value) {
        requireNonNull(key, "Key value must not be null.");
        Node<K, V>[] tab = map;
        int hash = hash(key);
        int index = hash & (tab.length - 1);
        for (Node<K, V> e = tab[index]; e != null; e = e.next)
            if (e.key.equals(key))
                return e.setValue(value);
        tab[index] = new Node<>(hash, key, value, tab[index]);
        size++;
        if (size > (mapSize - (mapSize >>> 2)))
            expand();
        return null;
    }

    /**
     * Copies all mappings from the specified map to this map (optional operation). The effect is equivalent
     * to calling {@link #put(Object,Object)} for each key-value pair in the specified map. The map may resize
     * if the load factor (0.75) is exceeded. The behavior is undefined if the specified map is modified during
     * this operation.
     *
     * @param m mappings to be stored in this map
     * @throws NullPointerException if the specified map or any of its keys are null
     * @throws IllegalArgumentException if any key or value is not an instance of the key or value type
     *         specified at construction
     * (<a href="{@docRoot}/java.base/java/util/Map.html#optional-restrictions">optional</a>)
     */
    public void putAll(final Map<? extends K, ? extends V> m) {
        int mSize = m.size();
        if (mSize == 0)
            return;
        for (Map.Entry<? extends K, ? extends V> entry : m.entrySet())
            requireNonNull(entry.getKey(), "Key value must not be null.");
        int targetSize = size + mSize;
        if (targetSize > (mapSize - (mapSize >>> 2))) {
            int targetCapacity = mapSize;
            while (targetSize > (targetCapacity - (targetCapacity >>> 2)) && targetCapacity < MAXIMUM_CAPACITY)
                targetCapacity <<= 1;
            if (targetCapacity > mapSize) {
                mapSize = targetCapacity;
                Node<K, V>[] newMap = new Node[mapSize];
                transfer(this.map, newMap, mapSize);
                this.map = newMap;
            }
        }
        Node<K, V>[] tab = map;
        int mask = mapSize - 1;
        for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
            K key = entry.getKey();
            V value = entry.getValue();
            int hash = hash(key);
            int index = hash & mask;
            boolean updated = false;
            for (Node<K, V> e = tab[index]; e != null; e = e.next)
                if (e.key.equals(key)) {
                    e.setValue(value);
                    updated = true;
                    break;
                }
            if (!updated) {
                tab[index] = new Node<>(hash, key, value, tab[index]);
                size++;
            }
        }
    }

    /**
     * Associates the specified value with the specified key if the key is not already associated with a
     * value (optional operation). If the key exists, no change is made. The map may resize if the load
     * factor (0.75) is exceeded.
     *
     * @param key the key with which the specified value is to be associated
     * @param value the value to be associated with the key
     * @return the current value associated with the key, or {@code null} if none
     * @throws NullPointerException if the key or value is null
     * @throws IllegalArgumentException if the key or value is not an instance of the key or value type
     *         specified at construction
     * (<a href="{@docRoot}/java.base/java/util/Map.html#optional-restrictions">optional</a>)
     */
    public V putIfAbsent(final K key, final V value) {
        requireNonNull(key, "Key value must not be null.");
        requireNonNull(value, "Value must not be null.");
        Node<K, V>[] tab = map;
        int hash = hash(key);
        int index = hash & (tab.length - 1);
        for (Node<K, V> e = tab[index]; e != null; e = e.next)
            if (e.key.equals(key))
                return e.value;
        tab[index] = new Node<>(hash, key, value, tab[index]);
        size++;
        if (size > (mapSize - (mapSize >>> 2)))
            expand();
        return null;
    }

    /**
     * Removes the mapping for the specified key from this map if present (optional operation). More formally,
     * removes the mapping for a key {@code k} such that {@code Objects.equals(key, k)}. The map may shrink
     * if the size falls below one-quarter of the current capacity and the capacity exceeds 16.
     *
     * @param key the key whose mapping is to be removed
     * @return the previous value associated with the key, or {@code null} if none
     * @throws NullPointerException if the key is null
     * @throws ClassCastException if the key is not an instance of the key type specified at construction
     * (<a href="{@docRoot}/java.base/java/util/Map.html#optional-restrictions">optional</a>)
     */
    public V remove(final Object key) {
        requireNonNull(key, "Key value must not be null.");
        Node<K, V>[] tab = map;
        int index = hash(key) & (tab.length - 1);
        Node<K, V> current = tab[index];
        Node<K, V> previous = null;
        while (current != null) {
            if (current.key.equals(key)) {
                V oldValue = current.value;
                remove(previous, index, current);
                return oldValue;
            }
            previous = current;
            current = current.next;
        }
        return null;
    }

    /**
     * Removes the mapping for the specified key if it is currently mapped to the specified value
     * (optional operation). More formally, removes the mapping for a key {@code k} if and only if
     * {@code Objects.equals(key, k)} and {@code Objects.equals(value, v)}, where {@code v} is the
     * value currently mapped to {@code k}. Returns {@code true} if the mapping was removed.
     * The map may shrink if the size falls below one-quarter of the current capacity and the capacity
     * exceeds 16.
     *
     * @param key the key whose mapping is to be removed
     * @param value the value expected to be associated with the key
     * @return {@code true} if the mapping was removed, {@code false} otherwise
     * @throws NullPointerException if the key or value is null
     * @throws ClassCastException if the key is not an instance of the key type specified at construction
     * (<a href="{@docRoot}/java.base/java/util/Map.html#optional-restrictions">optional</a>)
     */
    public boolean remove(final Object key, final Object value) {
        requireNonNull(key, "Key value must not be null.");
        requireNonNull(value, "Value must not be null.");
        Node<K, V>[] tab = map;
        int index = hash(key) & (tab.length - 1);
        Node<K, V> previous = null;
        Node<K, V> current = tab[index];
        while (current != null) {
            if (current.key.equals(key) && Objects.equals(current.value, value))
                return remove(previous, index, current);
            previous = current;
            current = current.next;
        }
        return false;
    }

    /**
     * Replaces the value associated with the specified key with the given value, if the key is present
     * (optional operation). If no mapping exists for the key, no change is made. The map may resize if
     * the load factor (0.75) is exceeded.
     *
     * @param key the key whose value is to be replaced
     * @param value the new value to associate with the key
     * @return the previous value associated with the key, or {@code null} if none
     * @throws NullPointerException if the key or value is null
     * @throws IllegalArgumentException if the key or value is not an instance of the key or value type
     *         specified at construction
     */
    public V replace(final K key, final V value) {
        requireNonNull(key, "Key value must not be null.");
        requireNonNull(value, "Value must not be null.");
        Node<K, V>[] tab = map;
        int index = hash(key) & (tab.length - 1);
        for (Node<K, V> e = tab[index]; e != null; e = e.next)
            if (e.key.equals(key))
                return e.setValue(value);
        return null;
    }

    /**
     * Replaces the value associated with the specified key with the new value, if the key is mapped to
     * the specified old value (optional operation). Returns {@code true} if the replacement occurred.
     *
     * @param key the key whose value is to be replaced
     * @param oldValue the expected current value
     * @param newValue the new value to associate with the key
     * @return {@code true} if the value was replaced, {@code false} otherwise
     * @throws NullPointerException if the key, oldValue, or newValue is null
     * @throws IllegalArgumentException if the key or newValue is not an instance of the key or value type
     *         specified at construction
     */
    public boolean replace(final K key, final V oldValue, final V newValue) {
        requireNonNull(key, "Key value must not be null.");
        requireNonNull(oldValue, "Old value must not be null.");
        requireNonNull(newValue, "New value must not be null.");
        Node<K, V>[] tab = map;
        int index = hash(key) & (tab.length - 1);
        for (Node<K, V> node = tab[index]; node != null; node = node.next)
            if (node.key.equals(key) && Objects.equals(node.value, oldValue)) {
                node.value = newValue;
                return true;
            }
        return false;
    }

    /**
     * Replaces each value in this map with the result of applying the given function to its key and
     * current value (optional operation). The function should not modify this map during computation,
     * as this may lead to undefined behavior since this implementation does not detect concurrent
     * modifications.
     *
     * @param function the function to compute new values, taking a key and current value
     * @throws IllegalArgumentException if the function is null
     * @throws IllegalArgumentException if any computed value (if non-null) is not an instance of the
     *         value type specified at construction
     */
    public void replaceAll(final BiFunction<? super K, ? super V, ? extends V> function) {
        requireNonNull(function, "BiFunction must not be null.");
        Node<K, V>[] tab = map;
        for (Node<K, V> node : tab)
            for (Node<K, V> nodeInner = node; nodeInner != null; nodeInner = nodeInner.next)
                nodeInner.value = function.apply(nodeInner.key, nodeInner.value);
    }

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map
     */
    public int size() {
        return this.size;
    }

    /**
     * Returns a string representation of this map, in the format <code>{key1=value1, key2=value2, ...}</code>.
     * The string contains all key-value mappings, with each pair separated by a comma and a space, enclosed
     * in curly braces. If the map is empty, returns <code>{}</code>. The order of entries is not guaranteed.
     *
     * @return a string representation of this map
     */
    public String toString() {
        if (size == 0)
            return "{}";
        StringBuilder stringBuilder = new StringBuilder("{");
        boolean first = true;
        Node<K, V>[] tab = map;
        for (Node<K, V> entry : tab)
            for (Node<K, V> node = entry; node != null; node = node.next) {
                if (!first)
                    stringBuilder.append(", ");
                stringBuilder.append(node);
                first = false;
            }
        return stringBuilder.append("}").toString();
    }

    /**
     * Returns a new {@link Collection} containing all values in this map. The collection is not backed
     * by the map, so changes to the collection do not affect the map, and vice versa. The collection
     * may contain null values.
     *
     * @return a new collection containing all values in this map
     */
    public Collection<V> values() {
        return new ValuesView();
    }

    private void expand() {
        Node<K, V>[] tab = map;
        int oldCapacity = tab.length;
        if (oldCapacity >= MAXIMUM_CAPACITY)
            return;
        int newCapacity = oldCapacity << 1;
        Node<K, V>[] newMap = new Node[newCapacity];
        transfer(tab, newMap, newCapacity);
        this.map = newMap;
        this.mapSize = newCapacity;
    }

    /**
     * Computes a bucket index for the specified key by hashing its value and mapping it to a non-negative
     * index in the map's internal array.
     *
     * @param key the key to hash
     * @return the computed bucket index
     * @throws NullPointerException if the key is null
     */
    private static int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    private void reduce() {
        Node<K, V>[] tab = map;
        if (mapSize <= 16 || size > mapSize / 4)
            return;
        int newCapacity = mapSize >> 1;
        if (newCapacity < 16) {
            newCapacity = 16;
        }
        if (newCapacity >= mapSize)
            return;
        Node<K, V>[] newMap = new Node[newCapacity];
        transfer(tab, newMap, newCapacity);
        this.map = newMap;
        this.mapSize = newCapacity;
    }

    /**
     * Removes the specified entry from the hash map at the given index
     * and updates the linked list accordingly.
     * <p>
     * If {@code previous} is {@code null}, the entry is the first node in the bucket
     * and the bucket head is updated. Otherwise, the entry is unlinked from
     * the previous node.
     * </p>
     * <p>
     * Decrements the size counter and triggers resizing (down-sizing) if the
     * load becomes sufficiently low (size ≤ mapSize/4 and mapSize > 16).
     * </p>
     *
     * @param previous the node before the one being removed, or {@code null}
     *                 if the node to remove is the first in the bucket
     * @param index    the bucket index in the table where the entry resides
     * @param entry    the node to be removed
     * @return {@code true} (always, as this internal method assumes the entry exists)
     */
    private boolean remove(final Node<K, V> previous, final int index, final Node<K, V> entry) {
        Node<K, V>[] tab = map;
        if (previous == null)
            tab[index] = entry.next;
        else
            previous.next = entry.next;
        size--;
        if (mapSize > 16 && size <= mapSize / 4)
            reduce();
        return true;
    }

    private void transfer(Node<K, V>[] oldMap, Node<K, V>[] newMap, int newCapacity) {
        int oldCapacity = oldMap.length;
        boolean expanding = newCapacity > oldCapacity;
        int testMask = expanding ? oldCapacity : newCapacity;
        for (int i = 0; i < oldCapacity; ++i) {
            Node<K, V> head = oldMap[i];
            if (head != null) {
                oldMap[i] = null;
                Node<K, V> lowHead = null, lowTail = null;
                Node<K, V> highHead = null, highTail = null;
                Node<K, V> next;
                do {
                    next = head.next;
                    if ((head.hash & testMask) == 0) {
                        if (lowTail == null)
                            lowHead = head;
                        else
                            lowTail.next = head;
                        lowTail = head;
                    } else {
                        if (highTail == null)
                            highHead = head;
                        else
                            highTail.next = head;
                        highTail = head;
                    }
                    head = next;
                } while (head != null);
                if (lowTail != null) {
                    lowTail.next = null;
                    newMap[i] = lowHead;
                }
                if (highTail != null) {
                    highTail.next = null;
                    newMap[i + testMask] = highHead;
                }
            }
        }
    }

    private final class KeySetView extends java.util.AbstractSet<K> {
        public int size() {
            return CustomMap.this.size();
        }

        public void clear() {
            CustomMap.this.clear();
        }

        public boolean contains(Object o) {
            return CustomMap.this.containsKey(o);
        }

        public boolean remove(Object o) {
            int oldSize = CustomMap.this.size;
            CustomMap.this.remove(o);
            return CustomMap.this.size < oldSize;
        }

        public java.util.Iterator<K> iterator() {
            return new KeyIterator();
        }
    }

    private final class KeyIterator implements java.util.Iterator<K> {
        private int bucketIndex = 0;
        private Node<K, V> nextNode = null;
        private Node<K, V> lastReturned = null;

        private KeyIterator() {
            advanceToNextNode();
        }

        private void advanceToNextNode() {
            if (nextNode != null && nextNode.next != null) {
                nextNode = nextNode.next;
                return;
            }
            nextNode = null;
            while (bucketIndex < mapSize) {
                Node<K, V> head = map[bucketIndex++];
                if (head != null) {
                    nextNode = head;
                    break;
                }
            }
        }

        public boolean hasNext() {
            return nextNode != null;
        }

        public K next() {
            if (nextNode == null)
                throw new java.util.NoSuchElementException();
            lastReturned = nextNode;
            K key = nextNode.key;
            advanceToNextNode();
            return key;
        }

        public void remove() {
            if (lastReturned == null)
                throw new IllegalStateException();
            CustomMap.this.remove(lastReturned.key);
            lastReturned = null;
        }
    }

    /**
     * A key-value pair representing a map entry, used internally to store mappings in the hash table.
     * Implements {@link Map.Entry} implicitly through {@link SimpleEntry} in {@code entrySet}.
     */
    private static class Node<K, V> {
        final int hash;
        final K key;
        V value;
        Node<K, V> next;

        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        public final String toString() {
            return key + "=" + value;
        }
    }

    private final class EntrySetView extends java.util.AbstractSet<Map.Entry<K, V>> {
        public int size() {
            return CustomMap.this.size();
        }

        public void clear() {
            CustomMap.this.clear();
        }

        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry<?, ?> entry))
                return false;
            Object k = entry.getKey();
            Object v = entry.getValue();
            V mappedVal = CustomMap.this.get(k);
            return mappedVal != null && mappedVal.equals(v);
        }

        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry<?, ?> entry))
                return false;
            return CustomMap.this.remove(entry.getKey(), entry.getValue());
        }

        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator();
        }
    }

    private final class ValuesView extends AbstractCollection<V> {
        public int size() {
            return CustomMap.this.size();
        }

        public void clear() {
            CustomMap.this.clear();
        }

        public boolean contains(Object o) {
            return CustomMap.this.containsValue(o);
        }

        public java.util.Iterator<V> iterator() {
            return new ValueIterator();
        }
    }

    private final class EntryIterator implements Iterator<Map.Entry<K, V>> {
        private int bucketIndex = 0;
        private Node<K, V> nextNode = null;
        private Node<K, V> lastReturned = null;

        private EntryIterator() {
            advanceToNextNode();
        }

        private void advanceToNextNode() {
            if (nextNode != null && nextNode.next != null) {
                nextNode = nextNode.next;
                return;
            }
            nextNode = null;
            while (bucketIndex < mapSize) {
                Node<K, V> head = map[bucketIndex++];
                if (head != null) {
                    nextNode = head;
                    break;
                }
            }
        }

        public boolean hasNext() {
            return nextNode != null;
        }

        public Map.Entry<K, V> next() {
            if (nextNode == null)
                throw new java.util.NoSuchElementException();
            lastReturned = nextNode;
            Node<K, V> current = nextNode;
            advanceToNextNode();

            return new SimpleEntry<>(current.key, current.value) {
                public V setValue(V value) {
                    return current.setValue(value);
                }
            };
        }

        public void remove() {
            if (lastReturned == null)
                throw new IllegalStateException();
            CustomMap.this.remove(lastReturned.key);
            lastReturned = null;
        }
    }

    private final class ValueIterator implements java.util.Iterator<V> {
        private int bucketIndex = 0;
        private Node<K, V> nextNode = null;
        private Node<K, V> lastReturned = null;

        private ValueIterator() {
            advanceToNextNode();
        }

        private void advanceToNextNode() {
            if (nextNode != null && nextNode.next != null) {
                nextNode = nextNode.next;
                return;
            }
            nextNode = null;
            while (bucketIndex < mapSize) {
                Node<K, V> head = map[bucketIndex++];
                if (head != null) {
                    nextNode = head;
                    break;
                }
            }
        }

        public boolean hasNext() {
            return nextNode != null;
        }

        public V next() {
            if (nextNode == null)
                throw new java.util.NoSuchElementException();
            lastReturned = nextNode;
            V value = nextNode.value;
            advanceToNextNode();
            return value;
        }

        public void remove() {
            if (lastReturned == null)
                throw new IllegalStateException();
            CustomMap.this.remove(lastReturned.key);
            lastReturned = null;
        }
    }
}