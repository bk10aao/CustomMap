# Map
Implementation of a Map using an array

## Methods
1. `CustomMap(K key, V value)` - constructor.
2. `CustomMap(K key, V value, int mapSize)` - constructor.
3. `void clear()` - resets map to empty.
4. `boolean containsKey(K key)` - returns true if contains key, else false. Throws NullPointerException on null key.
5. `boolean containsValue(V value)` - returns true if contains value, else false. Throws NullPointerException on null value.
6. `boolean equals(CustomMap<K, V> o)` - returns true if the map matches, else false.
7. `V get(K key)` - returns value associated to key, else null if key not present. Throws NullPointerException on null key. 
8. `V getOrDefault(K key, V defaultValue)` - get value associated to key if present, else return default value. Throws NullPointerException on null key.
9. `int hashCode()` - get hashcode for Map.
10. `Set keySet()` - returns Set of keys in map.
11. `V put(K key, V value)` - puts value into map with associated key. Returns item previously associated with key if present, else null. Throws IllegalArgumentException on non-matching key type or value type. Throws NullPointerException if key or value is null.
12. `V putIfAbsent(K key, V value)` - puts value in map if not present, returns previous associated item if present else null. Throws NullPointerException on null key or null value.
13. `V remove(K key)` - remove and return value from map if present else returns null. Throws NullPointerException on null key.
14. `boolean remove(K key, V value)` - removes value from map if key is present and matches value. Throws NullPointerException on null key or value.
15. `V replace(K key, V value)` - replace the value for associated key. Returns item previously associated to key, else false. throws NullPointerException on null key or value.
16. `boolean replace(K key, V oldValue, V newValue)` - replaces value in map if associated value matches previous value. Returns true if successful, else false. Throws NullPointerException on null key, oldValue or newValue.
17. `int size()` - returns number of items in map.
18. `String toString()` - returns string representation of map.
19. `Collection<V> values()` - returns a collection of all values associated to keys in map.

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