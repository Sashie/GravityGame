package me.sashie.gravitis.tools;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import me.sashie.gravitis.Player;
import me.sashie.gravitis.entities.BreakableEntity;
import me.sashie.gravitis.entities.Entity;
import me.sashie.gravitis.entities.pieces.Piece;

import java.util.Collection;
import java.util.List;


public class MiningVacuum implements Tool {

    private Vector2 position;
    private Vector2 direction;
    private Vector2 startPosition; // Store the initial position of the laser
    private boolean active = true; // Whether the wave is active
    public float vacuumRadius = 75f; // Radius within which objects are pulled
    public float vacuumStrength = 10f; // Strength of the pull effect
    public float time = 0;
    public float maxTime = 1;

    public MiningVacuum(Vector2 startPosition, Vector2 direction) {
        this.position = startPosition.cpy();
        this.startPosition = startPosition.cpy(); // Save the starting position
        this.direction = direction.nor(); // Normalize direction to ensure consistent movement
    }

    public float getVacuumRadius() {
        return vacuumRadius;
    }

    public void setVacuumRadius(float vacuumRadius) {
        this.vacuumRadius = vacuumRadius;
    }

    public float getVacuumStrength() {
        return vacuumStrength;
    }

    public void setVacuumStrength(float vacuumStrength) {
        this.vacuumStrength = vacuumStrength;
    }

    @Override
    public Vector2 getPosition() {
        return this.position;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public boolean checkCollision(Entity obj, Player player) {
        return false;
    }

    @Override
    public void update(List<Entity> entities, Player player) {
        for (Entity entity : entities) {
            if (!(entity instanceof Piece) || !active) return;
            Piece piece = (Piece) entity;
            float distance = piece.getPosition().dst(player.getPosition());
            if (distance < vacuumRadius) {
                Vector2 pullDirection = player.getPosition().cpy().sub(piece.getPosition()).nor();
                piece.getPosition().add(pullDirection.scl(vacuumStrength));
            }

        }

        /*for (List<Entity> entities : chunkEntities) {
            for (Entity entity : entities) {
                if (!(entity instanceof BreakableEntity) || !active) return;
                BreakableEntity breakable = (BreakableEntity) entity;
                for (int i = 0; i < breakable.getPieces().size(); i++) {
                    Piece piece = breakable.getPieces().get(i);
                    float distance = piece.getPosition().dst(player.getPosition());
                    if (distance < vacuumRadius) {
                        Vector2 pullDirection = player.getPosition().cpy().sub(piece.getPosition()).nor();
                        piece.getPosition().add(pullDirection.scl(vacuumStrength));
                    }
                }
            }
        }*/

        time += 0.01f;

        if (time >= maxTime) {
            active = false;
        }
    }

    @Override
    public void render(ShapeRenderer shapeRenderer, Player player) {
        if (!active) return;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.CYAN);
        shapeRenderer.circle(player.getPosition().x, player.getPosition().y, getVacuumRadius());
        shapeRenderer.end();

    }

    @Override
    public void setMaxRange(float maxDistance) {

    }
}
