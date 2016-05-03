package com.nicholasnassar.ninja;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.*;
import com.nicholasnassar.ninja.components.StateComponent;

public class NinjaAssetManager {
    private final AssetManager manager;

    private final OrderedMap<String, Array<TextureRegion>> blocks;
    private final OrderedMap<String, IntMap<Array<Animation>>> creatureAnimations;
    private final ObjectMap<String, TextureRegion> entities;
    private final ObjectMap<String, Music> music;

    public NinjaAssetManager() {
        manager = new AssetManager();

        blocks = new OrderedMap<String, Array<TextureRegion>>();

        creatureAnimations = new OrderedMap<String, IntMap<Array<Animation>>>();

        entities = new ObjectMap<String, TextureRegion>();

        music = new ObjectMap<String, Music>();

        manager.load("ui/uiskin.json", Skin.class);
        manager.load("ui/buttons/foreground.png", Texture.class);
        manager.load("ui/buttons/background.png", Texture.class);
        manager.load("ui/buttons/delete.png", Texture.class);

        manager.load("pack.atlas", TextureAtlas.class);

        music.put("main_menu", null);

        for (String key : music.keys()) {
            manager.load("sounds/music/" + key + ".mp3", Music.class);
        }
    }

    public Skin getSkin() {
        return manager.get("ui/uiskin.json", Skin.class);
    }

    public TextureRegion getEntity(String name) {
        return entities.get(name);
    }

    public void finishedLoading() {
        TextureAtlas atlas = manager.get("pack.atlas");

        ObjectMap<String, Sprite> spriteAnimations = new ObjectMap<String, Sprite>();

        for (TextureAtlas.AtlasRegion region : atlas.getRegions()) {
            String regionName = region.name;

            String name = regionName.substring(regionName.indexOf("/") + 1);

            Sprite sprite = atlas.createSprite(regionName);

            if (regionName.startsWith("animations")) {
                spriteAnimations.put(name, sprite);
            } else if (regionName.startsWith("entities")) {
                entities.put(name, sprite);
            }
        }

        addBlocks( atlas);

        IntMap<Array<Animation>> animations = new IntMap<Array<Animation>>();

        Animation idle = new Animation(1 / 4f, spriteAnimations.get("player_idle").split(13, 27)[0]);

        Animation idle2 = new Animation(1 / 10f, spriteAnimations.get("player_idle2").split(13, 40)[0]);

        Animation idle3 = new Animation(1 / 10f, spriteAnimations.get("player_idle3").split(13, 40)[0]);

        Array<Animation> idleAnimations = new Array<Animation>();

        idleAnimations.addAll(idle, idle2, idle3);

        animations.put(StateComponent.STATE_IDLE, idleAnimations);

        Animation walk = new Animation(1 / 15f, spriteAnimations.get("player_walk").split(13, 27)[0]);

        walk.setPlayMode(Animation.PlayMode.LOOP);

        animations.put(StateComponent.STATE_WALKING, Array.with(walk));

        animations.put(StateComponent.STATE_IN_AIR, Array.with(new Animation(1 / 10f, spriteAnimations.get("player_in_air").split(17, 27)[0])));

        Animation roll = new Animation(1 / 25f, spriteAnimations.get("player_roll").split(22, 16)[0]);

        animations.put(StateComponent.STATE_GROUND_ROLL, Array.with(roll));

        Animation wallSlide = new Animation(1, spriteAnimations.get("player_wall_slide"));

        animations.put(StateComponent.STATE_WALL_SLIDE, Array.with(wallSlide));

        creatureAnimations.put("player", animations);

        animations = new IntMap<Array<Animation>>();

        idle = new Animation(1 / 4f, spriteAnimations.get("soldier_idle").split(18, 32)[0]);

        idle.setPlayMode(Animation.PlayMode.LOOP);

        animations.put(StateComponent.STATE_IDLE, Array.with(idle));
        animations.put(StateComponent.STATE_IN_AIR, Array.with(idle));
        animations.put(StateComponent.STATE_WALL_SLIDE, Array.with(idle));

        walk = new Animation(1 / 15f, spriteAnimations.get("soldier_walk").split(18, 32)[0]);

        walk.setPlayMode(Animation.PlayMode.LOOP);
        animations.put(StateComponent.STATE_WALKING, Array.with(walk));

        creatureAnimations.put("soldier", animations);

        for (String key : music.keys()) {
            Music music = manager.get("sounds/music/" + key + ".mp3");

            music.setLooping(true);

            this.music.put(key, music);
        }
    }

    public void addBlocks(TextureAtlas atlas) {
        JsonValue json = new JsonReader().parse(Gdx.files.internal("blocks.json"));

        for (JsonValue block : json.get("blocks").iterator()) {
            String sprite = block.getString("sprite");

            blocks.put(sprite, Array.with(atlas.createSprite("blocks/" + sprite).split(16, 16)[0]));
        }
    }

    public TextureRegion getUIElement(String key) {
        return new TextureRegion((Texture) manager.get("ui/" + key));
    }

    public TextureRegion getBlock(String key) {
        return getBlock(key, (int) (Math.random() * blocks.get(key).size));

    }

    private TextureRegion getBlock(String key, int variation) {
        return blocks.get(key).get(variation);
    }

    public OrderedMap<String, Array<TextureRegion>> getBlocks() {
        return blocks;
    }

    public Music getMusic(String key) {
        return music.get(key);
    }

    public OrderedMap<String, IntMap<Array<Animation>>> getCreatureAnimations() {
        return creatureAnimations;
    }

    public IntMap<Array<Animation>> getAnimationsFor(String creature) {
        return creatureAnimations.get(creature);
    }

    public boolean update() {
        return manager.update();
    }

    public void dispose() {
        manager.dispose();
    }
}
