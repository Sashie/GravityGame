package me.sashie.gravitis.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import me.sashie.gravitis.Player;

public abstract class Entity {

    protected Vector2 position;
    protected float radius;

    public Entity(Vector2 position, float radius) {
        this.position = position;
        this.radius = radius;
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getRadius() {
        return radius;
    }

    public boolean isAlive() {
        return this.radius > 10f;
    }

    public abstract void render(ShapeRenderer shapeRenderer, Player player);
    public abstract void update(Player player);
    public abstract void onHit();
}

