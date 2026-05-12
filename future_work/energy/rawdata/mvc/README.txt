ANDROID ARCHITECTURE ENERGY BENCHMARK RESULTS
════════════════════════════════════════════════════════════

TEST INFORMATION
────────────────────────────────────────────────────────────
Date: 2025-11-15 13:48:23
Device: SM-A556E (a55x)
Android: 15 (SDK 35)
Architecture: MVC (mvc)

MEASUREMENT METHOD
────────────────────────────────────────────────────────────
Hardware Energy Counter

DEVICE CONDITIONS
────────────────────────────────────────────────────────────
Battery Level: 86%
Temperature: 31,6°C
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
Chat_Streaming: 21,55 mWh
Shopping_Cart: 21,35 mWh
Product_Browsing: 42,34 mWh

Total Energy: 85,25 mWh
Average Power: 1639,49 mW

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