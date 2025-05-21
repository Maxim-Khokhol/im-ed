package com.proImg.image_editor.state;

import java.awt.image.BufferedImage;

public class InvertColorsEffect implements ImageEffectState {

    @Override
    public BufferedImage applyEffect(BufferedImage image) {
        System.out.println("Applying Invert Colors effect...");

        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage invertedImage = new BufferedImage(width, height, image.getType());

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = image.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;
                int invertedRed = 255 - red;
                int invertedGreen = 255 - green;
                int invertedBlue = 255 - blue;
                int invertedRgb = (invertedRed << 16) | (invertedGreen << 8) | invertedBlue;
                invertedImage.setRGB(x, y, invertedRgb);
            }
        }

        return invertedImage;
    }
}
