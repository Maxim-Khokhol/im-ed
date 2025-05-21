package com.proImg.image_editor.entities;

import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.proImg.image_editor.composite.Controllable;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Entity
@Data
@Table(name = "collages")
@AllArgsConstructor
@NoArgsConstructor
public class Collage implements Cloneable, Controllable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String backgroundColor;
    private Long personalId;
    private int width;
    private int height;

    @Column(name = "`left`")
    private int left;

    @Column(name = "`top`")
    private int top;

    @ManyToOne
    @JoinColumn(name = "project_id")
    @JsonBackReference
    private Project project;

    @OneToMany(mappedBy = "collage", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Image> images = new ArrayList<>();

    @Override
    public void addImage(Image image) {
        if (images == null) {
            images = new ArrayList<>();
        }
        images.add(image);

    }
    @Override
    public void removeImage(Image image) {
        if (images != null) {
            images.remove(image);
        }
    }

    @Override
    public void scale(double scaleFactor) {
        this.width = (int) (this.width * scaleFactor);
        this.height = (int) (this.height * scaleFactor);

        for (Image image : images) {
            image.scale(scaleFactor);
        }
    }

    @Override
    public void move(int step, String action){
        switch (action) {
            case "right" -> this.left -= step;
            case "left" -> this.left += step;
            case "top" -> this.top += step;
            case "bottom" -> this.top -= step;
            default ->
                    throw new UnsupportedOperationException("Unsupported operation, use only 'right', 'left', 'top', 'bottom' ");
        }
        for (int i = 0; i < images.size(); i++){
            getChild(i).move(step, action);
        }
    }

    @Override
    public Image getChild(int index){
        if (index < 0 || index >= images.size()) {
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds.");
        }
        return images.get(index);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Collage collage = (Collage) o;
        return Objects.equals(id, collage.id);
    }

    @Override
    public Collage clone() {
        try {
            Collage cloned = (Collage) super.clone();
            cloned.id = null;
            cloned.personalId = System.currentTimeMillis() + new Random().nextInt(100000);
            cloned.project = null;

            cloned.images = new ArrayList<>();
            for (Image image : this.images) {
                Image clonedImage = image.clone();
                clonedImage.setCollage(cloned);
                clonedImage.setBlockPersonalId(cloned.personalId);
                cloned.images.add(clonedImage);
            }

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning not supported", e);
        }
    }
}
