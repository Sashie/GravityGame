package me.sashie.gravitis.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import me.sashie.gravitis.Player;

public abstract class ClientEntity extends Entity {

    public abstract void render(ShapeRenderer shapeRenderer, Player player);

    public ClientEntity(String id, Vector2 position, float radius) {
        super(id, position, radius);
    }

}

