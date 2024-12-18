package me.sashie.gravitis.tools;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import me.sashie.gravitis.Player;
import me.sashie.gravitis.entities.Entity;

import java.util.Collection;
import java.util.List;

public class MiningLaser implements Tool {
    private Vector2 position;
    private Vector2 direction;
    private Vector2 startPosition; // Store the initial position of the laser
    private float speed = 1.1f;
    private float length = 20f;
    private float lineWidth = 5f; // Line width for the laser beam
    private float maxDistance = 80f; // Maximum distance the laser can travel
    private boolean active = true; // Determines if the laser is still active

    public MiningLaser(Vector2 startPosition, Vector2 direction) {
        this.position = startPosition.cpy();
        this.startPosition = startPosition.cpy(); // Save the starting position
        this.direction = direction.nor(); // Normalize direction to ensure consistent movement

    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public boolean checkCollision(Entity obj, Player player) {
        if (!obj.isAlive(player) || !active) return false;

        // Line segment endpoints
        Vector2 laserStart = position.cpy();
        Vector2 laserEnd = position.cpy().add(direction.cpy().scl(length));

        // Circle collision (assuming BreakableObject is circular)
        return intersectsCircle(laserStart, laserEnd, obj.getPosition(), obj.getRadius());
    }

    private boolean intersectsCircle(Vector2 start, Vector2 end, Vector2 circleCenter, float circleRadius) {
        Vector2 startToCenter = circleCenter.cpy().sub(start);
        Vector2 laserVector = end.cpy().sub(start);
        float laserLengthSquared = laserVector.len2();

        float projection = startToCenter.dot(laserVector) / laserLengthSquared;
        projection = Math.max(0, Math.min(1, projection)); // Clamp projection between 0 and 1

        // Closest point on the laser to the circle
        Vector2 closestPoint = start.cpy().add(laserVector.scl(projection));

        // Check distance from the closest point to the circle's center
        return closestPoint.dst2(circleCenter) <= circleRadius * circleRadius;
    }

    @Override
    public void update(List<Entity> entities, Player player) {
        if (!active) return; // Skip updating if the laser is inactive

        // Move the laser in the direction with the defined speed
        position.add(direction.scl(speed));

        // Check if the laser has traveled its maximum distance
        if (position.dst(startPosition) > maxDistance) {
            active = false; // Mark the laser as inactive
        }
    }

    @Override
    public void render(ShapeRenderer shapeRenderer, Player player) {
        if (!active) return; // Skip rendering if the laser is inactive
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rectLine(position.x, position.y, position.x + direction.x * length, position.y + direction.y * length, lineWidth); // Set the line width to make the laser thicker
        shapeRenderer.end();
    }

    @Override
    public void setMaxRange(float maxDistance) {
        this.maxDistance = maxDistance;
    }
}

