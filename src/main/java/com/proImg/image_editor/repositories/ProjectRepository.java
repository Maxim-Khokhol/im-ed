package com.proImg.image_editor.repositories;

import com.proImg.image_editor.entities.Project;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"collages", "images"})
    @Query("SELECT p FROM Project p WHERE p.id = :projectId")
    Optional<Project> findByIdWithDetails(Long projectId);




}
