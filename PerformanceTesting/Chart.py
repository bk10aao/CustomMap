import matplotlib.pyplot as plt
import csv

def read_csv(filename):
    data = {}
    with open(filename, 'r') as f:
        reader = csv.DictReader(f)
        for row in reader:
            size = int(row['Size'])
            for key, value in row.items():
                if key == 'Size':
                    continue
                if key not in data:
                    data[key] = []
                data[key].append((size, int(value)))
    return data

# Load data from both CSVs
custom_data = read_csv('custom_map_performance.csv')
hashmap_data = read_csv('hash_map_performance.csv')

methods = sorted(custom_data.keys())  # Sort alphabetically for consistency
sizes = [size for size, _ in custom_data[methods[0]]]  # Use any method for sizes

# Create a single figure with subplots stacked vertically
n_methods = len(methods)
fig, axes = plt.subplots(nrows=n_methods, ncols=1, figsize=(12, 4 * n_methods))
axes = [axes] if n_methods == 1 else axes  # Handle single subplot case

# Plot each method's comparison
for idx, method in enumerate(methods):
    custom_times = [time for _, time in custom_data[method]]
    hash_times = [time for _, time in hashmap_data[method]]

    axes[idx].plot(sizes, custom_times, label='CustomMap')
    axes[idx].plot(sizes, hash_times, label='HashMap')
    axes[idx].set_xlabel('Map Size')
    axes[idx].set_ylabel('Time (ns)')
    axes[idx].set_title(f'Performance Comparison: {method}')
    axes[idx].legend()
    axes[idx].grid(True)

# Adjust layout and save
plt.tight_layout()
plt.savefig('All_Map_Performance_Comparisons.png', dpi=300, bbox_inches='tight')
plt.close()

print(f"All comparison charts saved in 'All_Map_Performance_Comparisons.png' for methods: {', '.join(methods)}")
