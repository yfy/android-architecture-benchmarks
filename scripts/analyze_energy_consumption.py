#!/usr/bin/env python3
"""
Energy Consumption Analysis for Android Architecture Comparison
Processes energy consumption data and performs statistical analysis
"""

import json
import os
import csv
import numpy as np
from scipy.stats import friedmanchisquare
import scikit_posthocs as sp
import pandas as pd
from itertools import combinations
from datetime import datetime
from collections import defaultdict

# ============ KONFIGÜRASYON ============
ARCH_MAPPING = {
    "classicmvvm": "Classic MVVM",
    "hybrid": "HYBRID",
    "mvc": "MVC",
    "mvi": "MVI",
    "mvp": "MVP",
    "singlestatemvvm": "Single-State MVVM"
}

ARCHITECTURES = ["classicmvvm", "hybrid", "mvc", "mvi", "mvp", "singlestatemvvm"]
SCENARIOS = ["Chat_Streaming", "Shopping_Cart", "Product_Browsing"]
DATA_DIR = "rawdata/energy"
OUTPUT_FILE = "analysis_result/energy_analysis_results.json"
SPIKE_THRESHOLD_MWH = 35

# ============ VERİ YÜKLEME ============
def parse_european_float(value_str):
    """Parse European format number (comma as decimal separator)"""
    if not value_str or value_str.strip() == '':
        return 0.0
    try:
        # Try standard format first
        return float(value_str)
    except ValueError:
        # Try European format (comma as decimal separator)
        try:
            return float(value_str.replace(',', '.'))
        except ValueError:
            return 0.0

def parse_boolean(value_str):
    """Parse boolean from string"""
    if isinstance(value_str, bool):
        return value_str
    value_lower = str(value_str).lower().strip()
    return value_lower in ['true', '1', 'yes', 't']

def parse_csv_row_with_comma_decimals(line_parts):
    """
    Parse CSV row where comma is used as decimal separator.
    Expected columns: Iteration,Energy_mWh,Charge_mAh,Power_mW,Duration_Sec,Valid,Battery_%,Temp_C,Operations
    
    Strategy: Find 'true' or 'false' (Valid column), then reconstruct numeric fields before it.
    Each numeric field uses comma as decimal separator: e.g., 43,5650 -> 43.5650
    """
    # Find Valid column (true/false)
    valid_idx = None
    valid_value = None
    for i, part in enumerate(line_parts):
        if part.lower() in ['true', 'false']:
            valid_idx = i
            valid_value = part.lower() == 'true'
            break
    
    if valid_idx is None:
        return None
    
    # Expected structure: 5 numeric fields before Valid
    # Iteration,Energy_mWh,Charge_mAh,Power_mW,Duration_Sec,Valid,...
    before_valid = line_parts[:valid_idx]
    
    try:
        # Each numeric field is split into integer and decimal parts
        # Pattern: iteration, energy_int, energy_dec, charge_int, charge_dec, power_int, power_dec, duration_int, duration_dec
        
        # Duration_Sec is the last numeric field before Valid (last 2 parts)
        if len(before_valid) >= 2:
            duration_sec = float(f"{before_valid[-2]}.{before_valid[-1]}")
        else:
            duration_sec = 0.0
        
        # Energy_mWh is the second field (indices 1-2)
        if len(before_valid) >= 3:
            energy_mwh = float(f"{before_valid[1]}.{before_valid[2]}")
        else:
            energy_mwh = 0.0
        
        return {
            'energy_mwh': energy_mwh,
            'duration_sec': duration_sec,
            'valid': valid_value
        }
    except (ValueError, IndexError):
        return None

def load_scenario_data(arch_name, scenario):
    """Load and normalize energy data for a scenario"""
    filename = f"detailed_{scenario}.csv"
    filepath = os.path.join(DATA_DIR, arch_name, filename)
    
    if not os.path.exists(filepath):
        return None
    
    normalized_energies = []
    
    with open(filepath, 'r', encoding='utf-8') as f:
        lines = f.readlines()
        if len(lines) < 2:
            return None
        
        # Skip header
        for line in lines[1:]:
            line = line.strip()
            if not line:
                continue
            
            # Split by comma
            parts = line.split(',')
            
            # Parse using custom parser
            parsed = parse_csv_row_with_comma_decimals(parts)
            if not parsed or not parsed['valid']:
                continue
            
            energy_mwh = parsed['energy_mwh']
            duration_sec = parsed['duration_sec']
            
            if duration_sec <= 0:
                continue
            
            # Normalize to 60-second baseline
            normalized_energy = (energy_mwh / duration_sec) * 60
            normalized_energies.append(normalized_energy)
    
    return normalized_energies if normalized_energies else None

def load_all_data():
    """Load all energy consumption data"""
    data = {}
    
    for arch_name in ARCHITECTURES:
        if not os.path.exists(os.path.join(DATA_DIR, arch_name)):
            print(f"Warning: Architecture folder not found: {arch_name}")
            continue
        
        data[arch_name] = {}
        for scenario in SCENARIOS:
            scenario_data = load_scenario_data(arch_name, scenario)
            if scenario_data:
                data[arch_name][scenario] = scenario_data
            else:
                print(f"Warning: No valid data for {arch_name}/{scenario}")
    
    return data

# ============ İSTATİSTİKSEL ANALİZ ============
def calculate_descriptive_stats(values):
    """Calculate descriptive statistics"""
    if len(values) == 0:
        return {
            "mean": 0.0,
            "median": 0.0,
            "std": 0.0,
            "cv_percent": 0.0,
            "min": 0.0,
            "max": 0.0
        }
    
    values_arr = np.array(values)
    mean_val = np.mean(values_arr)
    median_val = np.median(values_arr)
    std_val = np.std(values_arr, ddof=1) if len(values) > 1 else 0.0
    cv_percent = (std_val / mean_val * 100) if mean_val != 0 else 0.0
    min_val = np.min(values_arr)
    max_val = np.max(values_arr)
    
    return {
        "mean": round(float(mean_val), 4),
        "median": round(float(median_val), 4),
        "std": round(float(std_val), 4),
        "cv_percent": round(float(cv_percent), 4),
        "min": round(float(min_val), 4),
        "max": round(float(max_val), 4)
    }

def cliffs_delta(x, y):
    """Cliff's Delta: nonparametric effect size"""
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
    """Interpret Cliff's Delta effect size"""
    abs_d = abs(delta)
    if abs_d < 0.147:
        return "negligible"
    elif abs_d < 0.33:
        return "small"
    elif abs_d < 0.474:
        return "medium"
    else:
        return "large"

def get_direction_interpretation(delta, arch1, arch2):
    """Get direction interpretation for energy (negative = arch1 more efficient)
    
    For energy consumption: lower is better
    - Positive delta: arch1 has higher energy (arch1 less efficient, arch2 more efficient)
    - Negative delta: arch1 has lower energy (arch1 more efficient, arch2 less efficient)
    """
    if abs(delta) < 0.147:
        return "equivalent"
    elif delta < 0:
        return f"{arch1} more efficient"
    else:
        return f"{arch2} more efficient"

# ============ ANA FONKSİYON ============
def main():
    """Main execution"""
    print("="*80)
    print("ENERGY CONSUMPTION ANALYSIS")
    print("="*80)
    print()
    
    # Load data
    print("Loading energy consumption data...")
    raw_data = load_all_data()
    
    # Normalize data and calculate statistics
    print("Normalizing data to 60-second baseline...")
    normalized_data = {}
    
    for arch_name in ARCHITECTURES:
        if arch_name not in raw_data:
            continue
        
        normalized_data[arch_name] = {}
        for scenario in SCENARIOS:
            if scenario in raw_data[arch_name]:
                energies = raw_data[arch_name][scenario]
                stats = calculate_descriptive_stats(energies)
                normalized_data[arch_name][scenario] = {
                    "iterations": [round(v, 4) for v in energies],
                    **stats
                }
    
    # Calculate overall statistics (sum of medians across scenarios)
    overall_medians = {}
    for arch_name in normalized_data.keys():
        total_median = 0.0
        count = 0
        for scenario in SCENARIOS:
            if scenario in normalized_data[arch_name]:
                total_median += normalized_data[arch_name][scenario]["median"]
                count += 1
        if count > 0:
            overall_medians[arch_name] = total_median
    
    # Rankings
    print("Calculating rankings...")
    summary = {
        "by_scenario": {},
        "overall": {}
    }
    
    for scenario in SCENARIOS:
        scenario_medians = {}
        for arch_name in normalized_data.keys():
            if scenario in normalized_data[arch_name]:
                scenario_medians[arch_name] = normalized_data[arch_name][scenario]["median"]
        
        # Rank by median (lower is better for energy)
        ranked = sorted(scenario_medians.items(), key=lambda x: x[1])
        rankings = []
        for rank, (arch_name, median) in enumerate(ranked, 1):
            rankings.append({
                "rank": rank,
                "architecture": arch_name,
                "median": round(median, 4)
            })
        
        # Calculate range percentage
        if len(ranked) > 1:
            min_val = ranked[0][1]
            max_val = ranked[-1][1]
            range_percent = ((max_val - min_val) / min_val * 100) if min_val > 0 else 0.0
        else:
            range_percent = 0.0
        
        summary["by_scenario"][scenario] = {
            "rankings": rankings,
            "range_percent": round(range_percent, 4)
        }
    
    # Overall rankings
    ranked_overall = sorted(overall_medians.items(), key=lambda x: x[1])
    overall_rankings = []
    for rank, (arch_name, total_median) in enumerate(ranked_overall, 1):
        overall_rankings.append({
            "rank": rank,
            "architecture": arch_name,
            "total_median": round(total_median, 4)
        })
    
    if len(ranked_overall) > 1:
        min_val = ranked_overall[0][1]
        max_val = ranked_overall[-1][1]
        range_percent = ((max_val - min_val) / min_val * 100) if min_val > 0 else 0.0
    else:
        range_percent = 0.0
    
    summary["overall"] = {
        "rankings": overall_rankings,
        "range_percent": round(range_percent, 4)
    }
    
    # Statistical tests
    print("Performing statistical tests...")
    statistical_tests = {}
    
    for scenario in SCENARIOS:
        print(f"  Analyzing {scenario}...")
        
        # Get data for all architectures
        scenario_data = {}
        for arch_name in ARCHITECTURES:
            if arch_name in normalized_data and scenario in normalized_data[arch_name]:
                scenario_data[arch_name] = normalized_data[arch_name][scenario]["iterations"]
        
        if len(scenario_data) < 3:
            print(f"    Warning: Not enough data for {scenario}")
            continue
        
        # Friedman test (need balanced data)
        arch_names_list = list(scenario_data.keys())
        all_runs = [scenario_data[arch] for arch in arch_names_list]
        
        # Balance data (truncate to minimum length)
        min_len = min(len(runs) for runs in all_runs)
        balanced = [runs[:min_len] for runs in all_runs]
        
        # Transpose for Friedman
        matrix = np.array(balanced).T
        
        try:
            # Friedman test
            stat, p_value = friedmanchisquare(*[matrix[:, i] for i in range(matrix.shape[1])])
            friedman_result = {
                "statistic": round(float(stat), 4),
                "p_value": round(float(p_value), 4),
                "significant": p_value < 0.05
            }
            
            # Nemenyi post-hoc (only if significant)
            nemenyi_result = {"performed": False, "pairwise_p_values": {}, "significant_pairs": []}
            if friedman_result["significant"]:
                try:
                    df = pd.DataFrame(matrix, columns=arch_names_list)
                    nemenyi_matrix = sp.posthoc_nemenyi_friedman(df)
                    
                    nemenyi_result["performed"] = True
                    for arch1, arch2 in combinations(arch_names_list, 2):
                        pair_key = f"{arch1}_vs_{arch2}"
                        p_val = nemenyi_matrix.loc[arch1, arch2]
                        nemenyi_result["pairwise_p_values"][pair_key] = round(float(p_val), 4)
                        if p_val < 0.05:
                            nemenyi_result["significant_pairs"].append(pair_key)
                except Exception as e:
                    print(f"    Warning: Nemenyi test failed: {e}")
        except Exception as e:
            print(f"    Warning: Friedman test failed: {e}")
            friedman_result = {"statistic": None, "p_value": None, "significant": False}
            nemenyi_result = {"performed": False, "pairwise_p_values": {}, "significant_pairs": []}
        
        # Cliff's Delta (all pairs)
        cliffs_delta_result = {}
        for arch1, arch2 in combinations(arch_names_list, 2):
            delta = cliffs_delta(scenario_data[arch1], scenario_data[arch2])
            interpretation = interpret_delta(delta)
            direction = get_direction_interpretation(delta, arch1, arch2)
            
            pair_key = f"{arch1}_vs_{arch2}"
            cliffs_delta_result[pair_key] = {
                "delta": round(float(delta), 4),
                "interpretation": interpretation,
                "direction": direction
            }
        
        statistical_tests[scenario] = {
            "friedman": friedman_result,
            "nemenyi": nemenyi_result,
            "cliffs_delta": cliffs_delta_result
        }
    
    # Overall statistical test (combined across scenarios)
    print("  Analyzing overall (combined scenarios)...")
    
    # Combine all scenarios for each architecture
    overall_combined_data = {}
    for arch_name in ARCHITECTURES:
        if arch_name in normalized_data:
            combined = []
            for scenario in SCENARIOS:
                if scenario in normalized_data[arch_name]:
                    combined.extend(normalized_data[arch_name][scenario]["iterations"])
            if combined:
                overall_combined_data[arch_name] = combined
    
    if len(overall_combined_data) >= 3:
        arch_names_list = list(overall_combined_data.keys())
        all_runs = [overall_combined_data[arch] for arch in arch_names_list]
        
        min_len = min(len(runs) for runs in all_runs)
        balanced = [runs[:min_len] for runs in all_runs]
        matrix = np.array(balanced).T
        
        try:
            stat, p_value = friedmanchisquare(*[matrix[:, i] for i in range(matrix.shape[1])])
            friedman_result = {
                "statistic": round(float(stat), 4),
                "p_value": round(float(p_value), 4),
                "significant": p_value < 0.05
            }
            
            nemenyi_result = {"performed": False, "pairwise_p_values": {}, "significant_pairs": []}
            if friedman_result["significant"]:
                try:
                    df = pd.DataFrame(matrix, columns=arch_names_list)
                    nemenyi_matrix = sp.posthoc_nemenyi_friedman(df)
                    
                    nemenyi_result["performed"] = True
                    for arch1, arch2 in combinations(arch_names_list, 2):
                        pair_key = f"{arch1}_vs_{arch2}"
                        p_val = nemenyi_matrix.loc[arch1, arch2]
                        nemenyi_result["pairwise_p_values"][pair_key] = round(float(p_val), 4)
                        if p_val < 0.05:
                            nemenyi_result["significant_pairs"].append(pair_key)
                except Exception as e:
                    print(f"    Warning: Nemenyi test failed for overall: {e}")
        except Exception as e:
            print(f"    Warning: Friedman test failed for overall: {e}")
            friedman_result = {"statistic": None, "p_value": None, "significant": False}
            nemenyi_result = {"performed": False, "pairwise_p_values": {}, "significant_pairs": []}
        
        cliffs_delta_result = {}
        for arch1, arch2 in combinations(arch_names_list, 2):
            delta = cliffs_delta(overall_combined_data[arch1], overall_combined_data[arch2])
            interpretation = interpret_delta(delta)
            direction = get_direction_interpretation(delta, arch1, arch2)
            
            pair_key = f"{arch1}_vs_{arch2}"
            cliffs_delta_result[pair_key] = {
                "delta": round(float(delta), 4),
                "interpretation": interpretation,
                "direction": direction
            }
        
        statistical_tests["overall"] = {
            "friedman": friedman_result,
            "nemenyi": nemenyi_result,
            "cliffs_delta": cliffs_delta_result
        }
    
    # Spike analysis
    print("Analyzing energy spikes...")
    spike_analysis = {
        "threshold_mWh": SPIKE_THRESHOLD_MWH,
        "by_architecture_scenario": {}
    }
    
    for arch_name in ARCHITECTURES:
        if arch_name not in normalized_data:
            continue
        
        spike_analysis["by_architecture_scenario"][arch_name] = {}
        for scenario in SCENARIOS:
            if scenario in normalized_data[arch_name]:
                energies = normalized_data[arch_name][scenario]["iterations"]
                spike_count = sum(1 for e in energies if e > SPIKE_THRESHOLD_MWH)
                total_valid = len(energies)
                spike_frequency = (spike_count / total_valid * 100) if total_valid > 0 else 0.0
                
                spike_analysis["by_architecture_scenario"][arch_name][scenario] = {
                    "spike_count": spike_count,
                    "total_valid": total_valid,
                    "spike_frequency_percent": round(spike_frequency, 4)
                }
    
    # Build final JSON structure
    results = {
        "metadata": {
            "analysis_date": datetime.now().isoformat(),
            "normalization_formula": "Normalized_Energy = (Measured_Energy / Test_Duration) × 60",
            "baseline_duration_sec": 60,
            "architectures": ARCHITECTURES,
            "scenarios": SCENARIOS
        },
        "normalized_data": normalized_data,
        "summary": summary,
        "statistical_tests": statistical_tests,
        "spike_analysis": spike_analysis
    }
    
    # Helper function to convert numpy types to native Python types
    def convert_to_native(obj):
        if isinstance(obj, (np.integer, np.int_)):
            return int(obj)
        elif isinstance(obj, (np.floating, np.float64, np.float32)):
            return float(obj)
        elif isinstance(obj, np.ndarray):
            return obj.tolist()
        elif isinstance(obj, (np.bool_, bool)):
            return bool(obj)
        elif isinstance(obj, dict):
            return {key: convert_to_native(value) for key, value in obj.items()}
        elif isinstance(obj, list):
            return [convert_to_native(item) for item in obj]
        return obj
    
    # Convert all numpy types to native Python types
    results = convert_to_native(results)
    
    # Save JSON
    os.makedirs(os.path.dirname(OUTPUT_FILE), exist_ok=True)
    print(f"\nSaving results to: {OUTPUT_FILE}")
    with open(OUTPUT_FILE, 'w') as f:
        json.dump(results, f, indent=2, ensure_ascii=False)
    
    # Console output
    print("\n" + "="*80)
    print("=== ENERGY ANALYSIS RESULTS ===")
    print("="*80)
    
    print("\nOVERALL RANKINGS (Total Normalized Energy - Lower is Better):")
    print(f"{'Rank':<6}{'Architecture':<20}{'Total (mWh)':<15}{'vs Best':<15}")
    print("-"*56)
    
    best_total = overall_rankings[0]["total_median"]
    for rank_info in overall_rankings:
        arch = rank_info["architecture"]
        total = rank_info["total_median"]
        vs_best = f"+{(total - best_total) / best_total * 100:.1f}%" if total > best_total else "-"
        print(f"{rank_info['rank']:<6}{arch:<20}{total:<15.4f}{vs_best:<15}")
    
    # Scenario-specific rankings
    for scenario in SCENARIOS:
        print(f"\nSCENARIO: {scenario}")
        print(f"{'Rank':<6}{'Architecture':<20}{'Median (mWh)':<15}{'CV%':<10}")
        print("-"*51)
        
        if scenario in summary["by_scenario"]:
            for rank_info in summary["by_scenario"][scenario]["rankings"]:
                arch = rank_info["architecture"]
                median = rank_info["median"]
                if arch in normalized_data and scenario in normalized_data[arch]:
                    cv = normalized_data[arch][scenario]["cv_percent"]
                    print(f"{rank_info['rank']:<6}{arch:<20}{median:<15.4f}{cv:<10.2f}")
            
            # Statistical significance
            if scenario in statistical_tests:
                friedman = statistical_tests[scenario]["friedman"]
                if friedman["statistic"] is not None:
                    sig_str = "SIGNIFICANT" if friedman["significant"] else "NOT SIGNIFICANT"
                    print(f"\nFriedman Test: χ² = {friedman['statistic']:.4f}, p = {friedman['p_value']:.4f} [{sig_str}]")
                
                nemenyi = statistical_tests[scenario]["nemenyi"]
                if nemenyi["performed"] and nemenyi["significant_pairs"]:
                    print("Significant Nemenyi pairs:")
                    for pair in nemenyi["significant_pairs"][:5]:  # Show first 5
                        p_val = nemenyi["pairwise_p_values"][pair]
                        print(f"  {pair.replace('_', ' ')} (p={p_val:.4f})")
                
                cliffs = statistical_tests[scenario]["cliffs_delta"]
                large_effects = [pair for pair, data in cliffs.items() if data["interpretation"] == "large"]
                if large_effects:
                    print("Large effect sizes:")
                    for pair in large_effects[:5]:  # Show first 5
                        delta_data = cliffs[pair]
                        print(f"  {pair.replace('_', ' ')} (δ={delta_data['delta']:.4f}, {delta_data['direction']})")
    
    print("\n" + "="*80)
    print("✓ Analysis complete!")
    print(f"✓ Results saved to: {OUTPUT_FILE}")
    print("="*80)

if __name__ == "__main__":
    main()

