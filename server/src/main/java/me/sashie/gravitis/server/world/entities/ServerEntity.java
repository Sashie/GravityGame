package me.sashie.gravitis.server.world.entities;

import com.badlogic.gdx.math.Vector2;
import me.sashie.gravitis.entities.Entity;
import me.sashie.gravitis.server.ServerPlayer;
import me.sashie.gravitis.server.world.ServerWorld;

import java.util.Collection;
import java.util.UUID;

public abstract class ServerEntity extends Entity {
    ServerWorld world;

    public abstract void update(Collection<ServerPlayer> players);

    public ServerEntity(ServerWorld world, Vector2 position, float radius) {
        super(UUID.randomUUID().toString(), position, radius);
        this.world = world;
    }

}
