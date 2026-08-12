package custommap;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 3, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Fork(3)
public class CustomMapPerformanceBenchmark {

    @Param({"10000", "20000", "30000", "40000", "50000", "60000", "70000", "80000", "90000", "100000"})
    public int size;

    private CustomMap<Integer, String> map;
    private Map<Integer, String> sourceMap;
    private Random random;

    @Setup(Level.Trial)
    public void setupTrial() {
        random = new Random(42);
        sourceMap = new HashMap<>();
        for (int i = 0; i < size; i++) {
            sourceMap.put(i, "Value" + i);
        }
    }

    @Setup(Level.Invocation)
    public void setupInvocation() {
        map = new CustomMap<>();
        for (int i = 0; i < size; i++) {
            map.put(i, "Value" + i);
        }
    }

    @Benchmark
    public CustomMap<Integer, String> benchmarkConstructor() {
        return new CustomMap<>();
    }

    @Benchmark
    public String benchmarkGet() {
        return map.get(random.nextInt(size * 2));
    }

    @Benchmark
    public String benchmarkGetOrDefault() {
        return map.getOrDefault(random.nextInt(size * 2), "Default");
    }

    @Benchmark
    public String benchmarkPut() {
        CustomMap<Integer, String> m = new CustomMap<>();
        for (int i = 0; i < size; i++) {
            m.put(random.nextInt(size * 2), "Value" + i);
        }
        return m.get(0);
    }

    @Benchmark
    public String benchmarkRemove() {
        return map.remove(random.nextInt(size * 2));
    }

    @Benchmark
    public boolean benchmarkRemoveWithValue() {
        return map.remove(random.nextInt(size * 2), "Value" + random.nextInt(size));
    }

    @Benchmark
    public boolean benchmarkContainsKey() {
        return map.containsKey(random.nextInt(size * 2));
    }

    @Benchmark
    public boolean benchmarkContainsValue() {
        return map.containsValue("Value" + random.nextInt(size));
    }

    @Benchmark
    public String benchmarkPutIfAbsent() {
        CustomMap<Integer, String> m = new CustomMap<>();
        for (int i = 0; i < size; i++) {
            m.putIfAbsent(random.nextInt(size * 2), "Value" + i);
        }
        return m.get(0);
    }

    @Benchmark
    public String benchmarkReplace() {
        return map.replace(random.nextInt(size * 2), "NewValue" + random.nextInt(size));
    }

    @Benchmark
    public boolean benchmarkReplaceWithOldNew() {
        return map.replace(random.nextInt(size * 2), "Value" + random.nextInt(size), "NewValue" + random.nextInt(size));
    }

    @Benchmark
    public Set<Integer> benchmarkKeySet() {
        return map.keySet();
    }

    @Benchmark
    public Collection<String> benchmarkValues() {
        return map.values();
    }

    @Benchmark
    public void benchmarkClear() {
        map.clear();
    }

    @Benchmark
    public boolean benchmarkEquals() {
        CustomMap<Integer, String> other = new CustomMap<>();
        for (int i = 0; i < size; i++) {
            other.put(i, "Value" + i);
        }
        return map.equals(other);
    }

    @Benchmark
    public int benchmarkHashCode() {
        return map.hashCode();
    }

    @Benchmark
    public Set<Map.Entry<Integer, String>> benchmarkEntrySet() {
        return map.entrySet();
    }

    @Benchmark
    public void benchmarkPutAll() {
        CustomMap<Integer, String> m = new CustomMap<>();
        m.putAll(sourceMap);
    }

    @Benchmark
    public String benchmarkCompute() {
        int key = random.nextInt(size * 2);
        return map.compute(key, (k, v) -> v == null ? "Value" : "Updated" + v);
    }

    @Benchmark
    public String benchmarkComputeIfAbsent() {
        int key = random.nextInt(size * 2);
        return map.computeIfAbsent(key, k -> "Value");
    }

    @Benchmark
    public String benchmarkComputeIfPresent() {
        int key = random.nextInt(size * 2);
        return map.computeIfPresent(key, (k, v) -> "Updated" + v);
    }

    @Benchmark
    public void benchmarkForEach() {
        map.forEach((k, v) -> {});
    }

    @Benchmark
    public String benchmarkMerge() {
        int key = random.nextInt(size * 2);
        return map.merge(key, "Value", (oldVal, newVal) -> oldVal == null ? newVal : oldVal + newVal);
    }

    @Benchmark
    public void benchmarkReplaceAll() {
        map.replaceAll((k, v) -> "Updated" + v);
    }

    @Benchmark
    public String benchmarkToString() {
        return map.toString();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(CustomMapPerformanceBenchmark.class.getSimpleName())
                .forks(3)
                .result("custom-map-results.csv")
                .resultFormat(ResultFormatType.CSV)
                .build();

        Collection<RunResult> results = new Runner(opt).run();
        writeCustomCsv(results);
    }

    private static void writeCustomCsv(Collection<RunResult> results) {
        try (FileWriter writer = new FileWriter("CustomMap_jmh_performance.csv")) {
            writer.write("Benchmark;Size;Score (ns/op)\n");
            for (RunResult result : results) {
                String benchmarkName = result.getParams().getBenchmark();
                String shortName = benchmarkName.substring(benchmarkName.lastIndexOf('.') + 1);

                double score = result.getPrimaryResult().getScore();
                String sizeVal = result.getParams().getParam("size");

                writer.write("\"" + shortName + "\";" + (sizeVal != null ? sizeVal : "N/A") + ";" + score + "\n");
            }
            System.out.println("JMH Performance report saved: CustomMap_jmh_performance.csv");
        } catch (IOException e) {
            System.err.println("Failed to write CSV: " + e.getMessage());
        }
    }
}