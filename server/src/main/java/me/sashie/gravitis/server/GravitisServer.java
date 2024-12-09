package me.sashie.gravitis.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Listener.ThreadedListener;
import com.esotericsoftware.kryonet.Listener.TypeListener;
import com.esotericsoftware.kryonet.Server;
import me.sashie.gravitis.Player;
import me.sashie.gravitis.entities.Entity;
import me.sashie.gravitis.network.Networking;
import me.sashie.gravitis.network.packets.*;
import me.sashie.gravitis.server.world.ServerWorld;

import java.io.IOException;
import java.util.*;

/** Launches the server application. */
public class GravitisServer {

    private static final String SERVER_PASSWORD = "hello";
    private static final int TCP_PORT = 54555;
    private static final int UDP_PORT = 54777;
    private static final int TICK_RATE = 50; // Updates per second

    private final Server server;
    private final Map<Integer, ServerPlayer> players = new HashMap<>(); // Track players by connection ID
    private final ServerWorld world;

    public GravitisServer() {
        server = new Server();
        Networking.registerClasses(server.getKryo());

        world = new ServerWorld(this);

        server.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof PacketLoginRequest) {
                    handleLogin(connection, (PacketLoginRequest) object);
                }
            }
        });
        TypeListener typeListener = new TypeListener();
        typeListener.addTypeHandler(PacketInPlayerMovement.class, (con, msg) -> {
            handlePlayerUpdate(con, msg);

        });
        typeListener.addTypeHandler(PacketInEntityHit.class, (con, msg) -> {
            handleEntityHit(msg);
        });
        server.addListener(new ThreadedListener(typeListener));

    }

    public Map<Integer, ServerPlayer> getPlayers() {
        return players;
    }

    public ServerWorld getWorld() {
        return world;
    }

    public Server getServer() {
        return server;
    }

    private void handleLogin(Connection connection, PacketLoginRequest request) {
        String username = request.username;
        String password = request.password;

        if (!password.equals(SERVER_PASSWORD)) {
            connection.sendTCP(new PacketLoginResponse().success = false);
            System.out.println("Login failed for " + username + ": Incorrect password.");
            return;
        }

        if (!players.containsKey(connection.getID())) {
            players.put(connection.getID(), new ServerPlayer(username, connection.getID(), server));
            connection.sendTCP(new PacketLoginResponse().success = true);
            System.out.println(username + " connected.");
        } else {
            connection.sendTCP(new PacketLoginResponse().success = false);
            System.out.println("Login failed for " + username + ": Username already taken.");
        }
    }

    private void handlePlayerUpdate(Connection connection, PacketInPlayerMovement update) {
        ServerPlayer player = players.get(connection.getID());
        if (player != null) {
            player.getPosition().set(update.position);
            player.velocity.set(update.velocity);
        }
    }

    private void handleEntityHit(PacketInEntityHit update) {
        for (List<Entity> entities : world.getChunkEntities().values()) {
            for (int j = 0; j < entities.size(); j++) {
                Entity entity = entities.get(j);
                if (entity.getId().equals(update.entityId)) {
                    for (Player player : players.values()) {
                        if (player.getUsername().equals(update.username)) {
                            entity.onHit(player);
                            if (!entity.isAlive(player)) {
                                PacketOutRemoveEntity removePacket = new PacketOutRemoveEntity();
                                removePacket.entityId = entity.getId();
                                server.sendToAllTCP(removePacket);
                                entities.remove(entity);
                            }
                        }
                    }
                }
            }
        }
    }

    private void updatePlayers() {
        for (ServerPlayer player : players.values()) {
            PacketOutPlayerMovement update = new PacketOutPlayerMovement();
            update.position = player.getPosition();
            update.velocity = player.velocity;
            server.sendToAllTCP(update);
        }
    }

    private void update() {
        // Update entities and synchronize with clients
        //updatePlayers();
        /*for (ServerPlayer player : players.values()) {

        }*/
        world.sendEntityUpdates(players.values());
        world.sendChunkUpdates();

        /*for (List<Entity> entities : world.getChunkEntities().values()) {
            for (int j = 0; j < entities.size(); j++) {
                ServerEntity entity = (ServerEntity) entities.get(j);
                for (ServerPlayer player : players.values()) {
                    entity.update(player);
                }
            }
        }*/
    }

    public void start() throws IOException {
        //server.bind(54555, 54777); // Ports for TCP/UDP
        server.bind(TCP_PORT, UDP_PORT);
        server.start();

        // Main server loop
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                update();
            }
        }, 0, 1000 / TICK_RATE);

        System.out.println("Server started on port: " + TCP_PORT);
    }

    public static void main(String[] args) {
        try {
            new GravitisServer().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
