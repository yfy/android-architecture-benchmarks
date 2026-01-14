#!/usr/bin/env python3
"""
Comprehensive Statistical Analysis and Score Calculation
for Android Architecture Benchmarks
"""

import json
import os
import numpy as np
from scipy import stats
from scipy.stats import rankdata
import scikit_posthocs as sp
import pandas as pd
from itertools import combinations
from datetime import datetime
from collections import defaultdict

# ============ KONFIGÜRASYON ============
ARCH_MAPPING = {
    "classicmvvm": "Classic MVVM",
    "mvc": "MVC",
    "mvi": "MVI",
    "mvp": "MVP",
    "singlestatemvvm": "Single-State MVVM"
}

ALL_TESTS = [
    "startupCold", "startupWarm",
    "productListScrollAndPagination", "productListRapidScrolling", "productListCategoryFiltering",
    "cartQuantityUpdatesWithDynamicSetup", "cartCheckoutFlow",
    "chatListRealtimeUpdates", "chatDetailMessageStreamAndSending", "chatRapidSwitching",
    "continuousScrollJankTest", "flingJankTest", "rapidDirectionChangeJankTest",
    "cartQuantityUpdatePerformance", "categoryFilterPerformance"
]

HIGHER_IS_BETTER = {
    "startupCold": False,
    "startupWarm": False,
    "productListScrollAndPagination": True,
    "productListRapidScrolling": True,
    "productListCategoryFiltering": True,
    "cartQuantityUpdatesWithDynamicSetup": True,
    "cartCheckoutFlow": True,
    "chatListRealtimeUpdates": True,
    "chatDetailMessageStreamAndSending": True,
    "chatRapidSwitching": True,
    "continuousScrollJankTest": True,
    "flingJankTest": True,
    "rapidDirectionChangeJankTest": True,
    "cartQuantityUpdatePerformance": True,
    "categoryFilterPerformance": True
}

DATA_DIR = "rawdata/performance"
OUTPUT_FILE = "analysis_result/comprehensive_analysis.json"

# ============ VERİ YÜKLEME ============
def get_metric_name(test_name):
    """Determine metric name for a test"""
    if test_name in ["startupCold", "startupWarm"]:
        return "timeToInitialDisplayMs"
    else:
        return "frameCount"

def get_runs_from_benchmark(benchmark, metric_name):
    """Extract runs data from benchmark"""
    # First try: metrics.[metricName].runs
    if "metrics" in benchmark and metric_name in benchmark["metrics"]:
        if "runs" in benchmark["metrics"][metric_name]:
            return benchmark["metrics"][metric_name]["runs"]
    
    return None

def load_benchmark_data():
    """Load all benchmark data from JSON files"""
    data = {}
    
    # Find all result JSON files
    json_files = {
        "classicmvvm": "classicmvvm_result.json",
        "mvc": "mvc_result.json",
        "mvi": "mvi_result.json",
        "mvp": "mvp_result.json",
        "singlestatemvvm": "singlestatemvvm_result.json"
    }
    
    for arch_key, filename in json_files.items():
        filepath = os.path.join(DATA_DIR, filename)
        if not os.path.exists(filepath):
            print(f"Warning: {filename} not found, skipping {arch_key}")
            continue
        
        with open(filepath, 'r') as f:
            arch_data = json.load(f)
        
        arch_name = ARCH_MAPPING[arch_key]
        data[arch_name] = {}
        
        if "benchmarks" not in arch_data:
            print(f"Warning: No 'benchmarks' in {filename}")
            continue
        
        for benchmark in arch_data["benchmarks"]:
            test_name = benchmark.get("name")
            if test_name not in ALL_TESTS:
                continue
            
            metric_name = get_metric_name(test_name)
            runs = get_runs_from_benchmark(benchmark, metric_name)
            
            if runs is None or len(runs) == 0:
                continue
            
            data[arch_name][test_name] = runs
    
    return data

def load_memory_data():
    """Load memory data from JSON files"""
    # Memory data is in the same JSON files, in a "memory" section or similar
    # For now, using hardcoded values from user's specification
    memory_data = {
        "Classic MVVM": {"initial": 30.40, "peak": 38.42, "growth": 0.55},
        "MVC": {"initial": 30.34, "peak": 37.71, "growth": 0.48},
        "MVI": {"initial": 29.57, "peak": 38.42, "growth": 1.09},
        "MVP": {"initial": 30.00, "peak": 38.00, "growth": 1.00},
        "Single-State MVVM": {"initial": 33.28, "peak": 40.87, "growth": 0.37}
    }
    return memory_data

def load_code_quality_data():
    """Load code quality data"""
    # Using hardcoded values from user's specification
    code_data = {
        "Classic MVVM": {"sloc": 2459, "debt_hours": 2.9, "avg_cog": 2.61, "avg_cyc": 1.95, "max_cyc": 9},
        "MVC": {"sloc": 2715, "debt_hours": 1.7, "avg_cog": 1.83, "avg_cyc": 1.75, "max_cyc": 9},
        "MVI": {"sloc": 2510, "debt_hours": 2.0, "avg_cog": 2.60, "avg_cyc": 2.38, "max_cyc": 9},
        "MVP": {"sloc": 3001, "debt_hours": 4.6, "avg_cog": 1.50, "avg_cyc": 1.61, "max_cyc": 10},
        "Single-State MVVM": {"sloc": 2442, "debt_hours": 2.0, "avg_cog": 2.50, "avg_cyc": 2.02, "max_cyc": 14}
    }
    return code_data

# ============ İSTATİSTİKSEL ANALİZ ============
def calculate_descriptive_stats(values):
    """Calculate descriptive statistics"""
    if len(values) == 0:
        return {"mean": 0, "median": 0, "std": 0, "cv_percent": 0, "min": 0, "max": 0, "n": 0}
    
    values_arr = np.array(values)
    mean_val = np.mean(values_arr)
    median_val = np.median(values_arr)
    std_val = np.std(values_arr, ddof=1) if len(values) > 1 else 0.0
    cv_percent = (std_val / mean_val * 100) if mean_val != 0 else 0.0
    min_val = np.min(values_arr)
    max_val = np.max(values_arr)
    n = len(values_arr)
    
    return {
        "mean": float(mean_val),
        "median": float(median_val),
        "std": float(std_val),
        "cv_percent": float(cv_percent),
        "min": float(min_val),
        "max": float(max_val),
        "n": int(n)
    }

def friedman_test(all_runs):
    """Perform Friedman test"""
    # Ensure equal sample sizes
    min_len = min(len(runs) for runs in all_runs if len(runs) > 0)
    if min_len == 0:
        return {"chi_squared": None, "df": 4, "p_value": None, "significant": False}
    
    balanced = [runs[:min_len] for runs in all_runs]
    matrix = np.array(balanced).T
    
    try:
        stat, p_value = stats.friedmanchisquare(*matrix.T)
        return {
            "chi_squared": float(stat),
            "df": 4,
            "p_value": float(p_value),
            "significant": bool(p_value < 0.05)
        }
    except Exception as e:
        return {"chi_squared": None, "df": 4, "p_value": None, "significant": False}

def nemenyi_test(all_runs, arch_names):
    """Perform Nemenyi post-hoc test"""
    min_len = min(len(runs) for runs in all_runs if len(runs) > 0)
    if min_len == 0:
        return {}
    
    balanced = [runs[:min_len] for runs in all_runs]
    matrix = np.array(balanced).T
    
    try:
        df = pd.DataFrame(matrix, columns=arch_names)
        nemenyi_matrix = sp.posthoc_nemenyi_friedman(df)
        
        result = {}
        for arch1, arch2 in combinations(arch_names, 2):
            pair_name = f"{arch1} vs {arch2}"
            p_value = nemenyi_matrix.loc[arch1, arch2]
            result[pair_name] = {
                "p_value": float(p_value),
                "significant": bool(p_value < 0.05)
            }
        return result
    except Exception as e:
        return {}

def cliffs_delta(x, y):
    """Calculate Cliff's Delta effect size"""
    n_x, n_y = len(x), len(y)
    dominance = 0
    for xi in x:
        for yj in y:
            if xi > yj:
                dominance += 1
            elif xi < yj:
                dominance -= 1
    return dominance / (n_x * n_y)

def interpret_delta(delta):
    """Interpret Cliff's Delta"""
    abs_d = abs(delta)
    if abs_d < 0.147:
        return "negligible"
    elif abs_d < 0.33:
        return "small"
    elif abs_d < 0.474:
        return "medium"
    else:
        return "large"

def calculate_cliffs_delta_all(all_runs, arch_names):
    """Calculate Cliff's Delta for all pairs"""
    result = {}
    for i, arch1 in enumerate(arch_names):
        for j, arch2 in enumerate(arch_names):
            if i >= j:
                continue
            pair_name = f"{arch1} vs {arch2}"
            delta = cliffs_delta(all_runs[i], all_runs[j])
            result[pair_name] = {
                "delta": float(delta),
                "effect": interpret_delta(delta)
            }
    return result

# ============ RANKING ============
def calculate_ranking(medians, higher_is_better):
    """Calculate fractional ranking"""
    if higher_is_better:
        # Negate for ranking (higher = better rank)
        ranks = rankdata([-m for m in medians], method='average')
    else:
        ranks = rankdata(medians, method='average')
    
    return ranks

# ============ SKOR HESAPLAMA ============
def calculate_performance_score(avg_rank):
    """S_perf = 120 - (Rank_avg × 20)"""
    return 120 - (avg_rank * 20)

def normalize_inverse(value, all_values):
    """Inverse normalization (lower value = higher score)"""
    min_val, max_val = min(all_values), max(all_values)
    if max_val == min_val:
        return 1.0
    return (max_val - value) / (max_val - min_val)

def calculate_memory_score(arch_data, all_data):
    """S_mem = ((I_norm + P_norm + G_norm) / 3) × 100"""
    all_initial = [d["initial"] for d in all_data.values()]
    all_peak = [d["peak"] for d in all_data.values()]
    all_growth = [d["growth"] for d in all_data.values()]
    
    i_norm = normalize_inverse(arch_data["initial"], all_initial)
    p_norm = normalize_inverse(arch_data["peak"], all_peak)
    g_norm = normalize_inverse(arch_data["growth"], all_growth)
    
    return ((i_norm + p_norm + g_norm) / 3) * 100

def calculate_code_quality_score(arch_data, all_data):
    """S_code = ((DD_norm + Cog_norm + Cyc_norm + Hotspot) / 4) × 100"""
    # Debt Density
    dd = arch_data["debt_hours"] / (arch_data["sloc"] / 1000)
    all_dd = [d["debt_hours"] / (d["sloc"] / 1000) for d in all_data.values()]
    
    all_cog = [d["avg_cog"] for d in all_data.values()]
    all_cyc = [d["avg_cyc"] for d in all_data.values()]
    
    dd_norm = normalize_inverse(dd, all_dd)
    cog_norm = normalize_inverse(arch_data["avg_cog"], all_cog)
    cyc_norm = normalize_inverse(arch_data["avg_cyc"], all_cyc)
    
    # Hotspot penalty
    hotspot = max(0, 1 - (arch_data["max_cyc"] / 20))
    
    return ((dd_norm + cog_norm + cyc_norm + hotspot) / 4) * 100

# ============ ANA FONKSİYON ============
def main():
    """Main execution"""
    print("="*80)
    print("ANDROID ARCHITECTURE BENCHMARK - COMPREHENSIVE ANALYSIS")
    print("="*80)
    print()
    
    # Load data
    print("Loading data...")
    benchmark_data = load_benchmark_data()
    memory_data = load_memory_data()
    code_data = load_code_quality_data()
    
    # Check data completeness
    print("\nDATA LOADED:")
    for arch_name in ARCH_MAPPING.values():
        if arch_name in benchmark_data:
            tests_found = len(benchmark_data[arch_name])
            print(f"✓ {arch_name}: {tests_found}/15 tests")
        else:
            print(f"✗ {arch_name}: 0/15 tests")
    
    # Initialize results
    results = {
        "metadata": {
            "analysis_date": datetime.now().strftime("%Y-%m-%d"),
            "total_tests": len(ALL_TESTS),
            "architectures": sorted(list(set(ARCH_MAPPING.values()))),
            "data_source": f"{DATA_DIR}/*.json",
            "statistical_methods": {
                "friedman": "Nonparametric test for comparing 5+ related groups",
                "nemenyi": "Post-hoc pairwise comparison after significant Friedman",
                "cliffs_delta": "Nonparametric effect size measure"
            }
        },
        "statistical_analysis": {},
        "rankings": {
            "per_test": {},
            "average_ranks": {}
        },
        "scores": {
            "performance": {"formula": "S_perf = 120 - (Avg_Rank × 20)", "scores": {}, "ranking": []},
            "memory": {"formula": "S_mem = ((I_norm + P_norm + G_norm) / 3) × 100", "raw_data": {}, "normalized": {}, "scores": {}, "ranking": []},
            "code_quality": {"formula": "S_code = ((DD_norm + Cog_norm + Cyc_norm + Hotspot) / 4) × 100", "raw_data": {}, "normalized": {}, "scores": {}, "ranking": []}
        },
        "summary": {
            "dimension_leaders": {},
            "statistical_summary": {}
        },
        "validation": {
            "data_completeness": {},
            "cross_checks": {}
        }
    }
    
    # Collect all ranks for average calculation
    all_ranks = defaultdict(list)
    
    # Analyze each test
    print("\nSTATISTICAL ANALYSIS:")
    print("Processing tests...")
    
    for test_name in ALL_TESTS:
        # Collect data for this test
        test_runs = {}
        test_medians = {}
        test_descriptive = {}
        
        for arch_name in ARCH_MAPPING.values():
            if arch_name in benchmark_data and test_name in benchmark_data[arch_name]:
                runs = benchmark_data[arch_name][test_name]
                test_runs[arch_name] = runs
                test_descriptive[arch_name] = calculate_descriptive_stats(runs)
                test_medians[arch_name] = test_descriptive[arch_name]["median"]
        
        if len(test_runs) < 2:
            continue
        
        arch_names = list(test_runs.keys())
        all_runs_list = [test_runs[arch] for arch in arch_names]
        
        # Statistical tests
        friedman_result = friedman_test(all_runs_list)
        nemenyi_result = {}
        if friedman_result["significant"]:
            nemenyi_result = nemenyi_test(all_runs_list, arch_names)
        cliffs_delta_result = calculate_cliffs_delta_all(all_runs_list, arch_names)
        
        # Ranking
        medians_list = [test_medians[arch] for arch in arch_names]
        higher_is_better = HIGHER_IS_BETTER[test_name]
        ranks = calculate_ranking(medians_list, higher_is_better)
        
        ranking_dict = {}
        for i, arch in enumerate(arch_names):
            ranking_dict[arch] = {"median": test_medians[arch], "rank": float(ranks[i])}
            all_ranks[arch].append(float(ranks[i]))
        
        # Sort by rank for per_test ranking
        sorted_ranking = sorted(ranking_dict.items(), key=lambda x: x[1]["rank"])
        results["rankings"]["per_test"][test_name] = [arch for arch, _ in sorted_ranking]
        
        # Store results
        results["statistical_analysis"][test_name] = {
            "metric_direction": "higher_is_better" if higher_is_better else "lower_is_better",
            "descriptive": test_descriptive,
            "friedman": friedman_result,
            "nemenyi": nemenyi_result,
            "cliffs_delta": cliffs_delta_result,
            "ranking": ranking_dict
        }
    
    # Calculate average ranks
    print("Calculating average ranks...")
    for arch_name in ARCH_MAPPING.values():
        if arch_name in all_ranks:
            avg_rank = np.mean(all_ranks[arch_name])
            results["rankings"]["average_ranks"][arch_name] = float(avg_rank)
        else:
            results["rankings"]["average_ranks"][arch_name] = 0.0
    
    # Performance scores
    print("Calculating performance scores...")
    perf_scores = {}
    for arch_name, avg_rank in results["rankings"]["average_ranks"].items():
        score = calculate_performance_score(avg_rank)
        perf_scores[arch_name] = {"avg_rank": avg_rank, "score": float(score)}
    
    results["scores"]["performance"]["scores"] = perf_scores
    results["scores"]["performance"]["ranking"] = sorted(perf_scores.items(), key=lambda x: x[1]["score"], reverse=True)
    results["scores"]["performance"]["ranking"] = [arch for arch, _ in results["scores"]["performance"]["ranking"]]
    
    # Memory scores
    print("Calculating memory scores...")
    for arch_name, mem_data in memory_data.items():
        results["scores"]["memory"]["raw_data"][arch_name] = {
            "initial_mb": mem_data["initial"],
            "peak_mb": mem_data["peak"],
            "growth_mb": mem_data["growth"]
        }
        
        all_initial = [d["initial"] for d in memory_data.values()]
        all_peak = [d["peak"] for d in memory_data.values()]
        all_growth = [d["growth"] for d in memory_data.values()]
        
        results["scores"]["memory"]["normalized"][arch_name] = {
            "i_norm": float(normalize_inverse(mem_data["initial"], all_initial)),
            "p_norm": float(normalize_inverse(mem_data["peak"], all_peak)),
            "g_norm": float(normalize_inverse(mem_data["growth"], all_growth))
        }
        
        score = calculate_memory_score(mem_data, memory_data)
        results["scores"]["memory"]["scores"][arch_name] = float(score)
    
    results["scores"]["memory"]["ranking"] = sorted(results["scores"]["memory"]["scores"].items(), key=lambda x: x[1], reverse=True)
    results["scores"]["memory"]["ranking"] = [arch for arch, _ in results["scores"]["memory"]["ranking"]]
    
    # Code quality scores
    print("Calculating code quality scores...")
    for arch_name, code_data_arch in code_data.items():
        dd = code_data_arch["debt_hours"] / (code_data_arch["sloc"] / 1000)
        all_dd = [d["debt_hours"] / (d["sloc"] / 1000) for d in code_data.values()]
        all_cog = [d["avg_cog"] for d in code_data.values()]
        all_cyc = [d["avg_cyc"] for d in code_data.values()]
        
        results["scores"]["code_quality"]["raw_data"][arch_name] = {
            "sloc": code_data_arch["sloc"],
            "debt_hours": code_data_arch["debt_hours"],
            "debt_density": float(dd),
            "avg_cog": code_data_arch["avg_cog"],
            "avg_cyc": code_data_arch["avg_cyc"],
            "max_cyc": code_data_arch["max_cyc"]
        }
        
        results["scores"]["code_quality"]["normalized"][arch_name] = {
            "dd_norm": float(normalize_inverse(dd, all_dd)),
            "cog_norm": float(normalize_inverse(code_data_arch["avg_cog"], all_cog)),
            "cyc_norm": float(normalize_inverse(code_data_arch["avg_cyc"], all_cyc)),
            "hotspot": float(max(0, 1 - (code_data_arch["max_cyc"] / 20)))
        }
        
        score = calculate_code_quality_score(code_data_arch, code_data)
        results["scores"]["code_quality"]["scores"][arch_name] = float(score)
    
    results["scores"]["code_quality"]["ranking"] = sorted(results["scores"]["code_quality"]["scores"].items(), key=lambda x: x[1], reverse=True)
    results["scores"]["code_quality"]["ranking"] = [arch for arch, _ in results["scores"]["code_quality"]["ranking"]]
    
    # Summary
    results["summary"]["dimension_leaders"] = {
        "performance": {
            "winner": results["scores"]["performance"]["ranking"][0],
            "score": results["scores"]["performance"]["scores"][results["scores"]["performance"]["ranking"][0]]["score"]
        },
        "memory": {
            "winner": results["scores"]["memory"]["ranking"][0],
            "score": results["scores"]["memory"]["scores"][results["scores"]["memory"]["ranking"][0]]
        },
        "code_quality": {
            "winner": results["scores"]["code_quality"]["ranking"][0],
            "score": results["scores"]["code_quality"]["scores"][results["scores"]["code_quality"]["ranking"][0]]
        }
    }
    
    # Statistical summary
    friedman_sig = sum(1 for t in results["statistical_analysis"].values() if t["friedman"]["significant"])
    friedman_nonsig = len(results["statistical_analysis"]) - friedman_sig
    total_nemenyi = sum(len(t["nemenyi"]) for t in results["statistical_analysis"].values())
    sig_nemenyi = sum(sum(1 for p in t["nemenyi"].values() if p["significant"]) for t in results["statistical_analysis"].values())
    total_large_effects = sum(sum(1 for d in t["cliffs_delta"].values() if d["effect"] == "large") for t in results["statistical_analysis"].values())
    
    results["summary"]["statistical_summary"] = {
        "friedman_significant_tests": friedman_sig,
        "friedman_nonsignificant_tests": friedman_nonsig,
        "total_significant_pairwise": sig_nemenyi,
        "total_large_effects": total_large_effects
    }
    
    # Validation
    for arch_name in ARCH_MAPPING.values():
        if arch_name in benchmark_data:
            tests_found = list(benchmark_data[arch_name].keys())
            missing = [t for t in ALL_TESTS if t not in tests_found]
            results["validation"]["data_completeness"][arch_name] = {
                "tests_found": len(tests_found),
                "missing": missing
            }
    
    # Save JSON
    os.makedirs(os.path.dirname(OUTPUT_FILE), exist_ok=True)
    print(f"\nSaving results to: {OUTPUT_FILE}")
    with open(OUTPUT_FILE, 'w') as f:
        json.dump(results, f, indent=2)
    
    # Print summary
    print("\n" + "="*80)
    print("SUMMARY")
    print("="*80)
    print(f"\nFriedman significant (p < 0.05): {friedman_sig}/{len(results['statistical_analysis'])} tests")
    print(f"Friedman non-significant: {friedman_nonsig}/{len(results['statistical_analysis'])} tests")
    print(f"Total significant pairwise (Nemenyi): {sig_nemenyi}/{total_nemenyi}")
    print(f"Large effect sizes (Cliff's δ ≥ 0.474): {total_large_effects}/150")
    
    print("\nPERFORMANCE SCORES (S_perf = 120 - Avg_Rank × 20):")
    print()
    for arch_name, data in sorted(perf_scores.items(), key=lambda x: x[1]["score"], reverse=True):
        print(f"{arch_name:<20} {data['score']:>6.2f} (Avg Rank: {data['avg_rank']:.2f})")
    
    print("\nMEMORY SCORES (S_mem):")
    print()
    for arch_name, score in sorted(results["scores"]["memory"]["scores"].items(), key=lambda x: x[1], reverse=True):
        print(f"{arch_name:<20} {score:>6.2f}")
    
    print("\nCODE QUALITY SCORES (S_code):")
    print()
    for arch_name, score in sorted(results["scores"]["code_quality"]["scores"].items(), key=lambda x: x[1], reverse=True):
        print(f"{arch_name:<20} {score:>6.2f}")
    
    print("\nDIMENSION LEADERS:")
    print(f"Performance:   {results['summary']['dimension_leaders']['performance']['winner']}")
    print(f"Memory:        {results['summary']['dimension_leaders']['memory']['winner']}")
    print(f"Code Quality:  {results['summary']['dimension_leaders']['code_quality']['winner']}")
    
    print("\n" + "="*80)
    print("✓ Analysis complete!")
    print(f"✓ Results saved to: {OUTPUT_FILE}")
    print("="*80)

if __name__ == "__main__":
    main()

