package com.proImg.image_editor.state;

import java.awt.image.BufferedImage;

public class CropEffect implements ImageEffectState {

    private final String direction;
    private final int value;

    public CropEffect(String direction, int value) {
        this.direction = direction.toLowerCase();
        this.value = value;
    }

    @Override
    public BufferedImage applyEffect(BufferedImage image) {
        System.out.println("Applying Crop effect...");
        int width = image.getWidth();
        int height = image.getHeight();
        int x = 0, y = 0, newWidth = width, newHeight = height;

        switch (direction) {
            case "top":
                y = value;
                newHeight -= value;
                break;
            case "left":
                x = value;
                newWidth -= value;
                break;
            case "bottom":
                newHeight -= value;
                break;
            case "right":
                newWidth -= value;
                break;
            default:
                throw new IllegalArgumentException("Invalid direction: " + direction);
        }

        if (newWidth <= 0 || newHeight <= 0) {
            throw new IllegalArgumentException("Crop value too large, resulting in zero or negative dimensions.");
        }

        return image.getSubimage(x, y, newWidth, newHeight);
    }
}
