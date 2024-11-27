package me.sashie.gravitis;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import me.sashie.gravitis.entities.BreakableEntity;
import me.sashie.gravitis.tools.MiningLaser;
import me.sashie.gravitis.tools.Tool;
import me.sashie.gravitis.tools.PulseWave;
import me.sashie.gravitis.tools.ToolType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Player {
    private final Vector2 position;
    private final Vector2 velocity;
    private float radius;
    private boolean absorbed;
    private boolean isShooting;
    private List<Tool> lasers = new ArrayList<>();
    private ToolType selectedTool;
    private final List<ToolType> toolTypes = Arrays.asList(ToolType.values());
    private float fuel = 500;


    public Player(Vector2 position, float radius) {
        this.position = position;
        this.velocity = new Vector2(0, 0);
        this.radius = radius;
        this.absorbed = false;
        this.isShooting = false;
        this.selectedTool = ToolType.MINING_LASER;
    }

    public Vector2 getPosition() {
        return position;
    }

    public boolean isAbsorbed() {
        return absorbed;
    }

    public void update(float delta, Planet orbitingPlanet, BreakableEntity breakableObject, Camera camera) {
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

        Tool laser = shootLaser(camera);
        if (laser != null) {
            lasers.add(laser);
        }

        for (int i = 0; i < lasers.size(); i++) {
            Tool l = lasers.get(i);
            l.update();

            // Check for collision with the breakable object
            if (breakableObject.isAlive() && l.checkCollision(breakableObject)) {
                breakableObject.hitByLaser();
                lasers.remove(l);
            }
        }

        Vector2 toPlanet = orbitingPlanet.getPosition().cpy().sub(position);
        float distanceToPlanet = toPlanet.len();

        // Apply gravitational pull if within range
        if (distanceToPlanet < orbitingPlanet.getRadius() * 100) {
            velocity.add(toPlanet.nor().scl(0.1f)); // Gravitational pull strength
        }

        Vector2 toPl = breakableObject.getPosition().cpy().sub(position);
        float distanceTo = toPl.len();

        // Apply gravitational pull if within range
        if (distanceTo < breakableObject.getRadius() * 100) {
            velocity.add(toPl.nor().scl(0.1f)); // Gravitational pull strength
        }

        // Apply player input to movement
        if (inputDirection != null) {
            velocity.add(inputDirection.nor().scl(inputStrength));
        }

        // Apply velocity and update position
        position.add(velocity);

        // Limit maximum speed
        if (velocity.len() > 5) velocity.nor().scl(5);
    }

    public void render(ShapeRenderer shapeRenderer) {
        // Render lasers

        for (Tool laser : lasers) {
            laser.render(shapeRenderer);
        }
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.circle(position.x, position.y, radius);
        shapeRenderer.end();
    }

    public Tool shootLaser(Camera camera) {
        if (isShooting) {
            Vector2 touchPosition = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            Vector3 unprojected = camera.unproject(new Vector3(touchPosition.x, touchPosition.y, 0));
            Vector2 worldTouchPosition = new Vector2(unprojected.x, unprojected.y);

            Vector2 inputDirection = worldTouchPosition.cpy().sub(getPosition());

            switch (getSelectedTool()) {
                case MINING_LASER -> {return new MiningLaser(position.cpy(), inputDirection);}
                case MINING_VACUUM -> {
                    //
                }
                case DEFENCE_DRONES -> {
                    //
                }
                case ATTACK_CANNON -> {
                    //
                }
                case ATTACK_PULSE -> {return new PulseWave(position.cpy(), inputDirection.len());}
            }
        }
        return null; // No laser if not shooting
    }

    public ToolType getSelectedTool() {
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
