package me.sashie.gravitis.entities;

import com.badlogic.gdx.math.Vector2;
import me.sashie.gravitis.Player;
import me.sashie.gravitis.network.entities.EntityData;

public abstract class Entity {

    protected EntityData data = new EntityData();

    public abstract boolean isAlive(Player player);
    public abstract void onHit(Player player);  // for damaging entity or dropping pieces, items etc...
    public abstract void onDeath(Player player);        // for death animations etc...

    public Entity(EntityData data) {
        this(data.id, data.position, data.radius);
    }

    public Entity(String id, Vector2 position, float radius) {
        this.data.id = id;
        this.data.position = position;
        this.data.radius = radius;
    }

    public EntityData getData() {
        return data;
    }

    public Vector2 getPosition() {
        return data.position;
    }

    public float getRadius() {
        return data.radius;
    }

    public String getId() {
        return data.id;
    }
}

