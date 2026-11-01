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

## Performance vs Java HashMap

| Method                           | Custom (ns) | JDK (ns)  | Ratio  |            Winner            |
|:---------------------------------|:------------|:----------|:-------|:----------------------------:|
| `clear()`                        | 30,619      | 28,129    | ~1.09× | **Statistically Equivalent** |
| `compute(K,BiFunction)`          | 504         | 360       | 1.40×  |           **JDK**            |
| `computeIfAbsent(K,Function)`    | 321         | 272       | 1.18×  |           **JDK**            |
| `computeIfPresent(K,BiFunction)` | 412         | 514       | 1.25×  |          **Custom**          |
| `constructor`                    | 108         | 59        | 1.83×  |           **JDK**            |
| `containsKey(K)`                 | 225         | 182       | 1.23×  |           **JDK**            |
| `containsValue(V)`               | 70,223      | 62,126    | ~1.13× | **Statistically Equivalent** |
| `entrySet()`                     | 120         | 65        | 1.85×  |           **JDK**            |
| `equals(Object o)`               | 1,267,376   | 1,354,022 | ~1.07× | **Statistically Equivalent** |
| `forEach(BiConsumer)`            | 139,240     | 119,667   | 1.16×  |           **JDK**            |
| `get(K)`                         | 248         | 180       | 1.38×  |           **JDK**            |
| `getOrDefault(K,V)`              | 432         | 182       | 2.37×  |           **JDK**            |
| `hashCode()`                     | 386,937     | 340,276   | ~1.14× | **Statistically Equivalent** |
| `keySet()`                       | 71          | 65        | ~1.09× | **Statistically Equivalent** |
| `merge(K,V,BiFunction)`          | 428         | 374       | ~1.14× | **Statistically Equivalent** |
| `put(K,V)`                       | 2,604,798   | 2,220,185 | 1.17×  |           **JDK**            |
| `putAll(Map)`                    | 558,742     | 467,844   | 1.19×  |           **JDK**            |
| `putIfAbsent(K,V)`               | 2,830,453   | 2,157,884 | 1.31×  |           **JDK**            |
| `remove(K)`                      | 261         | 192       | 1.36×  |           **JDK**            |
| `remove(K,V)`                    | 442         | 306       | 1.45×  |           **JDK**            |
| `replace(K,V)`                   | 430         | 310       | 1.39×  |           **JDK**            |
| `replace(K,V,V)`                 | 588         | 382       | 1.54×  |           **JDK**            |
| `replaceAll(BiFunction)`         | 598,839     | 624,791   | ~1.04× | **Statistically Equivalent** |
| `toString()`                     | 1,803,156   | 1,099,288 | 1.64×  |           **JDK**            |
| `values()`                       | 98          | 61        | 1.60×  |           **JDK**            |

## Performance Charts 

#### Note: The following performance charts are designed to be viewed in dark mode.
![Combined Performance Charts](PerformanceTesting/heatmap.png)
![Combined Performance Charts](PerformanceTesting/geometric.png)
![Combined Performance Charts](PerformanceTesting/Constructor.png)
![Combined Performance Charts](PerformanceTesting/clear.png)
![Combined Performance Charts](PerformanceTesting/compute.png)
![Combined Performance Charts](PerformanceTesting/computeIfAbsent.png)
![Combined Performance Charts](PerformanceTesting/computeIfPresent.png)
![Combined Performance Charts](PerformanceTesting/containsKey.png)
![Combined Performance Charts](PerformanceTesting/containsValue.png)
![Combined Performance Charts](PerformanceTesting/entrySet.png)
![Combined Performance Charts](PerformanceTesting/equals.png)
![Combined Performance Charts](PerformanceTesting/forEach.png)
![Combined Performance Charts](PerformanceTesting/get.png)
![Combined Performance Charts](PerformanceTesting/getOrDefault.png)
![Combined Performance Charts](PerformanceTesting/hashCode.png)
![Combined Performance Charts](PerformanceTesting/keySet.png)
![Combined Performance Charts](PerformanceTesting/merge.png)
![Combined Performance Charts](PerformanceTesting/put.png)
![Combined Performance Charts](PerformanceTesting/putAll.png)
![Combined Performance Charts](PerformanceTesting/putIfAbsent.png)
![Combined Performance Charts](PerformanceTesting/remove.png)
![Combined Performance Charts](PerformanceTesting/removeObject_Object.png)
![Combined Performance Charts](PerformanceTesting/replace.png)
![Combined Performance Charts](PerformanceTesting/replaceK_V_V.png)
![Combined Performance Charts](PerformanceTesting/replaceAll.png)
![Combined Performance Charts](PerformanceTesting/toString.png)
![Combined Performance Charts](PerformanceTesting/values.png)
