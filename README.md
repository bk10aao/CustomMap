# Map
Implementation of a Map using an array

All methods implemented are identical to those found in the Java [Map](https://docs.oracle.com/javase/8/docs/api/java/util/Map.html) interface.

## Build and Test

1. To build and test the project run command `./gradlew clean build`

## Time Complexity

| Method                                |       CustomMap       |        HashMap        | Winner  |
|:--------------------------------------|:---------------------:|:---------------------:|:-------:|
| **`clear()`**                         |         O(m)          |         O(m)          | **Tie** |
| **`compute(K, BiFunction)`**          | O(1) avg / O(n) worst | O(1) avg / O(n) worst | **Tie** |
| **`computeIfAbsent(K, Function)`**    | O(1) avg / O(n) worst | O(1) avg / O(n) worst | **Tie** |
| **`computeIfPresent(K, BiFunction)`** | O(1) avg / O(n) worst | O(1) avg / O(n) worst | **Tie** |
| **`containsKey(Object)`**             | O(1) avg / O(n) worst | O(1) avg / O(n) worst | **Tie** |
| **`containsValue(Object)`**           |         O(n)          |         O(n)          | **Tie** |
| **`entrySet()`**                      |         O(1)          |         O(1)          | **Tie** |
| **`equals(Object)`**                  |         O(n)          |         O(n)          | **Tie** |
| **`expand()`**                        |       O(m + n)        |       O(m + n)        | **Tie** |
| **`forEach(BiConsumer)`**             |         O(n)          |         O(n)          | **Tie** |
| **`get(Object)`**                     | O(1) avg / O(n) worst | O(1) avg / O(n) worst | **Tie** |
| **`getOrDefault(Object, V)`**         | O(1) avg / O(n) worst | O(1) avg / O(n) worst | **Tie** |
| **`hash(Object)`**                    |         O(1)          |         O(1)          | **Tie** |
| **`hashCode()`**                      |         O(n)          |         O(n)          | **Tie** |
| **`isEmpty()`**                       |         O(1)          |         O(1)          | **Tie** |
| **`keySet()`**                        |         O(1)          |         O(1)          | **Tie** |
| **`merge(K, V, BiFunction)`**         | O(1) avg / O(n) worst | O(1) avg / O(n) worst | **Tie** |
| **`put(K, V)`**                       | O(1) avg / O(n) worst | O(1) avg / O(n) worst | **Tie** |
| **`putAll(Map)`**                     |   O(m_src + n_src)    |   O(m_src + n_src)    | **Tie** |
| **`putIfAbsent(K, V)`**               | O(1) avg / O(n) worst | O(1) avg / O(n) worst | **Tie** |
| **`reduce()`**                        |       O(m + n)        |       O(m + n)        | **Tie** |
| **`remove(Object)`**                  | O(1) avg / O(n) worst | O(1) avg / O(n) worst | **Tie** |
| **`remove(Object, Object)`**          | O(1) avg / O(n) worst | O(1) avg / O(n) worst | **Tie** |
| **`replace(K, V)`**                   | O(1) avg / O(n) worst | O(1) avg / O(n) worst | **Tie** |
| **`replace(K, V, V)`**                | O(1) avg / O(n) worst | O(1) avg / O(n) worst | **Tie** |
| **`replaceAll(BiFunction)`**          |         O(n)          |         O(n)          | **Tie** |
| **`size()`**                          |         O(1)          |         O(1)          | **Tie** |
| **`toString()`**                      |         O(n)          |         O(n)          | **Tie** |
| **`values()`**                        |         O(1)          |         O(1)          | **Tie** |

### Space Complexity

| Method                                | CustomMap | HashMap  | Winner  |
|:--------------------------------------|:---------:|:--------:|:-------:|
| **`clear()`**                         |   O(1)    |   O(1)   | **Tie** |
| **`compute(K, BiFunction)`**          |   O(1)    |   O(1)   | **Tie** |
| **`computeIfAbsent(K, Function)`**    |   O(1)    |   O(1)   | **Tie** |
| **`computeIfPresent(K, BiFunction)`** |   O(1)    |   O(1)   | **Tie** |
| **`containsKey(Object)`**             |   O(1)    |   O(1)   | **Tie** |
| **`containsValue(Object)`**           |   O(1)    |   O(1)   | **Tie** |
| **`entrySet()`**                      |   O(n)    |   O(n)   | **Tie** |
| **`equals(Object)`**                  |   O(1)    |   O(1)   | **Tie** |
| **`expand()`**                        | O(m + n)  | O(m + n) | **Tie** |
| **`forEach(BiConsumer)`**             |   O(1)    |   O(1)   | **Tie** |
| **`get(Object)`**                     |   O(1)    |   O(1)   | **Tie** |
| **`getOrDefault(Object, V)`**         |   O(1)    |   O(1)   | **Tie** |
| **`hash(Object)`**                    |   O(1)    |   O(1)   | **Tie** |
| **`hashCode()`**                      |   O(1)    |   O(1)   | **Tie** |
| **`isEmpty()`**                       |   O(1)    |   O(1)   | **Tie** |
| **`keySet()`**                        |   O(n)    |   O(n)   | **Tie** |
| **`merge(K, V, BiFunction)`**         |   O(1)    |   O(1)   | **Tie** |
| **`put(K, V)`**                       |   O(1)    |   O(1)   | **Tie** |
| **`putAll(Map)`**                     | O(m + n)  | O(m + n) | **Tie** |
| **`putIfAbsent(K, V)`**               |   O(1)    |   O(1)   | **Tie** |
| **`reduce()`**                        | O(m + n)  | O(m + n) | **Tie** |
| **`remove(Object)`**                  |   O(1)    |   O(1)   | **Tie** |
| **`remove(Object, Object)`**          |   O(1)    |   O(1)   | **Tie** |
| **`replace(K, V)`**                   |   O(1)    |   O(1)   | **Tie** |
| **`replace(K, V, V)`**                |   O(1)    |   O(1)   | **Tie** |
| **`replaceAll(BiFunction)`**          |   O(1)    |   O(1)   | **Tie** |
| **`size()`**                          |   O(1)    |   O(1)   | **Tie** |
| **`toString()`**                      |   O(n)    |   O(n)   | **Tie** |
| **`values()`**                        |   O(n)    |   O(n)   | **Tie** |

**Notes**:
- **m**: Number of buckets in the map.
- **n**: Number of key-value mappings in the map.
- **$m_{\text{new}}$**: Number of buckets after resizing.
- **$m_{\text{src}}$**: Number of buckets in the source/input map.
- **$n_{\text{src}}$**: Number of entries in the source/input map.

# Performance vs Java HashMap

| Method                           | Custom (ns) | JDK (ns)  |   Ratio    |            Winner            |
|:---------------------------------|:------------|:----------|:----------:|:----------------------------:|
| `clear()`                        | 73,914      | 74,345    |   ~1.01×   | **Statistically Equivalent** |
| `compute(K,BiFunction)`          | 684         | 502       |   1.36×    |           **JDK**            |
| `computeIfAbsent(K,Function)`    | 406         | 376       |   ~1.08×   | **Statistically Equivalent** |
| `computeIfPresent(K,BiFunction)` | 762         | 1,050     |   1.38×    |          **Custom**          |
| `constructor`                    | 164         | 92        |   1.79×    |           **JDK**            |
| `containsKey(K)`                 | 300         | 415       |   1.38×    |          **Custom**          |
| `containsValue(V)`               | 136,738     | 119,654   |   1.14×    |           **JDK**            |
| `entrySet()`                     | 160         | 59        |   2.70×    |           **JDK**            |
| `equals(Object o)`               | 2,375,475   | 2,635,382 |   ~1.11×   | **Statistically Equivalent** |
| `forEach(BiConsumer)`            | 258,654     | 254,474   |   ~1.02×   | **Statistically Equivalent** |
| `get(K)`                         | 357         | 335       |   ~1.07×   | **Statistically Equivalent** |
| `getOrDefault(K,V)`              | 750         | 463       |   1.62×    |           **JDK**            |
| `hashCode()`                     | 693,495     | 615,993   |   ~1.13×   | **Statistically Equivalent** |
| `keySet()`                       | 124         | 84        |   1.47×    |           **JDK**            |
| `merge(K,V,BiFunction)`          | 582         | 1,411     |   2.43×    |          **Custom**          |
| `put(K,V)`                       | 4,318,986   | 4,469,980 |   ~1.03×   | **Statistically Equivalent** |
| `putAll(Map)`                    | 1,059,655   | 1,000,072 |   ~1.06×   | **Statistically Equivalent** |
| `putIfAbsent(K,V)`               | 4,925,956   | 3,887,748 |   1.27×    |           **JDK**            |
| `remove(K)`                      | 400         | 336       |   1.19×    |           **JDK**            |
| `remove(K,V)`                    | 417         | 472       |   ~1.13×   | **Statistically Equivalent** |
| `replace(K,V)`                   | 435         | 445       |   ~1.02×   | **Statistically Equivalent** |
| `replace(K,V,V)`                 | 913         | 826       |   ~1.11×   | **Statistically Equivalent** |
| `replaceAll(BiFunction)`         | 1,150,468   | 1,213,617 |   ~1.05×   | **Statistically Equivalent** |
| `toString()`                     | 3,437,562   | 1,995,256 |   1.72×    |           **JDK**            |
| `values()`                       | 80          | 87        |   ~1.09×   | **Statistically Equivalent** |

#### Note: The following performance charts are designed to be viewed in dark mode.
![Combined Performance Charts](PerformanceTesting/heatmap.png)
![Combined Performance Charts](PerformanceTesting/constructor.png)
![Combined Performance Charts](PerformanceTesting/clear().png)
![Combined Performance Charts](PerformanceTesting/compute(K,BiFunction).png)
![Combined Performance Charts](PerformanceTesting/computeIfAbsent(K,Function).png)
![Combined Performance Charts](PerformanceTesting/computeIfPresent(K,BiFunction).png)
![Combined Performance Charts](PerformanceTesting/containsKey(K).png)
![Combined Performance Charts](PerformanceTesting/containsValue(V).png)
![Combined Performance Charts](PerformanceTesting/entrySet().png)
![Combined Performance Charts](PerformanceTesting/equals(Object%20o).png)
![Combined Performance Charts](PerformanceTesting/forEach(BiConsumer).png)
![Combined Performance Charts](PerformanceTesting/get(K).png)
![Combined Performance Charts](PerformanceTesting/getOrDefault(K,V).png)
![Combined Performance Charts](PerformanceTesting/hashCode().png)
![Combined Performance Charts](PerformanceTesting/keySet().png)
![Combined Performance Charts](PerformanceTesting/merge(K,V,BiFunction).png)
![Combined Performance Charts](PerformanceTesting/put(K,V).png)
![Combined Performance Charts](PerformanceTesting/putAll(Map).png)
![Combined Performance Charts](PerformanceTesting/putIfAbsent(K,V).png)
![Combined Performance Charts](PerformanceTesting/remove(K).png)
![Combined Performance Charts](PerformanceTesting/remove(K,V).png)
![Combined Performance Charts](PerformanceTesting/replace(K,V).png)
![Combined Performance Charts](PerformanceTesting/replace(K,V,V).png)
![Combined Performance Charts](PerformanceTesting/replaceAll(BiFunction).png)
![Combined Performance Charts](PerformanceTesting/toString().png)
![Combined Performance Charts](PerformanceTesting/values().png)
