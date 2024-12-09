package me.sashie.gravitis.network.packets;

import com.badlogic.gdx.math.Vector2;

public class PacketOutPlayerMovement extends Packet {
    public String username;
    public Vector2 position;
    public Vector2 velocity;
}
