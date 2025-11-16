#!/bin/bash

################################################################################
# ADB Connection Monitor Script
#
# Monitors WiFi ADB connection and automatically reconnects if connection drops.
# Runs in background and checks connection every 30 seconds.
#
# Usage:
#   ./scripts/adb_monitor.sh <DEVICE_IP>
#
# Example:
#   ./scripts/adb_monitor.sh 192.168.1.100:5555
################################################################################

set -euo pipefail

DEVICE_IP="${1:-}"
CHECK_INTERVAL=15

if [ -z "$DEVICE_IP" ]; then
    echo "Error: Device IP required"
    echo "Usage: $0 <DEVICE_IP>"
    exit 1
fi

LOG_FILE="${LOG_FILE:-energy_benchmark_automation.log}"

# Find and setup ADB
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

# Setup ADB in PATH
adb_path=$(find_adb)
if [ -n "$adb_path" ]; then
    export PATH="$(dirname "$adb_path"):$PATH"
fi

log_message() {
    local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    echo "[$timestamp] [ADB Monitor] $1" | tee -a "$LOG_FILE"
}

check_connection() {
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

reconnect_device() {
    # Ensure ADB is available
    if ! command -v adb &> /dev/null; then
        log_message "ADB not available, cannot reconnect"
        return 1
    fi
    
    log_message "Connection lost! Attempting to reconnect to $DEVICE_IP..."
    
    # Check if device is IP:PORT format
    if echo "$DEVICE_IP" | grep -qE "^[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+:[0-9]+$"; then
        # WiFi ADB - use adb connect
        adb disconnect 2>/dev/null || true
        sleep 2
        
        # Extract IP and port
        local ip=$(echo "$DEVICE_IP" | cut -d: -f1)
        local port=$(echo "$DEVICE_IP" | cut -d: -f2)
        
        # Try to reconnect
        adb connect "$DEVICE_IP" 2>/dev/null
        sleep 5
        
        if check_connection; then
            log_message "Successfully reconnected to $DEVICE_IP"
            return 0
        else
            log_message "Failed to reconnect to $DEVICE_IP"
            return 1
        fi
    else
        # Other format (e.g., adb-XXX._adb-tls-connect._tcp) - try to find and reconnect
        log_message "Device format is not IP:PORT, attempting to detect and reconnect..."
        
        # Try to find the device again
        local detected=$(adb devices 2>/dev/null | awk '/device$/ {print $1}' | grep -v "^$" | head -1)
        
        if [ -n "$detected" ]; then
            # Update DEVICE_IP to detected device
            DEVICE_IP="$detected"
            log_message "Found device: $DEVICE_IP"
            
            if check_connection; then
                log_message "Successfully reconnected to $DEVICE_IP"
                return 0
            fi
        fi
        
        log_message "Failed to reconnect - device not found"
        return 1
    fi
}

log_message "ADB Monitor started for device: $DEVICE_IP"
log_message "Check interval: ${CHECK_INTERVAL} seconds"

# Main monitoring loop
while true; do
    if ! check_connection; then
        reconnect_device
    else
        log_message "Connection OK: $DEVICE_IP"
    fi
    
    sleep "$CHECK_INTERVAL"
done

