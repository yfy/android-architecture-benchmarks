ANDROID ARCHITECTURE ENERGY BENCHMARK RESULTS
════════════════════════════════════════════════════════════

TEST INFORMATION
────────────────────────────────────────────────────────────
Date: 2025-11-15 14:44:02
Device: SM-A556E (a55x)
Android: 15 (SDK 35)
Architecture: MVP (mvp)

MEASUREMENT METHOD
────────────────────────────────────────────────────────────
Hardware Energy Counter

DEVICE CONDITIONS
────────────────────────────────────────────────────────────
Battery Level: 78%
Temperature: 31,5°C
Screen Brightness: 144
Airplane Mode: Enabled

TEST SCENARIOS
────────────────────────────────────────────────────────────
1. Product Browsing: 60s continuous scroll + category filtering
2. Shopping Cart: 120s rapid quantity updates (15 items)
3. Chat Streaming: 60s message streaming observation

Each scenario: 15 iterations, 10s stabilization

RESULTS SUMMARY
────────────────────────────────────────────────────────────
Chat_Streaming: 21,14 mWh
Shopping_Cart: 20,93 mWh
Product_Browsing: 20,82 mWh

Total Energy: 62,88 mWh
Average Power: 1206,51 mW

DATA FILES
────────────────────────────────────────────────────────────
- energy_consumption_*.csv   Summary results
- detailed_*_*.csv           Per-iteration data
- energy_consumption_*.json  Machine-readable format
- README_*.txt               This file

NOTES
────────────────────────────────────────────────────────────
- Device unplugged during all measurements
- Results represent median of 15 iterations
- Charge counter resolution: ~1 mAh (Samsung limitation)
- For statistical analysis, use detailed CSV files

════════════════════════════════════════════════════════════