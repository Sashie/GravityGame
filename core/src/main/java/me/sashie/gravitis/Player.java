package me.sashie.gravitis;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Player {
    private final Vector2 position;
    private final Vector2 velocity;
    private float radius;
    private boolean absorbed;
    private boolean isShooting;

    public Player(Vector2 position, float radius) {
        this.position = position;
        this.velocity = new Vector2(0, 0);
        this.radius = radius;
        this.absorbed = false;
        this.isShooting = false;
    }

    public Vector2 getPosition() {
        return position;
    }

    public boolean isAbsorbed() {
        return absorbed;
    }

    public void update(float delta, Planet orbitingPlanet, BreakableObject breakableObject, Camera camera) {
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
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.circle(position.x, position.y, radius);
    }

    public Laser shootLaser(Camera camera) {
        if (isShooting) {
            Vector2 touchPosition = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            Vector3 unprojected = camera.unproject(new Vector3(touchPosition.x, touchPosition.y, 0));
            Vector2 worldTouchPosition = new Vector2(unprojected.x, unprojected.y);

            Vector2 inputDirection = worldTouchPosition.cpy().sub(getPosition());

            // Return the new laser in that direction
            return new Laser(position.cpy(), inputDirection);
        }
        return null; // No laser if not shooting
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
}
