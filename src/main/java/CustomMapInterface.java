import java.util.Collection;
import java.util.Set;

public interface CustomMapInterface<K, V> {

    /**
     * Clear all items from Map.
     */
    void clear();

    /**
     * Check if Map contains Key
     * @param key - key for value to be searched for in Map.
     * @return boolean true if contains key, else false.
     * @throws NullPointerException on null key.
     */
    boolean containsKey(K key);

    /**
     * Check if Map contains Value
     * @param value - value to be searched for in Map.
     * @return boolean true if contains value, else false.
     */
    boolean containsValue(V value);

    /**
     * Check if Map is equal to provided map
     * @param map - map to be compared to.
     * @return boolean true if equals, else false.
     */
     boolean equals(CustomMap<K, V> map);

    /**
     * Get Value by key.
     * @param key - key of value to be returned.
     * @return value associated to key, else null.
     * @throws NullPointerException on null key.
     */
    V get(K key);

    /**
     * Get Value by key.
     * @param key - key of value to be returned.
     * @param defaultValue - value to be returned if not present
     * @return value associated to key, or default value.
     * @throws NullPointerException on null key.
     */
    V getOrDefault(K key, V defaultValue);

    /**
     * Get hashcode for Map.
     * @return hashcode of map.
     */
    int hashCode();

    /**
     * Get keys as Set.
     * @return Set of keys in map.
     */
    Set keySet();

    /**
     * Put value in map
     * @param key - key to map to value
     * @param value - value to be associated to key
     * @throws IllegalArgumentException on non-matching key type or value type
     * @throws NullPointerException on null key
     * @return V item for previous key, else null if not previously used
     */
    V put(final K key, final V value);

    /**
     * Put value in map if not previously present, else returns previous value
     * @param key - key to map to value
     * @param value - value to be associated to key
     * @throws NullPointerException on null key
     * @throws NullPointerException on null value
     * @return V item for previous key, else null if not previously used
     */
    V putIfAbsent(final K key, final V value);

    /**
     * Remove item from map if key exist
     * @param key - key of item to remove
     * @throws NullPointerException on null key
     * @return V item for previous key, else null if not previously used
     */
    V remove(final K key);

    /**
     * Remove item from map if key and exists and value matches what is associated to key
     * @param key - key of item to remove
     * @param value - value of item that must match key
     * @throws NullPointerException on null key
     * @throws NullPointerException on null value
     * @return true if removed, else false;
     */
    boolean remove(final K key, final V value);

    /**
     * Replace value if key exists in Map
     * @param key - key for associated value
     * @param value - new value to be set
     * @throws NullPointerException on null key
     * @throws NullPointerException on null old value
     * @return V item previously associated to key, else null
     */
    V replace(K key, V value);

    /**
     * Replace value if key exists in Map
     * @param key - key for associated value
     * @param oldValue - new value to be set
     * @param newValue - new value to be set
     * @throws NullPointerException on null key
     * @throws NullPointerException on null old value
     * @throws NullPointerException on null new value
     * @return true if value is replaced, else false
     */
     boolean replace(K key, V oldValue, V newValue);

    /**
     * Get number of items in Map
     * @return - number of items in Map
     */
    int size();

    /**
     * Get String representation of Map
     * @return - String representation of Map
     */
    String toString();

    /**
     * Get all values associated to all keys in Map
     * @return Collection of all values associated to all keys in Map
     */
    Collection<V> values();
}
