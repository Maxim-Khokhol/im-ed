package com.proImg.image_editor.dtos;


import com.proImg.image_editor.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;



@Data
@AllArgsConstructor
public class UserDto extends User {
    private Long id;
    private String username;
    private String password;
    private String email;

}
