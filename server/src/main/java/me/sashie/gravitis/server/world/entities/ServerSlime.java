package me.sashie.gravitis.server.world.entities;

import com.badlogic.gdx.math.Vector2;
import me.sashie.gravitis.Player;
import me.sashie.gravitis.entities.EntityType;
import me.sashie.gravitis.server.ServerPlayer;
import me.sashie.gravitis.server.world.ServerWorld;

import java.util.Collection;

public class ServerSlime extends ServerLivingEntity {

    public ServerSlime(ServerWorld world, Vector2 position, float radius) {
        super(world, position, radius);
        data.type = EntityType.SLIME;
    }

    @Override
    public void onDeath(Player player) {

    }

    @Override
    public void update(Collection<ServerPlayer> players) {

    }

}
