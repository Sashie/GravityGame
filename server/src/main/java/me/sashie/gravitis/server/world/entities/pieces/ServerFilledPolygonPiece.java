package me.sashie.gravitis.server.world.entities.pieces;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import me.sashie.gravitis.Player;
import me.sashie.gravitis.entities.EntityType;
import me.sashie.gravitis.server.world.ServerWorld;

import java.util.Random;

public class ServerFilledPolygonPiece extends ServerPiece {

    private Random random = new Random();
    int sides;

    public ServerFilledPolygonPiece(ServerWorld world, Vector2 position, Color color) {
        super(world, position, color);
        sides = 3 + random.nextInt(7);
        data.type = EntityType.PIECE_POLY;
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
        // TODO
        //player.giveFuel(piece.getRadius() * 0.5f);
    }
}
