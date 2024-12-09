package me.sashie.gravitis;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import me.sashie.gravitis.entities.Entity;
import me.sashie.gravitis.network.packets.PacketInEntityHit;
import me.sashie.gravitis.tools.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ClientPlayer extends Player {
    public Gravitis game;
    private final Vector2 velocity;
    private boolean absorbed;
    private boolean isShooting;
    private List<Tool> activeTools = new ArrayList<>();
    private ToolType selectedTool;
    private final List<ToolType> toolTypes = Arrays.asList(ToolType.values());
    private float fuel = 500;

    public ClientPlayer(Gravitis game, Vector2 position, float radius) {
        super(game.getUsername(), position, radius);
        this.game = game;
        this.velocity = new Vector2(0, 0);
        this.absorbed = false;
        this.isShooting = false;
        this.selectedTool = ToolType.MINING_LASER;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public boolean isAbsorbed() {
        return absorbed;
    }

    public void update(float delta, Planet orbitingPlanet, Collection<List<Entity>> chunkEntities, Camera camera) {
        Vector2 inputDirection = null;
        float inputStrength = 0;

        // Handle movement with left mouse click
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            Vector2 touchPosition = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            Vector3 unprojected = camera.unproject(new Vector3(touchPosition.x, touchPosition.y, 0));
            Vector2 worldTouchPosition = new Vector2(unprojected.x, unprojected.y);

            inputDirection = worldTouchPosition.cpy().sub(getPosition());
            inputStrength = Math.min(inputDirection.len() / 50f, 1f); // Adjust for reasonable input scaling
        }

        // Handle shooting with right mouse click
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            isShooting = true;
        } else {
            isShooting = false;
        }

        // Set up input processor for scroll handling
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean scrolled(float amountX, float amountY) {
                cycleTool((int) amountY);
                return true;
            }
        });

        Tool tool = useTool(camera);
        if (tool != null) {
            activeTools.add(tool);
        }

        for (int i = 0; i < activeTools.size(); i++) {
            tool = activeTools.get(i);
            tool.update(game.entities, this);
            for (List<Entity> entities : chunkEntities) {
                for (int j = 0; j < entities.size(); j++) {
                    Entity entity = entities.get(j);
                    if (tool.checkCollision(entity, this)) {
                        PacketInEntityHit hitPacket = new PacketInEntityHit();
                        hitPacket.username = getUsername();
                        hitPacket.entityId = entity.getId();
                        game.getClient().sendTCP(hitPacket);
                        activeTools.remove(tool);
                    } else if (!tool.isActive()) {
                        activeTools.remove(tool);
                    }
                }
            }
        }

        Vector2 toPlanet = orbitingPlanet.getPosition().cpy().sub(getPosition());
        float distanceToPlanet = toPlanet.len();

        // Apply gravitational pull if within range
        if (distanceToPlanet < orbitingPlanet.getRadius() * 5) {
            velocity.add(toPlanet.nor().scl(0.1f)); // Gravitational pull strength
        }

        /*for (Entity entity : entities) {
            if (entity.isAlive()) {
                Vector2 toPl = entity.getPosition().cpy().sub(position);
                float distanceTo = toPl.len();

                // Apply gravitational pull if within range
                if (distanceTo < entity.getRadius() * 100) {
                    velocity.add(toPl.nor().scl(0.1f)); // Gravitational pull strength
                }
            }
        }*/

        // Apply player input to movement
        if (inputDirection != null) {
            velocity.add(inputDirection.nor().scl(inputStrength));
        }

        // Apply velocity and update position
        getPosition().add(velocity);

        // Limit maximum speed
        if (velocity.len() > 4) velocity.nor().scl(4);

        //velocity.nor().scl(1.5f);
    }

    public void render(ShapeRenderer shapeRenderer) {
        for (Tool tool : activeTools) {
            tool.render(shapeRenderer, this);
        }
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.circle(getPosition().x, getPosition().y, getRadius());
        shapeRenderer.end();
    }

    public Tool useTool(Camera camera) {
        if (isShooting) {
            Vector2 touchPosition = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            Vector3 unprojected = camera.unproject(new Vector3(touchPosition.x, touchPosition.y, 0));
            Vector2 worldTouchPosition = new Vector2(unprojected.x, unprojected.y);

            Vector2 inputDirection = worldTouchPosition.cpy().sub(getPosition());

            switch (getSelectedToolType()) {
                case MINING_LASER -> {return new MiningLaser(getPosition().cpy(), inputDirection);}
                case MINING_VACUUM -> {
                    return new MiningVacuum(getPosition().cpy(), inputDirection);
                }
                case DEFENCE_DRONES -> {
                    //
                }
                case ATTACK_CANNON -> {
                    //
                }
                case ATTACK_PULSE -> {return new PulseWave(getPosition().cpy(), 100);}
            }
        }
        return null; // No tool if not shooting
    }

    public ToolType getSelectedToolType() {
        return selectedTool;
    }

    private void cycleTool(int scrollAmount) {
        int currentIndex = toolTypes.indexOf(selectedTool);
        int nextIndex = (currentIndex - scrollAmount + toolTypes.size()) % toolTypes.size();
        selectedTool = toolTypes.get(nextIndex);
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
