import matplotlib.pyplot as plt
import csv
import math
from matplotlib.ticker import ScalarFormatter, MaxNLocator, AutoLocator

expected_methods = [
    'put(K,V)', 'get(K)', 'getOrDefault(K,V)', 'remove(K)', 'remove(K,V)',
    'containsKey(K)', 'containsValue(V)', 'putIfAbsent(K,V)', 'replace(K,V)',
    'replace(K,V,V)', 'keySet()', 'values()', 'clear()', 'equals(Object o)',
    'hashCode()', 'toString()'
]

EXPECTED_SIZE_COUNT = 10

def read_csv(filename):
    data = {}
    with open(filename, 'r') as f:
        reader = csv.DictReader(f, delimiter=';')
        headers = reader.fieldnames
        for row in reader:
            size = int(row['Size'])
            for key in headers:
                if key == 'Size':
                    continue
                if key not in data:
                    data[key] = []
                val = row[key]
                if val is None or val.strip() == '':
                    print(f"Warning: Empty value for {key} at size {size} in {filename}, skipping")
                    continue
                try:
                    data[key].append((size, int(val)))
                except Exception as e:
                    print(f"Error parsing {val} for {key} at size {size}: {e}")
    return data

try:
    custom_data = read_csv('CustomMap_performance.csv')
    hashmap_data = read_csv('HashMap_performance.csv')
except FileNotFoundError as e:
    print(f"Error: {e}")
    exit(1)

methods = sorted(set(custom_data.keys()) & set(hashmap_data.keys()) & set(expected_methods))
if not methods:
    print("Error: No common methods found")
    exit(1)

sizes = None
for m in methods:
    if len(custom_data[m]) == EXPECTED_SIZE_COUNT and len(hashmap_data[m]) == EXPECTED_SIZE_COUNT:
        sizes = [size for size, _ in custom_data[m]]
        break
if sizes is None:
    print("Error: No method with complete data for all sizes")
    exit(1)

valid_methods = [m for m in expected_methods if m in methods and
                 len(custom_data[m]) == len(sizes) and len(hashmap_data[m]) == len(sizes)]

if not valid_methods:
    print("Error: No valid methods to plot")
    exit(1)

fig, axes = plt.subplots(len(valid_methods), 1, figsize=(14, 4 * len(valid_methods)))
if len(valid_methods) == 1:
    axes = [axes]

for idx, method in enumerate(valid_methods):
    custom_times = [t for _, t in custom_data[method]]
    hash_times = [t for _, t in hashmap_data[method]]

    combined = custom_times + hash_times
    y_min_raw = min(combined)
    y_max_raw = max(combined)

    # Calculate padded limits:
    y_range = y_max_raw - y_min_raw
    if y_range == 0:
        # all values equal, add small padding
        y_min = max(0, y_min_raw * 0.95)
        y_max = y_max_raw * 1.05 if y_max_raw != 0 else 1
    else:
        padding = y_range * 0.1  # 10% padding top and bottom
        y_min = max(0, y_min_raw - padding)
        y_max = y_max_raw + padding

    axes[idx].plot(sizes, custom_times, label='CustomMap')
    axes[idx].plot(sizes, hash_times, label='HashMap')

    axes[idx].set_xlabel('Map Size')
    axes[idx].set_ylabel('Time (ns)')
    axes[idx].set_xlim(sizes[0], sizes[-1])
    axes[idx].set_ylim(y_min, y_max)
    axes[idx].set_title(method)
    axes[idx].legend(loc='center left', bbox_to_anchor=(1.0, 0.5))
    axes[idx].grid(True)

    # Use MaxNLocator for nice integer ticks, max 5 ticks
    axes[idx].yaxis.set_major_locator(MaxNLocator(nbins=5, integer=True, prune='both'))

    # Disable scientific notation
    axes[idx].yaxis.set_major_formatter(ScalarFormatter())
    axes[idx].yaxis.get_major_formatter().set_scientific(False)

plt.tight_layout()
plt.savefig('All_Map_Performance_Comparisons.png', dpi=300, bbox_inches='tight')
plt.close()

print(f"Saved chart for methods: {', '.join(valid_methods)}")
