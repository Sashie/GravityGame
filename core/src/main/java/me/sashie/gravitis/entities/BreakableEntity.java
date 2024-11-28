package me.sashie.gravitis.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import me.sashie.gravitis.Player;
import me.sashie.gravitis.entities.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public abstract class BreakableEntity extends Entity {
    private List<Piece> pieces;

    public abstract Color getColor();
    public abstract Piece getPiece();
    public abstract void onPickup(Piece piece, Player player);
    public abstract void renderShape(ShapeRenderer shapeRenderer);

    public BreakableEntity(Vector2 position, float radius) {
        super(position, radius);
        this.position = position;
        this.radius = radius;
        this.pieces = new ArrayList<>();
    }

    /*public boolean isAlive() {
        return this.radius > 10f;
    }*/

    public List<Piece> getPieces() {
        return pieces;
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

    public void onHit() {
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

}
