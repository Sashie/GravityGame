package me.sashie.gravitis.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import me.sashie.gravitis.ClientPlayer;
import me.sashie.gravitis.Player;
import me.sashie.gravitis.entities.pieces.Piece;
import me.sashie.gravitis.network.packets.PacketInEntityHit;

import java.util.ArrayList;
import java.util.List;

public abstract class BreakableEntity extends ClientEntity {

    public abstract Color getColor();
    public abstract void onPickup(Piece piece, Player player);
    public abstract void renderShape(ShapeRenderer shapeRenderer);

    public BreakableEntity(String id, Vector2 position, float radius) {
        super(id, position, radius);
    }

    @Override
    public boolean isAlive(Player player) {
        return this.getRadius() > 10f;
    }

    @Override
    public void onDeath(Player player) {}

    public void render(ShapeRenderer shapeRenderer, Player player) {
        // Render the object (can be a circle or any other shape)
        if (isAlive(player)) {
            shapeRenderer.setColor(getColor());
            renderShape(shapeRenderer);
        }

    }

    public void onHit(Player player) {

    }

}
