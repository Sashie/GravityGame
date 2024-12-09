package me.sashie.gravitis.network;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import me.sashie.gravitis.entities.Entity;
import me.sashie.gravitis.entities.EntityType;
import me.sashie.gravitis.network.entities.EntityData;
import me.sashie.gravitis.network.packets.*;

import java.util.ArrayList;

public class Networking {
    public static void registerClasses(Kryo kryo) {
        kryo.register(PacketLoginRequest.class);
        kryo.register(PacketLoginResponse.class);
        kryo.register(PacketInChunkUpdated.class);
        kryo.register(PacketInEntityHit.class);
        kryo.register(PacketInPlayerMovement.class);
        kryo.register(PacketOutChunkUpdate.class);
        kryo.register(PacketOutEntityUpdate.class);
        kryo.register(PacketOutPlayerMovement.class);
        kryo.register(PacketOutRemoveEntity.class);
        kryo.register(PacketOutSpawnEntity.class);
        kryo.register(EntityData.class);
        kryo.register(EntityType.class);
        kryo.register(Vector2.class);
        kryo.register(Color.class);
        kryo.register(ArrayList.class);
        //kryo.register(UUID.class); // crashes server at start
    }
}
