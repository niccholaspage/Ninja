package com.nicholasnassar.ninja.components;

import com.badlogic.ashley.core.Component;

public class GravityComponent implements Component {
    private boolean grounded;

    public GravityComponent() {
        grounded = false;
    }

    public void setGrounded(boolean grounded) {
        this.grounded = grounded;
    }

    public boolean isGrounded() {
        return grounded;
    }
}
