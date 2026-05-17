import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.springframework.util.Assert.isInstanceOf;
import static org.springframework.util.Assert.notNull;

/**
 * A hash table-based implementation of the {@link Map} interface, using an array
 * to handle collisions via chaining. This map does not permit null keys and enforces type constraints
 * on keys and values using the provided {@code Class<K>} and {@code Class<V>}. Null values are permitted
 * in some operations (e.g., {@code put}) but not others (e.g., {@code replace}). The map resizes
 * automatically based on a load factor of 0.75, using a predefined sequence of prime numbers for bucket sizes.
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
    private final Class<K> key;
    private final Class<V> value;

    private int mapSize = primes[0];
    private int size = 0;
    private static final double LOAD_FACTOR = 0.75f;
    private int primesIndex = 0;

    /**
     * Constructs an empty {@code CustomMap} with the specified key and value types and an initial
     * capacity of 17 buckets. The map uses a hash table with chaining (via a linked Node structure) to handle
     * collisions and resizes when the load factor (0.75) is exceeded.
     *
     * @param key the {@code Class} object representing the type of keys in this map
     * @param value the {@code Class} object representing the type of values in this map
     * @throws IllegalArgumentException if the key or value class is null
     */
    public CustomMap(final Class<K> key, final Class<V> value) {
        notNull(key, "key must not be null");
        notNull(value, "value must not be null");
        this.map = new Node[primes[0]];
        this.key = key;
        this.value = value;
    }

    /**
     * Removes all mappings from this map (optional operation). The map will be empty after this call,
     * with its internal array reset to the initial capacity (17 buckets).
     *
     * @implSpec
     * This implementation creates a new array of size equal to the smallest prime (17), resets the size to 0,
     * and updates the prime index to 0.

     * (<a href="{@docRoot}/java.base/java/util/Map.html#optional-restrictions">optional</a>)
     */
    public void clear() {
        this.primesIndex = 0;
        this.map = (Node<K, V>[]) new Node[primes[primesIndex]];
        this.mapSize = primes[primesIndex];
        this.size = 0;
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
     * @implSpec
     * This implementation performs:
     * <pre> {@code
     * V oldValue = get(key);
     * V newValue = remappingFunction.apply(key, oldValue);
     * if (newValue != null) {
     *     put(key, newValue);
     * } else if (oldValue != null) {
     *     remove(key);
     * }
     * return newValue;
     * }</pre>
     *
     * @param key the key whose value is to be computed
     * @param remappingFunction the function to compute the new value
     * @return the new value associated with the key, or {@code null} if none
     * @throws NullPointerException if the key or remappingFunction is null
     * @throws IllegalArgumentException if the key or computed value is not an instance of the key or value
     *         type specified at construction
     */
    public V compute(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(remappingFunction);
        isInstanceOf(this.key, key);
        V oldValue = get(key);
        V newValue = remappingFunction.apply(key, oldValue);
        checkMatchingValueInstance(newValue);
        if (newValue == null && oldValue != null) {
            remove(key);
            return null;
        }
        if(newValue != null)
            put(key, newValue);
        return newValue;
    }

    /**
     * If the specified key is not already associated with a value, computes a new value using the
     * given mapping function and associates it with the key. If the function returns {@code null},
     * no mapping is created. The map may resize if the load factor (0.75) is exceeded or shrink if
     * the size falls below one-quarter of the current capacity and the capacity exceeds 17.
     *
     * @param key the key whose value is to be computed if absent
     * @param mappingFunction the function to compute a value
     * @return the current (existing or computed) value associated with the key, or {@code null} if none
     * @throws NullPointerException if the key or mappingFunction is null
     * @throws IllegalArgumentException if the key or computed value is not an instance of the key or value
     *         type specified at construction
     */
    public V computeIfAbsent(final K key, final Function<? super K, ? extends V> mappingFunction) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(mappingFunction);
        isInstanceOf(this.key, key);
        if(containsKey(key))
            return get(key);
        V newValue = mappingFunction.apply(key);
        if (newValue != null) {
            checkMatchingValueInstance(newValue);
            put(key, newValue);
        }
        return newValue;
    }

    /**
     * If the specified key is associated with a value, computes a new value using the given remapping
     * function and the current value. If the function returns {@code null}, the mapping is removed.
     *
     * @implSpec
     * This implementation performs:
     * <pre> {@code
     * if (containsKey(key)) {
     *     V oldValue = get(key);
     *     V newValue = remappingFunction.apply(key, oldValue);
     *     if (newValue != null) {
     *         put(key, newValue);
     *     } else {
     *         remove(key);
     *     }
     *     return newValue;
     * }
     * return null;
     * }</pre>
     *
     * @param key the key whose value is to be computed
     * @param remappingFunction the function to compute a new value
     * @return the new value associated with the key, or {@code null} if none
     * @throws NullPointerException if the key or remappingFunction is null
     * @throws IllegalArgumentException if the key or computed value is not an instance of the key or value
     *         type specified at construction
     */
    public V computeIfPresent(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(remappingFunction);
        isInstanceOf(this.key, key);
        if(!containsKey(key))
            return null;
        V oldValue = get(key);
        V newValue = remappingFunction.apply(key, oldValue);
        checkMatchingValueInstance(newValue);
        if (newValue == null)
            remove(key);
        else
            put(key, newValue);
        return newValue;
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
        Objects.requireNonNull(key);
        int index = hash(key);
        for(Node<K, V> node = map[index]; node != null; node = node.next)
            if(node.key.equals(key))
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
        for(Node<K, V> entry : map)
            for(Node<K, V> node = entry; node != null; node = node.next)
                if (Objects.equals(node.value, value))
                    return true;
        return false;
    }

    /**
     * Returns a new {@link Set} containing all key-value mappings in this map. The set contains
     * {@link java.util.Map.Entry} objects and is not backed by the map, so changes to the set do not
     * affect the map, and vice versa.
     * The set contains {@link java.util.AbstractMap.SimpleEntry} objects
     *
     * @return a new set containing all key-value mappings in this map
     */
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> set = new HashSet<>();
        for(Node<K, V> entry : map)
            for (Node<K, V> e = entry; e != null; e = e.next)
                set.add(new AbstractMap.SimpleEntry<>(e.key, e.value));
        return set;
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
        if (o instanceof CustomMap<?, ?> otherCustom)
            if (!this.key.equals(otherCustom.key) || !this.value.equals(otherCustom.value))
                return false;
        for (Map.Entry<K, V> entry : entrySet()) {
            K key = entry.getKey();
            V value = entry.getValue();
            try {
                Object otherValue = otherMap.get(key);
                if (!Objects.equals(value, otherValue))
                    return false;
            } catch (ClassCastException e) {
                return false;
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
        Objects.requireNonNull(action);
        for (Node<K, V> node : map)
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
        Objects.requireNonNull(key);
        int index = hash(key);
        for (Node<K, V> entry = map[index]; entry != null; entry = entry.next)
            if(entry.key.equals(key))
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
        for (Node<K, V> node : map)
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
        Set<K> set = new HashSet<>();
        for (Node<K, V> entry : map)
            for (Node<K, V> n = entry; n != null; n = n.next)
                set.add(n.key);
        return set;
    }

    /**
     * If the specified key is not associated with a value, associates it with the given value.
     * Otherwise, replaces the current value with the result of applying the remapping function to
     * the current value and the given value (optional operation). If the remapping function returns
     * {@code null}, the mapping is removed. The map may resize if the load factor (0.75) is exceeded
     * or shrink if the size falls below one-quarter of the current capacity and the capacity exceeds 17.
     *
     * <p>The remapping function should not modify this map during computation, as this may lead to
     * undefined behavior since this implementation does not detect concurrent modifications.
     *
     * @implSpec
     * This implementation performs:
     * <pre> {@code
     * V previous = get(key);
     * if (previous == null)
     *     return put(key, value);
     * V newValue = remappingFunction.apply(previous, value);
     * if (newValue == null) {
     *     remove(key);
     *     return null;
     * }
     * return put(key, newValue);
     * }</pre>
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
        Objects.requireNonNull(key);
        Objects.requireNonNull(remappingFunction);
        validateKeyValuePair(key, value);
        V previous = get(key);
        if (previous == null)
            return put(key, value);
        V newValue = remappingFunction.apply(previous, value);
        if (newValue == null) {
            remove(key);
            return null;
        }
        checkMatchingValueInstance(newValue);
        return put(key, newValue);
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
     * @throws IllegalArgumentException if the key or value (if non-null) is not an instance of the key
     *         or value type specified at construction
     * (<a href="{@docRoot}/java.base/java/util/Map.html#optional-restrictions">optional</a>)
     */
    public V put(final K key, final V value) {
        validateKeyValuePair(key, value);
        int index = hash(key);
        Node<K, V> node = map[index];
        for (Node<K, V> e = node; e != null; e = e.next)
            if(e.key.equals(key))
                return e.setValue(value);
        map[index] = new Node<>(key, value, map[index]);
        size++;
        if((double)size / mapSize > LOAD_FACTOR)
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
        Objects.requireNonNull(m);
        int newSize = size + m.size();
        if ((double) newSize / mapSize > LOAD_FACTOR)
            expand();
        for(Map.Entry<? extends K, ? extends V> node : m.entrySet())
            put(node.getKey(), node.getValue());
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
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        validateKeyValuePair(key, value);
        return !containsKey(key) ? put(key, value) : getOrDefault(key, null);
    }

    /**
     * Removes the mapping for the specified key from this map if present (optional operation). More formally,
     * removes the mapping for a key {@code k} such that {@code Objects.equals(key, k)}. The map may shrink
     * if the size falls below one-quarter of the current capacity and the capacity exceeds 17.
     *
     * @param key the key whose mapping is to be removed
     * @return the previous value associated with the key, or {@code null} if none
     * @throws NullPointerException if the key is null
     * @throws ClassCastException if the key is not an instance of the key type specified at construction
     * (<a href="{@docRoot}/java.base/java/util/Map.html#optional-restrictions">optional</a>)
     */
    public V remove(final Object key) {
        Objects.requireNonNull(key);
        int index = hash(key);
        Node<K, V> current = map[index];
        Node<K, V> previous = null;
        while(current != null) {
            if(current.key.equals(key)) {
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
     * exceeds 17.
     *
     * @param key the key whose mapping is to be removed
     * @param value the value expected to be associated with the key
     * @return {@code true} if the mapping was removed, {@code false} otherwise
     * @throws NullPointerException if the key or value is null
     * @throws ClassCastException if the key is not an instance of the key type specified at construction
     * (<a href="{@docRoot}/java.base/java/util/Map.html#optional-restrictions">optional</a>)
     */
    public boolean remove(final Object key, final Object value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        int index = hash(key);
        Node<K, V> previous = null;
        Node<K, V> current = map[index];
        while(current != null) {
            if(current.key.equals(key) && Objects.equals(current.value, value))
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
     * (<a href="{@docRoot}/java.base/java/util/Map.html#optional-restrictions">optional</a>)
     */
    public V replace(final K key, final V value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        validateKeyValuePair(key, value);
        return containsKey(key) ? put(key, value) : null;
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
     * (<a href="{@docRoot}/java.base/java/util/Map.html#optional-restrictions">optional</a>)
     */
    public boolean replace(final K key, final V oldValue, final V newValue) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(oldValue);
        Objects.requireNonNull(newValue);
        validateKeyValuePair(key, newValue);
        int index = hash(key);
        for (Node<K, V> node = map[index]; node != null; node = node.next)
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
     * (<a href="{@docRoot}/java.base/java/util/Map.html#optional-restrictions">optional</a>)
     */
    public void replaceAll(final BiFunction<? super K, ? super V, ? extends V> function) {
        notNull(function, "function must not be null");
        for (Node<K, V> node : map)
            for (Node<K, V> nodeInner = node; nodeInner != null; nodeInner = nodeInner.next) {
                V newValue = function.apply(nodeInner.key, nodeInner.value);
                checkMatchingValueInstance(newValue);
                nodeInner.value = newValue;
            }
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
        if(size == 0)
            return "{}";
        StringBuilder stringBuilder = new StringBuilder("{");
        boolean first = true;
        for(Node<K, V> entry : map)
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
        List<V> list = new ArrayList<>();
        if(size == 0)
            return list;
        for (Node<K, V> entry : map)
            for (Node<K, V> node = entry; node != null; node = node.next)
                list.add(node.value);
        return list;
    }

    /**
     * Expands the map's internal array to the next prime size in the predefined sequence when the load
     * factor (0.75) is exceeded. All existing key-value mappings are rehashed into the new array.
     *
     * @implSpec
     * This implementation creates a new {@code CustomMap} with the next prime size, reinserts all entries
     * using {@code put}, and updates the internal array.
     */
    private void expand() {
        if (primesIndex + 1 >= primes.length)
            return;
        primesIndex++;
        int newCapacity = primes[primesIndex];
        Node<K, V>[] newMap = new Node[newCapacity];
        this.mapSize = newCapacity;
        transfer(map, newMap);
        this.map = newMap;
    }

    /**
     * Returns the current number of buckets in the map's internal array, used for testing of internal
     * resizing operations.
     *
     * @return the number of buckets in the map
     */
    public final int getMapSize() {
        return this.mapSize;
    }

    /**
     * Computes a bucket index for the specified key by hashing its value and mapping it to a non-negative
     * index in the map's internal array.
     *
     * @param key the key to hash
     * @return the computed bucket index
     * @throws NullPointerException if the key is null
     */
    private int hash(final Object key) {
        Objects.requireNonNull(key);
        int h = key.hashCode();
        h = (h ^ (h >>> 20) ^ (h >>> 12)) & 0x7FFFFFFF;
        return h % mapSize;
    }

    /**
     * Reduces the map's internal array to a smaller prime size if the current size is less than or equal
     * to one-quarter of the capacity and the capacity exceeds 17. All existing key-value mappings are
     * rehashed into the new array.
     *
     * @implSpec
     * This implementation creates a new {@code CustomMap} with a smaller prime size, reinserts all entries
     * using {@code put}, and updates the internal array.
     */
    private void reduce() {
        if (mapSize <= primes[0] || size > mapSize / 4)
            return;
        int target = (int)(size / LOAD_FACTOR) + 1;
        int newIndex = 0;
        for (int i = 0; i < primes.length; i++) {
            if (primes[i] >= target)
                break;
            newIndex = i;
        }
        if (newIndex == primesIndex)
            return;
        int newCapacity = primes[newIndex];
        Node<K, V>[] newMap = new Node[newCapacity];
        this.mapSize = newCapacity;
        this.primesIndex = newIndex;
        transfer(map, newMap);
        this.map = newMap;
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
     * load becomes sufficiently low (size ≤ mapSize/4 and mapSize > 17).
     * </p>
     *
     * @param previous the node before the one being removed, or {@code null}
     *                 if the node to remove is the first in the bucket
     * @param index    the bucket index in the table where the entry resides
     * @param entry    the node to be removed
     * @return {@code true} (always, as this internal method assumes the entry exists)
     */
    private boolean remove(Node<K, V> previous, int index, Node<K, V> entry) {
        if(previous == null)
            map[index] = entry.next;
        else
            previous.next = entry.next;
        size--;
        if (mapSize > 17 && size <= mapSize / 4)
            reduce();
        return true;
    }

    private void transfer(Node<K, V>[] oldMap, Node<K, V>[] newMap) {
        for(Node<K, V> entry : oldMap) {
            Node<K, V> node = entry;
            while(node != null) {
                Node<K, V> next = node.next;
                int index = hash(node.key);
                node.next = newMap[index];
                newMap[index] = node;
                node = next;
            }
        }
    }

    /**
     * Validates that the key value pair match that of Map instance
     *
     * @param key the {@code Class} object representing the type of key to compare to class instance
     * @param value the {@code Class} object representing the type of value to compare to class instance
     *
     * @throws IllegalArgumentException if key does not match that of class Key instance.
     * @throws IllegalArgumentException if value does not match that of class Value instance
     * @throws IllegalArgumentException if value null
     */
    private void validateKeyValuePair(K key, V value) {
        isInstanceOf(this.key, key);
        checkMatchingValueInstance(value);
    }

    /**
     * Validates that the value is of matching {@code Class} value instance type and that it is not {@code null}
     * @param newValue the {@code Class} object representing the type of value to compare to class instance
     * @throws IllegalArgumentException if value does not match that of class Value instance.
     */
    private void checkMatchingValueInstance(V newValue) {
        if (newValue != null)
            isInstanceOf(this.value, newValue);
    }

    /**
     * A key-value pair representing a map entry, used internally to store mappings in the hash table.
     * Implements {@link Map.Entry} implicitly through {@link AbstractMap.SimpleEntry} in {@code entrySet}.
     */
    private static class Node<K, V> {
        final K key;
        V value;
        Node<K, V> next;

        /**
         * Constructs a new entry with the specified key and value and links next Node.
         *
         * @param key the key for this entry
         * @param value the value for this entry (maybe null)
         * @param next the Node to link as next
         */
        Node(K key, V value, Node<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        /**
         * Sets the value of the current Node.
         *
         * @param newValue the new value for the  node
         */
        public V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        /**
         * Returns a string representation of the node, in the format <code>{key=value}</code>.
         * The string contains the key-value, with separated an equals sign.
         *
         * @return a string representation of this map
         */
        public final String toString() {
            return key + "=" + value;
        }
    }

    /**
     * Array of prime numbers used as bucket sizes for the map's internal array during resizing.
     */
    protected static final int[] primes = { 17, 23, 29, 37, 47, 59, 71, 89, 107, 131, 163, 197, 239, 293, 353, 431, 521, 631, 761, 919,
            1103, 1327, 1597, 1931, 2333, 2801, 3371, 4049, 4861, 5839, 7013, 8419, 10103, 12143, 14591,
            17519, 21023, 25229, 30293, 36353, 43627, 52361, 62851, 75431, 90523, 108631, 130363, 156437,
            187751, 225307, 270371, 324449, 389357, 467237, 560689, 672827, 807403, 968897, 1162687, 1395263,
            1674319, 2009191, 2411033, 2893249, 3471899, 4166287, 4999559, 5999471, 7199369 };
}