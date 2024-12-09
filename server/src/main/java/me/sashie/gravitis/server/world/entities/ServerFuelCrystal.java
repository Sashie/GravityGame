package me.sashie.gravitis.server.world.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import me.sashie.gravitis.entities.EntityType;
import me.sashie.gravitis.server.world.ServerWorld;
import me.sashie.gravitis.server.world.entities.pieces.ServerFilledPolygonPiece;
import me.sashie.gravitis.server.world.entities.pieces.ServerPiece;

public class ServerFuelCrystal extends ServerBreakableEntity {

    public ServerFuelCrystal(ServerWorld world, Vector2 position, float radius) {
        super(world, position, radius);
        data.type = EntityType.FUEL_CRYSTAL;
    }

    @Override
    public ServerPiece getPiece() {
        return new ServerFilledPolygonPiece(world, getPosition().cpy().add((float) Math.random() * getRadius(), (float) Math.random() * getRadius()), Color.VIOLET);
    }

}
