package me.sashie.gravitis.network.packets;

import com.badlogic.gdx.math.Vector2;
import me.sashie.gravitis.network.entities.EntityData;

import java.util.List;

public class PacketOutChunkUpdate extends Packet {
    public Vector2 chunkCoords;
    public List<EntityData> entities;
}
