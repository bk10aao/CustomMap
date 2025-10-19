# Map
Implementation of a Map using an array

All methods implemented are identical to those found in the Java [Map](https://docs.oracle.com/javase/8/docs/api/java/util/Map.html) interface.

## Build and Test

1. To build and test the project run command `./gradlew clean build`
2. To test the project run command `gradle test --tests CustomMapTest`

## Time Complexity

| Method                              | CustomMap                             | HashMap                               | Winner |
|-------------------------------------|---------------------------------------|---------------------------------------|--------|
| **clear()**                         | O(m)                                  | O(m)                                  | Tie    |
| **compute(K, BiFunction)**          | O(1) average, O(n) worst              | O(1) average, O(n) worst              | Tie    |
| **computeIfAbsent(K, Function)**    | O(1) average, O(n) worst              | O(1) average, O(n) worst              | Tie    |
| **computeIfPresent(K, BiFunction)** | O(1) average, O(n) worst              | O(1) average, O(n) worst              | Tie    |
| **containsKey(Object)**             | O(1) average, O(n) worst              | O(1) average, O(n) worst              | Tie    |
| **containsValue(Object)**           | O(n)                                  | O(n)                                  | Tie    |
| **entrySet()**                      | O(n)                                  | O(n)                                  | Tie    |
| **equals(Object)**                  | O(n)                                  | O(n)                                  | Tie    |
| **expand()**                        | O(m + n)                              | O(m + n)                              | Tie    |
| **forEach(BiConsumer)**             | O(n)                                  | O(n)                                  | Tie    |
| **get(Object)**                     | O(1) average, O(n) worst              | O(1) average, O(n) worst              | Tie    |
| **getOrDefault(Object, V)**         | O(1) average, O(n) worst              | O(1) average, O(n) worst              | Tie    |
| **hash(Object)**                    | O(1)                                  | O(1)                                  | Tie    |
| **hashCode()**                      | O(n)                                  | O(n)                                  | Tie    |
| **isEmpty()**                       | O(1)                                  | O(1)                                  | Tie    |
| **keySet()**                        | O(n)                                  | O(n)                                  | Tie    |
| **merge(K, V, BiFunction)**         | O(1) average, O(n) worst              | O(1) average, O(n) worst              | Tie    |
| **put(K, V)**                       | O(1) average, O(n) worst              | O(1) average, O(n) worst              | Tie    |
| **putAll(Map)**                     | O(m' + n') average, O(m' + n'n) worst | O(m' + n') average, O(m' + n'n) worst | Tie    |
| **putIfAbsent(K, V)**               | O(1) average, O(n) worst              | O(1) average, O(n) worst              | Tie    |
| **reduce()**                        | O(m + n)                              | O(m + n)                              | Tie    |
| **remove(Object)**                  | O(1) average, O(n) worst              | O(1) average, O(n) worst              | Tie    |
| **remove(Object, Object)**          | O(1) average, O(n) worst              | O(1) average, O(n) worst              | Tie    |
| **replace(K, V)**                   | O(1) average, O(n) worst              | O(1) average, O(n) worst              | Tie    |
| **replace(K, V, V)**                | O(1) average, O(n) worst              | O(1) average, O(n) worst              | Tie    |
| **replaceAll(BiFunction)**          | O(n)                                  | O(n)                                  | Tie    |
| **size()**                          | O(1)                                  | O(1)                                  | Tie    |
| **toString()**                      | O(n)                                  | O(n)                                  | Tie    |
| **values()**                        | O(n)                                  | O(n)                                  | Tie    |

## Space Complexity

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

![Combined Performance Charts](PerformanceTesting/clear().png)
![Combined Performance Charts](PerformanceTesting/compute(K,Bifunction).png)
![Combined Performance Charts](PerformanceTesting/computeIfPresent(K,BiFunction).png)
![Combined Performance Charts](PerformanceTesting/computerIfAbsent(K,Function).png)
![Combined Performance Charts](PerformanceTesting/containsKey(K).png)
![Combined Performance Charts](PerformanceTesting/containsValue(V).png)
![Combined Performance Charts](PerformanceTesting/entrySet().png)
![Combined Performance Charts](PerformanceTesting/equals(Object%20o).png)
![Combined Performance Charts](PerformanceTesting/forEach(BiConsumer).png)
![Combined Performance Charts](PerformanceTesting/get(K,V).png)
![Combined Performance Charts](PerformanceTesting/getOrDefault(K,V).png)
![Combined Performance Charts](PerformanceTesting/keySet().png)
![Combined Performance Charts](PerformanceTesting/merge(K,V,BiFunction).png)
![Combined Performance Charts](PerformanceTesting/put(K,V).png)
![Combined Performance Charts](PerformanceTesting/putAll(Map).png)
![Combined Performance Charts](PerformanceTesting/putIfAbsent(K,V).png)
![Combined Performance Charts](PerformanceTesting/remove(K).png)
![Combined Performance Charts](PerformanceTesting/remove(K,V).png)
![Combined Performance Charts](PerformanceTesting/replace(K,V).png)
![Combined Performance Charts](PerformanceTesting/replace(K,V,V).png)
![Combined Performance Charts](PerformanceTesting/toString().png)
![Combined Performance Charts](PerformanceTesting/values().png)