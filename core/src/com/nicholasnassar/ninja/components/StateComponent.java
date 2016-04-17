package com.nicholasnassar.ninja.components;

import com.badlogic.ashley.core.Component;

public class StateComponent implements Component {
    public static final int STATE_IDLE = 0;
    public static final int STATE_WALKING = 1;
    public static final int STATE_IN_AIR = 2;

    private int state;

    private float elapsedTime;

    public StateComponent() {
        state = STATE_IDLE;

        elapsedTime = 0;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        if (this.state != state) {
            elapsedTime = 0;
        }

        this.state = state;
    }

    public float getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(float elapsedTime) {
        this.elapsedTime = elapsedTime;
    }
}
