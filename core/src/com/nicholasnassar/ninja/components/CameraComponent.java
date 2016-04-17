package com.nicholasnassar.ninja.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Camera;

public class CameraComponent implements Component {
    private Entity[] targets;

    private int selected;

    private final Camera camera;

    public CameraComponent(Camera camera) {
        targets = null;

        this.camera = camera;

        selected = 0;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public void updateTargets(Entity... targets) {
        this.targets = targets;
    }

    public Entity getTarget() {
        return targets[selected];
    }

    public Entity[] getTargets() {
        return targets;
    }

    public Camera getCamera() {
        return camera;
    }
}
