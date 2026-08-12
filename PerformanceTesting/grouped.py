#!/usr/bin/env python3
"""
Generate grouped multi-panel benchmark charts comparing CustomMap and JDK HashMap.
Grouped into logical performance categories to solve scale distortion.
"""

import matplotlib

matplotlib.use('Agg')
import matplotlib.pyplot as plt
import matplotlib.ticker as mticker
from matplotlib.lines import Line2D
import pandas as pd
import numpy as np
import os

# ──────────────────────────────────────────────────────────────────────────────
# Configuration & Grouping Definition
# ──────────────────────────────────────────────────────────────────────────────

CUSTOMMAP_CSV_PATH = "CustomMap_jmh_performance.csv"
HASHMAP_CSV_PATH = "HashMap_jmh_performance.csv"

COLORS = {
    'purple': '#9B6EF3',  # CustomMap
    'blue': '#4DA6FF',  # JDK HashMap
    'grid': '#252525',
}

# Map CustomMap PascalCase columns to clean titles
OPERATION_TITLES = {
    'Get': 'get(K)',
    'GetOrDefault': 'getOrDefault(K,V)',
    'ContainsKey': 'containsKey(K)',
    'Put': 'put(K,V)',
    'PutIfAbsent': 'putIfAbsent(K,V)',
    'Remove': 'remove(K)',
    'Compute': 'compute(K,BiFunction)',
    'ComputeIfAbsent': 'computeIfAbsent(K,Function)',
    'ComputeIfPresent': 'computeIfPresent(K,BiFunction)',
    'Merge': 'merge(K,V,BiFunction)',
    'Replace': 'replace(K,V)',
    'ReplaceWithOldNew': 'replace(K,V,V)',
    'RemoveWithValue': 'remove(K,V)',
    'KeySet': 'keySet()',
    'Values': 'values()',
    'EntrySet': 'entrySet()',
    'PutAll': 'putAll(Map)',
    'ForEach': 'forEach(BiConsumer)',
    'Clear': 'clear()',
    'Equals': 'equals(Object o)',
    'HashCode': 'hashCode()',
    'ToString': 'toString()',
    'ContainsValue': 'containsValue(V)',
}

# Logical groupings to avoid scale distortion
OPERATION_GROUPS = {
    'Core Hot-Path CRUD Operations (O(1))': [
        'Get', 'GetOrDefault', 'ContainsKey', 'Put', 'PutIfAbsent', 'Remove'
    ],
    'Conditional & Compute Operations': [
        'Compute', 'ComputeIfAbsent', 'ComputeIfPresent', 'Merge', 'Replace', 'ReplaceWithOldNew', 'RemoveWithValue'
    ],
    'Bulk, Iteration & Object Contracts (O(N))': [
        'KeySet', 'Values', 'EntrySet', 'PutAll', 'ForEach', 'Clear', 'Equals', 'HashCode', 'ToString', 'ContainsValue'
    ]
}

# Mapping HashMap column headers back to CustomMap keys for lookups
HM_REVERSE_MAPPING = {
    'get(K)': 'Get',
    'getOrDefault(K,V)': 'GetOrDefault',
    'containsKey(K)': 'ContainsKey',
    'put(K,V)': 'Put',
    'putIfAbsent(K,V)': 'PutIfAbsent',
    'remove(K)': 'Remove',
    'compute(K,BiFunction)': 'Compute',
    'computeIfAbsent(K,Function)': 'ComputeIfAbsent',
    'computeIfPresent(K,BiFunction)': 'ComputeIfPresent',
    'merge(K,V,BiFunction)': 'Merge',
    'replace(K,V)': 'Replace',
    'replace(K,V,V)': 'ReplaceWithOldNew',
    'remove(K,V)': 'RemoveWithValue',
    'keySet()': 'KeySet',
    'values()': 'Values',
    'entrySet()': 'EntrySet',
    'putAll(Map)': 'PutAll',
    'forEach(BiConsumer)': 'ForEach',
    'clear()': 'Clear',
    'equals(Object o)': 'Equals',
    'hashCode()': 'HashCode',
    'toString()': 'ToString',
    'containsValue(V)': 'ContainsValue',
}


def load_data():
    cm_df = pd.read_csv(CUSTOMMAP_CSV_PATH, sep=';')
    hm_df = pd.read_csv(HASHMAP_CSV_PATH, sep=';')
    return cm_df, hm_df


def format_y_axis(value, pos):
    if value == 0:
        return '0'
    return f'{int(value):,}'


def generate_grouped_charts():
    cm_df, hm_df = load_data()
    sizes = sorted(cm_df['Size'].tolist())
    x_positions = list(range(len(sizes)))

    for group_name, ops in OPERATION_GROUPS.items():
        n_ops = len(ops)
        # Determine grid layout (e.g. 2 or 3 columns)
        ncols = 3 if n_ops >= 6 else 2
        nrows = math_ceil = (n_ops + ncols - 1) // ncols

        fig, axes = plt.subplots(nrows=nrows, ncols=ncols, figsize=(ncols * 5.2, nrows * 3.8), dpi=150)
        fig.patch.set_alpha(0)

        # Flatten axes array for easy iteration
        axes_flat = axes.flatten() if nrows > 1 or ncols > 1 else [axes]

        for idx, op_key in enumerate(ops):
            ax = axes_flat[idx]
            ax.set_facecolor('none')

            # Extract CustomMap values
            cm_vals = cm_df[op_key].tolist() if op_key in cm_df.columns else [np.nan] * len(sizes)

            # Find matching HashMap column
            hm_col = next((k for k, v in HM_REVERSE_MAPPING.items() if v == op_key), None)
            hm_vals = hm_df[hm_col].tolist() if hm_col and hm_col in hm_df.columns else [np.nan] * len(sizes)

            # Plot lines & markers
            ax.plot(x_positions, cm_vals, color=COLORS['purple'], linewidth=1.5, zorder=2)
            ax.plot(x_positions, hm_vals, color=COLORS['blue'], linewidth=1.5, zorder=2)
            ax.scatter(x_positions, cm_vals, color=COLORS['purple'], s=25, zorder=3)
            ax.scatter(x_positions, hm_vals, color=COLORS['blue'], s=25, zorder=3)

            ax.grid(True, color=COLORS['grid'], linewidth=0.6, linestyle='-', zorder=0)
            ax.set_axisbelow(True)

            # Formatting ticks
            ax.set_xticks(x_positions)
            ax.set_xticklabels([f'{s // 1000}k' if s >= 1000 else str(s) for s in sizes], color='white', fontsize=8)
            ax.tick_params(axis='x', colors='white', length=0, pad=4)

            ax.yaxis.set_major_formatter(mticker.FuncFormatter(format_y_axis))
            ax.tick_params(axis='y', colors='white', length=0, pad=4)
            for label in ax.get_yticklabels():
                label.set_color('white')
                label.set_fontsize(8)

            for spine in ax.spines.values():
                spine.set_visible(False)

            title_str = OPERATION_TITLES.get(op_key, op_key)
            ax.set_title(title_str, color='white', fontsize=11, fontweight='bold', pad=8)

        # Hide any unused subplots in the grid
        for idx in range(n_ops, len(axes_flat)):
            fig.delaxes(axes_flat[idx])

        # Global legend at the bottom
        legend_elements = [
            Line2D([0], [0], marker='o', color='none', markerfacecolor=COLORS['purple'], markersize=7, label='CustomMap'),
            Line2D([0], [0], marker='o', color='none', markerfacecolor=COLORS['blue'], markersize=7, label='JDK HashMap'),
        ]
        fig.legend(handles=legend_elements, loc='lower center', ncol=2, frameon=False, fontsize=11, bbox_to_anchor=(0.5, 0.02))

        fig.suptitle(f'Benchmark Comparison: {group_name}', color='white', fontsize=14, fontweight='bold', y=0.98)

        filename = f"group_{group_name.lower().replace(' ', '_').replace('(', '').replace(')', '').replace('/', '_')}.png"
        plt.tight_layout(rect=[0, 0.06, 1, 0.95])
        fig.savefig(filename, dpi=150, transparent=True, facecolor='none', edgecolor='none')
        plt.close(fig)
        print(f"✓ Saved grouped chart: {filename}")


if __name__ == '__main__':
    generate_grouped_charts()