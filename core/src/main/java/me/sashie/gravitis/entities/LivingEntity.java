package me.sashie.gravitis.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import me.sashie.gravitis.Player;

public abstract class LivingEntity extends ClientEntity {
    private int health;
    private float speed;

    public abstract Color getColor();
    public abstract void renderShape(ShapeRenderer shapeRenderer);
    public abstract void update(Player player);

    public LivingEntity(String id, Vector2 position, float radius) {
        super(id, position, radius);
    }

    @Override
    public boolean isAlive(Player player) {
        return this.health > 0;
    }

    public void render(ShapeRenderer shapeRenderer, Player player) {
        // Render the object (can be a circle or any other shape)
        if (isAlive(player)) {
            shapeRenderer.setColor(getColor());
            renderShape(shapeRenderer);
        }
    }

    public void onHit(Player player) {
        /*if (player.getSelectedToolType() == ToolType.MINING_LASER) {

        }*/
        this.health -= 1;
    }

}
