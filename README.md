# Map
Implementation of a Map using an array

# Methods
1. CustomMap(T key, T value) - constructor.
2. CustomMap(T key, T value, T mapSize) - constructor.
3. clear() - resets map to empty.
4. containsKey(T key) - returns true if contains key, else false. Throws NullPointerException on null key.
5. containsValue(T value) - returns true if contains value, else false. Throws NullPointerException on null value.
6. equals(Object map) - returns true if the map matches, else false.
7. get(T key) - returns object associated to key, else null if key not present. Throws NullPointerException on null key. 
8. getOrDefault(T key, T defaultValue) - get value associated to key if present, else return default value. Throws NullPointerException on null key.
9. keySet() - returns Set of keys in map.
10. put(T key, T value) - puts value into map with associated key. Returns item previous associated with key if present, else null. Throws IllegalArgumentException on non-matching key type or value type. Throws NullPointerException if key or value is null.
11. putIfAbsent(T key, T value) - puts value in map if not present, returns previous associated item if present else null. Throws NullPointerException on null key or null value.
12. remove(T key) - remove and return item from map if present else returns null. Throws NullPointerException on null key.
13. remove(T key, T value) - removes value from map if key is present and matches value. Throws NullPointerException on null key or value.
14. replace(T key, T value) - replace the value for associated key. Returns item previously associated to key, else false. throws NullPointerException on null key or value.
15. replace(T key, T oldValue, T newValue) - replaces value in map if associated value matches previous value. Returns true if successful. Throws NullPointerException on null key, oldValue or newValue.
16. size() - returns number of items in map.
17. values() - returns a collection of all values associated to keys in map.