package com.proImg.image_editor.state;

import java.awt.image.BufferedImage;

public interface ImageEffectState {
    BufferedImage applyEffect(BufferedImage image);
}