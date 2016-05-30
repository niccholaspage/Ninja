package com.nicholasnassar.ninja;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.nicholasnassar.ninja.screens.LoadingScreen;

public class NinjaGame extends Game {
    private NinjaAssetManager manager;

    private BitmapFont font;

    private SpriteBatch batch;

    private Skin skin;

    private PlatformFeatures platformFeatures;

    private Preferences preferences;

    public NinjaGame(PlatformFeatures platformFeatures) {
        this.platformFeatures = platformFeatures;
    }

    @Override
    public void create() {
        manager = new NinjaAssetManager();

        font = new BitmapFont();

        font.setColor(Color.RED);

        skin = null;

        batch = new SpriteBatch();

        platformFeatures.init();

        preferences = Gdx.app.getPreferences("com.nicholasnassar.ninja.settings");

        for (Control control : ControlManager.controls) {
            int keycode = preferences.getInteger("controls." + control.getId(), control.getDefaultKey());

            control.setKey(keycode);
        }

        ControlManager.touchControls = preferences.getBoolean("controls.touch_controls", ControlManager.touchControls);

        setScreen(new LoadingScreen(this, batch));
    }

    @Override
    public void resize(int width, int height) {
        batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);

        super.resize(width, height);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(135 / 255f, 206 / 255f, 235 / 255f, 1);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        super.render();
    }

    public NinjaAssetManager getAssetManager() {
        return manager;
    }

    public BitmapFont getFont() {
        return font;
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
    }

    public Skin getSkin() {
        return skin;
    }

    @Override
    public void setScreen(Screen screen) {
        Screen oldScreen = getScreen();

        if (oldScreen != null) {
            oldScreen.dispose();
        }

        super.setScreen(screen);
    }

    @Override
    public void dispose() {
        manager.dispose();

        font.dispose();

        batch.dispose();
    }

    public PlatformFeatures getPlatformFeatures() {
        return platformFeatures;
    }

    public Preferences getPreferences() {
        return preferences;
    }
}
