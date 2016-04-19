package com.nicholasnassar.ninja.components;

import com.badlogic.ashley.core.Component;

public class JumpComponent implements Component {
    private final float jumpHeight;

    private final int extraJumps;

    private int availableJumps;

    public JumpComponent(float jumpHeight) {
        this(jumpHeight, 0);
    }

    public JumpComponent(float jumpHeight, int extraJumps) {
        this.jumpHeight = jumpHeight;

        this.extraJumps = extraJumps;

        availableJumps = extraJumps;
    }

    public float getJumpHeight() {
        return jumpHeight;
    }

    public int getExtraJumps() {
        return extraJumps;
    }

    public void setAvailableJumps(int availableJumps) {
        if (availableJumps < 0) {
            availableJumps = 0;
        }

        this.availableJumps = availableJumps;
    }

    public int getAvailableJumps() {
        return availableJumps;
    }
}
