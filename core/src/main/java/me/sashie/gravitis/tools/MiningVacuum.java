package me.sashie.gravitis.tools;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import me.sashie.gravitis.entities.BreakableEntity;

public class MiningVacuum implements Tool {
    @Override
    public Vector2 getPosition() {
        return null;
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public boolean checkCollision(BreakableEntity obj) {
        return false;
    }

    @Override
    public void update() {

    }

    @Override
    public void render(ShapeRenderer shapeRenderer) {

    }

    @Override
    public void setMaxRange(float maxDistance) {

    }
}
