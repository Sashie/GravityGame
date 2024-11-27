package me.sashie.gravitis.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import me.sashie.gravitis.Player;
import me.sashie.gravitis.entities.pieces.FilledPolygonPiece;
import me.sashie.gravitis.entities.pieces.Piece;

import java.util.Random;

public class FuelCrystal extends BreakableEntity {

    private float[] vertices;
    private Random random = new Random();
    int sides;

    public FuelCrystal(Vector2 position, float radius) {
        super(position, radius);
        sides = 3 + random.nextInt(7);
    }

    @Override
    public Color getColor() {
        return Color.MAROON;
    }

    @Override
    public Piece getPiece() {
        return new FilledPolygonPiece(getPosition().cpy().add((float) Math.random() * getRadius(), (float) Math.random() * getRadius()), Color.VIOLET);
    }

    @Override
    public void onPickup(Piece piece, Player player) {
        player.giveFuel(piece.getSize() * 0.5f);
    }

    @Override
    public void renderShape(ShapeRenderer shapeRenderer) {
        Vector2 position = getPosition();

        float[] vertices = new float[sides * 2];
        float angleStep = 360f / sides;

        // Generate vertices with random radius variance
        for (int i = 0; i < sides; i++) {
            float angle = angleStep * i * MathUtils.degreesToRadians;
            vertices[i * 2] = position.x + MathUtils.cos(angle) * getRadius();
            vertices[i * 2 + 1] = position.y + MathUtils.sin(angle) * getRadius();
        }

        // Render the crystal as a filled polygon
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(getColor());

        // Draw triangles to form the filled polygon
        for (int i = 0; i < sides; i++) {
            int nextIndex = (i + 1) % sides;
            shapeRenderer.triangle(
                position.x, position.y, // Center point
                vertices[i * 2], vertices[i * 2 + 1], // Current vertex
                vertices[nextIndex * 2], vertices[nextIndex * 2 + 1] // Next vertex
            );
        }

        shapeRenderer.end();
    }

}
