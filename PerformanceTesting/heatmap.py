import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns

# 1. Load the benchmark data files
custom_map_df = pd.read_csv('CustomMap_performance.csv', sep=';')
hash_map_df = pd.read_csv('HashMap_performance.csv', sep=';')

# 2. Extract methods and sizes
methods = [col for col in custom_map_df.columns if col != 'Size']
sizes = custom_map_df['Size'].tolist()

# 3. Construct the relative performance matrix (Log2 ratios)
heatmap_data = np.zeros((len(methods), len(sizes)))
text_labels = []

for i, m in enumerate(methods):
    row_labels = []
    for j, size in enumerate(sizes):
        c_val = custom_map_df.loc[custom_map_df['Size'] == size, m].values[0]
        j_val = hash_map_df.loc[hash_map_df['Size'] == size, m].values[0]

        # Prevent division by zero errors
        if c_val == 0: c_val = 1
        if j_val == 0: j_val = 1

        # Log2 ratio calculation (Positive values indicate CustomMap is faster)
        ratio = np.log2(j_val / c_val)
        heatmap_data[i, j] = ratio

        if j_val > c_val:
            factor = j_val / c_val
            row_labels.append(f"+{factor:.1f}x")
        else:
            factor = c_val / j_val
            row_labels.append(f"-{factor:.1f}x")
    text_labels.append(row_labels)

text_labels = np.array(text_labels)

# 4. Sort methods from top to bottom by their geometric trends
avg_ratios = np.mean(heatmap_data, axis=1)
sorted_idx = np.argsort(avg_ratios)

heatmap_data = heatmap_data[sorted_idx]
text_labels = text_labels[sorted_idx]
sorted_methods = [methods[idx] for idx in sorted_idx]

# 5. Initialize figure with a fully transparent background
fig, ax = plt.subplots(figsize=(15, 13), facecolor='none')
ax.set_facecolor('none')

# 6. Clip the log2 ratios to [-4.0, 4.0] (maps color range bounds to 16x variation)
# This prevents extreme outliers from washing out minor parity differences
clipped_heatmap_data = np.clip(heatmap_data, -4.0, 4.0)

# 7. Create a custom divergent colormap (Red = HashMap Faster, Blue = CustomMap Faster)
cmap = sns.diverging_palette(15, 240, as_cmap=True)

# 8. Render the Seaborn Heatmap
sns.heatmap(clipped_heatmap_data,
            annot=text_labels,
            fmt="",
            cmap=cmap,
            center=0,
            xticklabels=sizes,
            yticklabels=sorted_methods,
            ax=ax,
            cbar_kws={'label': '← JDK Faster (HashMap)  |  Relative Speedup Scale (Clipped at 16x)  |  Custom Faster (CustomMap) →'},
            linewidths=0.8,
            linecolor='#555555',
            annot_kws={'size': 10, 'weight': 'bold'})

# 9. Format Title, Labels, and Colorbar styling to match transparency guidelines
ax.set_title('Java Map Performance Speedup Matrix Heatmap Across Sizes\n(Positive = CustomMap Faster, Negative = HashMap Faster)',
             color='#ffffff', fontsize=16, fontweight='bold', pad=20)
ax.set_ylabel('Map Interface Methods', color='#aaaaaa', fontsize=13, labelpad=10)
ax.set_xlabel('Collection Size (Elements)', color='#aaaaaa', fontsize=13, labelpad=10)

ax.tick_params(colors='#ffffff', labelsize=11)
plt.xticks(rotation=45)
plt.yticks(rotation=0)

# Style colorbar text to match dark/transparent setups
cbar = ax.collections[0].colorbar
cbar.ax.tick_params(colors='#ffffff', labelsize=10)
cbar.ax.yaxis.label.set_color('#ffffff')
cbar.ax.yaxis.label.set_fontsize(12)

# 10. Compute tight layouts and save the file
plt.tight_layout()
plt.savefig('map_performance_heatmap_transparent.png', dpi=300, transparent=True)
plt.close()