package me.sashie.gravitis.network.packets;

import com.badlogic.gdx.math.Vector2;
import me.sashie.gravitis.network.entities.EntityData;

public class PacketOutEntityUpdate extends Packet {
    public EntityData data;
    public Vector2 velocity;
}
