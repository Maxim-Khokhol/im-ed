package com.proImg.image_editor.memento;

import com.proImg.image_editor.state.ImageEffectState;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class ImageOriginator {
    private String base64;

    public void setBase64(String base64) {
        this.base64 = base64;
    }
    public ImageMemento createMemento() {
        return new ImageMemento(base64);
    }

    public void setMemento(ImageMemento memento) {
        if (memento != null) {
            this.base64 = memento.getBase64();
        }
    }
    public void applyEffect(ImageEffectState effect) {
        try {
            System.out.println("Applying effect: " + effect.getClass().getSimpleName());

            byte[] imageBytes = Base64.getDecoder().decode(base64.split(",")[1]);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            BufferedImage currentImage = ImageIO.read(bis);

            BufferedImage resultImage = effect.applyEffect(currentImage);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(resultImage, "png", bos);
            this.base64 = "data:image/png;base64," + Base64.getEncoder().encodeToString(bos.toByteArray());

            createMemento();

        } catch (Exception e) {
            System.err.println("Error applying effect: " + e.getMessage());
        }
    }


    public String getBase64() {
        return base64;
    }
}
