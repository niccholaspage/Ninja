package com.nicholasnassar.ninja;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.nicholasnassar.ninja.components.PhysicsComponent;

public class SpatialMap {
    private final static int CELL_SIZE = 4;

    private IntMap<Array<Entity>> entities;

    private int rows;

    public SpatialMap(int width, int height) {
        resize(width, height);
    }

    public void resize(int width, int height) {
        int cols = width / CELL_SIZE;

        this.rows = height / CELL_SIZE;

        entities = new IntMap<Array<Entity>>(cols * rows);
    }

    private Array<Entity> getNode(int cell) {
        Array<Entity> node = entities.get(cell);

        if (node == null) {
            entities.put(cell, node = new Array<Entity>());
        }

        return node;
    }

    public void clear() {
        for (Array<Entity> array : entities.values()) {
            array.clear();
        }
    }

    public void retrieve(ObjectSet<Entity> returnEntities, PhysicsComponent physics) {
        Vector3 position = physics.getPosition();

        float leftX = position.x;

        float rightX = position.x + physics.getWidth();

        float bottomY = position.y;

        float topY = position.y + physics.getHeight();

        for (float x = leftX; x <= rightX; x++) {
            for (float y = bottomY; y <= topY; y++) {
                returnEntities.addAll(getEntitiesAt(x, y));
            }
        }
    }

    private Array<Entity> getEntitiesAt(float x, float y) {
        int cellPosition = getCellPos(x, y);

        return getNode(cellPosition);
    }

    public void addEntity(PhysicsComponent physics, Entity entity) {
        Vector3 position = physics.getPosition();

        float leftX = position.x;

        float rightX = position.x + physics.getWidth();

        float bottomY = position.y;

        float topY = position.y + physics.getHeight();

        for (float x = leftX; x <= rightX; x++) {
            for (float y = bottomY; y <= topY; y++) {
                int cellPosition = getCellPos(x, y);

                getNode(cellPosition).add(entity);
            }
        }
    }

    private int getCellPos(float x, float y) {
        return (int) (x / CELL_SIZE + y / CELL_SIZE * rows);
        //return (int) (Math.floor(x / CELL_SIZE) + Math.floor(y / CELL_SIZE) * rows);
    }
}
