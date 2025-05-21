package com.proImg.image_editor.controllers;

import com.proImg.image_editor.entities.Collage;
import com.proImg.image_editor.entities.Image;
import com.proImg.image_editor.entities.Project;
import com.proImg.image_editor.service.CollageService;
import com.proImg.image_editor.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final CollageService collageService;




    @Autowired
    public ProjectController(ProjectService projectService, CollageService collageService) {
        this.projectService = projectService;
        this.collageService = collageService;
    }


    @PostMapping("/createProject")
    public ResponseEntity<Project> createProject(@RequestBody Map<String, Object> request) {
        String name = (String) request.get("name");
        Long userId = Long.valueOf((Integer) request.get("userId"));

        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        try {
            Project createdProject = projectService.createProject(name, userId);
            return ResponseEntity.ok(createdProject);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }


    @GetMapping
    public ResponseEntity<List<Project>> getProjectsByUserId(@RequestParam Long userId) {
        List<Project> userProjects = projectService.getProjectsByUserId(userId);
        return ResponseEntity.ok(userProjects);
    }



    @GetMapping("/{projectId}")
    public ResponseEntity<Project> getProjectById(@PathVariable Long projectId) {
        Optional<Project> project = projectService.getProjectById(projectId);

        if (project.isPresent()) {
            Project p = project.get();
            p.getImages().forEach(image -> {
                String base64 = Base64.getEncoder().encodeToString(image.getPicByte());
                image.setBase64("data:" + image.getType() + ";base64," + base64);
            });
            return ResponseEntity.ok(p);
        } else {
            return ResponseEntity.notFound().build();
        }
    }



    @PutMapping("/{projectId}/update")
    public ResponseEntity<Project> updateProjectData(
            @PathVariable Long projectId,
            @RequestBody Map<String, Object> request) {

        List<LinkedHashMap<String, Object>> collagesData = (List<LinkedHashMap<String, Object>>) request.get("collages");

        List<Collage> collages = collagesData.stream().map(data -> {
            Collage collage = new Collage();
            collage.setBackgroundColor((String) data.get("backgroundColor"));
            collage.setWidth((Integer) data.get("width"));
            collage.setHeight((Integer) data.get("height"));
            collage.setLeft((Integer) data.get("left"));
            collage.setTop((Integer) data.get("top"));
            collage.setPersonalId((Long) data.get("id"));
            return collage;
        }).toList();


        Project updatedProject = projectService.updateProjectWithData(projectId, collages);
        return ResponseEntity.ok(updatedProject);
    }


    @PutMapping("/{projectId}/update/images")
    public ResponseEntity<Project> updateProjectImages(
            @PathVariable Long projectId,
            @RequestBody Map<String, Object> request) {

        List<LinkedHashMap<String, Object>> imagesData = (List<LinkedHashMap<String, Object>>) request.get("images");

        List<Image> images = imagesData.stream().map(data -> {
            Image image = new Image();
            image.setName((String) data.get("name"));
            image.setType((String) data.get("type"));
            image.setWidth((Integer) data.get("width"));
            image.setHeight((Integer) data.get("height"));
            image.setNaturalWidth((Integer) data.get("naturalWidth"));
            image.setNaturalHeight((Integer) data.get("naturalHeight"));
            image.setLeft((Integer) data.get("left"));
            image.setPersonalId((Long) data.get("id"));
            image.setTop((Integer) data.get("top"));
            image.setBlockPersonalId((Long) data.get("blockId"));
            image.setPicByte(Base64.getDecoder().decode(((String) data.get("base64")).split(",")[1]));
            return image;
        }).toList();

        Project updatedProject = projectService.updateProjectWithImages(projectId, images);
        return ResponseEntity.ok(updatedProject);
    }


    @PostMapping("/{projectId}/clone")
    public ResponseEntity<Project> cloneProject(@PathVariable Long projectId) {
        try {
            Project clonedProject = projectService.cloneProject(projectId);
            return ResponseEntity.ok(clonedProject);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }







    @PostMapping("/{projectId}/generateCollage")
    public ResponseEntity<byte[]> generateCollage(
            @PathVariable Long projectId,
            @RequestBody Map<String, Object> request) {
        try {
            String fileType = (String) request.getOrDefault("fileType", "png");

            byte[] collageBytes = collageService.generateCollage(request, fileType);

            int uniqueId = (int) (Math.random() * 90000) + 10000;

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=collage_" + uniqueId + "." + fileType);
            headers.add(HttpHeaders.CONTENT_TYPE, "image/" + fileType);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(collageBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }






    @PutMapping("/{projectId}/scale")
    public ResponseEntity<Void> scaleComponent(
            @PathVariable Long projectId,
            @RequestParam(required = false) Long collageId,
            @RequestParam(required = false) Long imageId,
            @RequestParam double scaleFactor,
            @RequestParam String selectionMode) {

        if(Objects.equals(selectionMode, "block")){
            projectService.scaleBlock(projectId, collageId, scaleFactor);
        } else {
            projectService.scaleImage(projectId, imageId, scaleFactor);
        }
        return ResponseEntity.ok().build();
    }



    @PutMapping("/{projectId}/move")
    public ResponseEntity<Void> moveComponent(
            @PathVariable Long projectId,
            @RequestParam(required = false) Long collageId,
            @RequestParam(required = false) Long imageId,
            @RequestParam int step,
            @RequestParam String selectionMode,
            @RequestParam String action) {

        if(Objects.equals(selectionMode, "block")){
            projectService.moveBlock(projectId, collageId, step, action);
        } else {
            projectService.moveImage(projectId, imageId, step, action);
        }
        return ResponseEntity.ok().build();
    }




    @PutMapping("/{projectId}/delete/image")
    public ResponseEntity<Void> deleteImage(
            @PathVariable Long projectId, @RequestParam Long imageId) {
        projectService.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{projectId}/delete/collage")
    public ResponseEntity<Void> deleteCollage(
            @PathVariable Long projectId, @RequestParam Long blockId) {
        projectService.deleteCollage(blockId);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/{projectId}/delete")
    public ResponseEntity<Void> deleteProject(@PathVariable Long projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.noContent().build();
    }



}







