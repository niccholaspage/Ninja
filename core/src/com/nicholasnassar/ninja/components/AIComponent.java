package com.nicholasnassar.ninja.components;

import com.badlogic.ashley.core.Component;

public class AIComponent implements Component {
    private float lastAction;

    public AIComponent() {
        lastAction = 0f;
    }

    public float getLastAction() {
        return lastAction;
    }

    public void setLastAction(float lastAction) {
        this.lastAction = lastAction;
    }
}
