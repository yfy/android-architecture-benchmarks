#!/usr/bin/env python3
"""
Update dependencies in app/build.gradle.kts
Only updates feature implementation dependencies, keeps API dependencies active
"""

import sys
import re
import argparse

def update_dependencies(gradle_file, architecture, hybrid=False):
    """Update dependencies for given architecture"""
    
    with open(gradle_file, 'r') as f:
        lines = f.readlines()
    
    # Get module names
    if hybrid or architecture == "hybrid":
        # Hybrid architecture uses specific implementations
        # Product: Classic MVVM (stress test excellence)
        # Cart: MVP (cart updates champion)
        # Chat: Single-State MVVM (balanced performance + best code quality)
        # Note: Single-State MVVM uses base module name without suffix
        product_module = "productImplClassicmvvm"
        cart_module = "cartImplMvp"
        chat_module = "chatImpl"  # Single-State MVVM is the default/base implementation
    elif architecture == "singlestatemvvm":
        product_module = "productImpl"
        cart_module = "cartImpl"
        chat_module = "chatImpl"
    else:
        # Capitalize first letter
        cap_arch = architecture[0].upper() + architecture[1:]
        product_module = f"productImpl{cap_arch}"
        cart_module = f"cartImpl{cap_arch}"
        chat_module = f"chatImpl{cap_arch}"
    
    # Find feature section
    feature_start = None
    feature_end = None
    
    for i, line in enumerate(lines):
        if '//feature' in line:
            feature_start = i
        elif feature_start is not None and line.strip() == '}' and i > feature_start + 10:
            feature_end = i
            break
    
    if feature_start is None or feature_end is None:
        print(f"Error: Could not find feature section in {gradle_file}")
        return False
    
    # Build regex patterns for the three modules we need
    product_pattern = rf'projects\.feature\.{re.escape(product_module)}\b'
    cart_pattern = rf'projects\.feature\.{re.escape(cart_module)}\b'
    chat_pattern = rf'projects\.feature\.{re.escape(chat_module)}\b'
    
    # Process lines in feature section
    for i in range(feature_start, feature_end):
        line = lines[i]
        original_line = line
        
        # Ensure API dependencies are uncommented
        if 'productApi' in line or 'cartApi' in line or 'chatApi' in line:
            lines[i] = re.sub(r'^\s*//+\s*', '    ', line)
            if not lines[i].startswith('    implementation'):
                lines[i] = '    ' + lines[i].lstrip()
            continue
        
        # Check if this is an implementation line for product/cart/chat
        if 'Impl' in line and ('product' in line or 'cart' in line or 'chat' in line):
            # Check if this is one of the modules we need to keep uncommented
            is_target_module = (
                re.search(product_pattern, line) or
                re.search(cart_pattern, line) or
                re.search(chat_pattern, line)
            )
            
            if is_target_module:
                # Uncomment this line (remove any leading //)
                lines[i] = re.sub(r'^\s*//+\s*', '    ', line)
                if not lines[i].startswith('    implementation'):
                    lines[i] = '    ' + lines[i].lstrip()
            else:
                # Comment out this line (add // before implementation)
                # First, remove any existing comments to get clean line
                uncommented = re.sub(r'^\s*//+\s*', '    ', line)
                if uncommented.strip().startswith('implementation'):
                    # Add // before implementation, preserving indentation
                    lines[i] = re.sub(r'^(\s*)implementation', r'\1//implementation', uncommented)
                else:
                    lines[i] = uncommented
    
    # Write back
    with open(gradle_file, 'w') as f:
        f.writelines(lines)
    
    # Verify - check for exact module paths
    content = ''.join(lines)
    
    # Use exact regex patterns to match full module paths
    # Format: implementation(projects.feature.productImplMvp)
    product_pattern = rf'implementation\(projects\.feature\.{re.escape(product_module)}\)'
    cart_pattern = rf'implementation\(projects\.feature\.{re.escape(cart_module)}\)'
    chat_pattern = rf'implementation\(projects\.feature\.{re.escape(chat_module)}\)'
    
    # Check for uncommented versions (without the // prefix)
    product_uncommented = len([line for line in lines if re.search(product_pattern, line) and not line.strip().startswith('//')])
    cart_uncommented = len([line for line in lines if re.search(cart_pattern, line) and not line.strip().startswith('//')])
    chat_uncommented = len([line for line in lines if re.search(chat_pattern, line) and not line.strip().startswith('//')])
    
    if product_uncommented == 1 and cart_uncommented == 1 and chat_uncommented == 1:
        arch_name = "hybrid" if (hybrid or architecture == "hybrid") else architecture
        print(f"✓ Updated dependencies for {arch_name}")
        print(f"  Product: {product_module}, Cart: {cart_module}, Chat: {chat_module}")
        return True
    else:
        print(f"✗ Failed to update dependencies correctly")
        print(f"  Product: {product_uncommented} (expected 1), Cart: {cart_uncommented} (expected 1), Chat: {chat_uncommented} (expected 1)")
        return False

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Update Gradle dependencies for architecture')
    parser.add_argument('gradle_file', help='Path to build.gradle.kts file')
    parser.add_argument('architecture', help='Architecture name (e.g., classicmvvm, mvp, hybrid)')
    parser.add_argument('--hybrid', action='store_true', help='Enable hybrid architecture mode')
    
    args = parser.parse_args()
    
    success = update_dependencies(args.gradle_file, args.architecture, args.hybrid)
    sys.exit(0 if success else 1)

