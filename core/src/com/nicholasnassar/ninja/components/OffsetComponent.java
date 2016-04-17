package com.nicholasnassar.ninja.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.IntIntMap;

public class OffsetComponent implements Component {
    private final IntIntMap stateOffsets;

    public OffsetComponent(IntIntMap stateOffsets) {
        this.stateOffsets = stateOffsets;
    }

    public IntIntMap getStateOffsets() {
        return stateOffsets;
    }
}
