# Map
Implementation of a Map using an array

## Methods
1. `CustomMap(Class<K> key, Class<V> value)` - Constructs an empty map with the specified key and value types and an initial capacity of 17 buckets.
    - **Throws**: `IllegalArgumentException` if the key or value class is null.
2. `CustomMap(Class<K> key, Class<V> value, int mapSize)` - Constructs an empty map with the specified key and value types and an initial capacity set to the smallest prime number greater than or equal to `mapSize`. Used internally for resizing.
    - **Throws**: `IllegalArgumentException` if the key or value class is null.
3. `void clear()` - Removes all mappings, resetting the map to an empty state with an initial capacity of 17 buckets.
4. `V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction)` - Computes a new value for the specified key using the remapping function. Removes the mapping if the function returns null.
    - **Returns**: The new value, or null if none.
    - **Throws**: `NullPointerException` if the key or remapping function is null; `IllegalArgumentException` if the key or computed value (if non-null) is not of the specified type.
5. `V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction)` - Computes and associates a value for the key if absent, using the mapping function. Does nothing if the function returns null.
    - **Returns**: The existing or computed value, or null if none.
    - **Throws**: `NullPointerException` if the key or mapping function is null; `IllegalArgumentException` if the key or computed value (if non-null) is not of the specified type.
6. `V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction)` - Computes a new value for the key if present, using the remapping function. Removes the mapping if the function returns null.
    - **Returns**: The new value, or null if none.
    - **Throws**: `NullPointerException` if the key or remapping function is null; `IllegalArgumentException` if the key or computed value (if non-null) is not of the specified type.
7. `boolean containsKey(Object key)` - Returns true if the map contains the specified key.
    - **Throws**: `NullPointerException` if the key is null; `ClassCastException` if the key is not of the specified type.
8. `boolean containsValue(Object value)` - Returns true if the map contains the specified value.
    - **Throws**: None (null values are allowed).
9. `Set<Map.Entry<K, V>> entrySet()` - Returns a new set containing all key-value mappings, unbacked by the map.
10. `boolean equals(Object o)` - Returns true if the specified object is a `CustomMap` with the same key and value types, size, and mappings.
11. `void forEach(BiConsumer<? super K, ? super V> action)` - Performs the specified action for each key-value mapping.
    - **Throws**: `NullPointerException` if the action is null.
12. `V get(Object key)` - Returns the value associated with the key, or null if absent.
    - **Throws**: `NullPointerException` if the key is null; `ClassCastException` if the key is not of the specified type.
13. `V getOrDefault(Object key, V defaultValue)` - Returns the value associated with the key, or the default value if absent.
    - **Throws**: `NullPointerException` if the key is null; `ClassCastException` if the key is not of the specified type.
14. `int hashCode()` - Returns the hash code for the map, based on the XOR of key and value hash codes.
15. `Set<K> keySet()` - Returns a new set containing all keys, unbacked by the map.
16. `V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction)` - Associates the value with the key if absent, or applies the remapping function to the existing and given values. Removes the mapping if the function returns null.
    - **Returns**: The new value, or null if none.
    - **Throws**: `NullPointerException` if the key, value, or remapping function is null; `IllegalArgumentException` if the key, value, or computed value (if non-null) is not of the specified type.
17. `V put(K key, V value)` - Associates the value with the key, replacing any existing value. Returns the previous value, or null if none.
    - **Throws**: `NullPointerException` if the key is null; `IllegalArgumentException` if the key or value (if non-null) is not of the specified type.
18. `void putAll(Map<? extends K, ? extends V> m)` - Copies all mappings from the specified map to this map.
    - **Throws**: `NullPointerException` if the map or any of its keys are null; `IllegalArgumentException` if any key or value is not of the specified type.
19. `V putIfAbsent(K key, V value)` - Associates the value with the key if absent. Returns the existing value, or null if none.
    - **Throws**: `NullPointerException` if the key or value is null; `IllegalArgumentException` if the key or value is not of the specified type.
20. `V remove(Object key)` - Removes and returns the value associated with the key, or null if absent.
    - **Throws**: `NullPointerException` if the key is null; `ClassCastException` if the key is not of the specified type.
21. `boolean remove(Object key, Object value)` - Removes the mapping for the key if its value matches the specified value. Returns true if removed.
    - **Throws**: `NullPointerException` if the key or value is null; `ClassCastException` if the key is not of the specified type.
22. `V replace(K key, V value)` - Replaces the value for the key if present. Returns the previous value, or null if none.
    - **Throws**: `NullPointerException` if the key or value is null; `IllegalArgumentException` if the key or value is not of the specified type.
23. `boolean replace(K key, V oldValue, V newValue)` - Replaces the value for the key if it matches the old value. Returns true if replaced.
    - **Throws**: `NullPointerException` if the key, old value, or new value is null; `IllegalArgumentException` if the key or new value is not of the specified type.
24. `void replaceAll(BiFunction<? super K, ? super V, ? extends V> function)` - Replaces each value with the result of applying the function to its key and value.
    - **Throws**: `NullPointerException` if the function is null; `IllegalArgumentException` if any computed value (if non-null) is not of the specified type.
25. `int size()` - Returns the number of key-value mappings in the map.
26. `String toString()` - Returns a string representation of the map in the format `{key1=value1, key2=value2, ...}`.
27. `Collection<V> values()` - Returns a new collection containing all values, unbacked by the map.
## Build and Test

1. To build and test the project run command `./gradlew clean build`
2. To test the project run command `gradle test --tests CustomMapTest`

## Time Complexity

| Method                              | CustomMap                         | HashMap                           | Winner |
|-------------------------------------|-----------------------------------|-----------------------------------|--------|
| **clear()**                         | O(m)                              | O(m)                              | Tie    |
| **compute(K, BiFunction)**          | O(1) avg, O(n) worst              | O(1) avg, O(n) worst              | Tie    |
| **computeIfAbsent(K, Function)**    | O(1) avg, O(n) worst              | O(1) avg, O(n) worst              | Tie    |
| **computeIfPresent(K, BiFunction)** | O(1) avg, O(n) worst              | O(1) avg, O(n) worst              | Tie    |
| **containsKey(Object)**             | O(1) avg, O(n) worst              | O(1) avg, O(n) worst              | Tie    |
| **containsValue(Object)**           | O(n)                              | O(n)                              | Tie    |
| **entrySet()**                      | O(n)                              | O(n)                              | Tie    |
| **equals(Object)**                  | O(n)                              | O(n)                              | Tie    |
| **expand()**                        | O(m + n)                          | O(m + n)                          | Tie    |
| **forEach(BiConsumer)**             | O(n)                              | O(n)                              | Tie    |
| **get(Object)**                     | O(1) avg, O(n) worst              | O(1) avg, O(n) worst              | Tie    |
| **getOrDefault(Object, V)**         | O(1) avg, O(n) worst              | O(1) avg, O(n) worst              | Tie    |
| **hash(Object)**                    | O(1)                              | O(1)                              | Tie    |
| **hashCode()**                      | O(n)                              | O(n)                              | Tie    |
| **isEmpty()**                       | O(1)                              | O(1)                              | Tie    |
| **keySet()**                        | O(n)                              | O(n)                              | Tie    |
| **merge(K, V, BiFunction)**         | O(1) avg, O(n) worst              | O(1) avg, O(n) worst              | Tie    |
| **put(K, V)**                       | O(1) avg, O(n) worst              | O(1) avg, O(n) worst              | Tie    |
| **putAll(Map)**                     | O(m' + n') avg, O(m' + n'n) worst | O(m' + n') avg, O(m' + n'n) worst | Tie    |
| **putIfAbsent(K, V)**               | O(1) avg, O(n) worst              | O(1) avg, O(n) worst              | Tie    |
| **reduce()**                        | O(m + n)                          | O(m + n)                          | Tie    |
| **remove(Object)**                  | O(1) avg, O(n) worst              | O(1) avg, O(n) worst              | Tie    |
| **remove(Object, Object)**          | O(1) avg, O(n) worst              | O(1) avg, O(n) worst              | Tie    |
| **replace(K, V)**                   | O(1) avg, O(n) worst              | O(1) avg, O(n) worst              | Tie    |
| **replace(K, V, V)**                | O(1) avg, O(n) worst              | O(1) avg, O(n) worst              | Tie    |
| **replaceAll(BiFunction)**          | O(n)                              | O(n)                              | Tie    |
| **size()**                          | O(1)                              | O(1)                              | Tie    |
| **toString()**                      | O(n)                              | O(n)                              | Tie    |
| **values()**                        | O(n)                              | O(n)                              | Tie    |
- 
## Time Complexity Comparison

| Method                              | CustomMap | HashMap  | Winner |
|-------------------------------------|-----------|----------|--------|
| **clear()**                         | O(m)      | O(m)     | Tie    |
| **compute(K, BiFunction)**          | O(1)      | O(1)     | Tie    |
| **computeIfAbsent(K, Function)**    | O(1)      | O(1)     | Tie    |
| **computeIfPresent(K, BiFunction)** | O(1)      | O(1)     | Tie    |
| **containsKey(Object)**             | O(1)      | O(1)     | Tie    |
| **containsValue(Object)**           | O(1)      | O(1)     | Tie    |
| **entrySet()**                      | O(n)      | O(n)     | Tie    |
| **equals(Object)**                  | O(n)      | O(n)     | Tie    |
| **expand()**                        | O(m + n)  | O(m + n) | Tie    |
| **forEach(BiConsumer)**             | O(1)      | O(1)     | Tie    |
| **get(Object)**                     | O(1)      | O(1)     | Tie    |
| **getOrDefault(Object, V)**         | O(1)      | O(1)     | Tie    |
| **hash(Object)**                    | O(1)      | O(1)     | Tie    |
| **hashCode()**                      | O(n)      | O(n)     | Tie    |
| **isEmpty()**                       | O(1)      | O(1)     | Tie    |
| **keySet()**                        | O(n)      | O(n)     | Tie    |
| **merge(K, V, BiFunction)**         | O(1)      | O(1)     | Tie    |
| **put(K, V)**                       | O(1)      | O(1)     | Tie    |
| **putAll(Map)**                     | O(m + n)  | O(m + n) | Tie    |
| **putIfAbsent(K, V)**               | O(1)      | O(1)     | Tie    |
| **reduce()**                        | O(m + n)  | O(m + n) | Tie    |
| **remove(Object)**                  | O(1)      | O(1)     | Tie    |
| **remove(Object, Object)**          | O(1)      | O(1)     | Tie    |
| **replace(K, V)**                   | O(1)      | O(1)     | Tie    |
| **replace(K, V, V)**                | O(1)      | O(1)     | Tie    |
| **replaceAll(BiFunction)**          | O(1)      | O(1)     | Tie    |
| **size()**                          | O(1)      | O(1)     | Tie    |
| **toString()**                      | O(n)      | O(n)     | Tie    |
| **values()**                        | O(n)      | O(n)     | Tie    |

**Notes**:
- **m**: Number of buckets in the map.
- **n**: Number of key-value mappings.
- **m'**: Number of buckets after resizing.
- **n'**: Number of entries in the input map.

# Performance vs Java HashMap

![Combined Performance Charts](PerformanceTesting/All_Map_Performance_Comparisons.png)