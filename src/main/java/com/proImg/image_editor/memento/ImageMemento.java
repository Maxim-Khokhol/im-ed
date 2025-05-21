package com.proImg.image_editor.memento;

public class ImageMemento {
    private final String base64;

    public ImageMemento(String base64) {
        this.base64 = base64;
    }

    public String getBase64() {
        return base64;
    }
}
