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

