package com.nicholasnassar.ninja.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.nicholasnassar.ninja.Control;
import com.nicholasnassar.ninja.ControlManager;
import com.nicholasnassar.ninja.NinjaGame;

public class ControlsScreen extends NinjaScreen implements InputProcessor {
    private final NinjaGame game;

    private final Stage stage;

    private final OrderedMap<TextButton, Control> controlsGroup;

    public ControlsScreen(NinjaGame game, SpriteBatch batch) {
        super(batch);

        this.game = game;

        stage = new Stage(new ExtendViewport(792, 445.5f));

        InputMultiplexer multiplexer = new InputMultiplexer(stage, this);

        Gdx.input.setInputProcessor(multiplexer);

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

        controlsGroup = new OrderedMap<TextButton, Control>();

        for (Control control : ControlManager.controls) {
            table.add(new Label(control.getName() + ": ", game.getSkin())).padBottom(5);

            final TextButton controlButton = new TextButton(Input.Keys.toString(control.getKey()), game.getSkin());

            controlsGroup.put(controlButton, control);

            table.add(controlButton).padBottom(5);

            controlButton.getStyle().checked = controlButton.getStyle().down;

            controlButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    uncheckAll();

                    controlButton.setChecked(true);
                }
            });

            table.row();
        }

        table.add(new Label("Right click to cancel", game.getSkin())).padBottom(5).colspan(2).row();

        table.add(back).colspan(2);

        stage.addActor(table);
    }

    private void uncheckAll() {
        for (TextButton button : controlsGroup.keys()) {
            button.setChecked(false);
        }
    }

    private TextButton getChecked() {
        for (TextButton button : controlsGroup.keys()) {
            if (button.isChecked()) {
                return button;
            }
        }

        return null;
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

    @Override
    public boolean keyDown(int keycode) {
        TextButton checked = getChecked();

        if (checked != null) {
            controlsGroup.get(checked).setKey(keycode);

            checked.setText(Input.Keys.toString(keycode));

            uncheckAll();
        }

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.RIGHT) {
            uncheckAll();
        }

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        return true;
    }
}
