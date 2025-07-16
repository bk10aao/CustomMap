import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class HashMapPerformanceTest {

    private static final int[] SIZES = {1, 10, 50, 100, 250, 500, 1000, 2500, 5000, 10000, 25000, 50000};

    public static void main(String[] args) {
        try (FileWriter writer = new FileWriter("hash_map_performance.csv")) {
            writer.write("Size,Put,Get,GetOrDefault,ContainsKey,ContainsValue,RemoveKey,RemoveKeyValue,Replace,ReplaceOldNew,PutIfAbsent,Clear\n");

            for (int size : SIZES) {
                System.out.println("benchmarking: " + size);
                List<Integer> keys = generateRandomIntegers(size);
                List<Integer> values = generateRandomIntegers(size);

                long putTime, getTime, getOrDefaultTime, containsKeyTime, containsValueTime;
                long removeKeyTime, removeKeyValueTime, replaceTime, replaceOldNewTime;
                long putIfAbsentTime, clearTime;

                HashMap<Integer, Integer> map = new HashMap<>();

                // put()
                long start = System.nanoTime();
                for (int i = 0; i < size; i++) {
                    map.put(keys.get(i), values.get(i));
                }
                putTime = System.nanoTime() - start;

                // get()
                start = System.nanoTime();
                for (int i = 0; i < size; i++) {
                    map.get(keys.get(i));
                }
                getTime = System.nanoTime() - start;

                // getOrDefault()
                start = System.nanoTime();
                for (int i = 0; i < size; i++) {
                    map.getOrDefault(keys.get(i), -1);
                }
                getOrDefaultTime = System.nanoTime() - start;

                // containsKey()
                start = System.nanoTime();
                for (int i = 0; i < size; i++) {
                    map.containsKey(keys.get(i));
                }
                containsKeyTime = System.nanoTime() - start;

                // containsValue()
                start = System.nanoTime();
                for (int i = 0; i < size; i++) {
                    map.containsValue(values.get(i));
                }
                containsValueTime = System.nanoTime() - start;

                // remove(key)
                start = System.nanoTime();
                for (int i = 0; i < size; i++) {
                    map.remove(keys.get(i));
                }
                removeKeyTime = System.nanoTime() - start;

                // Re-populate map
                for (int i = 0; i < size; i++) {
                    map.put(keys.get(i), values.get(i));
                }

                // remove(key, value)
                start = System.nanoTime();
                for (int i = 0; i < size; i++) {
                    map.remove(keys.get(i), values.get(i));
                }
                removeKeyValueTime = System.nanoTime() - start;

                // Re-populate
                for (int i = 0; i < size; i++) {
                    map.put(keys.get(i), values.get(i));
                }

                // replace(key, value)
                start = System.nanoTime();
                for (int i = 0; i < size; i++) {
                    map.replace(keys.get(i), values.get(i));
                }
                replaceTime = System.nanoTime() - start;

                // replace(key, oldValue, newValue)
                start = System.nanoTime();
                for (int i = 0; i < size; i++) {
                    map.replace(keys.get(i), values.get(i), values.get(i)); // no change
                }
                replaceOldNewTime = System.nanoTime() - start;

                // putIfAbsent()
                start = System.nanoTime();
                for (int i = 0; i < size; i++) {
                    map.putIfAbsent(keys.get(i), values.get(i));
                }
                putIfAbsentTime = System.nanoTime() - start;

                // clear()
                start = System.nanoTime();
                map.clear();
                clearTime = System.nanoTime() - start;

                writer.write(String.format("%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d\n",
                        size,
                        putTime / 1_000, getTime / 1_000, getOrDefaultTime / 1_000,
                        containsKeyTime / 1_000, containsValueTime / 1_000,
                        removeKeyTime / 1_000, removeKeyValueTime / 1_000,
                        replaceTime / 1_000, replaceOldNewTime / 1_000,
                        putIfAbsentTime / 1_000, clearTime / 1_000
                ));
            }

            System.out.println("Benchmark results saved to hash_map_performance.csv");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Integer> generateRandomIntegers(int count) {
        Random random = new Random();
        List<Integer> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add(random.nextInt(1_000_000));
        }
        return list;
    }
}
