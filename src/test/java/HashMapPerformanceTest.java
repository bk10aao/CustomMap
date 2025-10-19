import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class HashMapPerformanceTest {
    public static void main(String[] args) {
        int[] sizes = { 10000, 20000, 30000, 40000, 50000, 60000, 70000, 80000, 90000, 100000 };

        long[][] results = new long[sizes.length][];
        Random random = new Random();

        for (int i = 0; i < sizes.length; i++) {
            int size = sizes[i];
            System.out.println("Benchmarking size: " + size);

            long putTime = benchmarkPut(size, random);
            long getTime = benchmarkGet(size, random);
            long getOrDefaultTime = benchmarkGetOrDefault(size, random);
            long removeTime = benchmarkRemove(size, random);
            long removeWithValueTime = benchmarkRemoveWithValue(size, random);
            long containsKeyTime = benchmarkContainsKey(size, random);
            long containsValueTime = benchmarkContainsValue(size, random);
            long putIfAbsentTime = benchmarkPutIfAbsent(size, random);
            long replaceTime = benchmarkReplace(size, random);
            long replaceWithOldNewTime = benchmarkReplaceWithOldNew(size, random);
            long keySetTime = benchmarkKeySet(size);
            long valuesTime = benchmarkValues(size);
            long clearTime = benchmarkClear(size);
            long equalsTime = benchmarkEquals(size);
            long toStringTime = benchmarkToString(size);
            long entrySetTime = benchmarkEntrySet(size);
            long putAllTime = benchmarkPutAll(size, random);
            long computeTime = benchmarkCompute(size, random);
            long computeIfAbsentTime = benchmarkComputeIfAbsent(size, random);
            long computeIfPresentTime = benchmarkComputeIfPresent(size, random);
            long forEachTime = benchmarkForEach(size);
            long mergeTime = benchmarkMerge(size, random);
            long replaceAllTime = benchmarkReplaceAll(size);

            results[i] = new long[]{
                    size, putTime, getTime, getOrDefaultTime, removeTime, removeWithValueTime,
                    containsKeyTime, containsValueTime, putIfAbsentTime, replaceTime,
                    replaceWithOldNewTime, keySetTime, valuesTime, clearTime,
                    equalsTime, toStringTime, entrySetTime, putAllTime,
                    computeTime, computeIfAbsentTime, computeIfPresentTime, forEachTime,
                    mergeTime, replaceAllTime
            };
        }

        try (FileWriter writer = new FileWriter("HashMap_performance.csv")) {
            writer.write("Size;put(K,V);get(K);getOrDefault(K,V);remove(K);remove(K,V);containsKey(K);containsValue(V);" +
                    "putIfAbsent(K,V);replace(K,V);replace(K,V,V);keySet();values();clear();equals(Object o);" +
                    "toString();entrySet();putAll(Map);compute(K,BiFunction);computeIfAbsent(K,Function);" +
                    "computeIfPresent(K,BiFunction);forEach(BiConsumer);merge(K,V,BiFunction);replaceAll(BiFunction)\n");
            for (long[] row : results) {
                for (int j = 0; j < row.length; j++) {
                    writer.write(String.valueOf(row[j]));
                    if (j < row.length - 1) writer.write(";");
                }
                writer.write("\n");
            }
            System.out.println("Performance data written to HashMap_performance.csv");
        } catch (IOException e) {
            System.err.println("Error writing CSV: " + e.getMessage());
        }
    }

    private static long benchmarkPut(int size, Random random) {
        HashMap<Integer, String> map = new HashMap<>();
        long start = System.nanoTime();
        for (int i = 0; i < size; i++) {
            Integer key = random.nextInt(size * 2);
            String value = "Value" + i;
            map.put(key, value);
        }
        return System.nanoTime() - start;
    }

    private static long benchmarkGet(int size, Random random) {
        HashMap<Integer, String> map = new HashMap<>();
        populateMap(map, size);
        long start = System.nanoTime();
        for (int i = 0; i < size; i++) {
            map.get(random.nextInt(size * 2));
        }
        return System.nanoTime() - start;
    }

    private static long benchmarkGetOrDefault(int size, Random random) {
        HashMap<Integer, String> map = new HashMap<>();
        populateMap(map, size);
        long start = System.nanoTime();
        for (int i = 0; i < size; i++) {
            map.getOrDefault(random.nextInt(size * 2), "Default");
        }
        return System.nanoTime() - start;
    }

    private static long benchmarkRemove(int size, Random random) {
        HashMap<Integer, String> map = new HashMap<>();
        populateMap(map, size);
        long start = System.nanoTime();
        for (int i = 0; i < size; i++) {
            map.remove(random.nextInt(size * 2));
        }
        return System.nanoTime() - start;
    }

    private static long benchmarkRemoveWithValue(int size, Random random) {
        HashMap<Integer, String> map = new HashMap<>();
        populateMap(map, size);
        long start = System.nanoTime();
        for (int i = 0; i < size; i++) {
            map.remove(random.nextInt(size * 2), "Value" + random.nextInt(size));
        }
        return System.nanoTime() - start;
    }

    private static long benchmarkContainsKey(int size, Random random) {
        HashMap<Integer, String> map = new HashMap<>();
        populateMap(map, size);
        long start = System.nanoTime();
        for (int i = 0; i < size; i++) {
            map.containsKey(random.nextInt(size * 2));
        }
        return System.nanoTime() - start;
    }

    private static long benchmarkContainsValue(int size, Random random) {
        HashMap<Integer, String> map = new HashMap<>();
        populateMap(map, size);
        long start = System.nanoTime();
        for (int i = 0; i < size; i++) {
            map.containsValue("Value" + random.nextInt(size));
        }
        return System.nanoTime() - start;
    }

    private static long benchmarkPutIfAbsent(int size, Random random) {
        HashMap<Integer, String> map = new HashMap<>();
        long start = System.nanoTime();
        for (int i = 0; i < size; i++) {
            map.putIfAbsent(random.nextInt(size * 2), "Value" + i);
        }
        return System.nanoTime() - start;
    }

    private static long benchmarkReplace(int size, Random random) {
        HashMap<Integer, String> map = new HashMap<>();
        populateMap(map, size);
        long start = System.nanoTime();
        for (int i = 0; i < size; i++) {
            map.replace(random.nextInt(size * 2), "NewValue" + i);
        }
        return System.nanoTime() - start;
    }

    private static long benchmarkReplaceWithOldNew(int size, Random random) {
        HashMap<Integer, String> map = new HashMap<>();
        populateMap(map, size);
        long start = System.nanoTime();
        for (int i = 0; i < size; i++) {
            map.replace(random.nextInt(size * 2), "Value" + random.nextInt(size), "NewValue" + i);
        }
        return System.nanoTime() - start;
    }

    private static long benchmarkKeySet(int size) {
        HashMap<Integer, String> map = new HashMap<>();
        populateMap(map, size);
        long start = System.nanoTime();
        map.keySet();
        return System.nanoTime() - start;
    }

    private static long benchmarkValues(int size) {
        HashMap<Integer, String> map = new HashMap<>();
        populateMap(map, size);
        long start = System.nanoTime();
        map.values();
        return System.nanoTime() - start;
    }

    private static long benchmarkClear(int size) {
        HashMap<Integer, String> map = new HashMap<>();
        populateMap(map, size);
        long start = System.nanoTime();
        map.clear();
        return System.nanoTime() - start;
    }

    private static long benchmarkEquals(int size) {
        HashMap<Integer, String> map1 = new HashMap<>();
        HashMap<Integer, String> map2 = new HashMap<>();
        populateMap(map1, size);
        populateMap(map2, size);
        long start = System.nanoTime();
        map1.equals(map2);
        return System.nanoTime() - start;
    }

    private static long benchmarkToString(int size) {
        HashMap<Integer, String> map = new HashMap<>();
        populateMap(map, size);
        long start = System.nanoTime();
        map.toString();
        return System.nanoTime() - start;
    }

    private static long benchmarkEntrySet(int size) {
        HashMap<Integer, String> map = new HashMap<>();
        populateMap(map, size);
        long start = System.nanoTime();
        map.entrySet();
        return System.nanoTime() - start;
    }

    private static long benchmarkPutAll(int size, Random random) {
        HashMap<Integer, String> map = new HashMap<>();
        Map<Integer, String> source = new HashMap<>();
        for (int i = 0; i < size; i++) {
            source.put(random.nextInt(size * 2), "Value" + i);
        }
        long start = System.nanoTime();
        map.putAll(source);
        return System.nanoTime() - start;
    }

    private static long benchmarkCompute(int size, Random random) {
        HashMap<Integer, String> map = new HashMap<>();
        populateMap(map, size);
        long start = System.nanoTime();
        for (int i = 0; i < size; i++) {
            final int x = i;
            map.compute(random.nextInt(size * 2), (k, v) -> v == null ? "Value" + x : "Updated" + v);
        }
        return System.nanoTime() - start;
    }

    private static long benchmarkComputeIfAbsent(int size, Random random) {
        HashMap<Integer, String> map = new HashMap<>();
        populateMap(map, size);
        long start = System.nanoTime();
        for (int i = 0; i < size; i++) {
            final int x = i;
            map.computeIfAbsent(random.nextInt(size * 2), k -> "Value" + x);
        }
        return System.nanoTime() - start;
    }

    private static long benchmarkComputeIfPresent(int size, Random random) {
        HashMap<Integer, String> map = new HashMap<>();
        populateMap(map, size);
        long start = System.nanoTime();
        for (int i = 0; i < size; i++) {
            map.computeIfPresent(random.nextInt(size * 2), (k, v) -> "Updated" + v);
        }
        return System.nanoTime() - start;
    }

    private static long benchmarkForEach(int size) {
        HashMap<Integer, String> map = new HashMap<>();
        populateMap(map, size);
        long start = System.nanoTime();
        map.forEach((k, v) -> {});
        return System.nanoTime() - start;
    }

    private static long benchmarkMerge(int size, Random random) {
        HashMap<Integer, String> map = new HashMap<>();
        populateMap(map, size);
        long start = System.nanoTime();
        for (int i = 0; i < size; i++) {
            map.merge(random.nextInt(size * 2), "Value" + i, (oldVal, newVal) -> oldVal == null ? newVal : oldVal + newVal);
        }
        return System.nanoTime() - start;
    }

    private static long benchmarkReplaceAll(int size) {
        HashMap<Integer, String> map = new HashMap<>();
        populateMap(map, size);
        long start = System.nanoTime();
        map.replaceAll((k, v) -> "Updated" + v);
        return System.nanoTime() - start;
    }

    private static void populateMap(HashMap<Integer, String> map, int size) {
        for (int i = 0; i < size; i++) {
            map.put(i, "Value" + i);
        }
    }
}