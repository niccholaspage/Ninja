package com.nicholasnassar.ninja;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Array;

public class ControlManager {
    public static final Array<Control> controls = new Array<Control>();

    static {
        controls.add(new Control("back", "Back", Input.Keys.ESCAPE));
        controls.add(new Control("move_up", "Move Up", Input.Keys.W));
        controls.add(new Control("move_left", "Move Left", Input.Keys.A));
        controls.add(new Control("move_down", "Move Down", Input.Keys.S));
        controls.add(new Control("move_right", "Move Right", Input.Keys.D));
        controls.add(new Control("jump", "Jump", Input.Keys.SPACE));
        controls.add(new Control("throw", "Throw", Input.Keys.G));
        controls.add(new Control("roll", "Roll", Input.Keys.S));
    }

    public static Control getControl(String id) {
        for (Control control : controls) {
            if (control.getId().equals(id)) {
                return control;
            }
        }

        throw new NullPointerException();
    }

    public static boolean isPressed(Control control) {
        return Gdx.input.isKeyPressed(control.getKey());
    }

    public static boolean isJustPressed(Control control) {
        return Gdx.input.isKeyJustPressed(control.getKey());
    }
}
