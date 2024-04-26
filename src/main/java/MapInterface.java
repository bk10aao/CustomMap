import java.util.Collection;
import java.util.Set;

public interface MapInterface<T> {

    /**
     * Clear all items from Map.
     */
    void clear();

    /**
     * Check if Map contains Key
     * @param key - item to be searched for in Map.
     * @return boolean true if contains key, else false.
     * @throws NullPointerException on null key.
     */
    boolean containsKey(T key);

    /**
     * Check if Map contains Value
     * @param value - item to be searched for in Map.
     * @return boolean true if contains value, else false.
     * @throws NullPointerException on null item.
     */
    boolean containsValue(T value);

    /**
     * Check if Map is equal to provided map
     * @param map - map to be compared to.
     * @return boolean true if equals, else false.
     */
     boolean equals(Object map);

    /**
     * Get Object by key.
     * @param key - key of item to be returned.
     * @return value associated to key, else null.
     * @throws NullPointerException on null key.
     */
    T get(T key);

    /**
     * Get Object by key.
     * @param key - key of item to be returned.
     * @param defaultValue - value to be returned if not present
     * @return value associated to key.
     * @throws NullPointerException on null key.
     */
    T getOrDefault(T key, T defaultValue);

    /**
     * Get keys as Set.
     * @return Set of keys in map.
     */
    Set keySet();

    /**
     * Put item in map
     * @param key - key to map to value
     * @param value - value to be associated to key
     * @throws IllegalArgumentException on non-matching key type or value type
     * @throws NullPointerException on null key
     * @return T item for previous key, else null if not previously used
     */
    T put(final T key, final T value);

    /**
     * Put item in map if not previously present, else returns previous value
     * @param key - key to map to value
     * @param value - value to be associated to key
     * @throws NullPointerException on null key
     * @throws NullPointerException on null value
     * @return T item for previous key, else null if not previously used
     */
    T putIfAbsent(final T key, final T value);

    /**
     * Remove item from map if key exist
     * @param key - key of item to remove
     * @throws NullPointerException on null key
     * @return T item for previous key, else null if not previously used
     */
    T remove(final T key);

    /**
     * Remove item from map if key and exists and value matches what is associated to key
     * @param key - key of item to remove
     * @param value - value of item that must match key
     * @throws NullPointerException on null key
     * @throws NullPointerException on null value
     * @return true if removed, else false;
     */
    boolean remove(final T key, final T value);

    /**
     * Replace value if key exists in Map
     * @param key - key for associated value
     * @param value - new value to be set
     * @throws NullPointerException on null key
     * @throws NullPointerException on null old value
     * @return T item previously associated to key, else null
     */
    T replace(T key, T value);

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
     boolean replace(T key, T oldValue, T newValue);

    /**
     * Get number of items in Map
     * @return - number of items in Map
     */
    int size();

    /**
     * Get all values associated to all keys in Map
     * @return Collection of all values associated to all keys in Map
     */
    Collection<T> values();
}
