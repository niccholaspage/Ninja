package com.nicholasnassar.ninja.components;

public class HealthComponent {
    private int health;

    private int maxHealth;

    public HealthComponent(int maxHealth) {
        this.maxHealth = maxHealth;

        health = maxHealth;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        if (health > maxHealth) {
            health = maxHealth;
        } else if (health < 0) {
            health = 0;
        }

        this.health = health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }
}
