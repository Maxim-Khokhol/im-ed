package com.proImg.image_editor.state;

import java.awt.image.BufferedImage;

public class SepiaEffect implements ImageEffectState {
    @Override
    public BufferedImage applyEffect(BufferedImage image) {
        System.out.println("Applying Sepia effect...");
        BufferedImage sepiaImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int pixel = image.getRGB(x, y);
                int alpha = (pixel >> 24) & 0xff;
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = pixel & 0xff;

                int tr = Math.min((int)(0.393 * red + 0.769 * green + 0.189 * blue), 255);
                int tg = Math.min((int)(0.349 * red + 0.686 * green + 0.168 * blue), 255);
                int tb = Math.min((int)(0.272 * red + 0.534 * green + 0.131 * blue), 255);

                sepiaImage.setRGB(x, y, (alpha << 24) | (tr << 16) | (tg << 8) | tb);
            }
        }
        return sepiaImage;
    }
}

