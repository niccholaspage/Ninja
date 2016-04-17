package com.nicholasnassar.ninja.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.nicholasnassar.ninja.PlatformFeatures;
import com.nicholasnassar.ninja.systems.LevelEditorSystem;

import java.io.StringWriter;

public class HtmlPlatformFeatures implements PlatformFeatures, Input.TextInputListener {
    private final Preferences preferences;

    private LevelEditorSystem levelEditorSystem;

    private boolean saving;

    public HtmlPlatformFeatures() {
        preferences = Gdx.app.getPreferences("com.nicholasnassar.ninja.settings");

        saving = false;
    }

    @Override
    public void loadLevel(LevelEditorSystem system) {
        Gdx.input.getTextInput(this, "Load Level", "", "Type the level name");

        levelEditorSystem = system;

        saving = false;
    }

    @Override
    public void saveLavel(LevelEditorSystem system) {
        Gdx.input.getTextInput(this, "Save Level", "", "Type the level name");

        levelEditorSystem = system;

        saving = true;
    }

    @Override
    public void input(String text) {
        String levelName = "level_" + text;

        if (saving) {
            StringWriter writer = new StringWriter();

            levelEditorSystem.save(writer);

            preferences.putString(levelName, writer.toString());
        } else {
            if (preferences.contains(levelName)) {
                levelEditorSystem.load(preferences.getString(text));
            }
        }
    }

    @Override
    public void canceled() {
        //Oh well..
    }
}
