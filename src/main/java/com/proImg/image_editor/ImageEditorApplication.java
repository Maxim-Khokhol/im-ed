package com.proImg.image_editor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ImageEditorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImageEditorApplication.class, args);
	}

}
