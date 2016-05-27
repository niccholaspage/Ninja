package com.nicholasnassar.ninja;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.ObjectSet;
import com.nicholasnassar.ninja.components.PhysicsComponent;

public class Grid {
    private final ComponentMapper<PhysicsComponent> physicsMapper;

    private final ObjectSet<Entity> retrieveSet;

    private ObjectSet<Entity>[][] grid;

    private int rows;

    private int cols;

    private static final int CELL_SIZE = 8;

    public Grid(Level level) {
        physicsMapper = ComponentMapper.getFor(PhysicsComponent.class);

        retrieveSet = new ObjectSet<Entity>();

        resize(level);
    }

    public void clear() {
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                grid[x][y].clear();
            }
        }
    }

    public void resize(Level level) {
        rows = (level.getHeight() + CELL_SIZE - 1) / CELL_SIZE;
        cols = (level.getWidth() + CELL_SIZE - 1) / CELL_SIZE;

        grid = new ObjectSet[cols][rows];

        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                grid[x][y] = new ObjectSet<Entity>();
            }
        }
    }

    public void addEntity(Entity entity) {
        PhysicsComponent physics = physicsMapper.get(entity);

        float entityX = physics.getPosition().x;

        float entityY = physics.getPosition().y;

        int leftX = (int) Math.max(0, entityX / CELL_SIZE);

        int rightX = (int) Math.min(cols - 1, (entityX + physics.getWidth()) / CELL_SIZE);

        int bottomY = (int) Math.max(0, entityY / CELL_SIZE);

        int topY = (int) Math.min(rows - 1, (entityY + physics.getHeight()) / CELL_SIZE);

        for (int x = leftX; x <= rightX; x++) {
            for (int y = bottomY; y <= topY; y++) {
                grid[x][y].add(entity);
            }
        }
    }

    public ObjectSet<Entity> retrieve(Entity entity) {
        retrieveSet.clear();

        PhysicsComponent physics = physicsMapper.get(entity);

        float entityX = physics.getPosition().x;

        float entityY = physics.getPosition().y;

        int leftX = (int) Math.max(0, entityX / CELL_SIZE);

        int rightX = (int) Math.min(cols - 1, (entityX + physics.getWidth()) / CELL_SIZE);

        int bottomY = (int) Math.max(0, entityY / CELL_SIZE);

        int topY = (int) Math.min(rows - 1, (entityY + physics.getHeight()) / CELL_SIZE);

        for (int x = leftX; x <= rightX; x++) {
            for (int y = bottomY; y <= topY; y++) {
                ObjectSet<Entity> cell = grid[x][y];

                for (Entity item : cell) {
                    retrieveSet.add(item);
                }
            }
        }
        return retrieveSet;
    }

    //TODO: Possible get nearest method
}
