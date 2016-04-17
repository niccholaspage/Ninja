package com.nicholasnassar.ninja.components;

import com.badlogic.ashley.core.Component;

public class JumpComponent implements Component {
    private final float jumpHeight;

    public JumpComponent(float jumpHeight) {
        this.jumpHeight = jumpHeight;
    }

    public float getJumpHeight() {
        return jumpHeight;
    }
}
