#!/usr/bin/env python3
"""
Android Architecture Scoring Calculator with Hybrid Architecture
Calculates Performance, Memory, and Code Quality scores
"""

import json
import os
import numpy as np
from scipy.stats import rankdata

# ============ KONFIGÜRASYON ============
ARCH_MAPPING = {
    "classicmvvm": "Classic MVVM",
    "mvc": "MVC",
    "mvi": "MVI",
    "mvp": "MVP",
    "singlestatemvvm": "Single-State MVVM",
    "hybrid": "Hybrid"
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

# ============ VERİ YÜKLEME ============
def get_metric_name(test_name):
    """Determine metric name for a test"""
    if test_name in ["startupCold", "startupWarm"]:
        return "timeToInitialDisplayMs"
    else:
        return "frameCount"

def get_runs_from_benchmark(benchmark, metric_name):
    """Extract runs data from benchmark"""
    if "metrics" in benchmark and metric_name in benchmark["metrics"]:
        if "runs" in benchmark["metrics"][metric_name]:
            return benchmark["metrics"][metric_name]["runs"]
    return None

def load_benchmark_data():
    """Load all benchmark data from JSON files"""
    data = {}
    
    json_files = {
        "classicmvvm": "classicmvvm_result.json",
        "mvc": "mvc_result.json",
        "mvi": "mvi_result.json",
        "mvp": "mvp_result.json",
        "singlestatemvvm": "singlestatemvvm_result.json",
        "hybrid": "hybrid_result.json"
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

def parse_memory_from_json(filepath):
    """Parse memory data from JSON file"""
    with open(filepath, 'r') as f:
        data = json.load(f)
    
    # Try to find memory data in memoryBenchmarkResult
    if "memoryBenchmarkResult" in data and len(data["memoryBenchmarkResult"]) > 0:
        # Find TotalPSS_MB row
        totalpss_row = None
        for row in data["memoryBenchmarkResult"]:
            if row.get("Label") == "TotalPSS_MB":
                totalpss_row = row
                break
        
        if totalpss_row:
            # Extract initial value
            initial = totalpss_row.get("01_AppLaunch")
            if initial is None:
                return None
            
            # Find peak value (check all snapshot columns)
            peak = initial
            for key, value in totalpss_row.items():
                if key.startswith(("0", "1")) and isinstance(value, (int, float)) and value > peak:
                    peak = value
            
            # Extract final value
            final = totalpss_row.get("10_Peak_AfterAllOperations") or totalpss_row.get("09_ChatDetail_AfterStream")
            if final is None:
                final = initial
            
            # Calculate growth
            growth = final - initial
            
            return {"initial": float(initial), "peak": float(peak), "growth": float(growth)}
    
    return None

def load_memory_data():
    """Load memory data from JSON files"""
    memory_data = {}
    
    json_files = {
        "classicmvvm": "classicmvvm_result.json",
        "mvc": "mvc_result.json",
        "mvi": "mvi_result.json",
        "mvp": "mvp_result.json",
        "singlestatemvvm": "singlestatemvvm_result.json",
        "hybrid": "hybrid_result.json"
    }
    
    # Known memory values (for non-hybrid architectures)
    known_values = {
        "Classic MVVM": {"initial": 30.40, "peak": 38.42, "growth": 0.55},
        "MVC": {"initial": 30.34, "peak": 37.71, "growth": 0.48},
        "MVI": {"initial": 29.57, "peak": 38.42, "growth": 1.09},
        "MVP": {"initial": 30.00, "peak": 38.00, "growth": 1.00},
        "Single-State MVVM": {"initial": 33.28, "peak": 40.87, "growth": 0.37}
    }
    
    for arch_key, filename in json_files.items():
        arch_name = ARCH_MAPPING[arch_key]
        filepath = os.path.join(DATA_DIR, filename)
        
        if arch_name in known_values:
            memory_data[arch_name] = known_values[arch_name]
        elif os.path.exists(filepath):
            parsed = parse_memory_from_json(filepath)
            if parsed:
                memory_data[arch_name] = parsed
            else:
                print(f"Warning: Could not parse memory data for {arch_name}")
    
    return memory_data

def load_code_quality_data():
    """Load code quality data (hybrid has no static analysis)"""
    code_data = {
        "Classic MVVM": {"sloc": 2459, "debt_hours": 2.9, "avg_cog": 2.61, "avg_cyc": 1.95, "max_cyc": 9},
        "MVC": {"sloc": 2715, "debt_hours": 1.7, "avg_cog": 1.83, "avg_cyc": 1.75, "max_cyc": 9},
        "MVI": {"sloc": 2510, "debt_hours": 2.0, "avg_cog": 2.60, "avg_cyc": 2.38, "max_cyc": 9},
        "MVP": {"sloc": 3001, "debt_hours": 4.6, "avg_cog": 1.50, "avg_cyc": 1.61, "max_cyc": 10},
        "Single-State MVVM": {"sloc": 2442, "debt_hours": 2.0, "avg_cog": 2.50, "avg_cyc": 2.02, "max_cyc": 14}
        # Hybrid: no static analysis data
    }
    return code_data

# ============ SCORE HESAPLAMA ============
def normalize_inverse(value, values_list):
    """Inverse normalization: lower = better"""
    values_arr = np.array(values_list)
    min_val = np.min(values_arr)
    max_val = np.max(values_arr)
    
    if max_val == min_val:
        return 1.0
    
    return (max_val - value) / (max_val - min_val)

def calculate_performance_score(benchmark_data):
    """Calculate Performance Score: PerfScore = 120 - (AvgRank × 20)"""
    
    # Get all architectures
    architectures = list(benchmark_data.keys())
    
    # Calculate ranks for each test
    rankings = {}
    perf_data = {}
    
    for test_name in ALL_TESTS:
        # Get values for all architectures
        test_values = {}
        for arch_name in architectures:
            if test_name in benchmark_data[arch_name]:
                # Use mean of runs
                runs = benchmark_data[arch_name][test_name]
                test_values[arch_name] = np.mean(runs)
        
        if len(test_values) == 0:
            continue
        
        # Sort by value (direction depends on HIGHER_IS_BETTER)
        higher_is_better = HIGHER_IS_BETTER[test_name]
        sorted_archs = sorted(test_values.items(), key=lambda x: x[1], reverse=higher_is_better)
        
        # Calculate ranks (handle ties with fractional ranks)
        values_arr = np.array([v for _, v in sorted_archs])
        ranks = rankdata(-values_arr if higher_is_better else values_arr, method='average')
        
        rankings[test_name] = {}
        for i, (arch_name, _) in enumerate(sorted_archs):
            rankings[test_name][arch_name] = ranks[i]
    
    # Calculate average rank and performance score for each architecture
    perf_scores = {}
    for arch_name in architectures:
        ranks = []
        for test_name in rankings:
            if arch_name in rankings[test_name]:
                ranks.append(rankings[test_name][arch_name])
        
        if len(ranks) > 0:
            avg_rank = np.mean(ranks)
            perf_score = 120 - (avg_rank * 20)
            perf_score = max(20, min(100, perf_score))  # Clamp to [20, 100]
            
            perf_scores[arch_name] = {
                "avg_rank": float(avg_rank),
                "score": float(perf_score),
                "ranks": {test: float(rankings[test][arch_name]) for test in rankings if arch_name in rankings[test]}
            }
    
    return perf_scores, rankings

def calculate_memory_score(memory_data):
    """Calculate Memory Efficiency Score"""
    
    architectures = list(memory_data.keys())
    
    # Get all values for normalization
    all_initial = [memory_data[arch]["initial"] for arch in architectures]
    all_peak = [memory_data[arch]["peak"] for arch in architectures]
    all_growth = [memory_data[arch]["growth"] for arch in architectures]
    
    mem_scores = {}
    normalized_data = {}
    
    for arch_name in architectures:
        mem = memory_data[arch_name]
        
        # Normalize (inverse: lower = better)
        i_norm = normalize_inverse(mem["initial"], all_initial)
        p_norm = normalize_inverse(mem["peak"], all_peak)
        g_norm = normalize_inverse(mem["growth"], all_growth)
        
        # Calculate score
        mem_score = 100 * (i_norm + p_norm + g_norm) / 3
        
        normalized_data[arch_name] = {
            "i_norm": float(i_norm),
            "p_norm": float(p_norm),
            "g_norm": float(g_norm)
        }
        
        mem_scores[arch_name] = {
            "score": float(mem_score),
            "raw": {
                "initial": mem["initial"],
                "peak": mem["peak"],
                "growth": mem["growth"]
            },
            "normalized": normalized_data[arch_name]
        }
    
    return mem_scores

def calculate_code_quality_score(code_data):
    """Calculate Code Quality Score"""
    
    architectures = [arch for arch in code_data.keys()]
    
    # Calculate Debt Density for each architecture
    debt_densities = {}
    for arch_name in architectures:
        code = code_data[arch_name]
        debt_density = code["debt_hours"] / (code["sloc"] / 1000)
        debt_densities[arch_name] = debt_density
    
    # Get all values for normalization
    all_dd = list(debt_densities.values())
    all_cog = [code_data[arch]["avg_cog"] for arch in architectures]
    all_cyc = [code_data[arch]["avg_cyc"] for arch in architectures]
    
    code_scores = {}
    
    for arch_name in architectures:
        code = code_data[arch_name]
        dd = debt_densities[arch_name]
        
        # Normalize (inverse: lower = better)
        dd_norm = normalize_inverse(dd, all_dd)
        cog_norm = normalize_inverse(code["avg_cog"], all_cog)
        cyc_norm = normalize_inverse(code["avg_cyc"], all_cyc)
        
        # Hotspot penalty
        hotspot = max(0, 1 - code["max_cyc"] / 20)
        
        # Calculate score
        code_score = 100 * (dd_norm + cog_norm + cyc_norm + hotspot) / 4
        
        code_scores[arch_name] = {
            "score": float(code_score),
            "debt_density": float(dd),
            "dd_norm": float(dd_norm),
            "cog_norm": float(cog_norm),
            "cyc_norm": float(cyc_norm),
            "hotspot": float(hotspot)
        }
    
    return code_scores

def calculate_overall_score(perf_score, code_score, mem_score):
    """Calculate Overall Score"""
    if code_score is None:
        # If no code quality score, use only perf and memory with adjusted weights
        # Overall = (Perf+1)^0.50 × (Mem+1)^0.50
        return (perf_score + 1) ** 0.50 * (mem_score + 1) ** 0.50
    else:
        # Normal formula
        return (perf_score + 1) ** 0.40 * (code_score + 1) ** 0.35 * (mem_score + 1) ** 0.25

# ============ MAIN ============
def main():
    print("=" * 80)
    print("ANDROID ARCHITECTURE SCORING CALCULATOR (WITH HYBRID)")
    print("=" * 80)
    print()
    
    # Load data
    print("Loading benchmark data...")
    benchmark_data = load_benchmark_data()
    
    print("Loading memory data...")
    memory_data = load_memory_data()
    
    print("Loading code quality data...")
    code_data = load_code_quality_data()
    
    print(f"\nArchitectures loaded: {sorted(benchmark_data.keys())}")
    print(f"Memory data for: {sorted(memory_data.keys())}")
    print(f"Code quality data for: {sorted(code_data.keys())}")
    print()
    
    # Calculate Performance Scores
    print("=" * 80)
    print("1. PERFORMANCE SCORES")
    print("=" * 80)
    perf_scores, rankings = calculate_performance_score(benchmark_data)
    
    # Print performance ranking table
    print("\nPerformance Rankings (Lower rank = Better):")
    print("\nTest".ljust(30), end="")
    for arch in sorted(perf_scores.keys()):
        print(f"{arch[:15]:>15}", end="")
    print()
    print("-" * (30 + 15 * len(perf_scores)))
    
    for test_name in ALL_TESTS:
        if test_name in rankings:
            print(test_name[:30].ljust(30), end="")
            for arch in sorted(perf_scores.keys()):
                if arch in rankings[test_name]:
                    rank = rankings[test_name][arch]
                    print(f"{rank:>15.2f}", end="")
                else:
                    print(f"{'N/A':>15}", end="")
            print()
    
    print("\n" + "-" * (30 + 15 * len(perf_scores)))
    print("Average Rank".ljust(30), end="")
    for arch in sorted(perf_scores.keys()):
        print(f"{perf_scores[arch]['avg_rank']:>15.2f}", end="")
    print()
    
    print("\nPerformance Scores (PerfScore = 120 - AvgRank × 20):")
    for arch in sorted(perf_scores.items(), key=lambda x: x[1]['score'], reverse=True):
        print(f"  {arch[0]:<25} {arch[1]['score']:>6.2f} (Avg Rank: {arch[1]['avg_rank']:.2f})")
    
    # Calculate Memory Scores
    print("\n" + "=" * 80)
    print("2. MEMORY EFFICIENCY SCORES")
    print("=" * 80)
    mem_scores = calculate_memory_score(memory_data)
    
    print("\nMemory Scores (MemScore = 100 × (I_norm + P_norm + G_norm) / 3):")
    print("\nArchitecture".ljust(25), "Initial".rjust(10), "Peak".rjust(10), "Growth".rjust(10), "Score".rjust(10))
    print("-" * 65)
    for arch in sorted(mem_scores.items(), key=lambda x: x[1]['score'], reverse=True):
        mem = arch[1]
        print(f"{arch[0]:<25} {mem['raw']['initial']:>10.2f} {mem['raw']['peak']:>10.2f} "
              f"{mem['raw']['growth']:>10.2f} {mem['score']:>10.2f}")
    
    # Calculate Code Quality Scores
    print("\n" + "=" * 80)
    print("3. CODE QUALITY SCORES")
    print("=" * 80)
    code_scores = calculate_code_quality_score(code_data)
    
    print("\nCode Quality Scores (CodeQuality = 100 × (DD_norm + Cog_norm + Cyc_norm + Hotspot) / 4):")
    print("\nArchitecture".ljust(25), "DebtDensity".rjust(12), "Score".rjust(10))
    print("-" * 47)
    for arch in sorted(code_scores.items(), key=lambda x: x[1]['score'], reverse=True):
        code = arch[1]
        print(f"{arch[0]:<25} {code['debt_density']:>12.4f} {code['score']:>10.2f}")
    
    if "Hybrid" not in code_scores:
        print(f"\n  {'Hybrid':<25} {'N/A (No static analysis)':>32}")
    
    # Calculate Overall Scores
    print("\n" + "=" * 80)
    print("4. OVERALL SCORES")
    print("=" * 80)
    
    overall_scores = {}
    for arch_name in perf_scores.keys():
        perf = perf_scores[arch_name]['score']
        mem = mem_scores[arch_name]['score']
        code = code_scores.get(arch_name, {}).get('score')
        
        overall = calculate_overall_score(perf, code, mem)
        overall_scores[arch_name] = overall
    
    print("\nOverall Scores (Overall = (Perf+1)^0.40 × (Code+1)^0.35 × (Mem+1)^0.25):")
    print("\nArchitecture".ljust(25), "Perf".rjust(10), "Memory".rjust(10), "Code".rjust(10), "Overall".rjust(10))
    print("-" * 65)
    
    sorted_overall = sorted(overall_scores.items(), key=lambda x: x[1], reverse=True)
    for arch, overall in sorted_overall:
        perf = perf_scores[arch]['score']
        mem = mem_scores[arch]['score']
        code = code_scores.get(arch, {}).get('score')
        code_str = f"{code:.2f}" if code is not None else "N/A"
        
        print(f"{arch:<25} {perf:>10.2f} {mem:>10.2f} {code_str:>10} {overall:>10.2f}")
    
    # Summary
    print("\n" + "=" * 80)
    print("5. SUMMARY - DIMENSION LEADERS")
    print("=" * 80)
    
    perf_winner = max(perf_scores.items(), key=lambda x: x[1]['score'])[0]
    mem_winner = max(mem_scores.items(), key=lambda x: x[1]['score'])[0]
    code_winner = max(code_scores.items(), key=lambda x: x[1]['score'])[0] if code_scores else "N/A"
    overall_winner = sorted_overall[0][0]
    
    print(f"\nPerformance Leader:  {perf_winner}")
    print(f"Memory Leader:       {mem_winner}")
    print(f"Code Quality Leader: {code_winner}")
    print(f"Overall Winner:      {overall_winner}")
    
    print("\n" + "=" * 80)
    print("Analysis complete!")
    print("=" * 80)

if __name__ == "__main__":
    main()

