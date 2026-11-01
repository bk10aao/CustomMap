#!/usr/bin/env python3
"""
Generate performance benchmark charts as PNG files comparing CustomMap and JDK HashMap
with a transparent background. Matches the style of the reference charts exactly.
"""

import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import matplotlib.ticker as mticker
from matplotlib.lines import Line2D
import pandas as pd
import os
import sys
import numpy as np

# ──────────────────────────────────────────────────────────────────────────────
# Configuration
# ──────────────────────────────────────────────────────────────────────────────

CUSTOMMAP_CSV_PATH = "CustomMap_wide_matrix.csv"
HASHMAP_CSV_PATH = "HashMap_wide_matrix.csv"
OUTPUT_DIR = "."

COLORS = {
    'purple': '#9B6EF3',  # Neon Purple for CustomMap
    'blue': '#4DA6FF',    # Bright Blue for JDK HashMap
    'bg': '#0D0D0D',
    'grid': '#252525',
}

FIGURE_SIZE = (12, 6.2)
DPI = 150

# Map display titles / filenames to their exact wide matrix column headers
OPERATIONS = {
    'Constructor()': 'Constructor()',
    'clear()': 'clear()',
    'compute()': 'compute()',
    'computeIfAbsent()': 'computeIfAbsent()',
    'computeIfPresent()': 'computeIfPresent()',
    'containsKey()': 'containsKey()',
    'containsValue()': 'containsValue()',
    'entrySet()': 'entrySet()',
    'equals()': 'equals()',
    'forEach()': 'forEach()',
    'get()': 'get()',
    'getOrDefault()': 'getOrDefault()',
    'hashCode()': 'hashCode()',
    'keySet()': 'keySet()',
    'merge()': 'merge()',
    'put()': 'put()',
    'putAll()': 'putAll()',
    'putIfAbsent()': 'putIfAbsent()',
    'remove()': 'remove()',
    'remove(Object, Object)': 'remove(Object, Object)',
    'replace()': 'replace()',
    'replaceAll()': 'replaceAll()',
    'replace(K, V, V)': 'replace(K, V, V)',
    'toString()': 'toString()',
    'values()': 'values()',
}


# ──────────────────────────────────────────────────────────────────────────────
# CSV Loading
# ──────────────────────────────────────────────────────────────────────────────

def load_csv(filepath):
    """Load semicolon-delimited wide matrix CSV file and return dict: {size: {op_name: time_value}}"""
    df = pd.read_csv(filepath, sep=';')
    data = {}
    for _, row in df.iterrows():
        size = int(row['Size'])
        data[size] = {
            col: float(row[col]) for col in df.columns if col != 'Size' and not pd.isna(row[col])
        }
    return data


# ──────────────────────────────────────────────────────────────────────────────
# Chart Generation
# ──────────────────────────────────────────────────────────────────────────────

def format_y_axis(value, pos):
    if value == 0:
        return '0'
    return f'{int(value):,}'


def create_chart(chart_title, col_name, custommap_data, hashmap_data,
                 canonical_sizes, output_path):
    custommap_values = [
        custommap_data[s][col_name] if s in custommap_data and col_name in custommap_data[s] else np.nan
        for s in canonical_sizes
    ]
    hashmap_values = [
        hashmap_data[s][col_name] if s in hashmap_data and col_name in hashmap_data[s] else np.nan
        for s in canonical_sizes
    ]

    fig, ax = plt.subplots(figsize=FIGURE_SIZE, dpi=DPI)
    fig.patch.set_alpha(0)
    ax.set_facecolor('none')

    x_positions = list(range(len(canonical_sizes)))

    ax.plot(x_positions, custommap_values, color=COLORS['purple'], linewidth=1.5, zorder=2)
    ax.plot(x_positions, hashmap_values, color=COLORS['blue'], linewidth=1.5, zorder=2)

    ax.scatter(x_positions, custommap_values, color=COLORS['purple'], s=35, marker='o', edgecolors=COLORS['purple'],
               linewidths=1.5, zorder=3)
    ax.scatter(x_positions, hashmap_values, color=COLORS['blue'], s=35, marker='o', edgecolors=COLORS['blue'],
               linewidths=1.5, zorder=3)

    ax.grid(True, color=COLORS['grid'], linewidth=0.8, linestyle='-', zorder=0)
    ax.set_axisbelow(True)

    ax.set_xticks(x_positions)
    ax.set_xticklabels([f'{s:,}' for s in canonical_sizes], color='white', fontsize=10)
    ax.tick_params(axis='x', colors='white', length=0, pad=8)
    ax.set_xlim(-0.4, len(canonical_sizes) - 0.6)

    ax.yaxis.set_major_formatter(mticker.FuncFormatter(format_y_axis))
    ax.tick_params(axis='y', colors='white', length=0, pad=8)
    for label in ax.get_yticklabels():
        label.set_color('white')
        label.set_fontsize(10)

    for spine in ax.spines.values():
        spine.set_visible(False)

    ax.set_xlabel('Size', color='white', fontsize=12, labelpad=12)
    ax.set_ylabel('Time (ns/op)', color='white', fontsize=11, labelpad=10)
    ax.set_title(chart_title, color='white', fontsize=15, fontweight='bold', pad=14)

    legend_elements = [
        Line2D([0], [0], marker='o', color='none', markerfacecolor=COLORS['purple'], markeredgecolor=COLORS['purple'],
               markeredgewidth=1.5, markersize=8, label='Custom', linestyle='none'),
        Line2D([0], [0], marker='o', color='none', markerfacecolor=COLORS['blue'], markeredgecolor=COLORS['blue'],
               markeredgewidth=1.5, markersize=8, label='JDK', linestyle='none'),
    ]

    leg = ax.legend(handles=legend_elements, loc='upper center', bbox_to_anchor=(0.5, -0.26), ncol=2, frameon=False,
                    fontsize=12, handlelength=1.5, handletextpad=0.6, columnspacing=2.0)

    for text in leg.get_texts():
        text.set_color('white')
        text.set_fontsize(12)

    plt.tight_layout(rect=[0, 0.18, 1, 1])
    fig.savefig(output_path, dpi=DPI, transparent=True, bbox_inches='tight', facecolor='none', edgecolor='none')
    plt.close(fig)


def main():
    if not os.path.exists(CUSTOMMAP_CSV_PATH):
        print(f"Error: Required file '{CUSTOMMAP_CSV_PATH}' not found.")
        sys.exit(1)
    if not os.path.exists(HASHMAP_CSV_PATH):
        print(f"Error: Required file '{HASHMAP_CSV_PATH}' not found.")
        sys.exit(1)

    os.makedirs(OUTPUT_DIR, exist_ok=True)

    custommap_data = load_csv(CUSTOMMAP_CSV_PATH)
    hashmap_data = load_csv(HASHMAP_CSV_PATH)

    canonical_sizes = sorted(list(set(custommap_data.keys()) | set(hashmap_data.keys())))

    for chart_label, col_name in OPERATIONS.items():
        if not any(col_name in custommap_data.get(s, {}) or col_name in hashmap_data.get(s, {}) for s in canonical_sizes):
            continue

        output_filename = chart_label.replace('/', '_').replace('(', '').replace(')', '').replace(',', '_') + '.png'
        output_path = os.path.join(OUTPUT_DIR, output_filename)
        create_chart(chart_label, col_name, custommap_data, hashmap_data, canonical_sizes, output_path)
        print(f"  ✓ {output_filename}")

    print(f"\n✓ All comparison charts saved successfully.")


if __name__ == '__main__':
    main()