# Raw Data

This directory contains the unprocessed benchmark outputs that support all manuscript tables.

## Directory structure

```
rawdata/
└── performance/
    ├── classicmvvm_result.json
    ├── singlestatemvvm_result.json
    ├── mvi_result.json
    ├── mvp_result.json
    ├── mvc_result.json
    ├── hybrid_result.json
    └── runs_detail.json
```

## JSON schema — `*_result.json`

Each architecture result file is a single JSON object with four top-level keys:

### `context`
Device and build metadata (Android version, CPU, manufacturer, build type).

### `benchmarks`
Array of benchmark objects. Each element corresponds to one test scenario:

```json
{
  "name": "startupCold",
  "className": "com.yfy.basearchitecture.benchmark.startup.StartupBenchmark",
  "warmupIterations": 3,
  "repeatIterations": 15,
  "metrics": {
    "timeToInitialDisplayMs": {
      "minimum": 506.0,
      "maximum": 551.0,
      "median": 519.91,
      "runs": [...]
    }
  }
}
```

**Benchmark names across 5 pure architectures (15 tests each):**

| Name | Category | Manuscript Table |
|------|----------|-----------------|
| `startupCold` | Startup | Table 1 |
| `startupWarm` | Startup | Table 1 |
| `productListScrollAndPagination` | Frame rendering | Table 2 |
| `productListRapidScrolling` | Frame rendering | Table 2 |
| `productListCategoryFiltering` | Frame rendering | Table 2 |
| `cartQuantityUpdatesWithDynamicSetup` | Frame rendering | Table 2 |
| `cartCheckoutFlow` | Frame rendering | Table 2 |
| `chatListRealtimeUpdates` | Frame rendering | Table 2 |
| `chatDetailMessageStreamAndSending` | Frame rendering | Table 2 |
| `chatRapidSwitching` | Frame rendering | Table 2 |
| `continuousScrollJankTest` | Jank | Table 3 |
| `flingJankTest` | Jank | Table 3 |
| `rapidDirectionChangeJankTest` | Jank | Table 3 |
| `cartQuantityUpdatePerformance` | Interaction | Table 2 |
| `categoryFilterPerformance` | Interaction | Table 2 |

The `hybrid_result.json` contains a 16th benchmark: `memoryUsageCompleteFlow`, which captures the hybrid configuration's memory behaviour as a benchmark run (used in Table 10).

### `memoryBenchmarkResult`
Array of raw memory profiling snapshots for 10 application states:

| State label | Description |
|---|---|
| `01_AppLaunch` | Immediately after cold start |
| `02_ProductList_Initial` | Product list first load |
| `03_ProductList_AfterHeavyScroll` | After scrolling the full product list |
| `04_ProductDetail` | Product detail screen |
| `05_Cart_Initial` | Cart screen open |
| `06_Cart_AfterRapidUpdates` | Cart after 20 quantity updates |
| `07_ChatList` | Chat list screen |
| `08_ChatDetail_Initial` | Chat detail first load |
| `09_ChatDetail_AfterStream` | Chat detail after streaming 50 messages |
| `10_Peak_AfterAllOperations` | Peak memory across full session |

Metrics captured: Total PSS (MB), Private Dirty (MB), Native Heap (MB), Dalvik Heap (MB). Supports Tables 5 and 6.

### `staticCodeAnalysis`
Object containing SonarQube-derived metrics for the architecture's feature modules:

```json
{
  "size_metrics": {
    "lines_of_code": 2676,
    "source_lines": 2459,
    "files": 33,
    "classes": 29,
    "functions": 83
  },
  "complexity_metrics": {
    "total_cyclomatic_complexity": 162,
    "total_cognitive_complexity": 217,
    "avg_cyclomatic_complexity": 1.95,
    "avg_cognitive_complexity": 2.61
  },
  "maintainability_metrics": {
    "technical_debt_hours": 2.9,
    "technical_debt_ratio": 0.2,
    "code_smells": 13
  }
}
```

Supports Tables 7 and 8. Note: the Hybrid configuration deliberately has no `staticCodeAnalysis` entry because it reuses modules from the five pure architectures unchanged.

## `runs_detail.json`

Aggregated per-run statistics across all architectures. Keys follow the pattern `{architecture}_{benchmark}_{metric}` (e.g., `classicmvvm_startupCold_timeToInitialDisplayMs`). Used to derive the descriptive statistics in `analysis_result/performance/benchmarks.csv`.

## Naming convention

Architecture identifiers used throughout:

| Identifier | Architecture |
|---|---|
| `classicmvvm` | Classic MVVM (ViewModel + StateFlow) |
| `singlestatemvvm` | Single-State MVVM (custom state pattern) |
| `mvi` | MVI (Model–View–Intent) |
| `mvp` | MVP (Model–View–Presenter) |
| `mvc` | MVC (Model–View–Controller) |
| `hybrid` | Hybrid (Product: Classic MVVM, Cart: MVP, Chat: Single-State MVVM) |
