# Energy Consumption Data — Future Work

> **Scope note:** This data is NOT part of the PeerJ Computer Science manuscript  
> "An Empirical Evaluation of Android Architecture Patterns Under Compose-Based Workloads."  
> It is preserved here for a planned follow-up study.

## Directory structure

```
energy/
├── rawdata/                          Raw per-architecture energy measurements
│   ├── classicmvvm/
│   │   ├── energy_consumption.csv    Summary: mean mWh per scenario
│   │   ├── energy_consumption.json   Same data in JSON format
│   │   ├── detailed_Product_Browsing.csv   Per-iteration readings
│   │   ├── detailed_Shopping_Cart.csv
│   │   ├── detailed_Chat_Streaming.csv
│   │   └── README.txt               Test metadata (device, date, iterations)
│   ├── singlestatemvvm/  (same structure)
│   ├── mvi/              (same structure)
│   ├── mvp/              (same structure)
│   ├── mvc/              (same structure)
│   └── hybrid/           (same structure)
├── analysis/                         Processed/normalized results
│   ├── normalized_energy_data.csv    Per-architecture normalized energy values
│   └── normalized_energy_statistics.csv  Descriptive stats (mean, SD, CV)
├── energy_analysis_results.json      Full statistical analysis output
└── summary_energy_consumption.csv    Cross-architecture summary table
```

## Measurement protocol

- **Device:** Samsung (details in each architecture's README.txt)
- **Connection:** WiFi ADB (device unplugged for accurate battery-draw measurement)
- **Scenarios:** Product Browsing, Shopping Cart, Chat Streaming
- **Metric:** Battery consumption in mWh, derived from Android battery stats
- **Script:** `scripts/run_benchmarks.sh --energy`

## Preliminary findings (not peer-reviewed)

| Architecture | Energy (mWh) | Relative Rank |
|---|---|---|
| Hybrid | 60.54 | 1 |
| MVP | 60.71 | 2 |
| Classic MVVM | 76.45 | 3 |
| MVI | 79.40 | 4 |
| MVC | 81.84 | 5 |
| Single-State MVVM | 82.31 | 6 |

These values are preliminary. The measurement methodology requires further validation before publication.
