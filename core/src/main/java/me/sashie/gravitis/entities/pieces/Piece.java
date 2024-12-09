package me.sashie.gravitis.entities.pieces;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import me.sashie.gravitis.Player;
import me.sashie.gravitis.entities.ClientEntity;

public abstract class Piece extends ClientEntity {
    private Vector2 velocity;
    private Color color;

    public abstract boolean renderShape(ShapeRenderer shapeRenderer, Player player);
    public abstract boolean updateShape(Player player);

    public Piece(String id, Vector2 position, Color color) {
        super(id, position, 5 + (float) Math.random() * 10);
        this.data.position = position.cpy();
        this.color = color;
        this.velocity = new Vector2((float) Math.random() - 0.5f, (float) Math.random() - 0.5f).nor().scl(2f); // Random velocity
    }

    @Override
    public void render(ShapeRenderer shapeRenderer, Player player) {
        if (isAlive(player)) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            if (!renderShape(shapeRenderer, player)) {
                shapeRenderer.setColor(color);
                shapeRenderer.circle(data.position.x, data.position.y, data.radius);
            }
            shapeRenderer.end();
        }
    }

    @Override
    public boolean isAlive(Player player) {
        return getPosition().dst(player.getPosition()) > 30f;
    }

    public void setPosition(Vector2 position) {
        this.data.position = position;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
