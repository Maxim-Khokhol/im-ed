package com.proImg.image_editor.repositories;

import com.proImg.image_editor.entities.Collage;
import com.proImg.image_editor.entities.Image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByPersonalId(Long personalId);




}