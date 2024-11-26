package me.sashie.gravitis;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {
    private Gravitis game;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;

    private ParallaxBackground parallaxBackground;
    private BreakableObject breakableObject;
    private List<Laser> lasers = new ArrayList<>();
    private Player player;
    private Planet planet;
    private ArrayList<AI> aiCircles;

    private boolean followPlayer  = true; // Toggle for level view

    public GameScreen(Gravitis game) {
        this.game = game;
        shapeRenderer = new ShapeRenderer();

        // Initialize camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        parallaxBackground = new ParallaxBackground();

        // Initialize game objects
        planet = new Planet(new Vector2(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f), 50f);
        player = new Player(new Vector2(200, 200), 20);
        breakableObject = new BreakableObject(new Vector2(500, 500), 50f);
        aiCircles = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            float randomRadius = 3 + (float) Math.random() * 17; // Random size between 3 and 20
            aiCircles.add(new AI(new Vector2((float) Math.random() * 800, (float) Math.random() * 600), randomRadius));
        }
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update game logic
        update(delta);

        // Draw everything
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);


        // Pass the camera position to the background
        Vector2 cameraPosition = new Vector2(camera.position.x, camera.position.y);
        parallaxBackground.render(shapeRenderer, cameraPosition);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        planet.render(shapeRenderer);
        player.render(shapeRenderer);
        breakableObject.render(shapeRenderer, player);

        // Render lasers
        for (Laser laser : lasers) {
            laser.render(shapeRenderer);
        }
        for (AI ai : aiCircles) {
            ai.render(shapeRenderer);
        }
        shapeRenderer.end();

        // ESC key to pause the game
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.pauseGame();
        }
    }

    private void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            toggleCameraMode();
        }

        if (followPlayer) {
            // Camera follows the player
            camera.position.set(player.getPosition(), 0);
            camera.zoom = 1;
        } else {
            // Keep the camera centered on the planet in whole level view
            camera.position.set(planet.getPosition(), 0);
            camera.zoom = 6;
        }

        player.update(delta, planet, breakableObject, camera);

        Laser laser = player.shootLaser(camera);
        if (laser != null) {
            lasers.add(laser);
        }

        for (int i = 0; i < lasers.size(); i++) {
            Laser l = lasers.get(i);
            l.update();

            // Check for collision with the breakable object
            if (l.checkCollision(breakableObject)) {
                breakableObject.hitByLaser();
                lasers.remove(l);
            }
        }

        // Update the breakable object (update pieces if broken)
        breakableObject.update(player);

        for (AI ai : aiCircles) {
            ai.update(delta, player, aiCircles, planet);
        }

        // Remove absorbed AI
        aiCircles.removeIf(ai -> ai.isAbsorbed());

        // Check if game over
        if (player.isAbsorbed()) {
            game.pauseGame(); // Later, show a "Game Over" screen
        } else if (aiCircles.isEmpty()) {
            game.pauseGame(); // Later, show a "You Won" screen
        }
    }

    // Toggle camera mode (call this from input handling)
    public void toggleCameraMode() {
        followPlayer = !followPlayer;
    }

    @Override
    public void show() {}

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        shapeRenderer.dispose();
    }

    @Override
    public void dispose() {}
}

