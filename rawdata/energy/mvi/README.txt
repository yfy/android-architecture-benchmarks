ANDROID ARCHITECTURE ENERGY BENCHMARK RESULTS
════════════════════════════════════════════════════════════

TEST INFORMATION
────────────────────────────────────────────────────────────
Date: 2025-11-15 15:39:55
Device: SM-A556E (a55x)
Android: 15 (SDK 35)
Architecture: MVI (mvi)

MEASUREMENT METHOD
────────────────────────────────────────────────────────────
Hardware Energy Counter

DEVICE CONDITIONS
────────────────────────────────────────────────────────────
Battery Level: 70%
Temperature: 30,2°C
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
Chat_Streaming: 41,25 mWh
Shopping_Cart: 20,51 mWh
Product_Browsing: 20,32 mWh

Total Energy: 82,08 mWh
Average Power: 1580,50 mW

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