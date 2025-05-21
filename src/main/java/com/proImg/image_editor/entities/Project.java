package com.proImg.image_editor.entities;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import java.util.*;

@Entity
@Data
@Table(name = "projects")
@AllArgsConstructor
@NoArgsConstructor
public class Project implements Cloneable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Collage> collages = new ArrayList<>();



    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<Image> images = new HashSet<>();


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;




    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(id, project.id) && Objects.equals(name, project.name);
    }


    @Override
    public Project clone() {
        try {
            Project cloned = (Project) super.clone();
            cloned.id = null;

            cloned.collages = new ArrayList<>();
            for (Collage collage : this.collages) {
                Collage clonedCollage = collage.clone();
                clonedCollage.setProject(cloned);
                cloned.collages.add(clonedCollage);
            }

            cloned.images = new HashSet<>();
            for (Image image : this.images) {
                if (image.getCollage() == null) {
                    Image clonedImage = image.clone();
                    clonedImage.setProject(cloned);
                    cloned.images.add(clonedImage);
                }
            }

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning not supported", e);
        }
    }


}

