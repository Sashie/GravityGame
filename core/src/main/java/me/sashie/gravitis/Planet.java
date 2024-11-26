package me.sashie.gravitis;

import com.badlogic.gdx.math.Vector2;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Planet {
    private final Vector2 position;
    private final float radius;

    public Planet(Vector2 position, float radius) {
        this.position = position;
        this.radius = radius;
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getRadius() {
        return radius;
    }

    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.CYAN);
        shapeRenderer.circle(position.x, position.y, radius);
    }
}
