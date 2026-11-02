import pandas as pd


def convert_jmh_to_wide(input_csv, output_csv):
  df = pd.read_csv(input_csv, sep=';')

  # Mapping of JMH benchmark names to full method signatures
  benchmark_to_signature = {
      'benchmarkClear': 'clear()',
      'benchmarkCompute': 'compute(K, BiFunction)',
      'benchmarkComputeIfAbsent': 'computeIfAbsent(K, Function)',
      'benchmarkComputeIfPresent': 'computeIfPresent(K, BiFunction)',
      'benchmarkConstructor': 'Constructor()',
      'benchmarkContainsKey': 'containsKey(Object)',
      'benchmarkContainsValue': 'containsValue(Object)',
      'benchmarkEntrySet': 'entrySet()',
      'benchmarkEquals': 'equals(Object)',
      'benchmarkForEach': 'forEach(BiConsumer)',
      'benchmarkGet': 'get(Object)',
      'benchmarkGetOrDefault': 'getOrDefault(Object, V)',
      'benchmarkHashCode': 'hashCode()',
      'benchmarkKeySet': 'keySet()',
      'benchmarkMerge': 'merge(K, V, BiFunction)',
      'benchmarkPut': 'put(K, V)',
      'benchmarkPutAll': 'putAll(Map)',
      'benchmarkPutIfAbsent': 'putIfAbsent(K, V)',
      'benchmarkRemove': 'remove(Object)',
      'benchmarkRemoveWithValue': 'remove(Object, Object)',
      'benchmarkReplace': 'replace(K, V)',
      'benchmarkReplaceAll': 'replaceAll(BiFunction)',
      'benchmarkReplaceWithOldNew': 'replace(K, V, V)',
      'benchmarkToString': 'toString()',
      'benchmarkValues': 'values()',
  }

  df['Method'] = df['Benchmark'].map(benchmark_to_signature)

  # Pivot table: Size as rows, Method signatures as columns, Score (ns/op) as values
  pivot_df = (
      df.pivot(index='Size', columns='Method', values='Score (ns/op)')
      .reset_index()
  )

  # Save as a semicolon-delimited wide matrix CSV
  pivot_df.to_csv(output_csv, sep=';', index=False)
  print(f'Successfully converted {input_csv} -> {output_csv}')


if __name__ == '__main__':
  convert_jmh_to_wide(
      'CustomMap_jmh_performance.csv', 'CustomMap_wide_matrix.csv'
  )
  convert_jmh_to_wide('HashMap_jmh_performance.csv', 'HashMap_wide_matrix.csv')