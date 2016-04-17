package com.nicholasnassar.ninja.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nicholasnassar.ninja.NinjaAssetManager;
import com.nicholasnassar.ninja.NinjaGame;

public class LoadingScreen extends NinjaScreen {
    private final NinjaGame game;

    public LoadingScreen(NinjaGame game, SpriteBatch batch) {
        super(batch);

        this.game = game;
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        batch.begin();

        if (game.getAssetManager().update()) {
            NinjaAssetManager manager = game.getAssetManager();

            game.setSkin(manager.getSkin());

            manager.finishedLoading();

            game.setScreen(new MainMenuScreen(game, batch));
        } else {
            game.getFont().draw(batch, "Loading...", 0, 0);
        }

        batch.end();
    }
}
