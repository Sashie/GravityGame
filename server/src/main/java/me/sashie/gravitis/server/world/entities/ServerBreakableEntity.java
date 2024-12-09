package me.sashie.gravitis.server.world.entities;

import com.badlogic.gdx.math.Vector2;
import me.sashie.gravitis.Player;
import me.sashie.gravitis.network.packets.PacketOutEntityUpdate;
import me.sashie.gravitis.network.packets.PacketOutSpawnEntity;
import me.sashie.gravitis.server.ServerPlayer;
import me.sashie.gravitis.server.world.ServerWorld;
import me.sashie.gravitis.server.world.entities.pieces.ServerPiece;

import java.util.Collection;

public abstract class ServerBreakableEntity extends ServerEntity {

    public abstract ServerPiece getPiece();

    public ServerBreakableEntity(ServerWorld world, Vector2 position, float radius) {
        super(world, position, radius);
    }

    @Override
    public boolean isAlive(Player player) {
        return this.data.radius > 10f;
    }

    @Override
    public void onDeath(Player player) {

    }

    public void update(Collection<ServerPlayer> players) {
        PacketOutEntityUpdate update = new PacketOutEntityUpdate();
        update.data = this.getData();
        world.getGameServer().getServer().sendToAllTCP(update);
    }

    public void onHit(Player player) {
        // Break the object into pieces when hit by a laser
        for (int i = 0; i < 2; i++) {
            // Create pieces
            ServerPiece piece = this.getPiece();
            float size = piece.getRadius() * .2f;
            this.data.radius -= size;
            if (this.data.radius < 0) {
                this.data.radius = 0;
            }

            PacketOutSpawnEntity spawnPacket = new PacketOutSpawnEntity();
            spawnPacket.data = piece.getData();
            spawnPacket.data.color = piece.getColor();
            world.getGameServer().getServer().sendToAllTCP(spawnPacket);
            world.getAllServerEntities().add(piece);
        }
    }

}
