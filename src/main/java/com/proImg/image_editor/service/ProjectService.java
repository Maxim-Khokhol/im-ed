package com.proImg.image_editor.service;


import com.proImg.image_editor.entities.*;
import com.proImg.image_editor.repositories.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    private final CollageRepository collageRepository;


    private final ImageRepository imageRepository;


    @Autowired
    public ProjectService(ProjectRepository projectRepository,
                          UserRepository userRepository,
                          CollageRepository collageRepository,
                          ImageRepository imageRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.collageRepository = collageRepository;
        this.imageRepository = imageRepository;
    }

    public Project createProject(String name, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        Project project = new Project();
        project.setName(name);
        project.setUser(user);

        return projectRepository.save(project);
    }




    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findByIdWithDetails(id);
    }



    public List<Project> getProjectsByUserId(Long userId) {
        return projectRepository.findByUserId(userId);
    }



    public Project updateProjectWithImages(Long projectId, List<Image> images) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));


        project.getImages().clear();

        images.forEach(image -> System.out.println(image.getTop()));

        for (Image image : images) {
            if(image.getBlockPersonalId() != null){
                Collage collage = getCollageByPersonalId(image.getBlockPersonalId());
                collage.addImage(image);
                image.setCollage(collage);

            } else {
                image.setCollage(null);
            }
            image.setProject(project);
            imageRepository.save(image);
        }

        return projectRepository.save(project);
    }


    public Project updateProjectWithData(Long projectId, List<Collage> collages) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));

        project.getCollages().clear();

        for (Collage collage : collages) {
            collage.setProject(project);
            project.getCollages().add(collage);
        }
        return projectRepository.save(project);
    }




    @Transactional
    public Project cloneProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        Project clonedProject = project.clone();
        clonedProject.setName(project.getName() + " (Clone)");


        for (Collage collage : clonedProject.getCollages()) {
            collage.setProject(clonedProject);

            projectRepository.save(clonedProject);

            for (Image image : collage.getImages()) {
                image.setCollage(collage);
                image.setProject(clonedProject);
                imageRepository.save(image);
            }
        }


        return clonedProject;
    }





    public Collage getCollageByPersonalId(Long personalId) {
        return collageRepository.findByPersonalId(personalId)
                .orElseThrow(() -> new RuntimeException("Collage with personalId " + personalId + " not found"));
    }




    public void scaleBlock(Long projectId, Long collageId, double scaleFactor) {
        Collage collage = collageRepository.findByPersonalId(collageId)
                .orElseThrow(() -> new RuntimeException("Collage not found"));
        collage.scale(scaleFactor);
        collageRepository.save(collage);
    }

    public void scaleImage(Long projectId,Long imageId, double scaleFactor) {
        Image image = imageRepository.findByPersonalId(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        image.scale(scaleFactor);
        imageRepository.save(image);

    }

    public void moveBlock(Long projectId, Long collageId, int step, String action){
        Collage collage = collageRepository.findByPersonalId(collageId)
                .orElseThrow(() -> new RuntimeException("Collage not found"));
        collage.move(step, action);
        collageRepository.save(collage);

    }

    public void moveImage(Long projectId, Long imageId, int step, String action){
        Image image = imageRepository.findByPersonalId(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        image.move(step, action);
        imageRepository.save(image);
    }



    @Transactional
    public void deleteImage(Long imageId) {
        Image image = imageRepository.findByPersonalId(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found with ID: " + imageId));
        imageRepository.delete(image);
    }

    @Transactional
    public void deleteCollage(Long collageId) {
        Collage collage = collageRepository.findByPersonalId(collageId)
                .orElseThrow(() -> new RuntimeException("Collage not found with ID: " + collageId));
        List<Image> images = collage.getImages();

        for(Image image : images){
            imageRepository.delete(image);
        }

        collageRepository.delete(collage);
    }


    @Transactional
    public void deleteProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));
        projectRepository.delete(project);
    }



}