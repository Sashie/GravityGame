package me.sashie.gravitis;

import com.badlogic.gdx.math.Vector2;
import me.sashie.gravitis.tools.Tool;
import me.sashie.gravitis.tools.ToolType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Player {
    private final String username;
    private final Vector2 position;
    private final Vector2 velocity;
    private float radius;
    private boolean absorbed;
    private boolean isShooting;
    private List<Tool> activeToolEntities = new ArrayList<>();
    private ToolType selectedTool;
    private final List<ToolType> toolTypes = Arrays.asList(ToolType.values());
    private float fuel = 500;

    public Player(String username, Vector2 position, float radius) {
        this.username = username;
        this.position = position;
        this.velocity = new Vector2(0, 0);
        this.radius = radius;
        this.absorbed = false;
        this.isShooting = false;
        this.selectedTool = ToolType.MINING_LASER;
    }

    public String getUsername() {
        return username;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public boolean isAbsorbed() {
        return absorbed;
    }

    public ToolType getSelectedToolType() {
        return selectedTool;
    }

    private void cycleTool(int scrollAmount) {
        int currentIndex = toolTypes.indexOf(selectedTool);
        int nextIndex = (currentIndex - scrollAmount + toolTypes.size()) % toolTypes.size();
        selectedTool = toolTypes.get(nextIndex);
    }

    public void absorb(float amount) {
        radius += amount;
    }

    public void setAbsorbed(boolean absorbed) {
        this.absorbed = absorbed;
    }

    public float getRadius() {
        return radius;
    }

    public float getFuel() {
        return this.fuel;
    }

    public void setFuel(float fuel) {
        this.fuel = fuel;
    }

    public void giveFuel(float fuel) {
        this.fuel += fuel;
    }
}
