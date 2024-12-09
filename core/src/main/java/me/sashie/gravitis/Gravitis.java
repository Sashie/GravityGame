package me.sashie.gravitis;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Listener.ThreadedListener;
import com.esotericsoftware.kryonet.Listener.TypeListener;
import me.sashie.gravitis.entities.*;
import me.sashie.gravitis.entities.pieces.CirclePiece;
import me.sashie.gravitis.entities.pieces.FilledPolygonPiece;
import me.sashie.gravitis.network.Networking;
import me.sashie.gravitis.network.entities.EntityData;
import me.sashie.gravitis.network.packets.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** {@link ApplicationListener} implementation shared by all platforms. */
public class Gravitis extends Game {

    private static final String SERVER_HOST = "localhost";
    private static final int TCP_PORT = 54555;
    private static final int UDP_PORT = 54777;

    private Client client;
    private String username = "Sashie";

    public Map<Vector2, List<Entity>> chunkEntities = new HashMap<>();
    public List<Entity> entities = new ArrayList<>();

    public Gravitis() {
        client = new Client();
        Networking.registerClasses(client.getKryo());

        client.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                System.out.println("Connected to server.");
            }

            @Override
            public void disconnected(Connection connection) {
                System.out.println("Disconnected from server.");
            }

            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof PacketLoginResponse) {
                    PacketLoginResponse response = (PacketLoginResponse) object;
                    if (response.success) {
                        System.out.println("Login successful.");
                    } else {
                        System.out.println("Login failed. Please check your username or password.");
                    }
                }
            }
        });
        TypeListener typeListener = new TypeListener();
        typeListener.addTypeHandler(PacketOutChunkUpdate.class, (con, msg) -> {
            handleChunkUpdate(msg);
        });
        typeListener.addTypeHandler(PacketOutEntityUpdate.class, (con, msg) -> {
            handleEntityUpdate(msg);
        });
        typeListener.addTypeHandler(PacketOutRemoveEntity.class, (con, msg) -> {
            handleEntityRemove(msg);
        });
        typeListener.addTypeHandler(PacketOutSpawnEntity.class, (con, msg) -> {
            handleEntitySpawn(msg);
        });
        client.addListener(new ThreadedListener(typeListener));
    }

    private void handleEntityUpdate(PacketOutEntityUpdate update) {
        for (List<Entity> entities : chunkEntities.values()) {
            for (int j = 0; j < entities.size(); j++) {
                Entity entity = entities.get(j);
                if (entity.getData().id.equals(update.data.id)) {
                    entity.getData().position = update.data.position;
                    entity.getData().radius = update.data.radius;
                }
            }
        }
        for (int j = 0; j < entities.size(); j++) {
            Entity entity = entities.get(j);
            if (entity.getData().id.equals(update.data.id)) {
                entity.getData().position = update.data.position;
                entity.getData().radius = update.data.radius;
            }
        }
    }

    private void handleEntityRemove(PacketOutRemoveEntity update) {
        for (List<Entity> entities : chunkEntities.values()) {
            for (int i = 0; i < entities.size(); i++) {
                Entity entity = entities.get(i);
                if (entity.getId().equals(update.entityId)) {
                    entities.remove(entity);
                }
            }
        }
        for (int j = 0; j < entities.size(); j++) {
            Entity entity = entities.get(j);
            if (entity.getId().equals(update.entityId)) {
                entities.remove(entity);
            }
        }
    }

    private void handleChunkUpdate(PacketOutChunkUpdate update) {
        if (chunkEntities.containsKey(update.chunkCoords)) return;
        //if (entities.contains(update.chunkCoords)) return;
        List<Entity> entities = new ArrayList<>();
        for (EntityData entity : update.entities) {
            Entity output = null;
            switch (entity.type) {
                case FUEL_CRYSTAL -> output = new FuelCrystal(entity.id, entity.position, entity.radius);
                case GOLD_ORE -> output = new GoldOre(entity.id, entity.position, entity.radius);
                case SLIME -> output = new Slime(entity.id, entity.position, entity.radius);
            }
            entities.add(output);
            //this.entities.add(output);
        }

        chunkEntities.put(update.chunkCoords, entities);
    }

    private void handleEntitySpawn(PacketOutSpawnEntity update) {
        switch (update.data.type) {
            case FUEL_CRYSTAL -> entities.add(new FuelCrystal(update.data.id, update.data.position, update.data.radius));
            case GOLD_ORE -> entities.add(new GoldOre(update.data.id, update.data.position, update.data.radius));
            case SLIME -> entities.add(new Slime(update.data.id, update.data.position, update.data.radius));
            case PIECE_POLY -> entities.add(new FilledPolygonPiece(update.data.id, update.data.position, update.data.color));
            case PIECE_CIRCLE -> entities.add(new CirclePiece(update.data.id, update.data.position, update.data.color));
        }
    }

    public void start() throws IOException {
        client.start();
        client.connect(5000, SERVER_HOST, TCP_PORT, UDP_PORT);

        PacketLoginRequest login = new PacketLoginRequest();
        login.username = username;
        login.password = "hello";
        client.sendTCP(login);
    }

    @Override
    public void create() {
        try {
           start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setScreen(new GameScreen(this)); // Your main gameplay screen
    }

    public void resumeGame() {
        setScreen(new GameScreen(this)); // Resume the game
    }

    public Connection getClient() {
        return client;
    }

    public String getUsername() {
        return username;
    }
}
