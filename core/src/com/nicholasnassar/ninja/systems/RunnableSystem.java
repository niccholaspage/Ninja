package com.nicholasnassar.ninja.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.utils.ObjectFloatMap;

import java.util.Iterator;

public class RunnableSystem extends EntitySystem {
    private final ObjectFloatMap<Runnable> runnables;

    public RunnableSystem() {
        runnables = new ObjectFloatMap<Runnable>();
    }

    public void postRunnable(Runnable runnable, float remainingTime) {
        runnables.put(runnable, remainingTime);
    }

    @Override
    public void update(float deltaTime) {
        Iterator<ObjectFloatMap.Entry<Runnable>> iterator = runnables.iterator();

        while (iterator.hasNext()) {
            ObjectFloatMap.Entry<Runnable> entry = iterator.next();

            float remainingTime = entry.value - deltaTime;

            if (remainingTime <= 0) {
                entry.key.run();

                iterator.remove();
            } else {
                runnables.put(entry.key, remainingTime);
            }
        }
    }
}
