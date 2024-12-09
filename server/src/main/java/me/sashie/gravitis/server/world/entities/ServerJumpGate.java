package me.sashie.gravitis.server.world.entities;

import com.badlogic.gdx.math.Vector2;
import me.sashie.gravitis.Player;
import me.sashie.gravitis.server.ServerPlayer;
import me.sashie.gravitis.server.world.ServerWorld;

import java.util.Collection;
import java.util.UUID;

public class ServerJumpGate extends ServerEntity {
    //private final String targetSystem; // Name of the linked planetary system
    //private final Vector2 destination; // Coordinates in the target system
    private final float activationRadius;

    public ServerJumpGate(ServerWorld world, Vector2 position/*, String targetSystem, Vector2 destination*/) {
        super(world, position, 100);
        //this.targetSystem = targetSystem;
        //this.destination = destination;
        this.activationRadius = data.radius;
    }

    /*public String getTargetSystem() {
        return targetSystem;
    }

    public Vector2 getDestination() {
        return destination;
    }*/

    public boolean isPlayerInRange(Vector2 playerPosition) {
        return data.position.dst(playerPosition) <= activationRadius;
    }

    @Override
    public boolean isAlive(Player player) {
        return false;
    }

    @Override
    public void update(Collection<ServerPlayer> players) {

    }

    @Override
    public void onHit(Player player) {

    }

    @Override
    public void onDeath(Player player) {

    }
}
