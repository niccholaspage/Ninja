package com.nicholasnassar.ninja.components;

import com.badlogic.ashley.core.Component;

public class HealthComponent implements Component {
    private int health;

    private int maxHealth;

    private boolean dead;

    public HealthComponent(int maxHealth) {
        this.maxHealth = maxHealth;

        health = maxHealth;

        dead = false;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        if (health > maxHealth) {
            health = maxHealth;
        } else if (health <= 0) {
            health = 0;

            dead = true;
        }

        this.health = health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void damage(int damage) {
        setHealth(getHealth() - damage);
    }

    public boolean isDead() {
        return dead;
    }
}
