package com.proImg.image_editor;

import com.proImg.image_editor.dtos.RegistrationUserDto;
import com.proImg.image_editor.entities.User;
import com.proImg.image_editor.repositories.UserRepository;
import com.proImg.image_editor.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Test
    void createNewUser_success() {
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);

        UserService userService = new UserService();
        userService.setUserRepository(userRepository);
        userService.setPasswordEncoder(encoder);

        RegistrationUserDto dto = new RegistrationUserDto();
        dto.setUsername("test");
        dto.setPassword("test123");
        dto.setEmail("test@gmail.com");


        when(encoder.encode("test123")).thenReturn("encoded_pass");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        User saved = userService.createNewUser(dto);

        assertNotNull(saved);
        assertEquals("test", saved.getUsername());
        assertEquals("encoded_pass", saved.getPassword());
    }

    @Test
    void findByUsername_found() {
        UserRepository userRepository = mock(UserRepository.class);
        UserService service = new UserService();
        service.setUserRepository(userRepository);

        User user = new User();
        user.setUsername("demo");

        when(userRepository.findByUsername("demo")).thenReturn(Optional.of(user));

        Optional<User> result = service.findByUsername("demo");

        assertTrue(result.isPresent());
        assertEquals("demo", result.get().getUsername());
    }
}
