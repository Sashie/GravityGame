package me.sashie.gravitis.network.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import me.sashie.gravitis.entities.EntityType;

public class EntityData {
    public EntityType type;
    public String id;
    public Vector2 position;
    public float radius;
    public Color color;

    public EntityData() {}

    public EntityData(String id, Vector2 position, float radius) {
        this.id = id;
        this.position = position;
        this.radius = radius;
    }
}
