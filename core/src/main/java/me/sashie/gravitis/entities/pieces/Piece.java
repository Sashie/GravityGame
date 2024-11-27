package me.sashie.gravitis.entities.pieces;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import me.sashie.gravitis.Player;

public abstract class Piece {
    private Vector2 position;
    private Vector2 velocity;
    private float size;
    private Color color;

    public abstract boolean renderShape(ShapeRenderer shapeRenderer, Player player);
    public abstract boolean updateShape(Player player);

    public Piece(Vector2 position, Color color) {
        this.position = position.cpy();
        this.color = color;
        this.velocity = new Vector2((float) Math.random() - 0.5f, (float) Math.random() - 0.5f).nor().scl(2f); // Random velocity
        this.size = 5 + (float) Math.random() * 10; // Random size
    }

    public void render(ShapeRenderer shapeRenderer, Player player) {
        if (position.dst(player.getPosition()) > 30f) {
            if (!renderShape(shapeRenderer, player)) {
                shapeRenderer.setColor(color);
                shapeRenderer.circle(position.x, position.y, size);
            }
        }
    }

    public void update(Player player) {
        if (!updateShape(player)) {
            velocity.add(player.getPosition().cpy().sub(position).nor().scl(0.1f));
            position.add(velocity); // Move the piece based on velocity
        }
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
