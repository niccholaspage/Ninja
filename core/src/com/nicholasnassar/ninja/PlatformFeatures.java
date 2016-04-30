package com.nicholasnassar.ninja;

import com.nicholasnassar.ninja.systems.LevelEditorSystem;

public interface PlatformFeatures {
    void init();

    void loadLevel(LevelEditorSystem system);

    void saveLavel(LevelEditorSystem system);
}
