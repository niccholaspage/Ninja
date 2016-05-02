package com.nicholasnassar.ninja;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.nicholasnassar.ninja.screens.GameScreen;

public class MobileInput {
    private final Touchpad touchPad;

    private final Button jumpButton;

    private final Button throwButton;

    private final Button pauseButton;

    private final static float PIXELS_REQUIRED = 0.2f;

    private boolean jumpPressed;

    private boolean throwPressed;

    public MobileInput(final GameScreen screen, Touchpad touchPad, Button jumpButton, Button throwButton, Button pauseButton) {
        this.touchPad = touchPad;

        jumpPressed = false;

        this.jumpButton = jumpButton;

        this.throwButton = throwButton;

        this.pauseButton = pauseButton;

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

    public void resize(int width, int height) {
        touchPad.setSize(width / 7, width / 7);

        touchPad.setPosition(10, 10);

        jumpButton.setSize(width / 7, width / 7);

        jumpButton.setPosition(width - jumpButton.getWidth() - 5, 10);

        throwButton.setSize(width / 7, width / 7);

        throwButton.setPosition(width - jumpButton.getWidth() - throwButton.getWidth() - 10, 10);

        pauseButton.setSize(width / 14, width / 14);

        pauseButton.setPosition(width - pauseButton.getWidth() - 5, height - pauseButton.getHeight() - 5);
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
