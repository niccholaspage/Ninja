package com.nicholasnassar.ninja.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.nicholasnassar.ninja.Level;
import com.nicholasnassar.ninja.NinjaGame;

public class MainMenuScreen extends NinjaScreen {
    private final Stage stage;

    private final Label label;

    private final TextButton button;

    private final TextButton levelEditor;

    private final Music mainMenuMusic;

    public MainMenuScreen(final NinjaGame game, final SpriteBatch batch) {
        super(batch);

        stage = new Stage(new ScreenViewport());

        Gdx.input.setInputProcessor(stage);

        stage.addActor(label = new Label("nNINJA", game.getSkin()));

        stage.addActor(button = new TextButton("Play Game", game.getSkin()));

        final Level level = new Level(100, 20);

        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.input.setInputProcessor(null);

                game.setScreen(new GameScreen(game, batch, level, false));
            }
        });

        stage.addActor(levelEditor = new TextButton("Level Editor", game.getSkin()));

        levelEditor.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.input.setInputProcessor(null);

                game.setScreen(new GameScreen(game, batch, level, true));
            }
        });

        mainMenuMusic = game.getAssetManager().getMusic("main_menu");

        mainMenuMusic.play();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);

        label.setPosition(width / 2 - label.getWidth() / 2, height - 200);

        button.setPosition(width / 2 - button.getWidth() - 5, height - 300);

        levelEditor.setPosition(width / 2 + 5, height - 300);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();

        mainMenuMusic.stop();
    }
}
