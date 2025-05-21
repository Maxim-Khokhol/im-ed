package com.proImg.image_editor.facade;
import com.proImg.image_editor.state.*;
import java.awt.image.BufferedImage;

public class ImageEffectFacade {
    private ImageEffectState state;

    public void setEffect(String effectName, String direction,  int... params) {
        switch (effectName.toLowerCase()) {
            case "grayscale" -> state = new GrayScaleEffect();
            case "sepia" -> state = new SepiaEffect();
            case "blur" -> state = new BlurEffect();
            case "pixelation" -> state = new PixelationEffect(params.length > 0 ? params[0] : 10);
            case "invert-colors" -> state = new InvertColorsEffect();
            case "crop" -> state = new CropEffect(direction, params.length > 0 ? params[1] : 10);
            default -> throw new IllegalArgumentException("Unknown effect: " + effectName);
        }
    }

    public BufferedImage applyEffect(BufferedImage image) {
        if (state == null) {
            throw new IllegalStateException("No effect has been selected!");
        }
        return state.applyEffect(image);
    }
}


