package com.proImg.image_editor;

import com.proImg.image_editor.entities.Project;
import com.proImg.image_editor.entities.User;
import com.proImg.image_editor.repositories.*;
import com.proImg.image_editor.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProjectServiceTest {

    @Test
    void createProject_success() {
        UserRepository userRepository = mock(UserRepository.class);
        ProjectRepository projectRepository = mock(ProjectRepository.class);
        CollageRepository collageRepository = mock(CollageRepository.class);
        ImageRepository imageRepository = mock(ImageRepository.class);

        ProjectService projectService = new ProjectService(projectRepository, userRepository, collageRepository, imageRepository);

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(projectRepository.save(any(Project.class))).thenAnswer(i -> i.getArguments()[0]);

        Project project = projectService.createProject("My Project", 1L);

        assertNotNull(project);
        assertEquals("My Project", project.getName());
        assertEquals("testuser", project.getUser().getUsername());
    }
}
