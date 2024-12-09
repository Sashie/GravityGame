package me.sashie.gravitis.server;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Server;
import me.sashie.gravitis.Player;
import me.sashie.gravitis.network.packets.Packet;

public class ServerPlayer extends Player {
    public int connectionId;
    public Server server;
    public Vector2 velocity;

    public ServerPlayer(String username, int connectionId, Server server) {
        super(username, new Vector2(0, 0), 20);
        this.connectionId = connectionId;
        this.server = server;
        this.velocity = new Vector2(0, 0);
    }

    public Server getServer() {
        return server;
    }

    public void sendPacket(Packet packet) {
        server.sendToTCP(connectionId, packet);
    }

}
