package me.sashie.gravitis.tools;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import me.sashie.gravitis.Player;
import me.sashie.gravitis.entities.Entity;

import java.util.List;

public interface Tool {

    Vector2 getPosition();
    boolean isActive();
    boolean checkCollision(Entity obj, Player player);
    void update(List<Entity> entities, Player player);
    void render(ShapeRenderer shapeRenderer, Player player);
    void setMaxRange(float maxDistance);

}
