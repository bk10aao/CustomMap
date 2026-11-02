package custommap;

import java.util.AbstractCollection;
import java.util.AbstractMap.SimpleEntry;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * A hash table-based implementation of the {@link Map} interface, using an array
 * to handle collisions via chaining and Red-Black tree bins for high-collision performance.
 * This map does not permit null keys (throwing {@link NullPointerException}) and
 * supports null values in standard insertions while enforcing non-null constraints on
 * specific modifier operations (e.g., {@code replace}). The map resizes automatically
 * based on a load factor of 0.75, using power-of-two capacity sizing and bitwise masking.
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

    private static final int MAXIMUM_CAPACITY = 1_073_741_824;
    private static final int MIN_TREEIFY_CAPACITY = 64;
    private static final int TREEIFY_THRESHOLD   = 8;
    private static final int UNTREEIFY_THRESHOLD = 6;

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
     */
    public V compute(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        requireNonNull(key, "Key value must not be null.");
        requireNonNull(remappingFunction, "Remapping function must not be null.");
        Node<K, V>[] tab = map;
        if (tab == null || tab.length == 0)
            return null;
        int hash = hash(key);
        int index = hash & (tab.length - 1);
        Node<K, V> first = tab[index];

        V oldValue = null;
        TreeNode<K, V> tNode = null;
        Node<K, V> current = null;
        Node<K, V> previous = null;

        if (first instanceof TreeNode) {
            tNode = ((TreeNode<K, V>) first).getTreeNode(hash, key);
            if (tNode != null)
                oldValue = tNode.value;
        } else {
            current = first;
            while (current != null) {
                if (current.key.equals(key)) {
                    oldValue = current.value;
                    break;
                }
                previous = current;
                current = current.next;
            }
        }

        V newValue = remappingFunction.apply(key, oldValue);

        if (newValue == null) {
            if (tNode != null) {
                tNode.removeTreeNode(tab);
                size--;
                if (mapSize > 16 && size <= mapSize / 4)
                    reduce();
            } else if (current != null)
                remove(previous, index, current);
            return null;
        }
        if (tNode != null) {
            tNode.value = newValue;
            return newValue;
        }
        if (current != null) {
            current.value = newValue;
            return newValue;
        }
        if (first instanceof TreeNode) {
            TreeNode<K, V> p = ((TreeNode<K, V>) first).putTreeVal(tab, hash, key, newValue);
            if (p == null) {
                size++;
                if (size > (mapSize - (mapSize >>> 2)))
                    expand();
            }
        } else {
            int binCount = 0;
            for (Node<K, V> e = first; e != null; e = e.next)
                binCount++;
            tab[index] = new Node<>(hash, key, newValue, first);
            size++;
            if (binCount >= TREEIFY_THRESHOLD - 1)
                treeifyBin(tab, index);
            else if (size > (mapSize - (mapSize >>> 2)))
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
     */
    public V computeIfAbsent(final K key, final Function<? super K, ? extends V> mappingFunction) {
        requireNonNull(key, "Key must not be null.");
        requireNonNull(mappingFunction, "Mapping function must not be null.");
        Node<K, V>[] tab = map;
        if (tab == null || tab.length == 0)
            return null;
        int hash = hash(key);
        int index = hash & (tab.length - 1);
        Node<K, V> first = tab[index];
        if (first instanceof TreeNode) {
            TreeNode<K, V> p = ((TreeNode<K, V>) first).getTreeNode(hash, key);
            if (p != null)
                return p.value;
        } else
            for (Node<K, V> e = first; e != null; e = e.next)
                if (e.key.equals(key))
                    return e.value;
        V newValue = mappingFunction.apply(key);
        if (newValue != null) if (first instanceof TreeNode) {
            TreeNode<K, V> p = ((TreeNode<K, V>) first).putTreeVal(tab, hash, key, newValue);
            if (p == null) {
                size++;
                if (size > (mapSize - (mapSize >>> 2)))
                    expand();
            }
        } else {
            int binCount = 0;
            for (Node<K, V> e = first; e != null; e = e.next)
                binCount++;
            tab[index] = new Node<>(hash, key, newValue, first);
            size++;
            if (binCount >= TREEIFY_THRESHOLD - 1)
                treeifyBin(tab, index);
            else if (size > (mapSize - (mapSize >>> 2)))
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
     */
    public V computeIfPresent(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        requireNonNull(key, "Key value must not be null.");
        requireNonNull(remappingFunction, "Remapping function must not be null.");
        Node<K, V>[] tab = map;
        if (tab == null || tab.length == 0)
            return null;
        int hash = hash(key);
        int index = hash & (tab.length - 1);
        Node<K, V> first = tab[index];
        if (first == null)
            return null;
        if (first instanceof TreeNode) {
            TreeNode<K, V> tNode = ((TreeNode<K, V>) first).getTreeNode(hash, key);
            if (tNode != null) {
                V oldValue = tNode.value;
                V newValue = remappingFunction.apply(key, oldValue);
                if (newValue == null) {
                    tNode.removeTreeNode(tab);
                    size--;
                    if (mapSize > 16 && size <= mapSize / 4)
                        reduce();
                    return null;
                }
                tNode.value = newValue;
                return newValue;
            }
            return null;
        }

        Node<K, V> current = first;
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
        if (tab == null || tab.length == 0)
            return false;
        int hash = hash(key);
        int index = hash & (tab.length - 1);
        Node<K, V> first = tab[index];
        if (first == null)
            return false;
        if (first instanceof TreeNode)
            return ((TreeNode<K, V>) first).getTreeNode(hash, key) != null;
        for (Node<K, V> node = first; node != null; node = node.next)
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
        if (tab == null || tab.length == 0)
            return false;
        for (int i = 0; i < tab.length; i++)
            for (Node<K, V> node = tab[i]; node != null; node = node.next)
                if (Objects.equals(node.value, value))
                    return true;
        return false;
    }

    /**
     * Returns a {@link Set} view of the key-value mappings contained in this map.
     * The set is backed by the map, so changes to the map affect the set, and vice versa.
     *
     * @return a set view of the mappings contained in this map
     */
    public Set<Entry<K, V>> entrySet() {
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
        for (Node<K, V> head : tab)
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
        if (tab == null)
            return;
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
     */
    public V getOrDefault(final Object key, final V defaultValue) {
        requireNonNull(key, "Key value must not be null.");
        Node<K, V>[] tab = map;
        if (tab == null || tab.length == 0)
            return defaultValue;
        int hash = hash(key);
        int index = hash & (tab.length - 1);
        Node<K, V> first = tab[index];
        if (first == null)
            return defaultValue;
        if (first instanceof TreeNode) {
            TreeNode<K, V> p = ((TreeNode<K, V>) first).getTreeNode(hash, key);
            return (p != null) ? p.value : defaultValue;
        }
        for (Node<K, V> node = first; node != null; node = node.next)
            if (node.key.equals(key))
                return node.value;
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
        Node<K, V>[] tab = map;
        if (tab == null || tab.length == 0)
            return 0;
        int result = 0;
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
     * (<a href="{@docRoot}/java.base/java/util/Map.html#optional-restrictions">optional</a>)
     */
    public V merge(final K key, final V value, final BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        requireNonNull(key, "Key value must not be null.");
        requireNonNull(value, "Value must not be null.");
        requireNonNull(remappingFunction, "Remapping BiFunction must not be null.");
        Node<K, V>[] tab = map;
        if (tab == null || tab.length == 0)
            return null;
        int hash = hash(key);
        int index = hash & (tab.length - 1);
        Node<K, V> first = tab[index];

        TreeNode<K, V> tNode = null;
        Node<K, V> current = null;
        Node<K, V> previous = null;

        if (first instanceof TreeNode)
            tNode = ((TreeNode<K, V>) first).getTreeNode(hash, key);
        else {
            current = first;
            while (current != null) {
                if (current.key.equals(key))
                    break;
                previous = current;
                current = current.next;
            }
        }

        if (tNode == null && current == null) {
            if (first instanceof TreeNode) {
                TreeNode<K, V> p = ((TreeNode<K, V>) first).putTreeVal(tab, hash, key, value);
                if (p == null) {
                    size++;
                    if (size > (mapSize - (mapSize >>> 2)))
                        expand();
                }
            } else {
                int binCount = 0;
                for (Node<K, V> e = first; e != null; e = e.next)
                    binCount++;
                tab[index] = new Node<>(hash, key, value, first);
                size++;
                if (binCount >= TREEIFY_THRESHOLD - 1)
                    treeifyBin(tab, index);
                else if (size > (mapSize - (mapSize >>> 2)))
                    expand();
            }
            return value;
        }

        V oldValue = (tNode != null) ? tNode.value : current.value;
        V newValue = remappingFunction.apply(oldValue, value);
        if (newValue == null) {
            if (tNode != null) {
                tNode.removeTreeNode(tab);
                size--;
                if (mapSize > 16 && size <= mapSize / 4)
                    reduce();
            } else
                remove(previous, index, current);
            return null;
        }
        if (tNode != null)
            tNode.value = newValue;
        else
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
        if (tab == null || tab.length == 0)
            return null;
        int hash = hash(key);
        int index;
        int n;
        if (tab == null || (n = tab.length) == 0)
            n = (tab = map).length;
        index = hash & (n - 1);
        Node<K, V> first = tab[index];
        if (first == null)
            tab[index] = new Node<>(hash, key, value, null);
        else {
            Node<K, V> e;
            if (first instanceof TreeNode) {
                TreeNode<K, V> p = ((TreeNode<K, V>) first).putTreeVal(tab, hash, key, value);
                if (p == null) {
                    size++;
                    if (size > (mapSize - (mapSize >>> 2)))
                        expand();
                }
                return p == null ? null : p.setValue(value);
            }
            int binCount = 0;
            for (e = first; ; e = e.next) {
                if (e.key.equals(key))
                    return e.setValue(value);
                if (e.next == null) {
                    e.next = new Node<>(hash, key, value, null);
                    if (binCount >= TREEIFY_THRESHOLD - 1)
                        treeifyBin(tab, index);
                    break;
                }
                binCount++;
            }
        }
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
     * (<a href="{@docRoot}/java.base/java/util/Map.html#optional-restrictions">optional</a>)
     */
    public void putAll(final Map<? extends K, ? extends V> m) {
        int mSize = m.size();
        if (mSize == 0)
            return;
        for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
            requireNonNull(entry.getKey(), "Key value must not be null.");
            put(entry.getKey(), entry.getValue());
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
     * (<a href="{@docRoot}/java.base/java/util/Map.html#optional-restrictions">optional</a>)
     */
    public V putIfAbsent(final K key, final V value) {
        requireNonNull(key, "Key value must not be null.");
        requireNonNull(value, "Value must not be null.");
        Node<K, V>[] tab = map;
        if (tab == null || tab.length == 0)
            return null;
        int hash = hash(key);
        int index = hash & (tab.length - 1);
        Node<K, V> first = tab[index];
        if (first == null)
            tab[index] = new Node<>(hash, key, value, null);
        else {
            if (first instanceof TreeNode) {
                TreeNode<K, V> p = ((TreeNode<K, V>) first).putTreeVal(tab, hash, key, value);
                if (p != null)
                    return p.value;
                size++;
                if (size > (mapSize - (mapSize >>> 2)))
                    expand();
                return null;
            }

            int binCount = 0;
            for (Node<K, V> e = first; ; e = e.next) {
                if (e.key.equals(key))
                    return e.value;
                if (e.next == null) {
                    e.next = new Node<>(hash, key, value, null);
                    if (binCount >= TREEIFY_THRESHOLD - 1)
                        treeifyBin(tab, index);
                    break;
                }
                binCount++;
            }
        }
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
     * (<a href="{@docRoot}/java.base/java/util/Map.html#optional-restrictions">optional</a>)
     */
    public V remove(final Object key) {
        requireNonNull(key, "Key value must not be null.");
        Node<K, V>[] tab = map;
        if (tab == null || tab.length == 0)
            return null;
        int hash = hash(key);
        int index = hash & (tab.length - 1);
        Node<K, V> first = tab[index];
        if (first == null)
            return null;
        if (first instanceof TreeNode) {
            TreeNode<K, V> node = ((TreeNode<K, V>) first).getTreeNode(hash, key);
            if (node != null) {
                V oldValue = node.value;
                node.removeTreeNode(tab);
                size--;
                if (mapSize > 16 && size <= mapSize / 4)
                    reduce();
                return oldValue;
            }
        }
        Node<K, V> current = first;
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
     * (<a href="{@docRoot}/java.base/java/util/Map.html#optional-restrictions">optional</a>)
     */
    public boolean remove(final Object key, final Object value) {
        requireNonNull(key, "Key value must not be null.");
        requireNonNull(value, "Value must not be null.");
        Node<K, V>[] tab = map;
        if (tab == null || tab.length == 0)
            return false;
        int hash = hash(key);
        int index = hash & (tab.length - 1);
        Node<K, V> first = tab[index];
        if (first == null)
            return false;
        if (first instanceof TreeNode) {
            TreeNode<K, V> node = ((TreeNode<K, V>) first).getTreeNode(hash, key);
            if (node != null && Objects.equals(node.value, value)) {
                node.removeTreeNode(tab);
                size--;
                if (mapSize > 16 && size <= mapSize / 4)
                    reduce();
                return true;
            }
            return false;
        }

        Node<K, V> current = first;
        Node<K, V> previous = null;
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
     */
    public V replace(final K key, final V value) {
        requireNonNull(key, "Key value must not be null.");
        requireNonNull(value, "Value must not be null.");
        Node<K, V>[] tab = map;
        if (tab == null || tab.length == 0)
            return null;
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
     */
    public boolean replace(final K key, final V oldValue, final V newValue) {
        requireNonNull(key, "Key value must not be null.");
        requireNonNull(oldValue, "Old value must not be null.");
        requireNonNull(newValue, "New value must not be null.");
        Node<K, V>[] tab = map;
        if (tab == null || tab.length == 0)
            return false;
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
     * @throws NullPointerException if the function is null
     */
    public void replaceAll(final BiFunction<? super K, ? super V, ? extends V> function) {
        requireNonNull(function, "BiFunction must not be null.");
        Node<K, V>[] tab = map;
        if (tab == null)
            return;
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

    /**
     * Doubles the capacity of this map's internal table when the threshold is exceeded.
     * If the current capacity has already reached {@link #MAXIMUM_CAPACITY}, table expansion
     * is suppressed, and capacity remains capped.
     * <p>
     * This method allocates a new node array of double the previous size, delegates
     * existing entries and Red-Black tree bins to the new table via {@link #transfer(Node[], Node[], int)},
     * and updates the internal reference and capacity threshold accordingly.
     */
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
    private static int hash(final Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    /**
     * Halves the capacity of this map's internal table when occupancy falls significantly low.
     * Shrinking occurs only if the current capacity exceeds the minimum threshold of 16 and
     * the number of mappings is less than or equal to one-quarter of the current capacity
     * ({@code size <= mapSize / 4}).
     * <p>
     * This method allocates a new node array of half the previous size (floored at 16),
     * transfers existing entries and Red-Black tree bins to the new table via
     * {@link #transfer(Node[], Node[], int)}, and updates the internal reference and capacity threshold accordingly.
     */
    private void reduce() {
        Node<K, V>[] tab = map;
        if (mapSize <= 16 || size > mapSize / 4)
            return;
        int newCapacity = mapSize >> 1;
        if (newCapacity < 16)
            newCapacity = 16;
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

    /**
     * Transfers all nodes and tree bins from the old hash table to the new resized table.
     * <p>
     * During an expansion, nodes in each bucket are either kept at their original index
     * or relocated to {@code index + oldCapacity} based on bitwise masking. Single nodes are
     * placed directly, linked lists are split into low and high sublists, and Red-Black tree bins
     * are partitioned via {@link TreeNode#split}. During a reduction, entries are rehashed and
     * reinserted into the smaller table.
     *
     * @param oldMap      the previous internal table array being resized
     * @param newMap      the newly allocated target table array
     * @param newCapacity the capacity of the new table (must be a power of two)
     */
    private void transfer(final Node<K, V>[] oldMap, final Node<K, V>[] newMap, final int newCapacity) {
        int oldCapacity = oldMap.length;
        boolean expanding = newCapacity > oldCapacity;
        int testMask = expanding ? oldCapacity : newCapacity;
        for (int i = 0; i < oldCapacity; ++i) {
            Node<K, V> head = oldMap[i];
            if (head != null)
                if (!expanding) {
                    Node<K, V> current = head;
                    while (current != null) {
                        Node<K, V> next = current.next;
                        int targetIndex = current.hash & (newCapacity - 1);
                        Node<K, V> newNode = new Node<>(current.hash, current.key, current.value, newMap[targetIndex]);
                        newMap[targetIndex] = newNode;
                        current = next;
                    }
                } else if (head.next == null)
                    newMap[head.hash & (newCapacity - 1)] = head;
            else if (head instanceof TreeNode)
                ((TreeNode<K, V>) head).split(newMap, i, oldCapacity);
            else {
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

    /**
     * Replaces the linked list of nodes in the specified bucket with a balanced Red-Black tree.
     * <p>
     * If the table capacity is less than {@link #MIN_TREEIFY_CAPACITY}, table expansion
     * is triggered via {@link #expand()} instead of treeifying the bin. Otherwise,
     * the traditional linked-list nodes are converted into doubly-linked {@link TreeNode}
     * instances, and the tree structure is balanced using {@link TreeNode#treeify(Node[])}.
     *
     * @param tab   the hash table array containing the bin
     * @param index the index of the bucket to be treeified
     */
    private void treeifyBin(final Node<K, V>[] tab, final int index) {
        if (tab == null || tab.length < MIN_TREEIFY_CAPACITY) {
            expand();
            return;
        }
        Node<K, V> e = tab[index];
        if (e == null)
            return;
        TreeNode<K, V> hd = null, tl = null;
        do {
            TreeNode<K, V> p = new TreeNode<>(e.hash, e.key, e.value, null);
            if (tl == null)
                hd = p;
            else {
                p.previous = tl;
                tl.next = p;
            }
            tl = p;
            e = e.next;
        } while (e != null);
        tab[index] = hd;
        if (hd != null)
            hd.treeify(tab);
    }

    private final class KeySetView extends AbstractSet<K> {

        /**
         * Removes all mappings from the backing {@code CustomMap}, leaving the key set empty.
         */
        public void clear() {
            CustomMap.this.clear();
        }

        /**
         * Returns {@code true} if this set contains the specified element.
         *
         * @param o element whose presence in this key set is to be tested
         * @return {@code true} if the specified element is present as a key, {@code false} otherwise
         * @throws NullPointerException if the specified element is null
         */
        public boolean contains(final Object o) {
            return CustomMap.this.containsKey(o);
        }

        /**
         * Returns an iterator over the elements in this key set.
         *
         * @return an iterator over the map's keys
         */
        public Iterator<K> iterator() {
            return new KeyIterator();
        }

        /**
         * Removes the specified element from this set if it is present, effectively
         * removing the corresponding mapping from the backing {@code CustomMap}.
         *
         * @param o object to be removed from this set, if present
         * @return {@code true} if the set contained the specified element and it was removed
         * @throws NullPointerException if the specified key is null
         */
        public boolean remove(final Object o) {
            int oldSize = CustomMap.this.size;
            CustomMap.this.remove(o);
            return CustomMap.this.size < oldSize;
        }

        /**
         * Returns the number of elements (keys) in this set.
         *
         * @return the number of key mappings in the map
         */
        public int size() {
            return CustomMap.this.size();
        }
    }

    /**
     * An iterator over the keys contained in this map.
     * <p>
     * Traverses the internal hash table buckets sequentially, handling linked lists
     * and Red-Black tree nodes. Supports safe element removal from the backing
     * {@code CustomMap} via {@link #remove()}.
     */
    private final class KeyIterator implements Iterator<K> {

        private int bucketIndex = 0;

        private Node<K, V> lastReturned, nextNode = null;

        /**
         * Constructs a new {@code KeyIterator} and advances to the first valid node.
         */
        private KeyIterator() {
            advanceToNextNode();
        }

        /**
         * Returns {@code true} if the iteration has more elements.
         *
         * @return {@code true} if there is at least one more key to iterate over
         */
        public boolean hasNext() {
            return nextNode != null;
        }

        /**
         * Returns the next key in the iteration.
         *
         * @return the next key
         * @throws NoSuchElementException if the iteration has no more elements
         */
        public K next() {
            if (nextNode == null)
                throw new NoSuchElementException();
            lastReturned = nextNode;
            K key = nextNode.key;
            advanceToNextNode();
            return key;
        }

        /**
         * Removes from the underlying map the last key returned by this iterator.
         * This method can be called only once per call to {@link #next()}.
         *
         * @throws IllegalStateException if the {@code next} method has not yet been called,
         *         or the {@code remove} method has already been called after the last call
         *         to the {@code next} method
         */
        public void remove() {
            if (lastReturned == null)
                throw new IllegalStateException();
            CustomMap.this.remove(lastReturned.key);
            lastReturned = null;
        }

        /**
         * Advances the internal pointer to the next available node across the map's buckets.
         */
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
    }

    /**
     * A key-value pair representing a map entry, used internally to store mappings in the hash table.
     * Implements {@link Entry} implicitly through {@link SimpleEntry} in {@code entrySet}.
     *
     * @param <K> the type of keys maintained by this node
     * @param <V> the type of mapped values
     */
    private static class Node<K, V> {

        final int hash;

        final K key;

        V value;

        Node<K, V> next;

        /**
         * Constructs a new internal node with the specified hash, key, value, and next pointer.
         *
         * @param hash  the hash value for the key
         * @param key   the key associated with the mapping
         * @param value the value associated with the mapping
         * @param next  the next node in the bucket's collision chain
         */
        private Node(final int hash, final K key, final V value, final Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        /**
         * Replaces the value corresponding to this entry with the specified value.
         *
         * @param newValue new value to be stored in this entry
         * @return the old value corresponding to the entry
         */
        public V setValue(final V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        /**
         * Returns a string representation of this node in the format {@code key=value}.
         *
         * @return string representation of the entry
         */
        public final String toString() {
            return key + "=" + value;
        }
    }

    /**
     * A {@link Set} view of the key-value mappings contained in this map.
     * <p>
     * The set is backed by the map, so changes to the map affect the set, and vice versa.
     * It supports element removal via {@link Iterator#remove()}, {@link Set#remove(Object)},
     * and {@link Set#clear()}, which safely mutate the underlying {@code CustomMap}.
     */
    private final class EntrySetView extends AbstractSet<Entry<K, V>> {

        /**
         * Removes all mappings from the backing {@code CustomMap}, leaving the entry set empty.
         */
        public void clear() {
            CustomMap.this.clear();
        }

        /**
         * Returns {@code true} if this set contains the specified mapping.
         *
         * @param o object to be checked for containment in this entry set
         * @return {@code true} if the specified object is a map entry present in the map, {@code false} otherwise
         */
        public boolean contains(final Object o) {
            if (!(o instanceof Entry<?, ?> entry))
                return false;
            Object k = entry.getKey();
            Object v = entry.getValue();
            V mappedVal = CustomMap.this.get(k);
            return mappedVal != null && mappedVal.equals(v);
        }

        /**
         * Returns an iterator over the key-value mappings in this entry set.
         *
         * @return an iterator over the map's entries
         */
        public Iterator<Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        /**
         * Removes the specified mapping from this set if it is present, effectively
         * removing the key-value mapping from the backing {@code CustomMap}.
         *
         * @param o object to be removed from this set, if present
         * @return {@code true} if the set contained the specified mapping and it was removed
         */
        public boolean remove(final Object o) {
            if (!(o instanceof Entry<?, ?> entry))
                return false;
            return CustomMap.this.remove(entry.getKey(), entry.getValue());
        }

        /**
         * Returns the number of key-value mappings in this set.
         *
         * @return the number of entries in the map
         */
        public int size() {
            return CustomMap.this.size();
        }
    }

    /**
     * An internal, mutable red-black tree node, doubling as this map's
     * {@link Map.Entry} implementation. A {@code Node} is directly wired into
     * the tree structure via its {@link #left}, {@link #right}, and
     * {@link #parent} links.
     *
     * <p>Nodes are handed out by the entry-set iterators so that
     * {@link #setValue(Object)} writes through to the map, as the {@link Map}
     * contract requires. They must not be retained after the iterator has
     * advanced, and the structural links are never exposed.
     *
     * @param <K> the type of the key held by this node
     * @param <V> the type of the value held by this node
     */
    private static final class TreeNode<K, V> extends Node<K, V> {

        private TreeNode<K, V> parent;
        private TreeNode<K, V> previous;
        private TreeNode<K, V> left;
        private TreeNode<K, V> right;

        private boolean RED;

        /**
         * Constructs a new red-black tree node with the specified hash, key, value, and next pointer.
         *
         * @param hash  the hash value for the key
         * @param key   the key associated with the mapping
         * @param value the value associated with the mapping
         * @param next  the next node in the collision chain
         */
        private TreeNode(final int hash, final K key, final V value, final Node<K, V> next) {
            super(hash, key, value, next);
        }

        /**
         * Compares two keys for ordering purposes within the red-black tree, utilizing
         * hash codes, {@link Comparable} implementation if available, and fallback identity hash codes.
         *
         * @param h1 the hash of the first key
         * @param h2 the hash of the second key
         * @param k1 the first key
         * @param k2 the second key
         * @return a negative integer, zero, or a positive integer as the first key is less than, equal to, or greater than the second
         */
        private int compare(final int h1, final int h2, final K k1, final K k2) {
            if (h1 != h2)
                return h1 < h2 ? -1 : 1;
            if (k1 instanceof Comparable) {
                int cmp = ((Comparable<? super K>) k1).compareTo(k2);
                if (cmp != 0)
                    return cmp;
            }
            return System.identityHashCode(k1) <= System.identityHashCode(k2) ? -1 : 1;
        }

        /**
         * Re-balances the red-black tree after a node deletion operation.
         *
         * @param root the root of the tree
         * @param x    the node being adjusted or its replacement
         * @return the updated root of the tree
         */
        private static <K, V> TreeNode<K, V> balanceDeletion(TreeNode<K, V> root, TreeNode<K, V> x) {
            for (TreeNode<K, V> grandparentNode, parentLeft, parentRight; ; ) {
                if (x == null || x == root)
                    return root;
                else if ((grandparentNode = x.parent) == null) {
                    x.RED = false;
                    return x;
                } else if (x.RED) {
                    x.RED = false;
                    return root;
                } else if ((parentLeft = grandparentNode.left) == x) {
                    if ((parentRight = grandparentNode.right) != null && parentRight.RED) {
                        parentRight.RED = false;
                        grandparentNode.RED = true;
                        root = rotateLeft(root, grandparentNode);
                        parentRight = (grandparentNode = x.parent) == null ? null : grandparentNode.right;
                    }
                    if (parentRight == null)
                        x = grandparentNode;
                    else {
                        TreeNode<K, V> siblingLeft = parentRight.left, siblingRight = parentRight.right;
                        if ((siblingRight == null || !siblingRight.RED) && (siblingLeft == null || !siblingLeft.RED)) {
                            parentRight.RED = true;
                            x = grandparentNode;
                        } else {
                            if (siblingRight == null || !siblingRight.RED) {
                                if (siblingLeft != null)
                                    siblingLeft.RED = false;
                                parentRight.RED = true;
                                root = rotateRight(root, parentRight);
                                parentRight = (grandparentNode = x.parent) == null ? null : grandparentNode.right;
                            }
                            if (parentRight != null) {
                                parentRight.RED = grandparentNode != null && grandparentNode.RED;
                                if ((siblingRight = parentRight.right) != null)
                                    siblingRight.RED = false;
                            }
                            if (grandparentNode != null) {
                                grandparentNode.RED = false;
                                root = rotateLeft(root, grandparentNode);
                            }
                            x = root;
                        }
                    }
                } else {
                    if (parentLeft != null && parentLeft.RED) {
                        parentLeft.RED = false;
                        grandparentNode.RED = true;
                        root = rotateRight(root, grandparentNode);
                        parentLeft = (grandparentNode = x.parent) == null ? null : grandparentNode.left;
                    }
                    if (parentLeft == null)
                        x = grandparentNode;
                    else {
                        TreeNode<K, V> siblingLeft = parentLeft.left, siblingRight = parentLeft.right;
                        if ((siblingLeft == null || !siblingLeft.RED) && (siblingRight == null || !siblingRight.RED)) {
                            parentLeft.RED = true;
                            x = grandparentNode;
                        } else {
                            if (siblingLeft == null || !siblingLeft.RED) {
                                if (siblingRight != null)
                                    siblingRight.RED = false;
                                parentLeft.RED = true;
                                root = rotateLeft(root, parentLeft);
                                parentLeft = (grandparentNode = x.parent) == null ? null : grandparentNode.left;
                            }
                            if (parentLeft != null) {
                                parentLeft.RED = grandparentNode != null && grandparentNode.RED;
                                if ((siblingLeft = parentLeft.left) != null)
                                    siblingLeft.RED = false;
                            }
                            if (grandparentNode != null) {
                                grandparentNode.RED = false;
                                root = rotateRight(root, grandparentNode);
                            }
                            x = root;
                        }
                    }
                }
            }
        }

        /**
         * Re-balances the red-black tree after a new node insertion.
         *
         * @param root        the root of the tree
         * @param currentNode the newly inserted node
         * @return the updated root of the tree
         */
        private static <K, V> TreeNode<K, V> balanceInsertion(TreeNode<K, V> root, TreeNode<K, V> currentNode) {
            currentNode.RED = true;
            for (TreeNode<K, V> parentNode, grandparentNode, grandparentLeft, grandparentRight; ; ) {
                if ((parentNode = currentNode.parent) == null) {
                    currentNode.RED = false;
                    return currentNode;
                } else if (!parentNode.RED || (grandparentNode = parentNode.parent) == null)
                    return root;
                if (parentNode == grandparentNode.left) {
                    if ((grandparentRight = grandparentNode.right) != null && grandparentRight.RED) {
                        grandparentRight.RED = false;
                        parentNode.RED = false;
                        grandparentNode.RED = true;
                        currentNode = grandparentNode;
                    } else {
                        if (currentNode == parentNode.right) {
                            root = rotateLeft(root, currentNode = parentNode);
                            grandparentNode = (parentNode = currentNode.parent) == null ? null : parentNode.parent;
                        }
                        if (parentNode != null) {
                            parentNode.RED = false;
                            if (grandparentNode != null) {
                                grandparentNode.RED = true;
                                root = rotateRight(root, grandparentNode);
                            }
                        }
                    }
                } else if ((grandparentLeft = grandparentNode.left) != null && grandparentLeft.RED) {
                    grandparentLeft.RED = false;
                    parentNode.RED = false;
                    grandparentNode.RED = true;
                    currentNode = grandparentNode;
                } else {
                    if (currentNode == parentNode.left) {
                        root = rotateRight(root, currentNode = parentNode);
                        grandparentNode = (parentNode = currentNode.parent) == null ? null : parentNode.parent;
                    }
                    if (parentNode != null) {
                        parentNode.RED = false;
                        if (grandparentNode != null) {
                            grandparentNode.RED = true;
                            root = rotateLeft(root, grandparentNode);
                        }
                    }
                }
            }
        }

        /**
         * Finds and returns the tree node matching the specified hash and key starting from this node.
         *
         * @param hash the hash value to look up
         * @param o    the key object to look up
         * @return the matching tree node, or {@code null} if not found
         */
        private TreeNode<K, V> find(final int hash, final Object o) {
            TreeNode<K, V> p = this;
            do {
                int ph = p.hash;
                K pk = p.key;
                if (ph > hash)
                    p = p.left;
                else if (ph < hash)
                    p = p.right;
                else if (Objects.equals(o, pk))
                    return p;
                else {
                    int dir = compare(hash, ph, (K) o, pk);
                    p = (dir < 0) ? p.left : p.right;
                }
            } while (p != null);
            return null;
        }

        /**
         * Locates a tree node by hash and key, beginning at the root of the tree structure.
         *
         * @param hash the hash value to look up
         * @param o    the key object to look up
         * @return the matching tree node, or {@code null} if not found
         */
        private TreeNode<K, V> getTreeNode(final int hash, final Object o) {
            return ((parent != null) ? root() : this).find(hash, o);
        }

        /**
         * Ensures that the given root node of a red-black tree bin is placed at the front
         * of the bucket's collision chain in the table.
         *
         16 * @param tab  the hash table array
         * @param root the root node of the tree
         */
        private static <K, V> void moveRootToFront(final Node<K, V>[] tab, final TreeNode<K, V> root) {
            int n;
            if (root != null && tab != null && (n = tab.length) > 0) {
                int index = (n - 1) & root.hash;
                TreeNode<K, V> first = (TreeNode<K, V>) tab[index];
                if (root != first) {
                    Node<K, V> rn;
                    tab[index] = root;
                    TreeNode<K, V> rp = root.previous;
                    if ((rn = root.next) != null)
                        ((TreeNode<K, V>) rn).previous = rp;
                    if (rp != null)
                        rp.next = rn;
                    if (first != null)
                        first.previous = root;
                    root.next = first;
                    root.previous = null;
                }
            }
        }

        /**
         * Inserts a new key-value mapping into the red-black tree structure.
         *
         * @param tab the hash table array
         * @param h   the hash value
         * @param k   the key
         * @param v   the value
         * @return the existing node if the key is already present, or {@code null} if a new node was inserted
         */
        private TreeNode<K, V> putTreeVal(final Node<K, V>[] tab, final int h, final K k, final V v) {
            TreeNode<K, V> root = (parent != null) ? root() : this;
            for (TreeNode<K, V> p = root; ; ) {
                int dir;
                int ph = p.hash;
                K pk = p.key;
                if (ph > h)
                    dir = -1;
                else if (ph < h)
                    dir = 1;
                else if (pk == k || (k.equals(pk)))
                    return p;
                else
                    dir = compare(h, ph, k, pk);
                TreeNode<K, V> xp = p;
                if ((p = (dir <= 0) ? p.left : p.right) == null) {
                    Node<K, V> xpn = xp.next;
                    TreeNode<K, V> x = new TreeNode<>(h, k, v, xpn);
                    if (dir <= 0)
                        xp.left = x;
                    else
                        xp.right = x;
                    xp.next = x;
                    x.parent = x.previous = xp;
                    if (xpn != null)
                        ((TreeNode<K, V>) xpn).previous = x;
                    moveRootToFront(tab, balanceInsertion(root, x));
                    return null;
                }
            }
        }

        /**
         * Removes this node from the red-black tree and re-balances the tree structure,
         * untreeifying the bin if the remaining nodes fall below the threshold.
         *
         * @param tab the hash table array
         */
        private void removeTreeNode(final Node<K, V>[] tab) {
            int n;
            if (tab == null || (n = tab.length) == 0)
                return;
            int index = (n - 1) & hash;
            TreeNode<K, V> first = (TreeNode<K, V>) tab[index];
            TreeNode<K, V> root = first;
            TreeNode<K, V> rl;
            TreeNode<K, V> successor = (TreeNode<K, V>) next;
            TreeNode<K, V> predecessor = previous;
            if (predecessor == null)
                tab[index] = first = successor;
            else
                predecessor.next = successor;
            if (successor != null)
                successor.previous = predecessor;
            if (first == null)
                return;
            if (root.parent != null)
                root = root.root();
            if (root == null || ((root.right == null || (rl = root.left) == null || rl.left == null))) {
                tab[index] = first.untreeify();
                return;
            }
            TreeNode<K, V> p = this, pl = left, pr = right, replacement;
            if (pl != null && pr != null) {
                TreeNode<K, V> s = pr, sl;
                while ((sl = s.left) != null)
                    s = sl;
                boolean c = s.RED;
                s.RED = p.RED;
                p.RED = c;
                TreeNode<K, V> sr = s.right;
                TreeNode<K, V> pp = p.parent;
                if (s == pr) {
                    p.parent = s;
                    s.right = p;
                } else {
                    TreeNode<K, V> sp = s.parent;
                    if ((p.parent = sp) != null)
                        if (s == sp.left)
                            sp.left = p;
                        else
                            sp.right = p;
                    if ((s.right = pr) != null)
                        pr.parent = s;
                }
                p.left = null;
                if ((p.right = sr) != null)
                    sr.parent = p;
                if ((s.left = pl) != null)
                    pl.parent = s;
                if ((s.parent = pp) == null)
                    root = s;
                else if (p == pp.left)
                    pp.left = s;
                else
                    pp.right = s;
                replacement = sr;
            } else
                replacement = Objects.requireNonNullElseGet(pl, () -> Objects.requireNonNullElse(pr, p));
            if (replacement != p) {
                TreeNode<K, V> pp = replacement.parent = p.parent;
                if (pp == null)
                    root = replacement;
                else if (p == pp.left)
                    pp.left = replacement;
                else
                    pp.right = replacement;
                p.left = p.right = p.parent = null;
            }
            TreeNode<K, V> r = p.RED ? root : balanceDeletion(root, replacement);
            if (replacement == p) {
                TreeNode<K, V> pp = p.parent;
                p.parent = null;
                if (pp != null)
                    if (p == pp.left)
                        pp.left = null;
                    else if (p == pp.right)
                        pp.right = null;
            }
            moveRootToFront(tab, r);
        }

        /**
         * Traverses parent links to find and return the root node of this red-black tree.
         *
         * @return the root node of the tree
         */
        private TreeNode<K, V> root() {
            for (TreeNode<K, V> r = this, p; ; ) {
                if ((p = r.parent) == null)
                    return r;
                r = p;
            }
        }

        /**
         * Performs a left rotation on the red-black tree around the specified node.
         *
         * @param root the current root of the tree
         * @param p    the pivot node for rotation
         * @return the new root of the tree
         */
        private static <K, V> TreeNode<K, V> rotateLeft(TreeNode<K, V> root, final TreeNode<K, V> p) {
            TreeNode<K, V> r = p.right;
            p.right = r.left;
            if (r.left != null)
                r.left.parent = p;
            r.parent = p.parent;
            if (p.parent == null)
                root = r;
            else if (p.parent.left == p)
                p.parent.left = r;
            else
                p.parent.right = r;
            r.left = p;
            p.parent = r;
            return root;
        }

        /**
         * Performs a right rotation on the red-black tree around the specified node.
         *
         * @param root the current root of the tree
         * @param p    the pivot node for rotation
         * @return the new root of the tree
         */
        private static <K, V> TreeNode<K, V> rotateRight(TreeNode<K, V> root, final TreeNode<K, V> p) {
            TreeNode<K, V> l = p.left;
            p.left = l.right;
            if (l.right != null)
                l.right.parent = p;
            l.parent = p.parent;
            if (p.parent == null)
                root = l;
            else if (p.parent.right == p)
                p.parent.right = l;
            else
                p.parent.left = l;
            l.right = p;
            p.parent = l;
            return root;
        }

        /**
         * Splits a tree bin into low and high bins during table resizing,
         * untreeifying them if they drop below the threshold.
         *
         * @param tab   the hash table array
         * @param index the current bucket index
         * @param bit   the bit mask used for partitioning
         */
        private void split(final Node<K, V>[] tab, final int index, final int bit) {
            TreeNode<K, V> b = this;
            TreeNode<K, V> loHead = null, loTail = null;
            TreeNode<K, V> hiHead = null, hiTail = null;
            int lc = 0, hc = 0;

            for (TreeNode<K, V> e = b, next; e != null; e = next) {
                next = (TreeNode<K, V>) e.next;
                e.next = null;
                if ((e.hash & bit) == 0) {
                    if ((e.previous = loTail) == null)
                        loHead = e;
                    else
                        loTail.next = e;
                    loTail = e;
                    ++lc;
                } else {
                    if ((e.previous = hiTail) == null)
                        hiHead = e;
                    else
                        hiTail.next = e;
                    hiTail = e;
                    ++hc;
                }
            }

            if (loHead != null)
                if (lc <= UNTREEIFY_THRESHOLD)
                    tab[index] = loHead.untreeify();
                else {
                    tab[index] = loHead;
                    if (hiHead != null)
                        loHead.treeify(tab);
                }
            if (hiHead != null)
                if (hc <= UNTREEIFY_THRESHOLD)
                    tab[index + bit] = hiHead.untreeify();
                else {
                    tab[index + bit] = hiHead;
                    if (loHead != null)
                        hiHead.treeify(tab);
                }
        }

        /**
         * Forms a balanced red-black tree out of the linked list nodes starting from this node.
         *
         * @param tab the hash table array
         */
        private void treeify(final Node<K, V>[] tab) {
            TreeNode<K, V> root = null;
            for (TreeNode<K, V> x = this, next; x != null; x = next) {
                next = (TreeNode<K, V>) x.next;
                x.left = x.right = null;
                if (root == null) {
                    x.parent = null;
                    x.RED = false;
                    root = x;
                } else
                    for (TreeNode<K, V> p = root; ; ) {
                        int dir = compare(x.hash, p.hash, x.key, p.key);
                        TreeNode<K, V> xp = p;
                        if ((p = (dir <= 0) ? p.left : p.right) == null) {
                            x.parent = xp;
                            if (dir <= 0)
                                xp.left = x;
                            else
                                xp.right = x;
                            root = balanceInsertion(root, x);
                            break;
                        }
                    }
            }
            moveRootToFront(tab, root);
        }

        /**
         * Converts this tree node structure back into a standard singly-linked list of {@link Node} instances.
         *
         * @return the head node of the new linked list
         */
        private Node<K, V> untreeify() {
            Node<K, V> hd = null, tl = null;
            for (Node<K, V> q = this; q != null; q = q.next) {
                Node<K, V> p = new Node<>(q.hash, q.key, q.value, null);
                if (tl == null)
                    hd = p;
                else
                    tl.next = p;
                tl = p;
            }
            return hd;
        }
    }

    /**
     * A {@link Collection} view of the values contained in this map[cite: 2].
     * <p>
     * The collection supports removal operations and clearing via {@link Collection#remove(Object)},
     * {@link Collection#clear()}, and iterator removal, which safely mutate the underlying {@code CustomMap}.
     */
    private final class ValuesView extends AbstractCollection<V> {

        /**
         * Removes all mappings from the backing {@code CustomMap}, leaving the values collection empty.
         */
        public void clear() {
            CustomMap.this.clear();
        }

        /**
         * Returns {@code true} if this collection maps one or more keys to the specified value.
         *
         * @param o value whose presence in this collection is to be tested
         * @return {@code true} if the specified value is mapped to by at least one key, {@code false} otherwise
         */
        public boolean contains(final Object o) {
            return CustomMap.this.containsValue(o);
        }

        /**
         * Returns an iterator over the elements in this values collection.
         *
         * @return an iterator over the map's values
         */
        public Iterator<V> iterator() {
            return new ValueIterator();
        }

        /**
         * Returns the number of values in this collection.
         *
         * @return the number of value mappings in the map
         */
        public int size() {
            return CustomMap.this.size();
        }
    }

    /**
     * An iterator over the key-value entries contained in this map.
     * <p>
     * Traverses the internal table sequentially across buckets, returning write-through
     * {@link Map.Entry} instances that reflect and modify the underlying map.
     */
    private final class EntryIterator implements Iterator<Entry<K, V>> {

        private int bucketIndex = 0;

        private Node<K, V> nextNode, lastReturned = null;

        /**
         * Constructs a new {@code EntryIterator} and advances to the first valid entry node.
         */
        private EntryIterator() {
            advanceToNextNode();
        }

        /**
         * Advances the internal pointer to the next available entry node across the map's buckets.
         */
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

        /**
         * Returns {@code true} if the iteration has more entries.
         *
         * @return {@code true} if there is at least one more entry to iterate over
         */
        public boolean hasNext() {
            return nextNode != null;
        }

        /**
         * Returns the next entry in the iteration.
         *
         * @return the next map entry
         * @throws NoSuchElementException if the iteration has no more elements
         */
        public Entry<K, V> next() {
            if (nextNode == null)
                throw new NoSuchElementException();
            lastReturned = nextNode;
            Node<K, V> current = nextNode;
            advanceToNextNode();

            return new SimpleEntry<>(current.key, current.value) {
                public V setValue(V value) {
                    return current.setValue(value);
                }
            };
        }

        /**
         * Removes from the underlying map the last entry returned by this iterator.
         * This method can be called only once per call to {@link #next()}.
         *
         * @throws IllegalStateException if the {@code next} method has not yet been called,
         *         or the {@code remove} method has already been called after the last call
         *         to the {@code next} method
         */
        public void remove() {
            if (lastReturned == null)
                throw new IllegalStateException();
            CustomMap.this.remove(lastReturned.key);
            lastReturned = null;
        }
    }

    /**
     * An iterator over the values contained in this map.
     * <p>
     * Traverses the internal table sequentially across buckets, returning values
     * while supporting safe element removal from the backing {@code CustomMap} via {@link #remove()}.
     */
    private final class ValueIterator implements Iterator<V> {

        private int bucketIndex = 0;

        private Node<K, V> lastReturned, nextNode = null;

        /**
         * Constructs a new {@code ValueIterator} and advances to the first valid value node.
         */
        private ValueIterator() {
            advanceToNextNode();
        }

        /**
         * Advances the internal pointer to the next available value node across the map's buckets.
         */
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

        /**
         * Returns {@code true} if the iteration has more values.
         *
         * @return {@code true} if there is at least one more value to iterate over
         */
        public boolean hasNext() {
            return nextNode != null;
        }

        /**
         * Returns the next value in the iteration.
         *
         * @return the next value
         * @throws NoSuchElementException if the iteration has no more elements
         */
        public V next() {
            if (nextNode == null)
                throw new NoSuchElementException();
            lastReturned = nextNode;
            V value = nextNode.value;
            advanceToNextNode();
            return value;
        }

        /**
         * Removes from the underlying map the mapping whose value was last returned by this iterator.
         * This method can be called only once per call to {@link #next()}.
         *
         * @throws IllegalStateException if the {@code next} method has not yet been called,
         *         or the {@code remove} method has already been called after the last call
         *         to the {@code next} method
         */
        public void remove() {
            if (lastReturned == null)
                throw new IllegalStateException();
            CustomMap.this.remove(lastReturned.key);
            lastReturned = null;
        }
    }
}