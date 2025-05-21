package com.proImg.image_editor.entities;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.proImg.image_editor.composite.Controllable;
import lombok.*;

import java.util.Random;

@Data
@NoArgsConstructor
@Entity
@Table(name = "images")
public class Image implements Cloneable, Controllable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String type;

    private int width;
    private int height;
    private int naturalWidth;
    private int naturalHeight;
    private Long personalId;


    @ManyToOne
    @JoinColumn(name = "collage_id")
    @JsonBackReference
    private Collage collage;



    private Long blockPersonalId;

    @Column(name = "`left`")
    private int left;

    @Column(name = "`top`")
    private int top;

    @Lob
    @Column(length = 50000000)
    private byte[] picByte;

    @ManyToOne
    @JoinColumn(name = "project_id")
    @JsonBackReference
    private Project project;

    @Transient
    private String base64;

    public Image(String name, String type, byte[] picByte, Project project) {
        this.name = name;
        this.type = type;
        this.picByte = picByte;
        this.project = project;
    }

    public Image(String name, String type, byte[] picByte) {
        this.name = name;
        this.type = type;
        this.picByte = picByte;
    }


    @Override
    public Image clone() {
        try {
            Image cloned = (Image) super.clone();
            cloned.id = null;
            cloned.personalId = System.currentTimeMillis() + new Random().nextInt(100000);
            cloned.collage = null;
            cloned.project = null;
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning not supported", e);
        }
    }




    @Override
    public void scale(double scaleFactor) {
        this.width = (int) (this.width * scaleFactor);
        this.height = (int) (this.height * scaleFactor);
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
    }

    @Override
    public Image getChild(int index) {
        throw new UnsupportedOperationException("Cannot get child from a leaf");
    }


    @Override
    public void addImage(Image image) {
        throw new UnsupportedOperationException("Cannot add an image to a leaf");
    }

    @Override
    public void removeImage(Image image) {
        throw new UnsupportedOperationException("Cannot remove an image from a leaf");
    }

}