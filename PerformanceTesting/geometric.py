import matplotlib.pyplot as plt
import numpy as np
import pandas as pd

# Load wide-matrix CSV files with semicolon delimiter
custom_df = pd.read_csv('CustomMap_wide_matrix.csv', sep=';')
jdk_df = pd.read_csv('HashMap_wide_matrix.csv', sep=';')

common_sizes = set(custom_df['Size']).intersection(set(jdk_df['Size']))
custom_df = (
    custom_df[custom_df['Size'].isin(common_sizes)]
    .sort_values('Size')
    .reset_index(drop=True)
)
jdk_df = (
    jdk_df[jdk_df['Size'].isin(common_sizes)]
    .sort_values('Size')
    .reset_index(drop=True)
)

methods = [col for col in custom_df.columns if col != 'Size' and col in jdk_df.columns]

custom_df_fixed = custom_df.copy()
jdk_df_fixed = jdk_df.copy()
for col in methods:
  custom_df_fixed[col] = pd.to_numeric(custom_df_fixed[col], errors='coerce').fillna(1).replace(0, 1)
  jdk_df_fixed[col] = pd.to_numeric(jdk_df_fixed[col], errors='coerce').fillna(1).replace(0, 1)

ratios = []
labels = []
colors = []

jdk_win_color = '#FF4D4D'    # Red
custom_win_color = '#4DA6FF' # Blue

for m in methods:
  # Using arithmetic mean to align with your standardized tables
  mean_custom = np.mean(custom_df_fixed[m])
  mean_jdk = np.mean(jdk_df_fixed[m])

  # Positive ratio = Custom faster (extends to the right)
  # Negative ratio = JDK faster (extends to the left)
  if mean_custom < mean_jdk:
    speedup = mean_jdk / mean_custom
    ratios.append(speedup - 1)
    colors.append(custom_win_color)
  else:
    speedup = mean_custom / mean_jdk
    ratios.append(-(speedup - 1))
    colors.append(jdk_win_color)
  labels.append(m)

sorted_indices = np.argsort(ratios)
sorted_ratios = [ratios[idx] for idx in sorted_indices]
sorted_labels = [labels[idx] for idx in sorted_indices]
sorted_colors = [colors[idx] for idx in sorted_indices]

# Strictly bound axis range to 2.0x (-1.0 to 1.0 relative difference)
left_limit = -1.0
right_limit = 1.0

fig_height = max(6, len(methods) * 0.45)
fig, ax = plt.subplots(figsize=(12, fig_height), facecolor='none')
ax.set_facecolor('none')

bars = ax.barh(
    range(len(sorted_labels)),
    sorted_ratios,
    color=sorted_colors,
    alpha=0.9,
    height=0.6,
)
ax.axvline(x=0, color='#ffffff', linewidth=1.2)

ax.set_xlim(left_limit, right_limit)

ticks = [-1.0, -0.5, 0.0, 0.5, 1.0]
ax.set_xticks(ticks)
ax.set_xticklabels(
    [f'{abs(t)+1:.1f}x' if abs(t) > 0.05 else 'Tie' for t in ticks],
    color='#ffffff',
    fontsize=11,
)

ax.set_ylim(-0.5, len(methods) - 0.5)
ax.set_yticks(range(len(sorted_labels)))
ax.set_yticklabels(sorted_labels, color='#ffffff', fontsize=10)

# Add exact numeric speedup values directly next to each bar
for idx, (bar, r) in enumerate(zip(bars, sorted_ratios)):
  val = abs(r)
  if val < 0.05:
    text_str = 'Tie'
  else:
    factor = val + 1
    if r < 0:
      text_str = f'JDK {factor:.2f}x'
    else:
      text_str = f'Custom {factor:.2f}x'

  # Position text outside the bar on the correct side
  if r >= 0:
    ax.text(
        r + 0.02,
        idx,
        f'  {text_str}',
        va='center',
        ha='left',
        color='#ffffff',
        fontsize=9,
        fontweight='bold',
    )
  else:
    ax.text(
        r - 0.02,
        idx,
        f'{text_str}  ',
        va='center',
        ha='right',
        color='#ffffff',
        fontsize=9,
        fontweight='bold',
    )

ax.set_title(
    (
        'Overall Relative Performance Comparison (Custom vs JDK)\n(Arithmetic'
        ' Mean Across All Sizes)'
    ),
    fontsize=14,
    fontweight='bold',
    pad=15,
    color='#ffffff',
)
ax.set_xlabel(
    '← JDK Faster  |  Relative Speedup Factor (Max 2.0x)  |  Custom Faster →',
    fontsize=12,
    labelpad=10,
    color='#ffffff',
)

ax.grid(True, axis='x', linestyle='--', alpha=0.3, color='#888888')
ax.tick_params(colors='#ffffff', which='both', length=0)

for spine in ax.spines.values():
  spine.set_edgecolor('#555555')

plt.tight_layout()
plt.savefig('geometric.png', dpi=300, transparent=True)
plt.close()
print('Generated comparison graph with 2.0x axis limit successfully!')