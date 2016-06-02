package com.nicholasnassar.ninja.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.nicholasnassar.ninja.Control;
import com.nicholasnassar.ninja.OptionsManager;
import com.nicholasnassar.ninja.NinjaGame;

import java.util.Map;

public class ControlsScreen extends NinjaScreen implements InputProcessor {
    private final NinjaGame game;

    private final Stage stage;

    private final OrderedMap<TextButton, Control> controlsGroup;

    private boolean recentlyChanged;

    public ControlsScreen(NinjaGame game, SpriteBatch batch) {
        super(batch);

        this.game = game;

        stage = new Stage(new ExtendViewport(792, 445.5f));

        InputMultiplexer multiplexer = new InputMultiplexer(stage, this);

        Gdx.input.setInputProcessor(multiplexer);

        Table table = new Table();

        table.align(Align.center);

        table.setFillParent(true);

        TextButton defaults = new TextButton("Defaults", game.getSkin());

        TextButton back = new TextButton("Back", game.getSkin());

        back.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                backPressed();
            }
        });

        controlsGroup = new OrderedMap<TextButton, Control>();

        defaults.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                uncheckAll();

                for (ObjectMap.Entry<TextButton, Control> entry : controlsGroup.entries()) {
                    int keycode = entry.value.getDefaultKey();

                    entry.value.setKey(keycode);

                    entry.key.setText(Input.Keys.toString(keycode));
                }

                saveControls();
            }
        });

        for (Control control : OptionsManager.controls) {
            table.add(new Label(control.getName() + ": ", game.getSkin())).padBottom(5);

            final TextButton controlButton = new TextButton(Input.Keys.toString(control.getKey()), game.getSkin());

            controlsGroup.put(controlButton, control);

            table.add(controlButton).padBottom(5);

            TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(controlButton.getStyle());

            style.checked = style.down;

            controlButton.setStyle(style);

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

        final CheckBox checkBox = new CheckBox("", game.getSkin());

        checkBox.setChecked(OptionsManager.touchControls);

        checkBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                OptionsManager.touchControls = checkBox.isChecked();

                saveControls();
            }
        });

        table.add(new Label("Touch Controls: ", game.getSkin()));
        table.add(checkBox).padBottom(5).row();

        table.add(defaults);
        table.add(back);

        stage.addActor(table);

        recentlyChanged = false;
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
        if (!recentlyChanged) {
            super.render(deltaTime);
        } else {
            recentlyChanged = false;
        }

        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    protected void backPressed() {
        game.setScreen(new OptionsScreen(game, batch));
    }

    private void saveControls() {
        Preferences preferences = game.getPreferences();

        for (Map.Entry<String, ?> property : preferences.get().entrySet()) {
            if (property.getKey().startsWith("controls.")) {
                preferences.remove(property.getKey());
            }
        }

        for (Control control : OptionsManager.controls) {
            preferences.putInteger("controls." + control.getId(), control.getKey());
        }

        preferences.putBoolean("controls.touch_controls", OptionsManager.touchControls);

        preferences.flush();
    }

    @Override
    public boolean keyDown(int keycode) {
        TextButton checked = getChecked();

        if (checked != null) {
            controlsGroup.get(checked).setKey(keycode);

            checked.setText(Input.Keys.toString(keycode));

            uncheckAll();

            recentlyChanged = true;

            saveControls();
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
