package me.sashie.gravitis.tools;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import me.sashie.gravitis.entities.BreakableEntity;

public interface Tool {

    Vector2 getPosition();
    boolean isActive();
    boolean checkCollision(BreakableEntity obj);
    void update();
    void render(ShapeRenderer shapeRenderer);
    void setMaxRange(float maxDistance);

}
