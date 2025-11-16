#!/usr/bin/env python3
"""
Update dependencies in app/build.gradle.kts
Only updates feature implementation dependencies, keeps API dependencies active
"""

import sys
import re

def update_dependencies(gradle_file, architecture):
    """Update dependencies for given architecture"""
    
    with open(gradle_file, 'r') as f:
        lines = f.readlines()
    
    # Get module names
    if architecture == "singlestatemvvm":
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
    
    # Process lines in feature section
    for i in range(feature_start, feature_end):
        line = lines[i]
        
        # Ensure API dependencies are uncommented
        if 'productApi' in line or 'cartApi' in line or 'chatApi' in line:
            lines[i] = re.sub(r'^\s*//+\s*', '    ', line)
            if not lines[i].startswith('    implementation'):
                lines[i] = '    ' + lines[i].lstrip()
            continue
        
        # Comment out ALL implementation lines first
        if 'Impl' in line and ('product' in line or 'cart' in line or 'chat' in line):
            # Remove any existing comments first
            original_line = re.sub(r'^\s*//+\s*', '    ', line)
            # Comment it out
            if original_line.strip().startswith('implementation'):
                lines[i] = re.sub(r'^(\s*)implementation', r'\1//implementation', original_line)
            else:
                lines[i] = original_line
        
        # Uncomment ONLY the three needed modules (exact match)
        # Use regex to match exact module name
        if re.search(rf'projects\.feature\.{re.escape(product_module)}\b', line):
            lines[i] = re.sub(r'^\s*//+\s*', '    ', lines[i])
            if not lines[i].startswith('    implementation'):
                lines[i] = '    ' + lines[i].lstrip()
        elif re.search(rf'projects\.feature\.{re.escape(cart_module)}\b', line):
            lines[i] = re.sub(r'^\s*//+\s*', '    ', lines[i])
            if not lines[i].startswith('    implementation'):
                lines[i] = '    ' + lines[i].lstrip()
        elif re.search(rf'projects\.feature\.{re.escape(chat_module)}\b', line):
            lines[i] = re.sub(r'^\s*//+\s*', '    ', lines[i])
            if not lines[i].startswith('    implementation'):
                lines[i] = '    ' + lines[i].lstrip()
    
    # Write back
    with open(gradle_file, 'w') as f:
        f.writelines(lines)
    
    # Verify
    content = ''.join(lines)
    product_count = content.count(f'implementation(projects.feature.{product_module})')
    cart_count = content.count(f'implementation(projects.feature.{cart_module})')
    chat_count = content.count(f'implementation(projects.feature.{chat_module})')
    
    if product_count == 1 and cart_count == 1 and chat_count == 1:
        print(f"✓ Updated dependencies for {architecture}")
        return True
    else:
        print(f"✗ Failed to update dependencies correctly")
        print(f"  Product: {product_count}, Cart: {cart_count}, Chat: {chat_count}")
        return False

if __name__ == '__main__':
    if len(sys.argv) != 3:
        print("Usage: update_dependencies.py <gradle_file> <architecture>")
        sys.exit(1)
    
    gradle_file = sys.argv[1]
    architecture = sys.argv[2]
    
    success = update_dependencies(gradle_file, architecture)
    sys.exit(0 if success else 1)

