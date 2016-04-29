package com.nicholasnassar.ninja;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.nicholasnassar.ninja.screens.GameScreen;

public class MobileInput {
    private final Touchpad touchPad;

    private final static float PIXELS_REQUIRED = 0.2f;

    private boolean jumpPressed;

    private boolean throwPressed;

    public MobileInput(final GameScreen screen, Touchpad touchPad, final Button jumpButton, final Button throwButton, final Button pauseButton) {
        this.touchPad = touchPad;

        jumpPressed = false;

        jumpButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                jumpPressed = true;

                return true;
            }
        });

        throwPressed = false;

        throwButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                throwPressed = true;

                return true;
            }
        });

        pauseButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                screen.togglePause();

                return true;
            }
        });
    }

    public boolean isLeftDown() {
        return touchPad.getKnobPercentX() < -PIXELS_REQUIRED;
    }

    public boolean isRightDown() {
        return touchPad.getKnobPercentX() > PIXELS_REQUIRED;
    }

    public boolean isUpDown() {
        return touchPad.getKnobPercentY() > PIXELS_REQUIRED;
    }

    public boolean isDownDown() {
        return touchPad.getKnobPercentY() < -PIXELS_REQUIRED;
    }

    public boolean isJumpPressed() {
        return jumpPressed;
    }

    public boolean isThrowPressed() {
        return throwPressed;
    }

    public void reset() {
        jumpPressed = false;

        throwPressed = false;
    }
}
