package me.sashie.gravitis;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class AI {
    private final Vector2 position;
    private final Vector2 velocity;
    private float radius;

    private float neutralTimer; // Timer for neutral behavior after splitting

    public AI(Vector2 position, float radius) {
        this.position = position;
        this.radius = radius;
        this.velocity = new Vector2();
        this.neutralTimer = 0;
    }


    public void update(float delta, Player player, List<AI> aiCircles, Planet orbitingPlanet) {
        Vector2 target = null;

        // Gravitational pull to maintain orbit
        Vector2 toPlanet = orbitingPlanet.getPosition().cpy().sub(position);
        float distanceToPlanet = toPlanet.len();

        if (distanceToPlanet > orbitingPlanet.getRadius() + 20) {
            // Apply gravitational pull if too far from orbit
            velocity.add(toPlanet.nor().scl(0.5f * delta)); // Increased pull strength
        }/* else if (distanceToPlanet < planet.getRadius() - 5) {
            // Push outward slightly if too close
            velocity.add(toPlanet.nor().scl(-1.5f * delta)); // Increased outward push
        }*/

        // Interaction with other AI
        for (AI other : aiCircles) {
            if (other == this || other.neutralTimer > 0) continue;

            float distance = position.dst(other.position);

            if (other.radius < radius && (target == null || distance < position.dst(target))) {
                target = other.position; // Chase smaller AI
            } else if (other.radius > radius && distance < 100) {
                // Avoid larger AI
                velocity.add(position.cpy().sub(other.position).nor().scl(0.8f * delta)); // Increased avoidance speed
            }
        }

        // Chase the target
        if (target != null) {
            velocity.add(new Vector2(target).sub(position).nor().scl(0.1f * delta)); // Increased chasing speed
        } else {
            // Chase player if smaller
            if (player.getPosition().dst(position) < 200 && player.getRadius() < radius) {
                velocity.add(player.getPosition().cpy().sub(position).nor().scl(0.2f * delta)); // Increased player chase speed
            }
        }

        // Apply gravity
        Vector2 gravity = new Vector2(orbitingPlanet.getPosition()).sub(position).nor();
        velocity.add(gravity.scl(0.08f));

        // Update position
        position.add(velocity);

        // Split logic
        if (radius > 40) {
            split(aiCircles);
        }

        // Limit speed
        if (velocity.len() > 3) velocity.nor().scl(3);
    }



    private void split(List<AI> aiCircles) {
        radius /= 2;
        AI child = new AI(new Vector2(position).add((float) Math.random() * 50 - 25, (float) Math.random() * 50 - 25), radius);
        child.neutralTimer = 3; // Neutral time after splitting
        aiCircles.add(child);
    }

    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.circle(position.x, position.y, radius);
    }

    public float getRadius() {
        return radius;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void absorb(float amount) {
        radius += amount;
    }

    public boolean isAbsorbed() {
        return radius <= 0;
    }
}
