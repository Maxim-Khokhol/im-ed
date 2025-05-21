package com.proImg.image_editor.state;

import java.awt.*;
import java.awt.image.BufferedImage;

public class GrayScaleEffect implements ImageEffectState {
    @Override
    public BufferedImage applyEffect(BufferedImage image) {
        System.out.println("Applying GrayScale effect...");
        BufferedImage grayImage = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY
        );
        Graphics g = grayImage.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return grayImage;
    }
}

