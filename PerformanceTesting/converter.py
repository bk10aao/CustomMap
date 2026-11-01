from pathlib import Path
import pandas as pd

HEADER_MAPPING = {
    # Constructors
    "benchmarkConstructor": "Constructor()",
    "constructor": "Constructor()",

    # Map Operations & Methods
    "benchmarkClear": "clear()",
    "benchmarkCompute": "compute()",
    "benchmarkComputeIfAbsent": "computeIfAbsent()",
    "benchmarkComputeIfPresent": "computeIfPresent()",
    "benchmarkContainsKey": "containsKey()",
    "benchmarkContainsValue": "containsValue()",
    "benchmarkEntrySet": "entrySet()",
    "benchmarkEquals": "equals()",
    "benchmarkForEach": "forEach()",
    "benchmarkGet": "get()",
    "benchmarkGetOrDefault": "getOrDefault()",
    "benchmarkHashCode": "hashCode()",
    "benchmarkKeySet": "keySet()",
    "benchmarkMerge": "merge()",
    "benchmarkPut": "put()",
    "benchmarkPutAll": "putAll()",
    "benchmarkPutIfAbsent": "putIfAbsent()",
    "benchmarkRemove": "remove()",
    "benchmarkRemoveWithValue": "remove(Object, Object)",
    "benchmarkReplace": "replace()",
    "benchmarkReplaceAll": "replaceAll()",
    "benchmarkReplaceWithOldNew": "replace(K, V, V)",
    "benchmarkToString": "toString()",
    "benchmarkValues": "values()"
}

COLUMN_ORDER = [
    "Constructor()",
    "clear()", "compute()", "computeIfAbsent()", "computeIfPresent()",
    "containsKey()", "containsValue()", "entrySet()", "equals()",
    "forEach()", "get()", "getOrDefault()", "hashCode()", "keySet()",
    "merge()", "put()", "putAll()", "putIfAbsent()", "remove()",
    "remove(Object, Object)", "replace()", "replaceAll()",
    "replace(K, V, V)", "toString()", "values()"
]


def convert_to_wide_matrix(input_file: str | Path, output_file: str | Path) -> None:
    input_path = Path(input_file)
    output_path = Path(output_file)

    if not input_path.exists():
        print(f"Skipping '{input_path}' (file not found).")
        return

    df = pd.read_csv(input_path, sep=';')

    # If the file uses standard JMH format (Benchmark, Size, Score)
    if 'Benchmark' in df.columns:
        df['Benchmark'] = df['Benchmark'].astype(str).str.strip('"\t ')
        df['Metric'] = df['Benchmark'].map(HEADER_MAPPING).fillna(df['Benchmark'])

        pivot_df = df.pivot_table(
            index='Size',
            columns='Metric',
            values='Score (ns/op)',
            aggfunc='mean'
        ).round()
    else:
        # If the file is already structured or uses custom headers, clean columns and sort
        pivot_df = df.set_index('Size') if 'Size' in df.columns else df
        # Rename any columns matching header mapping keys
        pivot_df = pivot_df.rename(columns=HEADER_MAPPING)
        pivot_df = pivot_df.round()

    # Convert to nullable Int64 to avoid NaN display issues
    pivot_df = pivot_df.astype('Int64')

    # Reorder columns according to COLUMN_ORDER with any extras appended
    available_columns = [col for col in COLUMN_ORDER if col in pivot_df.columns]
    extra_columns = [col for col in pivot_df.columns if col not in available_columns]
    pivot_df = pivot_df.reindex(columns=available_columns + extra_columns)

    pivot_df.to_csv(output_path, sep=';')
    print(f"Wide matrix CSV created: {output_path}")


if __name__ == "__main__":
    convert_to_wide_matrix("CustomMap_jmh_performance.csv", "CustomMap_wide_matrix.csv")
    convert_to_wide_matrix("HashMap_jmh_performance.csv", "HashMap_wide_matrix.csv")