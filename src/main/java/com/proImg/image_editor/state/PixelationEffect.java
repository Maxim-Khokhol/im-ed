package com.proImg.image_editor.state;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PixelationEffect implements ImageEffectState {

    private final int pixelSize;

    public PixelationEffect(int pixelSize) {
        this.pixelSize = pixelSize;
    }

    @Override
    public BufferedImage applyEffect(BufferedImage image) {
        System.out.println("Applying Pixelation effect...");
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage pixelatedImage = new BufferedImage(width, height, image.getType());
        Graphics2D g2d = pixelatedImage.createGraphics();

        for (int y = 0; y < height; y += pixelSize) {
            for (int x = 0; x < width; x += pixelSize) {
                int pixelColor = getAverageColor(image, x, y, pixelSize);
                g2d.setColor(new Color(pixelColor, true));
                g2d.fillRect(x, y, pixelSize, pixelSize);
            }
        }

        g2d.dispose();
        return pixelatedImage;
    }

    private int getAverageColor(BufferedImage image, int startX, int startY, int size) {
        int endX = Math.min(startX + size, image.getWidth());
        int endY = Math.min(startY + size, image.getHeight());

        int r = 0, g = 0, b = 0, count = 0;

        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                Color pixel = new Color(image.getRGB(x, y));
                r += pixel.getRed();
                g += pixel.getGreen();
                b += pixel.getBlue();
                count++;
            }
        }

        return new Color(r / count, g / count, b / count).getRGB();
    }
}
