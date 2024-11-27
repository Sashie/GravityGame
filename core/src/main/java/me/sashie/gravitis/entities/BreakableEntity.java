package me.sashie.gravitis.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import me.sashie.gravitis.Player;
import me.sashie.gravitis.entities.pieces.Piece;
import me.sashie.gravitis.tools.MiningLaser;

import java.util.ArrayList;
import java.util.List;

public abstract class BreakableEntity {
    private Vector2 position;
    private float radius;
    private List<Piece> pieces;

    public abstract Color getColor();
    public abstract Piece getPiece();
    public abstract void onPickup(Piece piece, Player player);
    public abstract void renderShape(ShapeRenderer shapeRenderer);

    public BreakableEntity(Vector2 position, float radius) {
        this.position = position;
        this.radius = radius;
        this.pieces = new ArrayList<>();
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

    public void render(ShapeRenderer shapeRenderer, Player player) {
        // Render the object (can be a circle or any other shape)
        if (isAlive()) {
            shapeRenderer.setColor(getColor());
            renderShape(shapeRenderer);
        }

        // Render the pieces
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < pieces.size(); i++) {
            Piece piece = pieces.get(i);
            piece.render(shapeRenderer, player);
        }
        shapeRenderer.end();
    }

    public void update(Player player) {
        // Update the pieces (e.g., apply gravity or movement if needed)
        for (int i = 0; i < pieces.size(); i++) {
            Piece piece = pieces.get(i);
            piece.update(player);
            if (piece.getPosition().dst(player.getPosition()) < 30f) {
                onPickup(piece, player);
                pieces.remove(piece);
            }
        }

        //pieces.removeIf(p -> p.getPosition().dst(player.getPosition()) < 30f);
    }

    public void hitByLaser() {
        // Break the object into pieces when hit by a laser
        //pieces.clear(); // Clear previous pieces
        for (int i = 0; i < 2; i++) {
            // Create pieces with random movement directions
            Piece piece = getPiece();
            float size = piece.getSize();
            this.radius -= size * .2f;
            if (this.radius < 0) {
                this.radius = 0;
            }
            pieces.add(piece);
        }
    }

    public boolean isHitByLaser(MiningLaser laser) {
        // Check if the laser hits the object
        return laser.checkCollision(this); // laser.getPosition().dst(position) < radius;
    }

}
