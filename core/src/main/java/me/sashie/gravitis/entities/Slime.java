package me.sashie.gravitis.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import me.sashie.gravitis.Player;

public class Slime extends LivingEntity {

    public Slime(String id, Vector2 position, float radius) {
        super(id, position, radius);
    }

    @Override
    public Color getColor() {
        return Color.FOREST;
    }

    @Override
    public void onDeath(Player player) {

    }

    @Override
    public void renderShape(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(getPosition().x, getPosition().y, getRadius());
        shapeRenderer.end();
    }

    @Override
    public void update(Player player) {

    }

}
