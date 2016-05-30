package com.nicholasnassar.ninja.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.nicholasnassar.ninja.Control;
import com.nicholasnassar.ninja.ControlManager;
import com.nicholasnassar.ninja.NinjaGame;

public class ControlsScreen extends NinjaScreen {
    private final NinjaGame game;

    private final Stage stage;

    public ControlsScreen(NinjaGame game, SpriteBatch batch) {
        super(batch);

        this.game = game;

        stage = new Stage(new ExtendViewport(792, 445.5f));

        Gdx.input.setInputProcessor(stage);

        Table table = new Table();

        table.align(Align.center);

        table.setFillParent(true);

        TextButton back = new TextButton("Back", game.getSkin());

        back.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                goBack();
            }
        });

        for (Control control : ControlManager.controls) {
            table.add(new Label(control.getName() + ": ", game.getSkin())).padBottom(5);
            table.add(new TextButton(Input.Keys.toString(control.getDefaultKey()), game.getSkin())).padBottom(5);
            table.row();
        }

        table.add(back).colspan(2);

        stage.addActor(table);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render(float deltaTime) {
        if (ControlManager.isJustPressed(back)) {
            goBack();
        }

        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    private void goBack() {
        game.setScreen(new OptionsScreen(game, batch));
    }
}
