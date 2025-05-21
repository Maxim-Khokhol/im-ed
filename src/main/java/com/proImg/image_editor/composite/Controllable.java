package com.proImg.image_editor.composite;

import com.proImg.image_editor.entities.Image;

public interface Controllable {
    void scale(double scaleFactor);
    void move(int step, String action);
    void addImage(Image image);
    void removeImage(Image image);

    Image getChild(int index);
}
