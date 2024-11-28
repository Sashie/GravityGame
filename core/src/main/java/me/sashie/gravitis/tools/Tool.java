package me.sashie.gravitis.tools;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import me.sashie.gravitis.Player;
import me.sashie.gravitis.entities.BreakableEntity;
import me.sashie.gravitis.entities.Entity;

public interface Tool {

    Vector2 getPosition();
    boolean isActive();
    boolean checkCollision(Entity obj, Player player);
    void update(Entity obj, Player player);
    void render(ShapeRenderer shapeRenderer, Player player);
    void setMaxRange(float maxDistance);

}
