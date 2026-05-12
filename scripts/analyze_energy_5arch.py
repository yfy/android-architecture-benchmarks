#!/usr/bin/env python3
"""
Energy Consumption Analysis for 5 Pure Architectures (no Hybrid).
Uses provided raw iteration data: Friedman, Nemenyi, Cliff's Delta.
Normalization: Normalized_Energy = (Measured_Energy / Duration_Sec) × 60.
"""

import json
import os
import numpy as np
from scipy.stats import friedmanchisquare
import scikit_posthocs as sp
import pandas as pd
from itertools import combinations
from datetime import datetime

# ============ CONFIGURATION (5 architectures only) ============
ARCH_MAPPING = {
    "classicmvvm": "Classic MVVM",
    "mvc": "MVC",
    "mvi": "MVI",
    "mvp": "MVP",
    "singlestatemvvm": "Single-State MVVM"
}

ARCHITECTURES_5 = ["classicmvvm", "mvc", "mvi", "mvp", "singlestatemvvm"]
SCENARIOS = ["Chat_Streaming", "Shopping_Cart", "Product_Browsing"]
OUTPUT_FILE = "analysis_result/energy_analysis_5arch.json"
SPIKE_THRESHOLD_MWH = 35

# ============ EMBEDDED RAW DATA (Energy_mWh, Duration_Sec) per iteration ============
# Source: User-provided detailed CSV blocks. Each list = 5 iterations for that scenario.
# Format: list of (energy_mwh, duration_sec) per iteration.

RAW_ITERATIONS = {
    "classicmvvm": {
        "Chat_Streaming": [
            (43.49, 61.57), (21.705, 61.23), (21.6675, 61.35), (43.36, 61.42), (21.68, 61.34)
        ],
        "Product_Browsing": [
            (21.475, 93.75), (42.85, 93.92), (21.425, 64.25), (42.505, 94.48), (42.77, 93.77)
        ],
        "Shopping_Cart": [
            (21.58, 61.54), (43.08, 92.06), (43.0, 90.85), (43.05, 90.89), (42.95, 91.42)
        ],
    },
    "mvc": {
        "Chat_Streaming": [
            (21.565, 61.76), (21.565, 61.38), (21.5525, 61.33), (21.54, 61.38), (21.54, 61.27)
        ],
        "Product_Browsing": [
            (42.575, 64.01), (21.27, 63.95), (42.54, 64.12), (21.17, 63.68), (42.34, 63.62)
        ],
        "Shopping_Cart": [
            (21.44, 61.06), (21.395, 61.21), (21.3525, 61.14), (21.31, 61.02), (21.305, 61.22)
        ],
    },
    "mvi": {
        "Chat_Streaming": [
            (20.665, 61.46), (41.33, 61.44), (41.25, 61.53), (20.625, 61.40), (41.25, 61.31)
        ],
        "Product_Browsing": [
            (20.335, 64.18), (20.335, 63.79), (20.32, 63.77), (20.305, 63.86), (20.305, 64.01)
        ],
        "Shopping_Cart": [
            (20.51, 62.53), (41.02, 60.99), (40.71, 92.37), (20.475, 61.14), (20.31, 62.06)
        ],
    },
    "mvp": {
        "Chat_Streaming": [
            (21.14, 61.52), (21.14, 61.36), (42.13, 61.45), (21.065, 61.41), (42.13, 61.39)
        ],
        "Product_Browsing": [
            (20.8125, 64.03), (41.63, 64.05), (20.815, 63.96), (41.32, 64.16), (20.66, 64.07)
        ],
        "Shopping_Cart": [
            (42.11, 60.97), (20.935, 60.97), (20.925, 62.38), (20.915, 61.19), (20.81, 60.98)
        ],
    },
    "singlestatemvvm": {
        "Chat_Streaming": [
            (42.62, 91.52), (42.62, 91.38), (42.56, 91.20), (42.5, 91.15), (42.5, 73.63)
        ],
        "Product_Browsing": [
            (41.71, 93.83), (41.71, 93.60), (41.83, 93.65), (41.83, 94.12), (41.78, 93.62)
        ],
        "Shopping_Cart": [
            (42.22, 91.81), (42.28, 91.10), (42.16, 91.80), (42.025, 92.57), (41.89, 90.85)
        ],
    },
}


def normalize_energy(energy_mwh, duration_sec):
    """Normalize to 60-second baseline: (energy / duration) * 60"""
    if duration_sec <= 0:
        return None
    return (energy_mwh / duration_sec) * 60


def build_normalized_data():
    """Build normalized_data from RAW_ITERATIONS."""
    normalized_data = {}
    for arch in ARCHITECTURES_5:
        normalized_data[arch] = {}
        for scenario in SCENARIOS:
            rows = RAW_ITERATIONS[arch][scenario]
            energies = []
            for e, d in rows:
                n = normalize_energy(e, d)
                if n is not None:
                    energies.append(n)
            if not energies:
                continue
            stats = calculate_descriptive_stats(energies)
            normalized_data[arch][scenario] = {
                "iterations": [round(v, 4) for v in energies],
                **stats
            }
    return normalized_data


def calculate_descriptive_stats(values):
    """Calculate descriptive statistics."""
    if len(values) == 0:
        return {"mean": 0.0, "median": 0.0, "std": 0.0, "cv_percent": 0.0, "min": 0.0, "max": 0.0}
    arr = np.array(values)
    mean_val = np.mean(arr)
    median_val = np.median(arr)
    std_val = np.std(arr, ddof=1) if len(arr) > 1 else 0.0
    cv = (std_val / mean_val * 100) if mean_val != 0 else 0.0
    return {
        "mean": round(float(mean_val), 4),
        "median": round(float(median_val), 4),
        "std": round(float(std_val), 4),
        "cv_percent": round(float(cv), 4),
        "min": round(float(np.min(arr)), 4),
        "max": round(float(np.max(arr)), 4),
    }


def cliffs_delta(x, y):
    """Cliff's Delta: nonparametric effect size. For energy: negative = x more efficient."""
    n_x, n_y = len(x), len(y)
    if n_x == 0 or n_y == 0:
        return 0.0
    dominance = 0
    for xi in x:
        for yj in y:
            if xi > yj:
                dominance += 1
            elif xi < yj:
                dominance -= 1
    return dominance / (n_x * n_y)


def interpret_delta(delta):
    """Interpret Cliff's Delta effect size."""
    abs_d = abs(delta)
    if abs_d < 0.147:
        return "negligible"
    elif abs_d < 0.33:
        return "small"
    elif abs_d < 0.474:
        return "medium"
    return "large"


def get_direction_interpretation(delta, arch1, arch2):
    """For energy: negative delta = arch1 more efficient (lower consumption)."""
    if abs(delta) < 0.147:
        return "equivalent"
    if delta < 0:
        return f"{arch1} more efficient"
    return f"{arch2} more efficient"


def convert_to_native(obj):
    """Convert numpy types to native Python for JSON."""
    if isinstance(obj, (np.integer, np.int_)):
        return int(obj)
    if isinstance(obj, (np.floating, np.float64, np.float32)):
        return float(obj)
    if isinstance(obj, np.ndarray):
        return obj.tolist()
    if isinstance(obj, (np.bool_, bool)):
        return bool(obj)
    if isinstance(obj, dict):
        return {k: convert_to_native(v) for k, v in obj.items()}
    if isinstance(obj, list):
        return [convert_to_native(i) for i in obj]
    return obj


def main():
    print("=" * 70)
    print("ENERGY ANALYSIS — 5 PURE ARCHITECTURES (no Hybrid)")
    print("=" * 70)

    normalized_data = build_normalized_data()

    # Overall medians (sum of scenario medians)
    overall_medians = {}
    for arch in ARCHITECTURES_5:
        total = 0.0
        for scenario in SCENARIOS:
            if scenario in normalized_data.get(arch, {}):
                total += normalized_data[arch][scenario]["median"]
        overall_medians[arch] = total

    # Summary: by scenario and overall
    summary = {"by_scenario": {}, "overall": {}}

    for scenario in SCENARIOS:
        scenario_medians = {
            arch: normalized_data[arch][scenario]["median"]
            for arch in ARCHITECTURES_5
            if scenario in normalized_data.get(arch, {})
        }
        ranked = sorted(scenario_medians.items(), key=lambda x: x[1])
        rankings = [
            {"rank": r, "architecture": arch, "median": round(med, 4)}
            for r, (arch, med) in enumerate(ranked, 1)
        ]
        min_val, max_val = ranked[0][1], ranked[-1][1]
        range_pct = ((max_val - min_val) / min_val * 100) if min_val > 0 else 0.0
        summary["by_scenario"][scenario] = {"rankings": rankings, "range_percent": round(range_pct, 4)}

    ranked_overall = sorted(overall_medians.items(), key=lambda x: x[1])
    overall_rankings = [
        {"rank": r, "architecture": arch, "total_median": round(tot, 4)}
        for r, (arch, tot) in enumerate(ranked_overall, 1)
    ]
    min_o, max_o = ranked_overall[0][1], ranked_overall[-1][1]
    range_o = ((max_o - min_o) / min_o * 100) if min_o > 0 else 0.0
    summary["overall"] = {"rankings": overall_rankings, "range_percent": round(range_o, 4)}

    # Statistical tests (Friedman df = 5-1 = 4)
    statistical_tests = {}

    for scenario in SCENARIOS:
        scenario_data = {
            arch: normalized_data[arch][scenario]["iterations"]
            for arch in ARCHITECTURES_5
            if scenario in normalized_data.get(arch, {})
        }
        arch_list = list(scenario_data.keys())
        if len(arch_list) < 3:
            continue

        all_runs = [scenario_data[a] for a in arch_list]
        min_len = min(len(r) for r in all_runs)
        balanced = [r[:min_len] for r in all_runs]
        matrix = np.array(balanced).T

        friedman_result = {"statistic": None, "p_value": None, "significant": False, "df": 4}
        nemenyi_result = {"performed": False, "pairwise_p_values": {}, "significant_pairs": []}

        try:
            stat, p_value = friedmanchisquare(*[matrix[:, i] for i in range(matrix.shape[1])])
            friedman_result = {
                "statistic": round(float(stat), 4),
                "p_value": round(float(p_value), 4),
                "significant": p_value < 0.05,
                "df": 4,
            }
            if friedman_result["significant"]:
                try:
                    df = pd.DataFrame(matrix, columns=arch_list)
                    nemenyi_matrix = sp.posthoc_nemenyi_friedman(df)
                    nemenyi_result["performed"] = True
                    for a1, a2 in combinations(arch_list, 2):
                        key = f"{a1}_vs_{a2}"
                        p_val = float(nemenyi_matrix.loc[a1, a2])
                        nemenyi_result["pairwise_p_values"][key] = round(p_val, 4)
                        if p_val < 0.05:
                            nemenyi_result["significant_pairs"].append(key)
                except Exception as e:
                    print(f"  Nemenyi {scenario}: {e}")
        except Exception as e:
            print(f"  Friedman {scenario}: {e}")

        cliffs = {}
        for a1, a2 in combinations(arch_list, 2):
            delta = cliffs_delta(scenario_data[a1], scenario_data[a2])
            cliffs[f"{a1}_vs_{a2}"] = {
                "delta": round(float(delta), 4),
                "interpretation": interpret_delta(delta),
                "direction": get_direction_interpretation(delta, a1, a2),
            }

        statistical_tests[scenario] = {
            "friedman": friedman_result,
            "nemenyi": nemenyi_result,
            "cliffs_delta": cliffs,
        }

    # Overall (combined scenarios)
    combined = {}
    for arch in ARCHITECTURES_5:
        vals = []
        for scenario in SCENARIOS:
            if scenario in normalized_data.get(arch, {}):
                vals.extend(normalized_data[arch][scenario]["iterations"])
        if vals:
            combined[arch] = vals

    if len(combined) >= 3:
        arch_list = list(combined.keys())
        all_runs = [combined[a] for a in arch_list]
        min_len = min(len(r) for r in all_runs)
        balanced = [r[:min_len] for r in all_runs]
        matrix = np.array(balanced).T

        friedman_result = {"statistic": None, "p_value": None, "significant": False, "df": 4}
        nemenyi_result = {"performed": False, "pairwise_p_values": {}, "significant_pairs": []}

        try:
            stat, p_value = friedmanchisquare(*[matrix[:, i] for i in range(matrix.shape[1])])
            friedman_result = {
                "statistic": round(float(stat), 4),
                "p_value": round(float(p_value), 4),
                "significant": p_value < 0.05,
                "df": 4,
            }
            if friedman_result["significant"]:
                try:
                    df = pd.DataFrame(matrix, columns=arch_list)
                    nemenyi_matrix = sp.posthoc_nemenyi_friedman(df)
                    nemenyi_result["performed"] = True
                    for a1, a2 in combinations(arch_list, 2):
                        key = f"{a1}_vs_{a2}"
                        p_val = float(nemenyi_matrix.loc[a1, a2])
                        nemenyi_result["pairwise_p_values"][key] = round(p_val, 4)
                        if p_val < 0.05:
                            nemenyi_result["significant_pairs"].append(key)
                except Exception as e:
                    print(f"  Nemenyi overall: {e}")
        except Exception as e:
            print(f"  Friedman overall: {e}")

        cliffs = {}
        for a1, a2 in combinations(arch_list, 2):
            delta = cliffs_delta(combined[a1], combined[a2])
            cliffs[f"{a1}_vs_{a2}"] = {
                "delta": round(float(delta), 4),
                "interpretation": interpret_delta(delta),
                "direction": get_direction_interpretation(delta, a1, a2),
            }

        statistical_tests["overall"] = {
            "friedman": friedman_result,
            "nemenyi": nemenyi_result,
            "cliffs_delta": cliffs,
        }

    # Spike analysis
    spike_analysis = {"threshold_mWh": SPIKE_THRESHOLD_MWH, "by_architecture_scenario": {}}
    for arch in ARCHITECTURES_5:
        spike_analysis["by_architecture_scenario"][arch] = {}
        for scenario in SCENARIOS:
            if scenario in normalized_data.get(arch, {}):
                iters = normalized_data[arch][scenario]["iterations"]
                spike_count = sum(1 for e in iters if e > SPIKE_THRESHOLD_MWH)
                spike_analysis["by_architecture_scenario"][arch][scenario] = {
                    "spike_count": spike_count,
                    "total_valid": len(iters),
                    "spike_frequency_percent": round(spike_count / len(iters) * 100, 4),
                }

    results = {
        "metadata": {
            "analysis_date": datetime.now().isoformat(),
            "scope": "5_pure_architectures_no_hybrid",
            "normalization_formula": "Normalized_Energy = (Measured_Energy / Test_Duration) × 60",
            "baseline_duration_sec": 60,
            "architectures": ARCHITECTURES_5,
            "scenarios": SCENARIOS,
        },
        "normalized_data": normalized_data,
        "summary": summary,
        "statistical_tests": statistical_tests,
        "spike_analysis": spike_analysis,
    }

    results = convert_to_native(results)

    os.makedirs(os.path.dirname(OUTPUT_FILE), exist_ok=True)
    with open(OUTPUT_FILE, "w") as f:
        json.dump(results, f, indent=2, ensure_ascii=False)

    # Console summary
    print("\nOVERALL RANKINGS (5 architectures, lower normalized energy = better):")
    print(f"{'Rank':<6}{'Architecture':<22}{'Total (mWh)':<14}")
    print("-" * 44)
    for r in overall_rankings:
        print(f"{r['rank']:<6}{r['architecture']:<22}{r['total_median']:<14.4f}")

    for scenario in SCENARIOS:
        print(f"\n{scenario}:")
        t = statistical_tests.get(scenario, {})
        fr = t.get("friedman", {})
        if fr.get("statistic") is not None:
            sig = "SIGNIFICANT" if fr["significant"] else "NOT SIGNIFICANT"
            print(f"  Friedman: χ²={fr['statistic']}, p={fr['p_value']} [{sig}]")
        ne = t.get("nemenyi", {})
        if ne.get("performed") and ne.get("significant_pairs"):
            print("  Nemenyi significant pairs:", ne["significant_pairs"][:5])

    print(f"\nResults saved to: {OUTPUT_FILE}")
    print("=" * 70)


if __name__ == "__main__":
    main()
