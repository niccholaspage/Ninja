package com.nicholasnassar.ninja;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Array;

public class ControlManager {
    public static final Array<Control> controls = new Array<Control>();

    static {
        controls.add(new Control("Back", Input.Keys.ESCAPE));
        controls.add(new Control("Move Up", Input.Keys.W));
        controls.add(new Control("Move Left", Input.Keys.A));
        controls.add(new Control("Move Down", Input.Keys.S));
        controls.add(new Control("Move Right", Input.Keys.D));
        controls.add(new Control("Jump", Input.Keys.SPACE));
        controls.add(new Control("Throw", Input.Keys.G));
        controls.add(new Control("Roll", Input.Keys.S));
    }

    public static Control getControl(String name) {
        for (Control control : controls) {
            if (control.getName().equals(name)) {
                return control;
            }
        }

        return null;
    }

    public static boolean isPressed(Control control) {
        return Gdx.input.isKeyPressed(control.getKey());
    }

    public static boolean isJustPressed(Control control) {
        return Gdx.input.isKeyJustPressed(control.getKey());
    }
}
