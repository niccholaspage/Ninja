package com.nicholasnassar.ninja.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.nicholasnassar.ninja.systems.LevelEditorSystem;

public class SelectButton extends ImageButton {
    public SelectButton(final LevelEditorSystem system, TextureRegion region, final String key, final Table... tables) {
        super(new TextureRegionDrawable(region));

        addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                for (Table table : tables) {
                    for (Actor select : table.getChildren()) {
                        if (select instanceof SelectButton) {
                            select.setColor(Color.WHITE);
                        }
                    }
                }

                system.setSelectedBuild(key);

                getColor().a = 0.5f;
            }
        });
    }
}
