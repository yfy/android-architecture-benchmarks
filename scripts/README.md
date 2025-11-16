# Benchmark Automation Scripts

This directory contains automation scripts for running benchmarks across all 5 Android architecture patterns.

## 📋 Scripts Overview

### 1. `run_benchmarks.sh` - Main Automation Script

The main orchestration script that:
- Iterates through all 5 architectures (classicmvvm, singlestatemvvm, mvc, mvp, mvi)
- Updates BuildConfig in both `app/build.gradle.kts` and `benchmark/build.gradle.kts`
- Updates dependencies in `app/build.gradle.kts` to select the correct modules
- Builds the app and benchmark modules
- Runs benchmarks with retry logic (supports both EnergyBenchmark and BenchmarkTestSuite)
- Includes cooldown periods: 2 minutes between retries, 5 minutes between architectures
- Manages state to resume from failures
- Monitors ADB connection (WiFi ADB monitoring for energy mode only)

**Benchmark Modes:**
- **Energy Mode** (`--energy`): Runs only `EnergyBenchmark` tests. Requires WiFi ADB connection (device must be unplugged for accurate power measurement).
- **General Mode** (`--general`, default): Runs `BenchmarkTestSuite` (all benchmarks: Startup, Rendering, Interaction, Memory). Works with USB or WiFi ADB connection.

### 2. `adb_monitor.sh` - ADB Connection Monitor

Background script that:
- Monitors WiFi ADB connection every 30 seconds
- Automatically reconnects if connection drops
- Logs all reconnection attempts
- Runs in background during benchmark execution

### 3. `update_dependencies.py` - Dependency Update Helper

Python script for updating Gradle dependencies:
- Updates feature implementation dependencies in `app/build.gradle.kts`
- Ensures only one architecture's modules are active at a time
- Used internally by the main script

## 🚀 Quick Start

### Prerequisites

1. **ADB installed** (script will auto-detect common locations)
2. **jq installed** (for JSON parsing)
3. **Device connected**:
   - **Energy mode**: WiFi ADB required (device must be unplugged)
   - **General mode**: USB or WiFi ADB (any connection type)
4. **Device IP configured** (for energy mode only, set `DEVICE_IP` environment variable)

**Note**: The script automatically searches for ADB in common locations:
- `~/Library/Android/sdk/platform-tools/adb` (macOS default)
- `/opt/homebrew/bin/adb` (Homebrew)
- Android Studio installation directories

If ADB is not found, the script will provide installation instructions.

**Energy Mode Requirements:**
- Device must be unplugged (power measurement requires battery-only operation)
- WiFi ADB connection must be established
- Device IP must be set via `DEVICE_IP` environment variable or auto-detected

### Basic Usage

```bash
# General benchmark mode (default) - runs BenchmarkTestSuite
./scripts/run_benchmarks.sh

# Energy benchmark mode - runs only EnergyBenchmark (requires WiFi ADB)
./scripts/run_benchmarks.sh --energy

# Dry run (test configuration updates only)
./scripts/run_benchmarks.sh -d

# Test single architecture (general mode)
./scripts/run_benchmarks.sh -a mvi

# Energy benchmark for single architecture
./scripts/run_benchmarks.sh --energy -a mvi

# Clean start (delete all previous data)
./scripts/run_benchmarks.sh -c

# Verbose mode
./scripts/run_benchmarks.sh -v

# Test connection (build and verify device connection, no benchmarks)
./scripts/run_benchmarks.sh -t
```

### Environment Variables

```bash
# Set device IP manually (optional - script will auto-detect if not set)
export DEVICE_IP="172.20.10.4:32999"

# Run with custom device IP
DEVICE_IP="172.20.10.4:32999" ./scripts/run_benchmarks.sh
```

**Note**: The script automatically detects connected devices. If only one device is connected, it will be used automatically. If multiple devices are connected, the first one will be used (or you can specify with `DEVICE_IP`).

## 📊 Architecture Configuration

The script automatically handles architecture selection:

### Module Naming Convention

- **Single-State MVVM**: `product-impl`, `cart-impl`, `chat-impl` (NO suffix)
- **Other Architectures**: `{feature}-impl-{architecture}`
  - Examples: `product-impl-mvi`, `cart-impl-mvp`, `chat-impl-classicmvvm`

### BuildConfig Updates

The script updates BuildConfig in two files:
1. `app/build.gradle.kts` - Sets `CURRENT_ARCHITECTURE` for the app
2. `benchmark/build.gradle.kts` - Sets `CURRENT_ARCHITECTURE` for benchmarks

The `ArchitectureConfig.kt` file automatically reads from BuildConfig, so no manual changes are needed.

### Dependency Updates

For each architecture, the script:
1. Comments out ALL 15 feature implementation dependencies
2. Uncomments ONLY the 3 modules for the current architecture:
   - Product module
   - Cart module  
   - Chat module

**Important**: All three features must use the same architecture pattern simultaneously.

## 📁 State Management

The script maintains state in `benchmark_state.json`:

```json
{
  "current_architecture": "mvi",
  "completed_architectures": ["classicmvvm", "mvc"],
  "last_update": "2025-11-12T23:30:00Z",
  "failed_attempts": {
    "mvp": 2
  }
}
```

### State Features

- **Resume from failure**: If script is interrupted, it resumes from the last incomplete architecture
- **Skip completed**: Already completed architectures are automatically skipped
- **Retry tracking**: Failed attempts are tracked per architecture
- **Auto-cleanup**: State file is deleted when all architectures complete

## 📂 Results Directory

Results are organized by architecture:

```
benchmark_results/
├── classicmvvm/
│   ├── energy_consumption_*.csv
│   ├── energy_consumption_*.json
│   ├── detailed_*.csv
│   └── README_*.txt
├── singlestatemvvm/
│   └── ...
├── mvc/
│   └── ...
├── mvp/
│   └── ...
└── mvi/
    └── ...
```

### Failed Test Cleanup

If a test fails:
- Incomplete output files are automatically deleted
- `detailed_*` files from previous successful iterations are kept
- Only files from the CURRENT failed test run are removed

## 🔌 ADB Connection Management

### Connection Check

The script checks device connection before each test:
- Verifies device is connected via `adb devices`
- Attempts automatic reconnection if connection is lost
- Retries test after reconnection

### Background Monitor

The `adb_monitor.sh` script runs in background:
- Checks connection every 30 seconds
- Automatically reconnects on disconnection
- Logs all connection events
- **Does NOT modify device settings**

### Manual Connection

If you need to manually connect:

```bash
# Connect via WiFi ADB
adb connect 192.168.1.100:5555

# Verify connection
adb devices
```

## 📝 Logging

All operations are logged to `energy_benchmark_automation.log`:

- Timestamped operations
- Architecture switches
- BuildConfig updates
- Test start/end
- Errors with full stack traces
- ADB connection events

## ⚙️ Configuration

### Script Configuration

Edit the configuration variables at the top of `run_benchmarks.sh`:

```bash
# Device Configuration
DEVICE_IP="${DEVICE_IP:-192.168.1.100:5555}"

# Architecture List
ARCHITECTURES=("classicmvvm" "singlestatemvvm" "mvc" "mvp" "mvi")

# Test Configuration
MAX_RETRIES=3
ADB_CHECK_INTERVAL=30
```

### Build Variant

The script always uses `mockRelease` build variant:
- App: `:app:assembleMockRelease`
- Benchmark: `:benchmark:assembleMockRelease`
- Test: `:benchmark:connectedMockReleaseAndroidTest --tests "com.yfy.basearchitecture.benchmark.energy.EnergyBenchmark"`

**Note**: Only EnergyBenchmark tests are executed, not all benchmark tests.

## 🛠️ Troubleshooting

### Device Not Connected

```bash
# Check ADB connection
adb devices

# Reconnect manually
adb disconnect
adb connect 172.20.10.4:32999  # Use your device's IP:PORT

# Or let the script auto-detect
./scripts/run_benchmarks.sh  # Will auto-detect connected device
```

**Auto-Detection**: The script automatically detects connected WiFi ADB devices. If your device is connected but not detected, make sure:
1. Device is connected via WiFi ADB (not USB)
2. `adb devices` shows the device as "device" (not "unauthorized" or "offline")
3. Device IP:PORT format is correct (e.g., `172.20.10.4:32999`)

### Build Failures

- Check that BuildConfig is enabled in both `app/build.gradle.kts` and `benchmark/build.gradle.kts`
- Verify Gradle sync completes successfully
- Check log file for detailed error messages

### Dependency Update Failures

- Verify module names match the naming convention
- Check that all 15 dependencies are in the correct section (lines 120-139)
- Ensure sed command works on your system (macOS vs Linux differences)

### State File Issues

```bash
# Delete state file to start fresh
rm benchmark_state.json

# Or use clean mode
./scripts/run_benchmarks.sh -c
```

## 🔍 Validation

### Pre-flight Checks

The script automatically checks:
- ✅ ADB installed and in PATH
- ✅ jq installed (for JSON parsing)
- ✅ Device connected via WiFi ADB
- ✅ Gradle wrapper exists
- ✅ Required Gradle files exist

### Error Handling

The script handles:
- Connection lost → Auto-reconnect, retry test (2 min cooldown)
- Build failure → Log error, skip architecture, continue
- Benchmark crash → Retry up to MAX_RETRIES (2 min cooldown between retries)
- Architecture transition → 5 minute cooldown between architectures
- Invalid state file → Delete and reinitialize
- BuildConfig update failed → Restore backup, skip architecture

## 📊 Example Output

```
╔════════════════════════════════════════════════════════╗
║   ANDROID ENERGY BENCHMARK AUTOMATION                  ║
╚════════════════════════════════════════════════════════╝

📋 Configuration:
   Device: 192.168.1.100:5555
   Architectures: 5
   Max Retries: 3

🔍 Pre-flight Checks:
   ✅ ADB installed
   ✅ Device connected
   ✅ Gradle wrapper found
   ✅ Project files found

════════════════════════════════════════════════════════
🏗️  Architecture: MVI (1/5)
════════════════════════════════════════════════════════

[23:15:30] Updating BuildConfig in app/build.gradle.kts...
[23:15:31] ✓ Updated BuildConfig in app/build.gradle.kts
[23:15:32] Updating BuildConfig in benchmark/build.gradle.kts...
[23:15:33] ✓ Updated BuildConfig in benchmark/build.gradle.kts
[23:15:34] Updating dependencies in app/build.gradle.kts...
[23:15:35] ✓ Updated dependencies for mvi
[23:15:36] Stopping Gradle daemon...
[23:15:38] Building app (mockRelease)...
[23:16:45] ✓ Build completed successfully
[23:16:46] Building benchmark (mockRelease)...
[23:17:30] ✓ Build completed successfully
[23:17:31] Checking device connection...
[23:17:32] Running benchmark for mvi (attempt 1/3)...
[23:32:45] ✓ Benchmark succeeded for mvi
[23:32:46] ✓ mvi completed successfully!
   Results: benchmark_results/mvi/
```

## 🎯 Best Practices

1. **Device Preparation**: Ensure device is unplugged, WiFi configured, and ready before starting
2. **Network Stability**: Use stable WiFi connection for ADB
3. **Battery Level**: Ensure device has sufficient battery (or keep plugged in)
4. **Clean Builds**: Use `-c` flag for clean start if experiencing issues
5. **Monitor Logs**: Check `energy_benchmark_automation.log` for detailed information
6. **Dry Run First**: Test configuration with `-d` flag before full run

## 📄 License

Part of the Android Architecture Benchmarks research project.

