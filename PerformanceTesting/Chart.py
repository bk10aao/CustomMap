import matplotlib.pyplot as plt
import csv
import math
from matplotlib.ticker import LogLocator, NullFormatter

expected_methods = [
    'put(K,V)', 'get(K)', 'getOrDefault(K,V)', 'remove(K)', 'remove(K,V)',
    'containsKey(K)', 'containsValue(V)', 'putIfAbsent(K,V)', 'replace(K,V)',
    'replace(K,V,V)', 'keySet()', 'values()', 'clear()', 'equals(Object o)',
    'hashCode()', 'toString()'
]

def read_csv(filename):
    data = {}
    with open(filename, 'r') as f:
        reader = csv.DictReader(f, delimiter=';')
        headers = reader.fieldnames

        for row in reader:
            size = int(row['Size'])
            row_data = {k: row[k] for k in headers}

            for key, value in row_data.items():
                if key == 'Size':
                    continue
                if key not in data:
                    data[key] = []
                try:
                    if value is None or value.strip() == '':
                        print(f"Warning: Empty value for {key} at size {size} in {filename}, skipping")
                        continue
                    data[key].append((size, int(value)))
                except (ValueError, TypeError) as e:
                    print(f"Error: Invalid value '{value}' for {key} at size {size} in {filename}, skipping: {e}")
                    continue
    return data

try:
    custom_data = read_csv('CustomMap_performance.csv')
    hashmap_data = read_csv('HashMap_performance.csv')
except FileNotFoundError as e:
    print(f"Error: CSV file not found: {e}")
    exit(1)

methods = sorted(set(custom_data.keys()) & set(hashmap_data.keys()) & set(expected_methods))
if not methods:
    print("Error: No common methods found between CSVs and expected methods")
    exit(1)

sizes = None
for method in methods:
    if len(custom_data[method]) == 15 and len(hashmap_data[method]) == 15:
        sizes = [size for size, _ in custom_data[method]]
        break
if sizes is None:
    print("Error: No method with complete data for all sizes")
    exit(1)

valid_methods = [m for m in expected_methods if m in methods and len(custom_data[m]) == len(sizes) and len(hashmap_data[m]) == len(sizes)]
n_methods = len(valid_methods)
if n_methods == 0:
    print("Error: No methods with valid data to plot")
    exit(1)

fig, axes = plt.subplots(nrows=n_methods, ncols=1, figsize=(14, 4 * n_methods))
axes = [axes] if n_methods == 1 else axes

for idx, method in enumerate(valid_methods):
    custom_times = [time for _, time in custom_data[method]]
    hash_times = [time for _, time in hashmap_data[method]]
    max_time = max(max(custom_times), max(hash_times))
    padded_y_max = max_time * 1.5
    rounded_y_max = 10 ** math.ceil(math.log10(padded_y_max))

    axes[idx].plot(sizes, custom_times, label='CustomMap')
    axes[idx].plot(sizes, hash_times, label='HashMap')
    axes[idx].set_xlabel('Map Size')
    axes[idx].set_ylabel('Time (ns)')
    axes[idx].set_yscale('log')
    axes[idx].set_xlim(0, 100000)
    axes[idx].set_ylim(1, rounded_y_max)
    axes[idx].set_title(f'Performance Comparison: {method}')
    axes[idx].legend(loc='center left', bbox_to_anchor=(1.0, 0.5))
    axes[idx].grid(True)
    axes[idx].yaxis.set_major_locator(LogLocator(base=10.0, numticks=10))
    axes[idx].yaxis.set_minor_locator(LogLocator(base=10.0, subs='auto', numticks=10))
    axes[idx].yaxis.set_minor_formatter(NullFormatter())

plt.tight_layout()
plt.savefig('All_Map_Performance_Comparisons.png', dpi=300, bbox_inches='tight')
plt.close()

print(f"All comparison charts saved in 'All_Map_Performance_Comparisons.png' for methods: {', '.join(valid_methods)}")
