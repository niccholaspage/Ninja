package com.nicholasnassar.ninja.components;

import com.badlogic.ashley.core.Component;

public class StateComponent implements Component {
    public static final int STATE_IDLE = 0;
    public static final int STATE_WALKING = 1;
    public static final int STATE_IN_AIR = 2;
    public static final int STATE_GROUND_ROLL = 3;

    private int state;

    private float elapsedTime;

    private final VisualComponent visual;

    public StateComponent(VisualComponent visual) {
        state = STATE_IDLE;

        elapsedTime = 0;

        this.visual = visual;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        if (this.state != state) {
            elapsedTime = 0;

            visual.randomize(state);
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
