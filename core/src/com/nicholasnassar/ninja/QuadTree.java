package com.nicholasnassar.ninja;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.nicholasnassar.ninja.components.PhysicsComponent;

public class QuadTree {
    private final static int MAX_ENT = 10;

    private final static int MAX_LEVELS = 5;

    private int level;

    private Array<Entity> collideEntities;

    private Rectangle bounds;

    private QuadTree[] nodes;

    private final static ComponentMapper<PhysicsComponent> PHYSICS_MAPPER = ComponentMapper.getFor(PhysicsComponent.class);

    public QuadTree(int pLevel, Rectangle pBounds) {
        level = pLevel;
        collideEntities = new Array<Entity>();
        bounds = pBounds;
        nodes = new QuadTree[4];
    }

    public void clear() {
        collideEntities.clear();

        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null) {
                nodes[i].clear();
                nodes[i] = null;
            }
        }
    }

    private void split() {
        int subWidth = (int) (bounds.getWidth() / 2);
        int subHeight = (int) (bounds.getHeight() / 2);
        int x = (int) bounds.getX();
        int y = (int) bounds.getY();

        nodes[0] = new QuadTree(level + 1, new Rectangle(x + subWidth, y, subWidth, subHeight));
        nodes[1] = new QuadTree(level + 1, new Rectangle(x, y, subWidth, subHeight));
        nodes[2] = new QuadTree(level + 1, new Rectangle(x, y + subHeight, subWidth, subHeight));
        nodes[3] = new QuadTree(level + 1, new Rectangle(x + subWidth, y + subHeight, subWidth, subHeight));
    }

    /*
     * Determine which node the object belongs to. -1 means
     * object cannot completely fit within a child node and is part
     * of the parent node but this time takes a rectangle as an
     * argument
     */
    private int getIndex(PhysicsComponent physics) {
        int index = -1;

        float verticalMidpoint = physics.getPosition().x + (bounds.getWidth() / 2);

        float horizontalMidpoint = bounds.getY() + (bounds.getHeight() / 2);

        boolean topQuadrant = (physics.getPosition().y > horizontalMidpoint);

        boolean bottomQuadrant = (physics.getPosition().y < horizontalMidpoint && physics.getPosition().y + physics.getHeight() < horizontalMidpoint);

        if (physics.getPosition().x > verticalMidpoint) {
            if (topQuadrant) {
                index = 1;
            } else if (bottomQuadrant) {
                index = 3;
            }
        }

        if (physics.getPosition().x < verticalMidpoint && physics.getPosition().x + physics.getWidth() < verticalMidpoint) {
            if (topQuadrant) {
                index = 0;
            } else if (bottomQuadrant) {
                index = 2;
            }
        }

        return index;
    }


    public void insert(Entity entity) {
        PhysicsComponent physics = PHYSICS_MAPPER.get(entity);

        if (nodes[0] != null) {
            int index = getIndex(physics);

            if (index != -1) {
                nodes[index].insert(entity);

                return;
            }
        }

        collideEntities.add(entity);

        if (collideEntities.size > MAX_ENT && level < MAX_LEVELS) {
            if (nodes[0] == null) {
                split();
            }

            int i = 0;

            while (i < collideEntities.size) {
                int index = getIndex(PHYSICS_MAPPER.get(collideEntities.get(i)));
                if (index != -1) {
                    nodes[index].insert(collideEntities.removeIndex(i));
                } else {
                    i++;
                }
            }
        }
    }

    public Array<Entity> retrieve(Array<Entity> returnEntities, PhysicsComponent physics) {
        int index = getIndex(physics);

        if (index != -1 && nodes[0] != null) {
            nodes[index].retrieve(returnEntities, physics);
        }

        returnEntities.addAll(collideEntities);

        return returnEntities;

    }
};