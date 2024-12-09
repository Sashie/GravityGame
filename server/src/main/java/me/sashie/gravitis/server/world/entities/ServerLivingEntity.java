package me.sashie.gravitis.server.world.entities;

import com.badlogic.gdx.math.Vector2;
import me.sashie.gravitis.Player;
import me.sashie.gravitis.server.world.ServerWorld;

public abstract class ServerLivingEntity extends ServerEntity {
    private int health;
    private float speed;

    public ServerLivingEntity(ServerWorld world, Vector2 position, float radius) {
        super(world, position, radius);
    }

    @Override
    public boolean isAlive(Player player) {
        return this.health > 0;
    }

    public void onHit(Player player) {
        /*if (player.getSelectedToolType() == ToolType.MINING_LASER) {

        }*/
        this.health -= 1;
    }

}
