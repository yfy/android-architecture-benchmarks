#!/bin/bash

################################################################################
# Android Benchmark Automation Script
# 
# Automates benchmark testing across 5 Android architecture patterns:
# - Classic MVVM, Single-State MVVM, MVC, MVP, MVI
#
# Supports two benchmark modes:
# - Energy Mode: Runs EnergyBenchmark (requires WiFi ADB, device unplugged)
# - General Mode: Runs BenchmarkTestSuite (all benchmarks, USB or WiFi ADB)
#
# Usage:
#   ./scripts/run_benchmarks.sh              # General mode (default)
#   ./scripts/run_benchmarks.sh --energy     # Energy mode
#   ./scripts/run_benchmarks.sh -d           # Dry run
#   ./scripts/run_benchmarks.sh -a mvi       # Single architecture
#   ./scripts/run_benchmarks.sh -c           # Clean start
#   ./scripts/run_benchmarks.sh -v           # Verbose mode
################################################################################

set -euo pipefail

# Configuration Variables
DEVICE_IP="${DEVICE_IP:-192.168.1.100:5555}"
ARCHITECTURES=("classicmvvm" "singlestatemvvm" "mvc" "mvp" "mvi")
MAX_RETRIES=3
ADB_CHECK_INTERVAL=30
SCENARIO_COOLDOWN=120
ARCHITECTURE_COOLDOWN=300

# Paths
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
APP_GRADLE="$PROJECT_ROOT/app/build.gradle.kts"
BENCHMARK_GRADLE="$PROJECT_ROOT/benchmark/build.gradle.kts"
RESULTS_DIR="$PROJECT_ROOT/benchmark_results"
STATE_FILE="$PROJECT_ROOT/benchmark_state.json"
LOG_FILE="$PROJECT_ROOT/energy_benchmark_automation.log"
ADB_MONITOR_SCRIPT="$SCRIPT_DIR/adb_monitor.sh"

# Flags
DRY_RUN=false
SINGLE_ARCH=""
CLEAN_MODE=false
VERBOSE=false
TEST_CONNECTION=false
BENCHMARK_MODE="general"  # "general" or "energy"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

################################################################################
# Utility Functions
################################################################################

log_info() {
    local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    echo -e "${BLUE}[$timestamp]${NC} $1" | tee -a "$LOG_FILE"
}

log_success() {
    local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    echo -e "${GREEN}[$timestamp] ✓${NC} $1" | tee -a "$LOG_FILE"
}

log_error() {
    local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    echo -e "${RED}[$timestamp] ✗${NC} $1" | tee -a "$LOG_FILE"
}

log_warning() {
    local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    echo -e "${YELLOW}[$timestamp] ⚠${NC} $1" | tee -a "$LOG_FILE"
}

log_verbose() {
    if [ "$VERBOSE" = true ]; then
        log_info "$1"
    fi
}

print_header() {
    echo ""
    echo "╔════════════════════════════════════════════════════════╗"
    if [ "$BENCHMARK_MODE" = "energy" ]; then
        echo "║   ANDROID ENERGY BENCHMARK AUTOMATION                  ║"
    else
        echo "║   ANDROID BENCHMARK AUTOMATION                         ║"
    fi
    echo "╚════════════════════════════════════════════════════════╝"
    echo ""
}

print_section() {
    echo ""
    echo "════════════════════════════════════════════════════════"
    echo "$1"
    echo "════════════════════════════════════════════════════════"
    echo ""
}

################################################################################
# Pre-flight Checks
################################################################################

find_adb() {
    # Check if adb is already in PATH
    if command -v adb &> /dev/null; then
        echo "adb"
        return 0
    fi
    
    # Common ADB locations on macOS
    local common_paths=(
        "$HOME/Library/Android/sdk/platform-tools/adb"
        "/Users/$USER/Library/Android/sdk/platform-tools/adb"
        "$HOME/Android/Sdk/platform-tools/adb"
        "/opt/homebrew/bin/adb"
        "/usr/local/bin/adb"
        "/usr/bin/adb"
    )
    
    # Check common locations
    for adb_path in "${common_paths[@]}"; do
        if [ -f "$adb_path" ] && [ -x "$adb_path" ]; then
            echo "$adb_path"
            return 0
        fi
    done
    
    # Try to find via Android Studio installation
    local android_studio_paths=(
        "$HOME/Library/Application Support/Google/AndroidStudio*/sdk/platform-tools/adb"
        "/Applications/Android Studio.app/Contents/plugins/android/lib/sdk/platform-tools/adb"
    )
    
    for pattern in "${android_studio_paths[@]}"; do
        for adb_path in $pattern; do
            if [ -f "$adb_path" ] && [ -x "$adb_path" ]; then
                echo "$adb_path"
                return 0
            fi
        done
    done
    
    return 1
}

setup_adb() {
    local adb_path=$(find_adb)
    
    if [ -n "$adb_path" ]; then
        # Add to PATH for this session
        export PATH="$(dirname "$adb_path"):$PATH"
        
        # Verify it works
        if command -v adb &> /dev/null; then
            log_success "ADB configured: $adb_path"
            return 0
        fi
    fi
    
    log_error "ADB not found. Please install Android SDK Platform Tools or add ADB to PATH"
    log_info "Common installation methods:"
    log_info "  - Install via Android Studio: Tools > SDK Manager > SDK Tools > Android SDK Platform-Tools"
    log_info "  - Install via Homebrew: brew install android-platform-tools"
    log_info "  - Add to PATH: export PATH=\"\$HOME/Library/Android/sdk/platform-tools:\$PATH\""
    return 1
}

check_prerequisites() {
    log_info "Running pre-flight checks..."
    
    local errors=0
    
    # Setup ADB (find and add to PATH)
    if ! setup_adb; then
        errors=$((errors + 1))
    else
        log_success "ADB available"
    fi
    
    # Check jq
    if ! command -v jq &> /dev/null; then
        log_error "jq not found in PATH (required for JSON parsing)"
        errors=$((errors + 1))
    else
        log_success "jq installed"
    fi
    
    # For energy mode, require WiFi ADB connection
    # For general mode, any ADB connection (USB or WiFi) is acceptable
    if [ "$BENCHMARK_MODE" = "energy" ]; then
        # Energy mode requires WiFi ADB
        if [ "$DEVICE_IP" = "192.168.1.100:5555" ] || ! check_device_connection; then
            log_info "Attempting to auto-detect WiFi ADB device..."
            if auto_detect_device; then
                log_success "WiFi ADB device detected: $DEVICE_IP"
            else
                log_error "No WiFi ADB device connected"
                log_info "Energy benchmark requires WiFi ADB connection (device must be unplugged)"
                log_info "Please connect a device via WiFi ADB or set DEVICE_IP environment variable"
                log_info "Example: DEVICE_IP=172.20.10.4:32999 ./scripts/run_benchmarks.sh --energy"
                errors=$((errors + 1))
            fi
        else
            log_success "WiFi ADB device connected: $DEVICE_IP"
        fi
    else
        # General mode - check for any ADB connection
        if ! adb devices 2>/dev/null | grep -q "device$"; then
            log_error "No device connected"
            log_info "Please connect a device via USB or WiFi ADB"
            errors=$((errors + 1))
        else
            log_success "Device connected (USB or WiFi ADB)"
        fi
    fi
    
    # Check Gradle wrapper
    if [ ! -f "$PROJECT_ROOT/gradlew" ]; then
        log_error "Gradle wrapper not found"
        errors=$((errors + 1))
    else
        log_success "Gradle wrapper found"
    fi
    
    # Check required files
    if [ ! -f "$APP_GRADLE" ]; then
        log_error "app/build.gradle.kts not found"
        errors=$((errors + 1))
    else
        log_success "app/build.gradle.kts found"
    fi
    
    if [ ! -f "$BENCHMARK_GRADLE" ]; then
        log_error "benchmark/build.gradle.kts not found"
        errors=$((errors + 1))
    else
        log_success "benchmark/build.gradle.kts found"
    fi
    
    if [ $errors -gt 0 ]; then
        log_error "Pre-flight checks failed with $errors error(s)"
        return 1
    fi
    
    log_success "All pre-flight checks passed"
    return 0
}

detect_connected_devices() {
    # Ensure ADB is available
    if ! command -v adb &> /dev/null; then
        return 1
    fi
    
    # Get list of all connected devices (any format)
    local all_devices=$(adb devices 2>/dev/null | awk '/device$/ {print $1}' | grep -v "^$")
    
    if [ -z "$all_devices" ]; then
        return 1
    fi
    
    # Prefer IP:PORT format devices for WiFi ADB
    local ip_port_devices=$(echo "$all_devices" | grep -E "^[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+:[0-9]+$")
    
    # If we have IP:PORT devices, use those; otherwise use any device
    local devices="$ip_port_devices"
    if [ -z "$devices" ]; then
        devices="$all_devices"
    fi
    
    # Count devices (handle both single line and multi-line)
    local device_count=$(echo "$devices" | grep -c . || echo "0")
    
    if [ "$device_count" -eq 0 ]; then
        return 1
    elif [ "$device_count" -eq 1 ]; then
        # Single device found, use it automatically
        echo "$devices" | head -n 1
        return 0
    else
        # Multiple devices, return all for user selection
        echo "$devices"
        return 2
    fi
}

auto_detect_device() {
    local detected=$(detect_connected_devices)
    local status=$?
    
    if [ $status -eq 0 ] && [ -n "$detected" ]; then
        # Single device found
        DEVICE_IP="$detected"
        
        # Check if it's IP:PORT format
        if echo "$DEVICE_IP" | grep -qE "^[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+:[0-9]+$"; then
            log_success "Auto-detected WiFi ADB device: $DEVICE_IP"
        else
            log_success "Auto-detected device: $DEVICE_IP"
            log_warning "Device is not in IP:PORT format. For WiFi ADB, use: adb connect <IP>:<PORT>"
        fi
        return 0
    elif [ $status -eq 2 ] && [ -n "$detected" ]; then
        # Multiple devices found
        log_warning "Multiple devices detected:"
        local device_num=1
        while IFS= read -r device; do
            if [ -n "$device" ]; then
                echo "   $device_num) $device"
                device_num=$((device_num + 1))
            fi
        done <<< "$detected"
        
        # If DEVICE_IP is set and matches one of the devices, use it
        if [ -n "$DEVICE_IP" ] && echo "$detected" | grep -q "^$DEVICE_IP$"; then
            log_info "Using configured device: $DEVICE_IP"
            return 0
        fi
        
        # Otherwise, use the first device
        local first_device=$(echo "$detected" | head -n 1 | tr -d '[:space:]')
        if [ -n "$first_device" ]; then
            DEVICE_IP="$first_device"
            log_info "Using first detected device: $DEVICE_IP"
            log_info "To use a different device, set DEVICE_IP environment variable"
            return 0
        fi
    fi
    
    # No devices found
    return 1
}

check_device_connection() {
    # Ensure ADB is available
    if ! command -v adb &> /dev/null; then
        return 1
    fi
    
    # Check if device is connected (exact match for device ID/IP:PORT)
    # Handle both IP:PORT format and other formats
    if adb devices 2>/dev/null | awk '/device$/ {print $1}' | grep -q "^${DEVICE_IP}$"; then
        return 0
    else
        return 1
    fi
}

################################################################################
# State Management
################################################################################

init_state() {
    if [ "$CLEAN_MODE" = true ]; then
        log_info "Cleaning previous state and results..."
        rm -rf "$RESULTS_DIR"
        rm -f "$STATE_FILE"
        log_success "Clean mode: removed previous state"
    fi
    
    if [ ! -f "$STATE_FILE" ]; then
        log_info "Initializing new state file..."
        cat > "$STATE_FILE" <<EOF
{
  "current_architecture": "",
  "completed_architectures": [],
  "last_update": "$(date -u +%Y-%m-%dT%H:%M:%SZ)",
  "failed_attempts": {}
}
EOF
        log_success "State file initialized"
    fi
}

load_state() {
    if [ -f "$STATE_FILE" ]; then
        local completed=$(jq -r '.completed_architectures[]' "$STATE_FILE" 2>/dev/null || echo "")
        if [ -n "$completed" ]; then
            log_info "Resuming from previous state:"
            echo "   Completed: [$(echo "$completed" | tr '\n' ' ' | sed 's/ $//')]"
        fi
    fi
}

save_state() {
    local arch=$1
    local completed=$2
    local failed_attempts=$3
    
    local temp_file=$(mktemp)
    jq --arg arch "$arch" \
       --argjson completed "$completed" \
       --argjson failed "$failed_attempts" \
       --arg timestamp "$(date -u +%Y-%m-%dT%H:%M:%SZ)" \
       '.current_architecture = $arch |
        .completed_architectures = $completed |
        .failed_attempts = $failed |
        .last_update = $timestamp' \
       "$STATE_FILE" > "$temp_file"
    mv "$temp_file" "$STATE_FILE"
}

is_architecture_completed() {
    local arch=$1
    if [ -f "$STATE_FILE" ]; then
        jq -e --arg arch "$arch" '.completed_architectures[] | select(. == $arch)' "$STATE_FILE" > /dev/null 2>&1
    else
        return 1
    fi
}

get_failed_attempts() {
    local arch=$1
    if [ -f "$STATE_FILE" ]; then
        jq -r --arg arch "$arch" '.failed_attempts[$arch] // 0' "$STATE_FILE"
    else
        echo "0"
    fi
}

increment_failed_attempts() {
    local arch=$1
    local current=$(get_failed_attempts "$arch")
    local new_count=$((current + 1))
    
    local temp_file=$(mktemp)
    jq --arg arch "$arch" --argjson count "$new_count" \
       '.failed_attempts[$arch] = $count' \
       "$STATE_FILE" > "$temp_file"
    mv "$temp_file" "$STATE_FILE"
}

mark_architecture_completed() {
    local arch=$1
    local temp_file=$(mktemp)
    jq --arg arch "$arch" \
       '.completed_architectures += [$arch] |
        .failed_attempts[$arch] = 0' \
       "$STATE_FILE" > "$temp_file"
    mv "$temp_file" "$STATE_FILE"
}

################################################################################
# Gradle File Updates
################################################################################

update_buildconfig() {
    local file=$1
    local architecture=$2
    
    log_verbose "Updating BuildConfig in $file to: $architecture"
    
    # Create backup
    cp "$file" "${file}.bak"
    
    # Update BuildConfig field - use simpler pattern matching
    # Pattern: buildConfigField("String", "CURRENT_ARCHITECTURE", "..."")
    # Replace with: buildConfigField("String", "CURRENT_ARCHITECTURE", "\"$architecture\"")
    if [[ "$OSTYPE" == "darwin"* ]]; then
        # macOS sed - use pipe delimiter and escape properly
        sed -i '' "s|buildConfigField(\"String\", \"CURRENT_ARCHITECTURE\", \".*\")|buildConfigField(\"String\", \"CURRENT_ARCHITECTURE\", \"\\\\\"$architecture\\\\\"\")|g" "$file"
    else
        # Linux sed
        sed -i "s|buildConfigField(\"String\", \"CURRENT_ARCHITECTURE\", \".*\")|buildConfigField(\"String\", \"CURRENT_ARCHITECTURE\", \"\\\\\"$architecture\\\\\"\")|g" "$file"
    fi
    
    # Verify change - check if the architecture value appears in the file
    # Use simpler pattern that matches the architecture name regardless of quotes
    if grep -q "CURRENT_ARCHITECTURE.*$architecture" "$file"; then
        rm -f "${file}.bak"
        log_success "Updated BuildConfig in $(basename "$file") to: $architecture"
        return 0
    else
        log_error "Failed to update BuildConfig in $(basename "$file")"
        log_verbose "Current content: $(grep CURRENT_ARCHITECTURE "$file" || echo 'not found')"
        mv "${file}.bak" "$file"
        return 1
    fi
}

get_module_name() {
    local feature=$1
    local architecture=$2
    
    if [ "$architecture" = "singlestatemvvm" ]; then
        echo "${feature}Impl"
    else
        # Capitalize first letter of architecture using awk (works on both macOS and Linux)
        local cap_arch=$(echo "$architecture" | awk '{print toupper(substr($0,1,1)) substr($0,2)}')
        echo "${feature}Impl${cap_arch}"
    fi
}

update_dependencies() {
    local architecture=$1
    
    log_verbose "Updating dependencies for architecture: $architecture"
    
    # Create backup
    cp "$APP_GRADLE" "${APP_GRADLE}.bak"
    
    # Use Python script for more reliable dependency updates
    local update_script="$SCRIPT_DIR/update_dependencies.py"
    
    if [ ! -f "$update_script" ]; then
        log_error "Python update script not found: $update_script"
        mv "${APP_GRADLE}.bak" "$APP_GRADLE"
        return 1
    fi
    
    if ! python3 "$update_script" "$APP_GRADLE" "$architecture" >> "$LOG_FILE" 2>&1; then
        log_error "Failed to update dependencies using Python script"
        mv "${APP_GRADLE}.bak" "$APP_GRADLE"
        return 1
    fi
    
    # Verify changes
    local product_module=$(get_module_name "product" "$architecture")
    local cart_module=$(get_module_name "cart" "$architecture")
    local chat_module=$(get_module_name "chat" "$architecture")
    
    local product_count=$(grep -c "implementation(projects.feature.${product_module})" "$APP_GRADLE" 2>/dev/null || echo "0")
    local cart_count=$(grep -c "implementation(projects.feature.${cart_module})" "$APP_GRADLE" 2>/dev/null || echo "0")
    local chat_count=$(grep -c "implementation(projects.feature.${chat_module})" "$APP_GRADLE" 2>/dev/null || echo "0")
    
    # Ensure counts are numeric
    product_count=${product_count:-0}
    cart_count=${cart_count:-0}
    chat_count=${chat_count:-0}
    
    if [ "$product_count" = "1" ] && [ "$cart_count" = "1" ] && [ "$chat_count" = "1" ]; then
        # Also verify API dependencies are present
        local api_count=$(grep -c "implementation(projects.feature.productApi)" "$APP_GRADLE" 2>/dev/null || echo "0")
        if [ "$api_count" = "1" ]; then
            rm -f "${APP_GRADLE}.bak"
            log_success "Updated dependencies for $architecture"
            return 0
        else
            log_error "API dependencies missing after update"
            mv "${APP_GRADLE}.bak" "$APP_GRADLE"
            return 1
        fi
    else
        log_error "Failed to update dependencies correctly"
        log_verbose "Product: $product_count, Cart: $cart_count, Chat: $chat_count"
        mv "${APP_GRADLE}.bak" "$APP_GRADLE"
        return 1
    fi
}

################################################################################
# Build and Test Execution
################################################################################

run_gradle_sync() {
    log_info "Stopping Gradle daemon..."
    cd "$PROJECT_ROOT"
    ./gradlew --stop || true
    
    if [ "$DRY_RUN" = true ]; then
        log_info "[DRY RUN] Would run: ./gradlew :app:assembleMockRelease"
        log_info "[DRY RUN] Would run: ./gradlew :benchmark:assembleMockRelease"
        return 0
    fi
    
    log_info "Building app (mockRelease)..."
    if ! ./gradlew :app:assembleMockRelease >> "$LOG_FILE" 2>&1; then
        log_error "Failed to build app"
        return 1
    fi
    
    log_info "Building benchmark (mockRelease)..."
    if ! ./gradlew :benchmark:assembleMockRelease >> "$LOG_FILE" 2>&1; then
        log_error "Failed to build benchmark"
        return 1
    fi
    
    log_success "Build completed successfully"
    return 0
}

run_benchmark_with_retry() {
    local arch=$1
    local attempts=0
    
    while [ $attempts -lt $MAX_RETRIES ]; do
        log_info "Running benchmark for $arch (attempt $((attempts + 1))/$MAX_RETRIES)"
        
        # Check connection before each attempt (only for energy mode)
        if [ "$BENCHMARK_MODE" = "energy" ] && ! check_device_connection; then
            log_warning "Device connection lost, attempting to reconnect..."
            if command -v adb &> /dev/null; then
                # Check if device is IP:PORT format
                if echo "$DEVICE_IP" | grep -qE "^[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+:[0-9]+$"; then
                    # WiFi ADB - use adb connect
                    adb disconnect 2>/dev/null || true
                    sleep 2
                    adb connect "$DEVICE_IP" 2>/dev/null
                    sleep 5
                else
                    # Other format (e.g., adb-XXX._adb-tls-connect._tcp) - try to detect device
                    log_info "Device format is not IP:PORT, attempting to detect device..."
                    local detected=$(adb devices 2>/dev/null | awk '/device$/ {print $1}' | grep -v "^$" | head -1)
                    if [ -n "$detected" ]; then
                        DEVICE_IP="$detected"
                        log_info "Found device: $DEVICE_IP"
                    else
                        log_error "No device found"
                        increment_failed_attempts "$arch"
                        attempts=$((attempts + 1))
                        if [ $attempts -lt $MAX_RETRIES ]; then
                            log_info "Waiting ${SCENARIO_COOLDOWN} seconds before retry..."
                            sleep $SCENARIO_COOLDOWN
                        fi
                        continue
                    fi
                fi
            else
                log_error "ADB not available"
                increment_failed_attempts "$arch"
                attempts=$((attempts + 1))
                if [ $attempts -lt $MAX_RETRIES ]; then
                    log_info "Waiting ${SCENARIO_COOLDOWN} seconds before retry..."
                    sleep $SCENARIO_COOLDOWN
                fi
                continue
            fi
            
            if ! check_device_connection; then
                log_error "Failed to reconnect to device"
                increment_failed_attempts "$arch"
                attempts=$((attempts + 1))
                if [ $attempts -lt $MAX_RETRIES ]; then
                    log_info "Waiting ${SCENARIO_COOLDOWN} seconds before retry..."
                    sleep $SCENARIO_COOLDOWN
                fi
                continue
            fi
            log_success "Reconnected to device"
        fi
        
        local start_time=$(date +%s)
        local benchmark_output=$(mktemp)
        local benchmark_pid
        local benchmark_class
        
        # Determine benchmark class based on mode
        if [ "$BENCHMARK_MODE" = "energy" ]; then
            benchmark_class="com.yfy.basearchitecture.benchmark.energy.EnergyBenchmark"
            log_info "Executing EnergyBenchmark tests (WiFi ADB required)..."
        else
            benchmark_class="com.yfy.basearchitecture.benchmark.BenchmarkTestSuite"
            log_info "Executing BenchmarkTestSuite (all benchmarks)..."
        fi
        
        if [ "$DRY_RUN" = true ]; then
            log_info "[DRY RUN] Would run: ./gradlew :benchmark:connectedMockReleaseAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=$benchmark_class"
            return 0
        fi
        
        log_info "Command: ./gradlew :benchmark:connectedMockReleaseAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=$benchmark_class"
        
        # Start benchmark in background
        (
            ./gradlew :benchmark:connectedMockReleaseAndroidTest -Pandroid.testInstrumentationRunnerArguments.class="$benchmark_class" > "$benchmark_output" 2>&1
            echo $? > "${benchmark_output}.exit"
        ) &
        benchmark_pid=$!
        
        # Monitor connection while benchmark runs (only for energy mode)
        if [ "$BENCHMARK_MODE" = "energy" ]; then
            local connection_check_interval=30
            local last_check=$(date +%s)
            
            while kill -0 "$benchmark_pid" 2>/dev/null; do
                local current_time=$(date +%s)
                local elapsed=$((current_time - last_check))
                
                # Check connection every 30 seconds
                if [ $elapsed -ge $connection_check_interval ]; then
                    if ! check_device_connection; then
                        log_warning "Connection lost during benchmark, attempting to reconnect..."
                        if command -v adb &> /dev/null; then
                            # Check if device is IP:PORT format
                            if echo "$DEVICE_IP" | grep -qE "^[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+:[0-9]+$"; then
                                # WiFi ADB - use adb connect
                                adb disconnect 2>/dev/null || true
                                sleep 2
                                adb connect "$DEVICE_IP" 2>/dev/null
                                sleep 5
                            else
                                # Other format - try to detect device
                                local detected=$(adb devices 2>/dev/null | awk '/device$/ {print $1}' | grep -v "^$" | head -1)
                                if [ -n "$detected" ]; then
                                    DEVICE_IP="$detected"
                                    log_info "Found device: $DEVICE_IP"
                                fi
                            fi
                        fi
                    fi
                    last_check=$current_time
                fi
                
                sleep 5
            done
        fi
        
        # Wait for benchmark to complete and get exit code
        wait "$benchmark_pid" 2>/dev/null || true
        local exit_code=$(cat "${benchmark_output}.exit" 2>/dev/null || echo "1")
        rm -f "${benchmark_output}.exit"
        
        # Always append output to log
        cat "$benchmark_output" >> "$LOG_FILE"
        
        if [ "$exit_code" = "0" ]; then
            # Success
            rm -f "$benchmark_output"
            
            local end_time=$(date +%s)
            local duration=$((end_time - start_time))
            local hours=$((duration / 3600))
            local minutes=$(((duration % 3600) / 60))
            local seconds=$((duration % 60))
            
            log_success "Benchmark succeeded for $arch"
            log_info "Test duration: ${hours}h ${minutes}m ${seconds}s"
            return 0
        else
            # Failure - show error details
            log_error "Benchmark failed for $arch"
            log_info "Error details:"
            
            # Extract and show key error messages
            local error_summary=$(grep -i "error\|exception\|failed\|failure" "$benchmark_output" | head -10)
            if [ -n "$error_summary" ]; then
                echo "$error_summary" | while IFS= read -r line; do
                    log_error "  $line"
                done
            else
                # Show last 20 lines if no specific errors found
                log_info "Last 20 lines of output:"
                tail -20 "$benchmark_output" | while IFS= read -r line; do
                    log_info "  $line"
                done
            fi
            
            rm -f "$benchmark_output"
            increment_failed_attempts "$arch"
            cleanup_failed_results "$arch"
            attempts=$((attempts + 1))
            
            if [ $attempts -lt $MAX_RETRIES ]; then
                log_info "Waiting ${SCENARIO_COOLDOWN} seconds before retry..."
                sleep $SCENARIO_COOLDOWN
            fi
        fi
    done
    
    log_error "Max retries ($MAX_RETRIES) exceeded for $arch"
    return 1
}

################################################################################
# Result Management
################################################################################

cleanup_failed_results() {
    local arch=$1
    local arch_dir="$RESULTS_DIR/$arch"
    
    if [ -d "$arch_dir" ]; then
        log_info "Cleaning up failed results for $arch..."
        # Find and delete files from current test run (energy_consumption_*.csv/json)
        find "$arch_dir" -name "energy_consumption_*.csv" -type f -delete 2>/dev/null || true
        find "$arch_dir" -name "energy_consumption_*.json" -type f -delete 2>/dev/null || true
        find "$arch_dir" -name "README_*.txt" -type f -delete 2>/dev/null || true
        log_verbose "Cleaned up failed result files for $arch"
    fi
}

ensure_results_directory() {
    local arch=$1
    local arch_dir="$RESULTS_DIR/$arch"
    mkdir -p "$arch_dir"
}

################################################################################
# ADB Monitor Management
################################################################################

start_adb_monitor() {
    # Only start ADB monitor for energy mode (WiFi ADB requires monitoring)
    if [ "$BENCHMARK_MODE" != "energy" ]; then
        return 0
    fi
    
    if [ "$DRY_RUN" = true ]; then
        log_info "[DRY RUN] Would start ADB monitor"
        return 0
    fi
    
    if [ ! -f "$ADB_MONITOR_SCRIPT" ]; then
        log_warning "ADB monitor script not found, skipping..."
        return 0
    fi
    
    chmod +x "$ADB_MONITOR_SCRIPT"
    
    # Check if already running
    if pgrep -f "adb_monitor.sh" > /dev/null; then
        log_info "ADB monitor already running"
    else
        log_info "Starting ADB monitor in background..."
        "$ADB_MONITOR_SCRIPT" "$DEVICE_IP" >> "$LOG_FILE" 2>&1 &
        ADB_MONITOR_PID=$!
        log_success "ADB monitor started (PID: $ADB_MONITOR_PID)"
    fi
}

stop_adb_monitor() {
    if pgrep -f "adb_monitor.sh" > /dev/null; then
        log_info "Stopping ADB monitor..."
        pkill -f "adb_monitor.sh" || true
        log_success "ADB monitor stopped"
    fi
}

################################################################################
# Main Execution
################################################################################

process_architecture() {
    local arch=$1
    local arch_index=$2
    local total=$3
    
    print_section "🏗️  Architecture: $(echo "$arch" | tr '[:lower:]' '[:upper:]') ($((arch_index + 1))/$total)"
    
    # Check if already completed (skip in test connection mode to allow retesting)
    if [ "$TEST_CONNECTION" != true ] && is_architecture_completed "$arch"; then
        log_info "Architecture $arch already completed, skipping..."
        return 0
    fi
    
    # Check device connection before starting (only for energy mode)
    if [ "$BENCHMARK_MODE" = "energy" ]; then
        if ! check_device_connection; then
            log_warning "Device connection lost, attempting to reconnect before starting benchmark..."
            if command -v adb &> /dev/null; then
                # Check if device is IP:PORT format
                if echo "$DEVICE_IP" | grep -qE "^[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+:[0-9]+$"; then
                    # WiFi ADB - use adb connect
                    adb disconnect 2>/dev/null || true
                    sleep 2
                    adb connect "$DEVICE_IP" 2>/dev/null
                    sleep 5
                else
                    # Other format - try to detect device
                    log_info "Device format is not IP:PORT, attempting to detect device..."
                    local detected=$(adb devices 2>/dev/null | awk '/device$/ {print $1}' | grep -v "^$" | head -1)
                    if [ -n "$detected" ]; then
                        DEVICE_IP="$detected"
                        log_info "Found device: $DEVICE_IP"
                    else
                        log_error "No device found. Please check device connection."
                        return 1
                    fi
                fi
                
                # Verify connection after reconnect attempt
                if ! check_device_connection; then
                    log_error "Failed to establish device connection. Skipping architecture."
                    return 1
                fi
                log_success "Device connection verified"
            else
                log_error "ADB not available. Skipping architecture."
                return 1
            fi
        fi
    fi
    
    # Update BuildConfig
    log_info "Updating BuildConfig in app/build.gradle.kts..."
    if ! update_buildconfig "$APP_GRADLE" "$arch"; then
        log_error "Failed to update app BuildConfig, skipping architecture"
        return 1
    fi
    
    log_info "Updating BuildConfig in benchmark/build.gradle.kts..."
    if ! update_buildconfig "$BENCHMARK_GRADLE" "$arch"; then
        log_error "Failed to update benchmark BuildConfig, skipping architecture"
        return 1
    fi
    
    # Update dependencies
    log_info "Updating dependencies in app/build.gradle.kts..."
    if ! update_dependencies "$arch"; then
        log_error "Failed to update dependencies, skipping architecture"
        return 1
    fi
    
    # Build
    if ! run_gradle_sync; then
        log_error "Build failed, skipping architecture"
        return 1
    fi
    
    # Ensure results directory exists
    ensure_results_directory "$arch"
    
    # If test connection mode, skip benchmark and just verify connection
    if [ "$TEST_CONNECTION" = true ]; then
        log_info "Test connection mode: Skipping benchmark execution"
        log_info "Verifying device connection after build..."
        
        # Wait a bit to simulate benchmark start
        sleep 2
        
        # Check connection (for general mode, check any device; for energy mode, check specific device)
        if [ "$BENCHMARK_MODE" = "energy" ]; then
            if check_device_connection; then
                log_success "Device connection verified - connection is stable"
                mark_architecture_completed "$arch"
                local completed=$(jq -c '.completed_architectures' "$STATE_FILE")
                local failed=$(jq -c '.failed_attempts' "$STATE_FILE")
                save_state "$arch" "$completed" "$failed"
                log_success "$arch connection test passed!"
                return 0
            else
                log_error "Device connection lost after build"
                return 1
            fi
        else
            # General mode - just check if any device is connected
            if adb devices 2>/dev/null | grep -q "device$"; then
                log_success "Device connection verified - connection is stable"
                mark_architecture_completed "$arch"
                local completed=$(jq -c '.completed_architectures' "$STATE_FILE")
                local failed=$(jq -c '.failed_attempts' "$STATE_FILE")
                save_state "$arch" "$completed" "$failed"
                log_success "$arch connection test passed!"
                return 0
            else
                log_error "Device connection lost after build"
                return 1
            fi
        fi
    fi
    
    # Run benchmark
    if run_benchmark_with_retry "$arch"; then
        mark_architecture_completed "$arch"
        local completed=$(jq -c '.completed_architectures' "$STATE_FILE")
        local failed=$(jq -c '.failed_attempts' "$STATE_FILE")
        save_state "$arch" "$completed" "$failed"
        log_success "$arch completed successfully!"
        log_info "Results: $RESULTS_DIR/$arch/"
        
        # Verify device connection after benchmark completion (only for energy mode)
        if [ "$BENCHMARK_MODE" = "energy" ]; then
            if ! check_device_connection; then
                log_warning "Device connection lost after benchmark completion. Attempting to reconnect..."
                if command -v adb &> /dev/null; then
                    if echo "$DEVICE_IP" | grep -qE "^[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+:[0-9]+$"; then
                        adb disconnect 2>/dev/null || true
                        sleep 2
                        adb connect "$DEVICE_IP" 2>/dev/null
                        sleep 5
                    else
                        local detected=$(adb devices 2>/dev/null | awk '/device$/ {print $1}' | grep -v "^$" | head -1)
                        if [ -n "$detected" ]; then
                            DEVICE_IP="$detected"
                            log_info "Found device: $DEVICE_IP"
                        fi
                    fi
                fi
            fi
        fi
        
        return 0
    else
        log_error "$arch failed after $MAX_RETRIES attempts"
        return 1
    fi
}

print_summary() {
    local total=${#ARCHITECTURES[@]}
    local completed=0
    local failed=0
    
    if [ -f "$STATE_FILE" ]; then
        completed=$(jq '.completed_architectures | length' "$STATE_FILE")
        failed=$((total - completed))
    fi
    
    echo ""
    print_section "🎉 BENCHMARK EXECUTION COMPLETE"
    echo "📊 Summary:"
    echo "   Total Architectures: $total"
    echo "   Successful: $completed"
    echo "   Failed: $failed"
    echo ""
    echo "📂 Results Directory: $RESULTS_DIR"
    echo "💾 Log File: $LOG_FILE"
    echo ""
}

cleanup_on_exit() {
    log_info "Cleaning up..."
    stop_adb_monitor
    
    # Delete state file if all architectures completed
    if [ -f "$STATE_FILE" ]; then
        local total=${#ARCHITECTURES[@]}
        local completed=$(jq '.completed_architectures | length' "$STATE_FILE" 2>/dev/null || echo "0")
        if [ "$completed" -eq "$total" ]; then
            log_info "All architectures completed, removing state file"
            rm -f "$STATE_FILE"
        fi
    fi
}

################################################################################
# Argument Parsing
################################################################################

parse_arguments() {
    while [[ $# -gt 0 ]]; do
        case $1 in
            -d|--dry-run)
                DRY_RUN=true
                shift
                ;;
            -a|--architecture)
                SINGLE_ARCH="$2"
                shift 2
                ;;
            -c|--clean)
                CLEAN_MODE=true
                shift
                ;;
            -v|--verbose)
                VERBOSE=true
                shift
                ;;
            -t|--test-connection)
                TEST_CONNECTION=true
                shift
                ;;
            -e|--energy)
                BENCHMARK_MODE="energy"
                shift
                ;;
            -g|--general)
                BENCHMARK_MODE="general"
                shift
                ;;
            -h|--help)
                echo "Usage: $0 [OPTIONS]"
                echo ""
                echo "Options:"
                echo "  -d, --dry-run          Test configuration updates without running benchmarks"
                echo "  -a, --architecture     Test only specified architecture (e.g., -a mvi)"
                echo "  -c, --clean            Delete all previous results and state"
                echo "  -v, --verbose          Enable verbose logging"
                echo "  -t, --test-connection  Quick test: build and verify device connection (no benchmarks)"
                echo "  -e, --energy           Run EnergyBenchmark (requires WiFi ADB, device unplugged)"
                echo "  -g, --general          Run BenchmarkTestSuite (all benchmarks, USB or WiFi ADB)"
                echo "  -h, --help             Show this help message"
                echo ""
                echo "Benchmark Modes:"
                echo "  --energy (default: --general)  Runs only EnergyBenchmark tests"
                echo "                                  Requires WiFi ADB connection (device must be unplugged)"
                echo "  --general                      Runs BenchmarkTestSuite (all benchmarks)"
                echo "                                  Works with USB or WiFi ADB connection"
                echo ""
                echo "Environment Variables:"
                echo "  DEVICE_IP              Device IP address for WiFi ADB (energy mode only)"
                exit 0
                ;;
            *)
                log_error "Unknown option: $1"
                echo "Use -h or --help for usage information"
                exit 1
                ;;
        esac
    done
}

################################################################################
# Main
################################################################################

main() {
    # Setup trap for cleanup
    trap cleanup_on_exit EXIT INT TERM
    
    # Parse arguments
    parse_arguments "$@"
    
    # Initialize logging
    mkdir -p "$(dirname "$LOG_FILE")"
    if [ "$BENCHMARK_MODE" = "energy" ]; then
        echo "=== Energy Benchmark Automation Started ===" >> "$LOG_FILE"
    else
        echo "=== Benchmark Automation Started ===" >> "$LOG_FILE"
    fi
    
    print_header
    
    log_info "Configuration:"
    echo "   Benchmark Mode: $BENCHMARK_MODE"
    if [ "$BENCHMARK_MODE" = "energy" ]; then
        echo "   Device: ${DEVICE_IP:-<auto-detect>} (WiFi ADB required)"
    else
        echo "   Device: USB or WiFi ADB (auto-detected)"
    fi
    echo "   Architectures: ${#ARCHITECTURES[@]}"
    echo "   Max Retries: $MAX_RETRIES"
    echo "   Scenario Cooldown: ${SCENARIO_COOLDOWN}s"
    echo "   Architecture Cooldown: ${ARCHITECTURE_COOLDOWN}s"
    if [ "$DRY_RUN" = true ]; then
        echo "   Mode: DRY RUN"
    fi
    if [ -n "$SINGLE_ARCH" ]; then
        echo "   Single Architecture: $SINGLE_ARCH"
    fi
    echo ""
    
    # Pre-flight checks
    print_section "🔍 Pre-flight Checks"
    if ! check_prerequisites; then
        log_error "Pre-flight checks failed. Please fix errors and try again."
        exit 1
    fi
    
    # Initialize state
    init_state
    load_state
    
    # Filter architectures if single architecture mode
    local architectures_to_process=("${ARCHITECTURES[@]}")
    if [ -n "$SINGLE_ARCH" ]; then
        if [[ " ${ARCHITECTURES[*]} " =~ " ${SINGLE_ARCH} " ]]; then
            architectures_to_process=("$SINGLE_ARCH")
            log_info "Single architecture mode: $SINGLE_ARCH"
        else
            log_error "Invalid architecture: $SINGLE_ARCH"
            log_info "Valid architectures: ${ARCHITECTURES[*]}"
            exit 1
        fi
    fi
    
    # Start ADB monitor
    start_adb_monitor
    
    # Process each architecture
    local total=${#architectures_to_process[@]}
    local index=0
    
    if [ "$TEST_CONNECTION" = true ]; then
        echo ""
        print_section "🔍 CONNECTION TEST MODE"
        log_info "This mode will:"
        log_info "  1. Build each architecture"
        log_info "  2. Verify device connection"
        log_info "  3. Skip benchmark execution"
        echo ""
    else
        if [ "$BENCHMARK_MODE" = "energy" ]; then
            echo ""
            print_section "⚡ ENERGY BENCHMARK MODE"
            log_info "Running EnergyBenchmark tests"
            log_info "WiFi ADB connection required (device must be unplugged)"
            echo ""
        else
            echo ""
            print_section "📊 GENERAL BENCHMARK MODE"
            log_info "Running BenchmarkTestSuite (all benchmarks)"
            log_info "USB or WiFi ADB connection supported"
            echo ""
        fi
    fi
    
    for arch in "${architectures_to_process[@]}"; do
        process_architecture "$arch" "$index" "$total"
        index=$((index + 1))
        
        # Add cooldown between architectures (except after last one)
        if [ $index -lt $total ]; then
            log_info "Waiting ${ARCHITECTURE_COOLDOWN} seconds before next architecture..."
            sleep $ARCHITECTURE_COOLDOWN
        fi
    done
    
    # Print summary
    print_summary
}

# Run main function
main "$@"

