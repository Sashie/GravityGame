package me.sashie.gravitis.entities.pieces;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import me.sashie.gravitis.Player;

import java.util.Random;

public class FilledPolygonPiece extends Piece {

    private Random random = new Random();
    int sides;

    public FilledPolygonPiece(Vector2 position, Color color) {
        super(position, color);
        sides = 3 + random.nextInt(7);
    }

    @Override
    public boolean renderShape(ShapeRenderer shapeRenderer, Player player) {
        Vector2 position = getPosition();

        float[] vertices = new float[sides * 2];
        float angleStep = 360f / sides;

        // Generate vertices with random radius variance
        for (int i = 0; i < sides; i++) {
            float angle = angleStep * i * MathUtils.degreesToRadians;
            vertices[i * 2] = position.x + MathUtils.cos(angle) * getSize();
            vertices[i * 2 + 1] = position.y + MathUtils.sin(angle) * getSize();
        }

        //shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
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
        //shapeRenderer.end();
        return true;
    }

    @Override
    public boolean updateShape(Player player) {
        return false;
    }
}
