package com.proImg.image_editor.repositories;

import com.proImg.image_editor.entities.Collage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollageRepository extends JpaRepository<Collage, Long> {

    Optional<Collage> findByPersonalId(Long personalId);

}
