package com.nicholasnassar.ninja.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nicholasnassar.ninja.ControlManager;

public class NinjaScreen extends ScreenAdapter {
    protected final SpriteBatch batch;

    public NinjaScreen(SpriteBatch batch) {
        this.batch = batch;
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyPressed(ControlManager.BACK)) {
            Gdx.app.exit();
        }
    }
}
