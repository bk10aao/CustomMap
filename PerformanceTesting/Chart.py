import matplotlib.pyplot as plt
import csv
import math
from matplotlib.ticker import ScalarFormatter

# Expected method names (excluding 'Size')
expected_methods = [
    'put(K,V)', 'get(K)', 'getOrDefault(K,V)', 'remove(K)', 'remove(K,V)',
    'containsKey(K)', 'containsValue(V)', 'putIfAbsent(K,V)', 'replace(K,V)',
    'replace(K,V,V)', 'keySet()', 'values()', 'clear()', 'equals(Object o)',
    'hashCode()', 'toString()'
]

EXPECTED_SIZE_COUNT = 10  # Updated size count


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


def nice_ceil(value):
    """
    Round value up to a nice number (1, 2, 5, 10, 20, 50, 100, 200, 500, 1000, ...)
    """
    if value <= 0:
        return 1
    magnitude = 10 ** int(math.floor(math.log10(value)))
    residual = value / magnitude
    if residual <= 1:
        nice = 1
    elif residual <= 2:
        nice = 2
    elif residual <= 5:
        nice = 5
    else:
        nice = 10
    return nice * magnitude


# Load data from both CSVs
try:
    custom_data = read_csv('CustomMap_performance.csv')
    hashmap_data = read_csv('HashMap_performance.csv')
except FileNotFoundError as e:
    print(f"Error: CSV file not found: {e}")
    exit(1)

# Ensure both CSVs have the expected methods
methods = sorted(set(custom_data.keys()) & set(hashmap_data.keys()) & set(expected_methods))
if not methods:
    print("Error: No common methods found between CSVs and expected methods")
    exit(1)

# Use sizes from a method with complete data
sizes = None
for method in methods:
    if len(custom_data[method]) == EXPECTED_SIZE_COUNT and len(hashmap_data[method]) == EXPECTED_SIZE_COUNT:
        sizes = [size for size, _ in custom_data[method]]
        break
if sizes is None:
    print("Error: No method with complete data for all sizes")
    exit(1)

# Filter valid methods
valid_methods = [m for m in expected_methods if
                 m in methods and len(custom_data[m]) == len(sizes) and len(hashmap_data[m]) == len(sizes)]
n_methods = len(valid_methods)
if n_methods == 0:
    print("Error: No methods with valid data to plot")
    exit(1)

# Create subplots
fig, axes = plt.subplots(nrows=n_methods, ncols=1, figsize=(14, 4 * n_methods))
axes = [axes] if n_methods == 1 else axes

# Plot each method
for idx, method in enumerate(valid_methods):
    custom_times = [time for _, time in custom_data[method]]
    hash_times = [time for _, time in hashmap_data[method]]

    max_time = max(max(custom_times), max(hash_times))
    min_time = min(min(custom_times), min(hash_times))

    # Y-axis min (start at 0 or just below min if min > 0)
    y_min = 0 if min_time <= 0 else min_time * 0.9

    # Y-axis max: 110% of max rounded nicely
    padded_max = max_time * 1.1
    y_max = nice_ceil(padded_max)

    axes[idx].plot(sizes, custom_times, label='CustomMap')
    axes[idx].plot(sizes, hash_times, label='HashMap')
    axes[idx].set_xlabel('Map Size')
    axes[idx].set_ylabel('Time (ns)')
    axes[idx].set_xlim(10000, 100000)
    axes[idx].set_ylim(y_min, y_max)
    axes[idx].set_title(f'Performance Comparison: {method}')

    # Legend top-left inside plot
    axes[idx].legend(loc='upper left', fontsize='small', frameon=True)

    axes[idx].grid(True)

    # Disable scientific notation
    axes[idx].yaxis.set_major_formatter(ScalarFormatter())
    axes[idx].yaxis.get_major_formatter().set_scientific(False)

# Adjust layout and save figure
plt.tight_layout()
plt.savefig('All_Map_Performance_Comparisons.png', dpi=300, bbox_inches='tight')
plt.close()

print(f"All comparison charts saved in 'All_Map_Performance_Comparisons.png' for methods: {', '.join(valid_methods)}")
