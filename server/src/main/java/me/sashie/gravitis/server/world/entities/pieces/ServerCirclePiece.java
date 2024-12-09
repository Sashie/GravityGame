package me.sashie.gravitis.server.world.entities.pieces;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import me.sashie.gravitis.Player;
import me.sashie.gravitis.entities.EntityType;
import me.sashie.gravitis.server.world.ServerWorld;

public class ServerCirclePiece extends ServerPiece {

    public ServerCirclePiece(ServerWorld world, Vector2 position, Color color) {
        super(world, position, color);
        data.type = EntityType.PIECE_CIRCLE;
    }

    @Override
    public boolean updateShape(Player player) {
        return false;
    }

    @Override
    public void onHit(Player player) {

    }

    @Override
    public void onDeath(Player player) {

    }

    @Override
    public void onPickup(Player player) {

    }
}
