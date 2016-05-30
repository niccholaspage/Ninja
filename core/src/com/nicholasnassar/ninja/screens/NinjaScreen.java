package com.nicholasnassar.ninja.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nicholasnassar.ninja.Control;
import com.nicholasnassar.ninja.ControlManager;

public class NinjaScreen extends ScreenAdapter {
    protected final SpriteBatch batch;

    protected final Control back;

    public NinjaScreen(SpriteBatch batch) {
        this.batch = batch;

        back = ControlManager.getControl("Back");
    }

    @Override
    public void render(float delta) {
        if (ControlManager.isJustPressed(back)) {
            Gdx.app.exit();
        }
    }
}
