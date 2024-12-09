package me.sashie.gravitis.server.world;

import com.badlogic.gdx.math.Vector2;
import com.sudoplay.joise.module.ModuleBasisFunction;
import com.sudoplay.joise.module.ModuleFractal;
import me.sashie.gravitis.entities.Entity;
import me.sashie.gravitis.network.entities.EntityData;
import me.sashie.gravitis.network.packets.PacketOutChunkUpdate;
import me.sashie.gravitis.network.packets.PacketOutRemoveEntity;
import me.sashie.gravitis.server.GravitisServer;
import me.sashie.gravitis.server.ServerPlayer;
import me.sashie.gravitis.server.world.entities.*;
import me.sashie.gravitis.server.world.entities.pieces.ServerPiece;

import java.util.*;

public class ServerWorld {
    public GravitisServer gameServer;
    private final Map<Vector2, List<Entity>> chunkEntities = new HashMap<>();
    private List<Entity> allServerEntities = new ArrayList<>();
    private ModuleFractal noiseGenerator;
    private static final int MAX_ENTITIES_PER_CHUNK = 10;
    private static final float CHUNK_SIZE = 1000f;
    private static final float VISIBLE_RANGE = 3000f; // Range to send chunks to players

    public ServerWorld(GravitisServer gameServer) {
        this.gameServer = gameServer;
        setupNoise();
    }

    public GravitisServer getGameServer() {
        return gameServer;
    }

    public List<Entity> getAllServerEntities() {
        return allServerEntities;
    }

    public Map<Vector2, List<Entity>> getChunkEntities() {
        return chunkEntities;
    }

    private void setupNoise() {
        noiseGenerator = new ModuleFractal(ModuleFractal.FractalType.FBM, ModuleBasisFunction.BasisType.SIMPLEX, ModuleBasisFunction.InterpolationType.QUINTIC);
        noiseGenerator.setNumOctaves(4); // Number of layers of noise
        noiseGenerator.setFrequency(0.01); // Frequency of the noise
        noiseGenerator.setSeed(42); // Fixed seed for reproducibility
    }

    public void sendAllChunkUpdates() {
        for (Map.Entry<Vector2, List<Entity>> entry : chunkEntities.entrySet()) {
            PacketOutChunkUpdate update = new PacketOutChunkUpdate();
            update.chunkCoords = entry.getKey();
            update.entities = getEntityData(entry.getValue());
            gameServer.getServer().sendToAllTCP(update);
        }
    }

    public void sendEntityUpdates(Collection<ServerPlayer> players) {
        for (int i = 0; i < allServerEntities.size(); i++) {
            Entity entity = allServerEntities.get(i);
            if (entity instanceof ServerEntity) {
                ((ServerEntity) entity).update(players);
                //PacketOutEntityUpdate packet = new PacketOutEntityUpdate();
                //packet.data = entity.getData();
                for (ServerPlayer player : players) {
                    //player.sendPacket(packet);
                    if (!entity.isAlive(player)) {
                        if (entity instanceof ServerPiece) {
                            ((ServerPiece) entity).onPickup(player);
                            allServerEntities.remove(entity);
                        }
                        PacketOutRemoveEntity removePacket = new PacketOutRemoveEntity();
                        removePacket.entityId = entity.getId();
                        gameServer.getServer().sendToAllTCP(removePacket);
                    }
                }
                //gameServer.getServer().sendToAllTCP(packet);

            }
        }
    }

    public void sendChunkUpdates() {
        for (Map.Entry<Integer, ServerPlayer> entry : gameServer.getPlayers().entrySet()) {
            int playerId = entry.getKey();
            ServerPlayer player = entry.getValue();

            Vector2 playerChunk = new Vector2(
                (float) Math.floor(player.getPosition().x / CHUNK_SIZE),
                (float) Math.floor(player.getPosition().y / CHUNK_SIZE)
            );

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    Vector2 neighborChunk = new Vector2(playerChunk.x + dx, playerChunk.y + dy);
                    generateEntitiesForChunk(neighborChunk);

                    PacketOutChunkUpdate update = new PacketOutChunkUpdate();
                    update.chunkCoords = neighborChunk;
                    update.entities = getEntityData(chunkEntities.get(neighborChunk));
                    gameServer.getServer().sendToTCP(playerId, update);
                }
            }
        }
    }

    private void generateEntitiesForChunk(Vector2 chunkCoords) {
        if (chunkEntities.containsKey(chunkCoords)) return;

        List<Entity> entitiesInChunk = new ArrayList<>();
        float chunkStartX = chunkCoords.x * CHUNK_SIZE;
        float chunkStartY = chunkCoords.y * CHUNK_SIZE;

        for (int i = 0; i < MAX_ENTITIES_PER_CHUNK; i++) {
            float localX = chunkStartX + (float) Math.random() * CHUNK_SIZE;
            float localY = chunkStartY + (float) Math.random() * CHUNK_SIZE;
            double noiseValue = noiseGenerator.get(localX, localY);

            if (noiseValue > 0.3) {
                ServerEntity entity;
                Vector2 position = new Vector2(localX, localY);
                float radius = 5f + (float) Math.random() * 15f;
                if (Math.random() < 0.25) {
                    entity = new ServerFuelCrystal(this, new Vector2(position.x, position.y), radius);
                } else if (Math.random() < 0.75) {
                    entity = new ServerSlime(this, new Vector2(position.x, position.y), radius);
                } else {
                    entity = new ServerGoldOre(this, new Vector2(position.x, position.y), radius);
                }

                if (isNonOverlapping(entity, entitiesInChunk)) {
                    entitiesInChunk.add(entity);
                    allServerEntities.add(entity);
                }
            }
        }

        chunkEntities.put(chunkCoords, entitiesInChunk);
    }

    private boolean isNonOverlapping(ServerEntity newEntity, List<Entity> existingEntities) {
        for (Entity entity : existingEntities) {
            if (entity.getPosition().dst(newEntity.getPosition()) < (entity.getRadius() + newEntity.getRadius())) {
                return false;
            }
        }
        return true;
    }

    public List<EntityData> getEntityData(List<Entity> entities) {
        List<EntityData> entityData = new ArrayList<>();
        for (Entity entity : entities) {
            entityData.add(entity.getData());
        }

        return  entityData;
    }
}
