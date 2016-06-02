package com.nicholasnassar.ninja.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.nicholasnassar.ninja.NinjaGame;
import com.nicholasnassar.ninja.OptionsManager;

public class OptionsScreen extends NinjaScreen {
    private final NinjaGame game;

    private final Stage stage;

    private final Slider musicVolume;

    public OptionsScreen(final NinjaGame game, final SpriteBatch batch) {
        super(batch);

        this.game = game;

        stage = new Stage(new ExtendViewport(792, 445.5f));

        Gdx.input.setInputProcessor(stage);

        Table table = new Table();

        table.align(Align.center);

        table.setFillParent(true);

        musicVolume = new Slider(0, 1, 0.01f, false, game.getSkin());

        musicVolume.setValue(OptionsManager.musicVolume);

        Slider soundVolume = new Slider(0, 1, 0.01f, false, game.getSkin());

        //TODO: Actual loading
        soundVolume.setValue(100);

        TextButton controls = new TextButton("Controls", game.getSkin());

        controls.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new ControlsScreen(game, batch));
            }
        });

        TextButton back = new TextButton("Back", game.getSkin());

        back.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                backPressed();
            }
        });

        table.add(new Label("Music Volume: ", game.getSkin()));
        table.add(musicVolume).row();
        table.add(new Label("Sound Volume: ", game.getSkin()));
        table.add(soundVolume).row();
        table.add(controls).colspan(2).padBottom(5).row();
        table.add(back).colspan(2);

        stage.addActor(table);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render(float deltaTime) {
        super.render(deltaTime);

        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    protected void backPressed() {
        save();

        game.setScreen(new MainMenuScreen(game, batch));
    }

    public void save() {
        Preferences preferences = game.getPreferences();

        OptionsManager.musicVolume = musicVolume.getValue();

        preferences.putFloat("volumes.music", OptionsManager.musicVolume);

        preferences.flush();
    }
}
