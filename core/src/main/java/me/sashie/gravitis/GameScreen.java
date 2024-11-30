package me.sashie.gravitis;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.sudoplay.joise.module.ModuleBasisFunction;
import com.sudoplay.joise.module.ModuleFractal;
import me.sashie.gravitis.entities.Entity;
import me.sashie.gravitis.entities.FuelCrystal;
import me.sashie.gravitis.entities.GoldOre;

import java.util.*;

public class GameScreen implements Screen {
    private Gravitis game;
    private SpriteBatch batch;
    private Stage hudStage;
    private Label fuelLabel;
    private Label toolLabel;

    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;

    private ParallaxBackground parallaxBackground;
    private ModuleFractal noiseGenerator;
    private Map<Vector2, List<Entity>> chunkEntities = new HashMap<>();
    private Random random;
    private static final float CHUNK_SIZE = 1000f; // Size of one chunk
    private static final int MAX_ENTITIES_PER_CHUNK = 10; // Limit for entities per chunk

    private Player player;
    private Planet planet;
    private ArrayList<AI> aiCircles;

    private boolean followPlayer  = true; // Toggle for level view

    public GameScreen(Gravitis game) {
        this.game = game;
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        // Initialize camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        parallaxBackground = new ParallaxBackground();

        //entities = new ArrayList<>();
        random = new Random();
        setupNoise();

        // Initialize game objects
        planet = new Planet(new Vector2(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f), 50f);
        player = new Player(new Vector2(200, 200), 20);

        aiCircles = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            float randomRadius = 3 + (float) Math.random() * 17; // Random size between 3 and 20
            aiCircles.add(new AI(new Vector2((float) Math.random() * 800, (float) Math.random() * 600), randomRadius));
        }
        setupHUD();
    }

    private void setupNoise() {
        noiseGenerator = new ModuleFractal(ModuleFractal.FractalType.FBM, ModuleBasisFunction.BasisType.SIMPLEX, ModuleBasisFunction.InterpolationType.QUINTIC);
        noiseGenerator.setNumOctaves(4); // Number of layers of noise
        noiseGenerator.setFrequency(0.01); // Frequency of the noise
        noiseGenerator.setSeed(42); // Fixed seed for reproducibility
    }

    private void generateEntitiesForChunk(Vector2 chunkCoords) {
        if (chunkEntities.containsKey(chunkCoords)) return; // Avoid regenerating existing chunks

        List<Entity> entitiesInChunk = new ArrayList<>();

        float chunkStartX = chunkCoords.x * CHUNK_SIZE;
        float chunkStartY = chunkCoords.y * CHUNK_SIZE;

        for (int i = 0; i < MAX_ENTITIES_PER_CHUNK; i++) {
            // Use noise for procedural placement within the chunk
            float localX = chunkStartX + random.nextFloat() * CHUNK_SIZE;
            float localY = chunkStartY + random.nextFloat() * CHUNK_SIZE;
            double noiseValue = noiseGenerator.get(localX, localY);

            if (noiseValue > 0.3) { // Threshold for spawning entities
                Entity entity;

                if (random.nextFloat() < 0.5) {
                    entity = new FuelCrystal(new Vector2(localX, localY), randomRadius());
                } else {
                    entity = new GoldOre(new Vector2(localX, localY), randomRadius());
                }

                // Check for overlaps with existing entities in this chunk
                if (isNonOverlapping(entity, entitiesInChunk)) {
                    entitiesInChunk.add(entity);
                }
            }
        }

        chunkEntities.put(chunkCoords, entitiesInChunk);
    }

    private boolean isNonOverlapping(Entity newEntity, List<Entity> entities) {
        for (Entity existing : entities) {
            if (existing.getPosition().dst(newEntity.getPosition()) < existing.getRadius() + newEntity.getRadius() + 10f) {
                return false; // Overlap detected
            }
        }
        return true;
    }

    private Vector2 getChunkCoords(Vector2 position) {
        return new Vector2(
            (float) Math.floor(position.x / CHUNK_SIZE),
            (float) Math.floor(position.y / CHUNK_SIZE)
        );
    }

    private void loadNearbyChunks(Vector2 playerPosition) {
        Vector2 currentChunk = getChunkCoords(playerPosition);

        // Load current chunk and adjacent chunks
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                Vector2 neighborChunk = new Vector2(currentChunk.x + dx, currentChunk.y + dy);
                generateEntitiesForChunk(neighborChunk);
            }
        }
    }

    private float randomRadius() {
        return 10f + random.nextFloat() * 20f; // Radius between 10 and 30
    }

    private void setupHUD() {
        // Create a stage for the HUD
        hudStage = new Stage(new ScreenViewport());

        // Create font and style for labels
        BitmapFont font = new BitmapFont(); // Use a custom font file if needed
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, com.badlogic.gdx.graphics.Color.WHITE);

        // Create labels
        fuelLabel = new Label("Fuel: " + (int) player.getFuel(), labelStyle);
        toolLabel = new Label("Tool: " + player.getSelectedToolType().name(), labelStyle);

        // Create a table for layout
        Table table = new Table();
        table.top().left(); // Position the table at the top-left of the screen
        table.setFillParent(true);

        // Add labels to the table
        table.add(fuelLabel).align(Align.left).pad(10);
        table.row();
        table.add(toolLabel).align(Align.left).pad(10);

        // Add the table to the stage
        hudStage.addActor(table);
    }

    public void updateHUD(float delta, Player player) {
        // Update fuel and tool information
        player.setFuel(Math.max(0, player.getFuel() - delta * 0.5f)); // Fuel decreasing
        fuelLabel.setText("Fuel: " + (int) player.getFuel());
        toolLabel.setText("Tool: " + player.getSelectedToolType().name());
    }

    public void renderHUD() {
        // Render the HUD
        batch.begin();
        hudStage.act();
        hudStage.draw();
        batch.end();
    }

    public Stage getHudStage() {
        return hudStage;
    }

    private void renderEntities(Player player) {
        Vector2 currentChunk = getChunkCoords(player.getPosition());

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                Vector2 neighborChunk = new Vector2(currentChunk.x + dx, currentChunk.y + dy);
                List<Entity> entities = chunkEntities.get(neighborChunk);
                if (entities != null) {
                    for (Entity entity : entities) {
                        entity.render(shapeRenderer, player);
                        //entity.update(player);
                    }
                }
            }
        }
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update game logic
        update(delta);
        updateHUD(delta, player);

        // Draw everything
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);

        // Pass the camera position to the background
        Vector2 cameraPosition = new Vector2(camera.position.x, camera.position.y);
        parallaxBackground.render(shapeRenderer, cameraPosition);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        planet.render(shapeRenderer);
        for (AI ai : aiCircles) {
            ai.render(shapeRenderer);
        }
        shapeRenderer.end();

        renderEntities(player);

        player.render(shapeRenderer);

        batch.setProjectionMatrix(camera.combined);

        renderHUD();
    }

    private void update(float delta) {
        // ESC key to pause the game
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.pauseGame();
        }

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

        player.update(delta, planet, chunkEntities.values(), camera);

        loadNearbyChunks(player.getPosition());

        for (List<Entity> entities : chunkEntities.values()) {
            for (Entity entity : entities) {
                entity.update(player);
            }
        }

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
        //hudStage.dispose();
    }

    @Override
    public void dispose() {}
}

