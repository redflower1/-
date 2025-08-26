#!/usr/bin/env python3
"""
Simple script to create basic app icons for the Skibidi Toilet Battle game
"""

from PIL import Image, ImageDraw
import os

def create_icon(size, filename):
    """Create a simple icon with toilet and camera elements"""
    # Create image with dark background
    img = Image.new('RGBA', (size, size), (26, 26, 46, 255))
    draw = ImageDraw.Draw(img)
    
    # Draw toilet (brown rectangle with rounded corners)
    toilet_margin = size // 6
    toilet_rect = [toilet_margin, toilet_margin * 2, size - toilet_margin, size - toilet_margin]
    draw.rounded_rectangle(toilet_rect, radius=size//10, fill=(139, 69, 19, 255))
    
    # Draw camera lens (gray circle in top right)
    lens_size = size // 4
    lens_pos = (size - lens_size - size//8, size//8)
    draw.ellipse([lens_pos[0], lens_pos[1], lens_pos[0] + lens_size, lens_pos[1] + lens_size], 
                 fill=(47, 79, 79, 255))
    
    # Inner lens (white)
    inner_size = lens_size // 2
    inner_pos = (lens_pos[0] + lens_size//4, lens_pos[1] + lens_size//4)
    draw.ellipse([inner_pos[0], inner_pos[1], inner_pos[0] + inner_size, inner_pos[1] + inner_size], 
                 fill=(255, 255, 255, 255))
    
    # Save the image
    img.save(filename, 'PNG')
    print(f"Created {filename} ({size}x{size})")

def main():
    # Create directories if they don't exist
    icon_dirs = [
        'app/src/main/res/mipmap-hdpi',
        'app/src/main/res/mipmap-mdpi', 
        'app/src/main/res/mipmap-xhdpi',
        'app/src/main/res/mipmap-xxhdpi',
        'app/src/main/res/mipmap-xxxhdpi'
    ]
    
    for dir_path in icon_dirs:
        os.makedirs(dir_path, exist_ok=True)
    
    # Icon sizes for different densities
    icon_sizes = {
        'app/src/main/res/mipmap-mdpi/ic_launcher.png': 48,
        'app/src/main/res/mipmap-hdpi/ic_launcher.png': 72,
        'app/src/main/res/mipmap-xhdpi/ic_launcher.png': 96,
        'app/src/main/res/mipmap-xxhdpi/ic_launcher.png': 144,
        'app/src/main/res/mipmap-xxxhdpi/ic_launcher.png': 192,
        'app/src/main/res/mipmap-mdpi/ic_launcher_round.png': 48,
        'app/src/main/res/mipmap-hdpi/ic_launcher_round.png': 72,
        'app/src/main/res/mipmap-xhdpi/ic_launcher_round.png': 96,
        'app/src/main/res/mipmap-xxhdpi/ic_launcher_round.png': 144,
        'app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png': 192,
    }
    
    # Create all icon sizes
    for filename, size in icon_sizes.items():
        create_icon(size, filename)
    
    print("All icons created successfully!")

if __name__ == "__main__":
    main()
