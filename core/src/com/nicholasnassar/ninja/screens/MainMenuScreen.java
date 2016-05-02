package com.nicholasnassar.ninja.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.nicholasnassar.ninja.NinjaGame;

public class MainMenuScreen extends NinjaScreen {
    private final Stage stage;

    private final Music mainMenuMusic;

    public MainMenuScreen(final NinjaGame game, final SpriteBatch batch) {
        super(batch);

        //stage = new Stage(new ScreenViewport());
        stage = new Stage(new ExtendViewport(792, 445.5f));

        Gdx.input.setInputProcessor(stage);

        Table mainMenuTable = new Table();

        mainMenuTable.align(Align.center);

        mainMenuTable.setFillParent(true);

        TextButton playGame = new TextButton("Play Game", game.getSkin());

        TextButton levelEditor = new TextButton("Level Editor", game.getSkin());

        mainMenuTable.add(new Label("nNINJA", game.getSkin())).colspan(2);
        mainMenuTable.row();
        mainMenuTable.add(playGame).padRight(5);
        mainMenuTable.add(levelEditor);

        stage.addActor(mainMenuTable);

        playGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                startGame(game, false);
            }
        });

        levelEditor.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                startGame(game, true);
            }
        });

        mainMenuMusic = game.getAssetManager().getMusic("main_menu");

        mainMenuMusic.play();
    }

    private void startGame(NinjaGame game, boolean editing) {
        Gdx.input.setInputProcessor(null);

        game.setScreen(new GameScreen(game, batch, editing));
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
