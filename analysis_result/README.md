# Analysis Results

This directory contains all processed analysis outputs that directly support the manuscript tables.

## File-to-table mapping

### `performance/benchmarks.csv`
**Supports: Tables 1, 2, 3, 4**

Columns: `architecture`, `test_name`, `className`, `test_type`, `unit`, `better`, `metric_name`, `metric_type`, `count`, `mean`, `sd`, `se`, `cv`, `ci95_low`, `ci95_high`, `min`, `max`, `median`, `range`, `range_pct`, `fps`, `afd_ms`, `total_time_est`, `time_per_action`

- Table 1 — Application startup: rows where `test_type = startup`
- Table 2 — Frame rendering: rows where `test_type = rendering` or `interaction`
- Table 3 — Jank tests: rows where `test_type = jank`
- Table 4 — Performance stability: `cv` column across all test types

Note: architecture names in this file use `classic_mvvm` (underscore) for Classic MVVM; all other names match the canonical identifiers (`singlestatemvvm`, `mvi`, `mvp`, `mvc`). This is a known minor inconsistency in the analysis script output.

### `performance/memory.csv`
**Supports: Table 5 (indicative)** — Memory Usage (Initial PSS, Peak PSS, Final PSS, Growth MB)

Note: this file contains integer-rounded values from a single profiling session. Table 5 in the manuscript reports averages across 3 independent iterations (n=3), yielding decimal values (e.g., Classic MVVM Initial PSS = 30.40 MB vs. 30 here). For MVP the integers coincide with the averages. The authoritative source for Table 5 values is `rawdata/performance/*_result.json` → `memoryBenchmarkResult` array (3 iterations per architecture).

### `performance/memory_phases.csv`
**Supports: Table 6** — Memory Snapshot across 5 stages (AppLaunch, ProductList, Cart, ChatList, Peak)

### `performance/static.csv`
**Supports: Tables 7 and 8**

- Table 7 — Static Code Quality Metrics (SLOC, files, classes, functions, technical debt, code smells)
- Table 8 — Code Complexity Analysis (cyclomatic, cognitive, distributions)

### `performance/statistical_tests.csv`
Friedman test results, Nemenyi post-hoc comparisons, and Cliff's delta effect sizes. Cited in the manuscript's statistical analysis section.

### `comprehensive_analysis.json`
**Supports: Table 9** — Multi-Dimensional Architecture Scores for 5 pure architectures.

Top-level keys: `metadata`, `statistical_analysis`, `rankings`, `scores`, `summary`, `validation`.

### `comprehensive_analysis_6arch.json`
6-architecture scoring analysis (5 pure + Hybrid) used in the Discussion section to contextualise Hybrid's multi-dimensional position. Note: **Table 10 in the manuscript is not a scoring table** — it reports Hybrid's raw performance metrics (startup times, frame counts, jank counts, stability CVs, memory). Those values come directly from `rawdata/performance/hybrid_result.json`, not from this file.

### `summary_5_architectures.csv`
**Supports: Table 9** — Tabular summary of performance, memory, and code quality scores for 5 pure architectures.

| Column | Description |
|---|---|
| `Architecture` | Display name |
| `Performance_Score` | Normalised score 0–100 |
| `Performance_Rank` | Rank among 5 architectures |
| `Memory_Score` | Normalised score 0–100 |
| `Memory_Rank` | Rank among 5 architectures |
| `Code_Quality_Score` | Normalised score 0–100 |
| `Code_Quality_Rank` | Rank among 5 architectures |

### `summary_6_architectures.csv`
6-architecture dimension scores (Performance, Memory, Code Quality) used in the Discussion section. This is **not** the source for Table 10; see the note under `comprehensive_analysis_6arch.json` above. Code quality score for Hybrid is intentionally N/A because Hybrid reuses the five pure-architecture modules unchanged.

## What is not here

Energy analysis outputs have been moved to `future_work/energy/` — they are not referenced in any manuscript table. See `future_work/README.md` for details.
