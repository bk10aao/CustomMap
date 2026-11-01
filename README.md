# Map
Implementation of a Map using an array

All methods implemented are identical to those found in the Java [Map](https://docs.oracle.com/javase/8/docs/api/java/util/Map.html) interface.

## Build and Test

1. To build and test the project run command `./gradlew clean build`

## Time Complexity

| Method                                |           CustomMap           |            HashMap            | Winner  |
|:--------------------------------------|:-----------------------------:|:-----------------------------:|:-------:|
| **`clear()`**                         |       $\mathcal{O}(m)$        |       $\mathcal{O}(m)$        | **Tie** |
| **`compute(K, BiFunction)`**          |     $\mathcal{O}(\log n)$     |     $\mathcal{O}(\log n)$     | **Tie** |
| **`computeIfAbsent(K, Function)`**    |     $\mathcal{O}(\log n)$     |     $\mathcal{O}(\log n)$     | **Tie** |
| **`computeIfPresent(K, BiFunction)`** |     $\mathcal{O}(\log n)$     |     $\mathcal{O}(\log n)$     | **Tie** |
| **`containsKey(Object)`**             |     $\mathcal{O}(\log n)$     |     $\mathcal{O}(\log n)$     | **Tie** |
| **`containsValue(Object)`**           |     $\mathcal{O}(m + n)$      |     $\mathcal{O}(m + n)$      | **Tie** |
| **`entrySet()`**                      |       $\mathcal{O}(1)$        |       $\mathcal{O}(1)$        | **Tie** |
| **`equals(Object)`**                  |       $\mathcal{O}(n)$        |       $\mathcal{O}(n)$        | **Tie** |
| **`expand()`**                        |     $\mathcal{O}(m + n)$      |     $\mathcal{O}(m + n)$      | **Tie** |
| **`forEach(BiConsumer)`**             |     $\mathcal{O}(m + n)$      |     $\mathcal{O}(m + n)$      | **Tie** |
| **`get(Object)`**                     |     $\mathcal{O}(\log n)$     |     $\mathcal{O}(\log n)$     | **Tie** |
| **`getOrDefault(Object, V)`**         |     $\mathcal{O}(\log n)$     |     $\mathcal{O}(\log n)$     | **Tie** |
| **`hash(Object)`**                    |       $\mathcal{O}(1)$        |       $\mathcal{O}(1)$        | **Tie** |
| **`hashCode()`**                      |     $\mathcal{O}(m + n)$      |     $\mathcal{O}(m + n)$      | **Tie** |
| **`isEmpty()`**                       |       $\mathcal{O}(1)$        |       $\mathcal{O}(1)$        | **Tie** |
| **`keySet()`**                        |       $\mathcal{O}(1)$        |       $\mathcal{O}(1)$        | **Tie** |
| **`merge(K, V, BiFunction)`**         |     $\mathcal{O}(\log n)$     |     $\mathcal{O}(\log n)$     | **Tie** |
| **`put(K, V)`**                       |     $\mathcal{O}(\log n)$     |     $\mathcal{O}(\log n)$     | **Tie** |
| **`putAll(Map)`**                     | $\mathcal{O}(n_{src} \log n)$ | $\mathcal{O}(n_{src} \log n)$ | **Tie** |
| **`putIfAbsent(K, V)`**               |     $\mathcal{O}(\log n)$     |     $\mathcal{O}(\log n)$     | **Tie** |
| **`reduce()`**                        |     $\mathcal{O}(m + n)$      |     $\mathcal{O}(m + n)$      | **Tie** |
| **`remove(Object)`**                  |     $\mathcal{O}(\log n)$     |     $\mathcal{O}(\log n)$     | **Tie** |
| **`remove(Object, Object)`**          |     $\mathcal{O}(\log n)$     |     $\mathcal{O}(\log n)$     | **Tie** |
| **`replace(K, V)`**                   |     $\mathcal{O}(\log n)$     |     $\mathcal{O}(\log n)$     | **Tie** |
| **`replace(K, V, V)`**                |     $\mathcal{O}(\log n)$     |     $\mathcal{O}(\log n)$     | **Tie** |
| **`replaceAll(BiFunction)`**          |     $\mathcal{O}(m + n)$      |     $\mathcal{O}(m + n)$      | **Tie** |
| **`size()`**                          |       $\mathcal{O}(1)$        |       $\mathcal{O}(1)$        | **Tie** |
| **`toString()`**                      |     $\mathcal{O}(m + n)$      |     $\mathcal{O}(m + n)$      | **Tie** |
| **`values()`**                        |       $\mathcal{O}(1)$        |       $\mathcal{O}(1)$        | **Tie** |

### Auxiliary Space Complexity

| Method                                |         CustomMap          |          HashMap           | Winner  |
|:--------------------------------------|:--------------------------:|:--------------------------:|:-------:|
| **`clear()`**                         |      $\mathcal{O}(1)$      |      $\mathcal{O}(1)$      | **Tie** |
| **`compute(K, BiFunction)`**          |      $\mathcal{O}(1)$      |      $\mathcal{O}(1)$      | **Tie** |
| **`computeIfAbsent(K, Function)`**    |      $\mathcal{O}(1)$      |      $\mathcal{O}(1)$      | **Tie** |
| **`computeIfPresent(K, BiFunction)`** |      $\mathcal{O}(1)$      |      $\mathcal{O}(1)$      | **Tie** |
| **`containsKey(Object)`**             |      $\mathcal{O}(1)$      |      $\mathcal{O}(1)$      | **Tie** |
| **`containsValue(Object)`**           |      $\mathcal{O}(1)$      |      $\mathcal{O}(1)$      | **Tie** |
| **`entrySet()`**                      |      $\mathcal{O}(1)$      |      $\mathcal{O}(1)$      | **Tie** |
| **`equals(Object)`**                  |      $\mathcal{O}(1)$      |      $\mathcal{O}(1)$      | **Tie** |
| **`expand()`**                        |      $\mathcal{O}(m)$      |      $\mathcal{O}(m)$      | **Tie** |
| **`forEach(BiConsumer)`**             |      $\mathcal{O}(1)$      |      $\mathcal{O}(1)$      | **Tie** |
| **`get(Object)`**                     |      $\mathcal{O}(1)$      |      $\mathcal{O}(1)$      | **Tie** |
| **`getOrDefault(Object, V)`**         |      $\mathcal{O}(1)$      |      $\mathcal{O}(1)$      | **Tie** |
| **`hash(Object)`**                    |      $\mathcal{O}(1)$      |      $\mathcal{O}(1)$      | **Tie** |
| **`hashCode()`**                      |      $\mathcal{O}(1)$      |      $\mathcal{O}(1)$      | **Tie** |
| **`isEmpty()`**                       |      $\mathcal{O}(1)$      |      $\mathcal{O}(1)$      | **Tie** |
| **`keySet()`**                        |      $\mathcal{O}(1)$      |      $\mathcal{O}(1)$      | **Tie** |
| **`merge(K, V, BiFunction)`**         |      $\mathcal{O}(1)$      |      $\mathcal{O}(1)$      | **Tie** |
| **`put(K, V)`**                       |      $\mathcal{O}(1)$      |      $\mathcal{O}(1)$      | **Tie** |
| **`putAll(Map)`**                     | $\mathcal{O}(m + n_{src})$ | $\mathcal{O}(m + n_{src})$ | **Tie** |
| **`putIfAbsent(K, V)`**               |      $\mathcal{O}(1)$      |      $\mathcal{O}(1)$      | **Tie** |
| **`reduce()`**                        |      $\mathcal{O}(m)$      |      $\mathcal{O}(m)$      | **Tie** |
| **`remove(Object)`**                  |      $\mathcal{O}(1)$      |      $\mathcal{O}(1)$      | **Tie** |
| **`remove(Object, Object)`**          |      $\mathcal{O}(1)$      |      $\mathcal{O}(1)$      | **Tie** |
| **`replace(K, V)`**                   |      $\mathcal{O}(1)$      |      $\mathcal{O}(1)$      | **Tie** |
| **`replace(K, V, V)`**                |      $\mathcal{O}(1)$      |      $\mathcal{O}(1)$      | **Tie** |
| **`replaceAll(BiFunction)`**          |      $\mathcal{O}(1)$      |      $\mathcal{O}(1)$      | **Tie** |
| **`size()`**                          |      $\mathcal{O}(1)$      |      $\mathcal{O}(1)$      | **Tie** |
| **`toString()`**                      |      $\mathcal{O}(n)$      |      $\mathcal{O}(n)$      | **Tie** |
| **`values()`**                        |      $\mathcal{O}(1)$      |      $\mathcal{O}(1)$      | **Tie** |

**Notes:**
- **$m$**: Number of buckets in the map (table capacity).
- **$n$**: Number of key-value mappings (entries) currently in the map.
- **$m_{\text{new}}$**: Number of buckets after resizing.
- **$n_{\text{src}}$**: Number of entries in the source/input map (`m.size()`).

### Performance vs Java HashMap (Averaged Across All Sizes)

| Method                           | Custom (ns) | JDK (ns)  | Ratio  |            Winner            |
|:---------------------------------|:------------|:----------|:-------|:----------------------------:|
| `clear()`                        | 28,738      | 28,129    | ~1.02× | **Statistically Equivalent** |
| `compute(K,BiFunction)`          | 375         | 360       | ~1.04× | **Statistically Equivalent** |
| `computeIfAbsent(K,Function)`    | 238         | 272       | ~0.87× | **Statistically Equivalent** |
| `computeIfPresent(K,BiFunction)` | 319         | 514       | 0.62×  |          **Custom**          |
| `Constructor()`                  | 79          | 59        | 1.35×  |           **JDK**            |
| `containsKey(K)`                 | 174         | 183       | ~0.95× | **Statistically Equivalent** |
| `containsValue(V)`               | 67,494      | 62,126    | ~1.09× | **Statistically Equivalent** |
| `entrySet()`                     | 63          | 65        | ~0.98× | **Statistically Equivalent** |
| `equals(Object o)`               | 1,102,429   | 1,354,022 | 0.81×  |          **Custom**          |
| `forEach(BiConsumer)`            | 119,682     | 119,667   | ~1.00× | **Statistically Equivalent** |
| `get(K)`                         | 177         | 180       | ~0.98× | **Statistically Equivalent** |
| `getOrDefault(K,V)`              | 184         | 182       | ~1.01× | **Statistically Equivalent** |
| `hashCode()`                     | 341,099     | 340,276   | ~1.00× | **Statistically Equivalent** |
| `keySet()`                       | 61          | 65        | ~0.93× | **Statistically Equivalent** |
| `merge(K,V,BiFunction)`          | 361         | 375       | ~0.96× | **Statistically Equivalent** |
| `put(K,V)`                       | 2,285,876   | 2,220,185 | ~1.03× | **Statistically Equivalent** |
| `putAll(Map)`                    | 473,918     | 467,844   | ~1.01× | **Statistically Equivalent** |
| `putIfAbsent(K,V)`               | 2,184,049   | 2,157,884 | ~1.01× | **Statistically Equivalent** |
| `remove(K)`                      | 190         | 192       | ~0.99× | **Statistically Equivalent** |
| `remove(K,V)`                    | 300         | 306       | ~0.98× | **Statistically Equivalent** |
| `replace(K,V)`                   | 292         | 310       | ~0.94× | **Statistically Equivalent** |
| `replace(K,V,V)`                 | 377         | 382       | ~0.99× | **Statistically Equivalent** |
| `replaceAll(BiFunction)`         | 569,235     | 624,791   | ~0.91× | **Statistically Equivalent** |
| `toString()`                     | 1,790,306   | 1,099,288 | 1.63×  |           **JDK**            |
| `values()`                       | 59          | 61        | ~0.97× | **Statistically Equivalent** |

> *Note: Execution time differences within $\pm 15\%$ ($\le 1.15\times$ ratio) are categorized as **Statistically Equivalent** to account for standard JMH microbenchmark noise and JIT warm-up variance.*

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
