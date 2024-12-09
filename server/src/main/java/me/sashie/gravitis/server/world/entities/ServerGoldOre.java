package me.sashie.gravitis.server.world.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import me.sashie.gravitis.entities.EntityType;
import me.sashie.gravitis.server.world.ServerWorld;
import me.sashie.gravitis.server.world.entities.pieces.ServerCirclePiece;
import me.sashie.gravitis.server.world.entities.pieces.ServerPiece;

public class ServerGoldOre extends ServerBreakableEntity {

    public ServerGoldOre(ServerWorld world, Vector2 position, float radius) {
        super(world, position, radius);
        data.type = EntityType.GOLD_ORE;
    }

    @Override
    public ServerPiece getPiece() {
        return new ServerCirclePiece(world, getPosition().cpy().add((float) Math.random() * getRadius(), (float) Math.random() * getRadius()), Color.GOLD);
    }

}
