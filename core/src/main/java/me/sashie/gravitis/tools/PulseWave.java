package me.sashie.gravitis.tools;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import me.sashie.gravitis.Player;
import me.sashie.gravitis.entities.BreakableEntity;
import me.sashie.gravitis.entities.Entity;

public class PulseWave implements Tool {
    private Vector2 position;      // Center of the pulse wave (player's position)
    private float radius;          // Current radius of the wave
    private float maxRadius;       // Maximum expansion radius
    private float expansionSpeed = 0.5f;  // Speed of radius expansion
    private boolean active = true; // Whether the wave is active

    public PulseWave(Vector2 startPosition, float maxRadius) {
        this.position = startPosition.cpy();
        this.radius = 0f; // Start from zero and grow outward
        this.maxRadius = maxRadius;
    }

    @Override
    public Vector2 getPosition() {
        return this.position;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public void update(Entity obj, Player player) {
        if (!active) return;

        // Expand the wave
        radius += expansionSpeed;

        // Deactivate when reaching max radius
        if (radius >= maxRadius) {
            active = false;
        }
    }

    public boolean checkCollision(Entity obj, Player player) {
        if (!obj.isAlive() || !active) return false;

        // Check if the object's position is within the wave's radius
        float distanceToObject = obj.getPosition().dst(position);
        return distanceToObject <= radius + obj.getRadius();
    }

    public void render(ShapeRenderer shapeRenderer, Player player) {
        if (!active) return;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        // Draw the wave (e.g., as a circle)
        shapeRenderer.setColor(0.5f, 0.8f, 1f, 1f); // Light blue
        shapeRenderer.circle(position.x, position.y, radius);
        shapeRenderer.end();
    }

    @Override
    public void setMaxRange(float maxDistance) {
        this.maxRadius = maxDistance;
    }
}
