package me.sashie.gravitis;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.List;

public class BreakableObject {
    private Vector2 position;
    private float radius;
    private List<Piece> pieces;

    public BreakableObject(Vector2 position, float radius) {
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

    public boolean isDestroyed() {
        return this.radius > 10f;
    }

    public void render(ShapeRenderer shapeRenderer, Player player) {
        // Render the object (can be a circle or any other shape)
        if (isDestroyed()) {
            shapeRenderer.setColor(Color.GRAY);
            shapeRenderer.circle(position.x, position.y, radius);
        }

        // Render the pieces
        for (Piece piece : pieces) {
            piece.render(shapeRenderer, player);
        }
    }

    public void update(Player player) {
        // Update the pieces (e.g., apply gravity or movement if needed)
        /*for (Piece piece : pieces) {
            piece.update(player);
        }*/
        for (int i = 0; i < pieces.size(); i++) {
            pieces.get(i).update(player);
        }
        pieces.removeIf(p -> p.position.dst(player.getPosition()) < 30f);
    }

    public void hitByLaser() {
        // Break the object into pieces when hit by a laser
        //pieces.clear(); // Clear previous pieces
        for (int i = 0; i < 1; i++) {
            // Create pieces with random movement directions
            Piece piece = new Piece(position.cpy().add((float) Math.random() * radius, (float) Math.random() * radius));
            float size = piece.size;
            this.radius -= size * .2f;
            if (this.radius < 0) {
                this.radius = 0;
            }
            pieces.add(piece);
        }
    }

    public boolean isHitByLaser(Laser laser) {
        // Check if the laser hits the object
        return laser.checkCollision(this); // laser.getPosition().dst(position) < radius;
    }

    private class Piece {
        private Vector2 position;
        private Vector2 velocity;
        private float size;

        public Piece(Vector2 position) {
            this.position = position;
            this.velocity = new Vector2((float) Math.random() - 0.5f, (float) Math.random() - 0.5f).nor().scl(2f); // Random velocity
            this.size = 5 + (float) Math.random() * 5; // Random size
        }

        public void render(ShapeRenderer shapeRenderer, Player player) {
            if (position.dst(player.getPosition()) > 30f) {
                shapeRenderer.setColor(Color.ORANGE);
                shapeRenderer.circle(position.x, position.y, size);
            }
        }

        public void update(Player player) {
            if (position.dst(player.getPosition()) > 30f) {
                velocity.add(player.getPosition().cpy().sub(position).nor().scl(0.1f));
                position.add(velocity); // Move the piece based on velocity
            }
        }
    }
}
