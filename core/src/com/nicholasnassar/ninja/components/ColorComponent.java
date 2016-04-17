package com.nicholasnassar.ninja.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;

public class ColorComponent implements Component {
    private final Color color;

    public ColorComponent(float red, float green, float blue) {
        this(red, green, blue, 1);
    }

    public ColorComponent(float red, float green, float blue, float alpha) {
        color = new Color(red, green, blue, alpha);
    }

    public Color getColor() {
        return color;
    }
}
