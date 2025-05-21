package com.proImg.image_editor.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;

    @Column(name = "email", unique = true)
    private String email;

    private String password;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<Project> ownedProjects;


}

