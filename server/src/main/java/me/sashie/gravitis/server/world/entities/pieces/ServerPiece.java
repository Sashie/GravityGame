package me.sashie.gravitis.server.world.entities.pieces;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import me.sashie.gravitis.Player;
import me.sashie.gravitis.network.packets.PacketOutEntityUpdate;
import me.sashie.gravitis.server.ServerPlayer;
import me.sashie.gravitis.server.world.ServerWorld;
import me.sashie.gravitis.server.world.entities.ServerEntity;

import java.util.Collection;

public abstract class ServerPiece extends ServerEntity {
    private Vector2 velocity;
    private Color color;

    public abstract boolean updateShape(Player player);
    public abstract void onPickup(Player player);

    public ServerPiece(ServerWorld world, Vector2 position, Color color) {
        super(world, position, 5 + (float) Math.random() * 10);
        this.data.position = position.cpy();
        this.color = color;
        this.velocity = new Vector2((float) Math.random() - 0.5f, (float) Math.random() - 0.5f).nor().scl(2f); // Random velocity
    }

    @Override
    public void update(Collection<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            if (data.position.dst(player.getPosition()) < 1000) {
                if (!updateShape(player)) {
                    velocity.add(player.getPosition().cpy().sub(data.position).nor().scl(1.5f));
                    data.position.add(velocity); // Move the piece based on velocity

                    PacketOutEntityUpdate update = new PacketOutEntityUpdate();
                    update.data = this.getData();
                    player.sendPacket(update);
                }
            }
        }
    }

    @Override
    public boolean isAlive(Player player) {
        return getPosition().dst(player.getPosition()) > 30f;
    }

    public void setPosition(Vector2 position) {
        this.data.position = position;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
