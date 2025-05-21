package com.proImg.image_editor.controllers;

import com.proImg.image_editor.facade.ImageEffectFacade;
import com.proImg.image_editor.memento.ImageCaretaker;
import com.proImg.image_editor.memento.ImageMemento;
import com.proImg.image_editor.memento.ImageOriginator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageEffectFacade facade = new ImageEffectFacade();
    private final Map<Long, ImageCaretaker> caretakers = new HashMap<>();
    private final Map<Long, ImageOriginator> originators = new HashMap<>();



    @PostMapping("/apply")
    public ResponseEntity<String> applyEffect(@RequestParam String effect,
                                              @RequestParam(required = false, defaultValue = "10") int pixelSize,
                                              @RequestParam(required = false, defaultValue = "top") String direction,
                                              @RequestParam(required = false, defaultValue = "10") int cropValue,
                                              @RequestBody ImageRequest request) {
        try {
            Long imageId = request.getId();
            ImageOriginator originator = originators.computeIfAbsent(imageId, id -> new ImageOriginator());
            ImageCaretaker caretaker = caretakers.computeIfAbsent(imageId, id -> new ImageCaretaker());

            if (caretaker.getInitialState(imageId) == null) {
                originator.setBase64(request.getBase64());
                caretaker.saveInitialState(imageId, originator.createMemento());
            }

            caretaker.saveState(imageId, originator.createMemento());

            facade.setEffect(effect, direction, pixelSize, cropValue);
            originator.applyEffect(facade::applyEffect);

            caretaker.saveState(imageId, originator.createMemento());
            return ResponseEntity.ok(originator.getBase64());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing image: " + e.getMessage());
        }
    }






    @PostMapping("/undo")
    public ResponseEntity<String> undoEffect(@RequestParam Long imageId) {
        ImageCaretaker caretaker = caretakers.get(imageId);
        ImageOriginator originator = originators.get(imageId);

        if (caretaker == null || originator == null) {
            return ResponseEntity.badRequest().body("No caretaker or originator found for this image.");
        }

        ImageMemento previousState = caretaker.undo(imageId);
        if (previousState != null) {
            originator.setMemento(previousState);
            return ResponseEntity.ok(originator.getBase64());
        }

        ImageMemento initialState = caretaker.getInitialState(imageId);
        if (initialState != null) {
            originator.setMemento(initialState);
            return ResponseEntity.ok(originator.getBase64());
        }

        return ResponseEntity.badRequest().body("No states available to undo.");
    }


    public static class ImageRequest {
        private Long id;
        private String base64;
        private String type;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getBase64() {
            return base64;
        }

        public void setBase64(String base64) {
            this.base64 = base64;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

}