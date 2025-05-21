package com.proImg.image_editor.state;

import java.awt.image.BufferedImage;

public class BlurEffect implements ImageEffectState {

    @Override
    public BufferedImage applyEffect(BufferedImage image) {
        System.out.println("Applying Strong Blur effect...");

        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage blurredImage = new BufferedImage(width, height, image.getType());

        int kernelSize = 15;
        int passes = 3;

        BufferedImage tempImage = image;

        for (int pass = 0; pass < passes; pass++) {
            BufferedImage currentImage = new BufferedImage(width, height, image.getType());

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int[] rgb = getAverageColor(tempImage, x, y, kernelSize);
                    int blurredPixel = (rgb[0] << 16) | (rgb[1] << 8) | rgb[2];
                    currentImage.setRGB(x, y, blurredPixel);
                }
            }

            tempImage = currentImage;
        }

        return tempImage;
    }

    private int[] getAverageColor(BufferedImage image, int x, int y, int kernelSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        int red = 0, green = 0, blue = 0, count = 0;

        int halfKernel = kernelSize / 2;

        for (int dx = -halfKernel; dx <= halfKernel; dx++) {
            for (int dy = -halfKernel; dy <= halfKernel; dy++) {
                int nx = x + dx;
                int ny = y + dy;

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    int rgb = image.getRGB(nx, ny);
                    red += (rgb >> 16) & 0xFF;
                    green += (rgb >> 8) & 0xFF;
                    blue += rgb & 0xFF;
                    count++;
                }
            }
        }

        return new int[]{red / count, green / count, blue / count};
    }
}

