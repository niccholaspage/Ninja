package com.nicholasnassar.ninja.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nicholasnassar.ninja.Control;
import com.nicholasnassar.ninja.OptionsManager;

public class NinjaScreen extends ScreenAdapter {
    protected final SpriteBatch batch;

    protected final Control back;

    public NinjaScreen(SpriteBatch batch) {
        this.batch = batch;

        back = OptionsManager.getControl("back");
    }

    protected void backPressed() {
        Gdx.app.exit();
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || OptionsManager.isJustPressed(back)) {
            backPressed();
        }
    }
}
