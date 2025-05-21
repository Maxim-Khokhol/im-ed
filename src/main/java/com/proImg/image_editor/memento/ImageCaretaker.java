package com.proImg.image_editor.memento;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class ImageCaretaker {
    private final Map<Long, Stack<ImageMemento>> histories = new HashMap<>();
    private final Map<Long, ImageMemento> initialStates = new HashMap<>();

    public void saveInitialState(Long imageId, ImageMemento memento) {
        initialStates.putIfAbsent(imageId, memento);
        System.out.println("Initial state saved for image ID: " + imageId);
    }

    public ImageMemento getInitialState(Long imageId) {
        return initialStates.get(imageId);
    }

    public void saveState(Long imageId, ImageMemento memento) {
        histories.computeIfAbsent(imageId, k -> new Stack<>()).push(memento);
        System.out.println("State saved for image ID: " + imageId + ". Total states: " + histories.get(imageId).size());
    }


    public ImageMemento undo(Long imageId) {
        Stack<ImageMemento> history = histories.get(imageId);
        if (history != null && !history.isEmpty()) {
            return history.pop();
        }
        return null;
    }



    public boolean hasHistory(Long imageId) {
        Stack<ImageMemento> history = histories.get(imageId);
        return history != null && !history.isEmpty();
    }
}

