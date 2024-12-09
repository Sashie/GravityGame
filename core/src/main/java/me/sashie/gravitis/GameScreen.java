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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import me.sashie.gravitis.entities.ClientEntity;
import me.sashie.gravitis.entities.Entity;
import me.sashie.gravitis.network.packets.PacketInPlayerMovement;

import java.util.*;
import java.util.List;

public class GameScreen implements Screen {
    private Gravitis game;
    private Skin skin;
    private SpriteBatch batch;
    private Stage hudStage;
    private Label fuelLabel;
    private Label toolLabel;
    private Table pauseMenuTable;
    private Table settingsTable;
    private boolean isPaused = false;

    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;

    private ParallaxBackground parallaxBackground;

    private static final float CHUNK_SIZE = 1000f; // Size of one chunk

    private ClientPlayer player;
    private Planet planet;
    private ArrayList<AI> aiCircles;

    private boolean followPlayer  = true; // Toggle for level view

    public GameScreen(Gravitis game) {
        this.game = game;
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        // Initialize camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        parallaxBackground = new ParallaxBackground();

        // Initialize game objects
        planet = new Planet(new Vector2(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f), 50f);
        player = new ClientPlayer(game, new Vector2(200, 200), 20);

        aiCircles = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            float randomRadius = 3 + (float) Math.random() * 17; // Random size between 3 and 20
            aiCircles.add(new AI(new Vector2((float) Math.random() * 800, (float) Math.random() * 600), randomRadius));
        }
        setupHUD();
        setupPauseMenu();
        setupSettingsMenu();
    }

    private Vector2 getChunkCoords(Vector2 position) {
        return new Vector2(
            (int) Math.floor(position.x / CHUNK_SIZE),
            (int) Math.floor(position.y / CHUNK_SIZE)
        );
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

    private void setupPauseMenu() {
        pauseMenuTable = new Table();
        pauseMenuTable.setFillParent(true);
        pauseMenuTable.setVisible(false);

        TextButton resumeButton = new TextButton("Resume", skin);
        resumeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                togglePause();
            }
        });

        TextButton settingsButton = new TextButton("Settings", skin);
        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showSettingsMenu();
            }
        });

        TextButton quitButton = new TextButton("Quit", skin);
        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit(); // Quit the game
            }
        });

        pauseMenuTable.add(resumeButton).pad(10).row();
        pauseMenuTable.add(settingsButton).pad(10).row();
        pauseMenuTable.add(quitButton).pad(10).row();

        hudStage.addActor(pauseMenuTable);
    }

    private void setupSettingsMenu() {
        settingsTable = new Table();
        settingsTable.setFillParent(true);
        settingsTable.setVisible(false);

        Label settingsLabel = new Label("Settings", skin);
        settingsLabel.setFontScale(2f);

        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showPauseMenu();
            }
        });

        // Add dummy settings options
        Label volumeLabel = new Label("Volume", skin);
        Slider volumeSlider = new Slider(0, 100, 1, false, skin);
        volumeSlider.setValue(50);

        Label graphicsLabel = new Label("Graphics Quality", skin);
        SelectBox<String> graphicsSelectBox = new SelectBox<>(skin);
        graphicsSelectBox.setItems("Low", "Medium", "High");

        settingsTable.add(settingsLabel).colspan(2).pad(20).row();
        settingsTable.add(volumeLabel).pad(10);
        settingsTable.add(volumeSlider).pad(10).row();
        settingsTable.add(graphicsLabel).pad(10);
        settingsTable.add(graphicsSelectBox).pad(10).row();
        settingsTable.add(backButton).colspan(2).pad(20);

        hudStage.addActor(settingsTable);
    }

    public void updateHUD(float delta, ClientPlayer player) {
        // Update fuel and tool information
        player.setFuel(Math.max(0, player.getFuel() - delta * 0.5f)); // Fuel decreasing
        fuelLabel.setText("Fuel: " + (int) player.getFuel());
        toolLabel.setText("Tool: " + player.getSelectedToolType().name());
    }

    private void togglePause() {
        isPaused = !isPaused;
        pauseMenuTable.setVisible(isPaused);
        settingsTable.setVisible(false);

        if (isPaused) {
            Gdx.input.setInputProcessor(hudStage);
        } else {
            Gdx.input.setInputProcessor(null);
        }
    }

    private void showSettingsMenu() {
        pauseMenuTable.setVisible(false);
        settingsTable.setVisible(true);
    }

    private void showPauseMenu() {
        pauseMenuTable.setVisible(true);
        settingsTable.setVisible(false);
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

    private void renderEntities(ClientPlayer player) {
        Vector2 currentChunk = getChunkCoords(player.getPosition());

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                Vector2 neighborChunk = new Vector2(currentChunk.x + dx, currentChunk.y + dy);
                List<Entity> entities = game.chunkEntities.get(neighborChunk);
                if (entities != null) {
                    for (int i = 0; i < entities.size(); i++) {
                        ClientEntity entity = (ClientEntity) entities.get(i);
                        if (entity.isAlive(player)) {
                            entity.render(shapeRenderer, player);
                        }
                    }
                }
            }
        }
        //shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < game.entities.size(); i++) {
            ClientEntity entity = (ClientEntity) game.entities.get(i);
            if (entity.isAlive(player)) {
                entity.render(shapeRenderer, player);
            }
        }
        //shapeRenderer.end();
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // ESC key to pause the game
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            togglePause();
        }

        // Update game logic
        if (!isPaused) {
            update(delta);
            updateHUD(delta, player);
        }

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

        player.update(delta, planet, game.chunkEntities.values(), camera);

        PacketInPlayerMovement playerUpdate = new PacketInPlayerMovement();
        playerUpdate.username = player.getUsername();
        playerUpdate.position = player.getPosition();
        playerUpdate.velocity = player.getVelocity();
        game.getClient().sendTCP(playerUpdate);

        //for (int j = 0; j < game.entities.size(); j++) {
        //    ClientEntity entity = (ClientEntity) game.entities.get(j);
            // Render breakable entity pieces globally to avoid disappearing pieces once player leaves chunk
            /*if (entity instanceof BreakableEntity) {
                if (!entity.isAlive(player) && !((BreakableEntity) entity).getPieces().isEmpty()) {
                    entity.render(shapeRenderer, player);
                } else if (!entity.isAlive(player) && ((BreakableEntity) entity).getPieces().isEmpty()) {
                    entity.onDeath(player);
                    PacketInRemoveEntity removePacket = new PacketInRemoveEntity();
                    removePacket.entityId = entity.getId();
                    game.getClient().sendTCP(removePacket);
                    game.entities.remove(entity);
                }
            }*/

        //    if (!entity.isAlive(player)/* && !(entity instanceof BreakableEntity)*/) {
        //        entity.onDeath(player);
        //        PacketInRemoveEntity removePacket = new PacketInRemoveEntity();
        //        removePacket.entityId = entity.getId();
        //        game.getClient().sendTCP(removePacket);
        //        game.entities.remove(entity);
        //    }
        //}

        /*for (List<Entity> entities : game.chunkEntities.values()) {
            for (int j = 0; j < entities.size(); j++) {
                ClientEntity entity = (ClientEntity) entities.get(j);
                //entity.update(player);
                // Render breakable entity pieces globally to avoid disappearing pieces once player leaves chunk
                if (entity instanceof BreakableEntity) {
                    if (!entity.isAlive(player) && !((BreakableEntity) entity).getPieces().isEmpty()) {
                        entity.render(shapeRenderer, player);
                    } else if (!entity.isAlive(player) && ((BreakableEntity) entity).getPieces().isEmpty()) {
                        entity.onDeath(player);
                        PacketInRemoveEntity removePacket = new PacketInRemoveEntity();
                        removePacket.entityId = entity.getId();
                        game.getClient().sendTCP(removePacket);
                        entities.remove(entity);
                    }
                }

                if (!entity.isAlive(player) && !(entity instanceof BreakableEntity)) {
                    entity.onDeath(player);
                    PacketInRemoveEntity removePacket = new PacketInRemoveEntity();
                    removePacket.entityId = entity.getId();
                    game.getClient().sendTCP(removePacket);
                    entities.remove(entity);
                }
            }
        }*/

        for (AI ai : aiCircles) {
            ai.update(delta, player, aiCircles, planet);
        }

        // Remove absorbed AI
        aiCircles.removeIf(ai -> ai.isAbsorbed());

        // Check if game over
        if (player.isAbsorbed()) {
            togglePause(); // Later, show a "Game Over" screen
        } else if (aiCircles.isEmpty()) {
            togglePause(); // Later, show a "You Won" screen
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
        hudStage.getViewport().update(width, height, true);

        // Recalculate the table layout for pause menu and settings
        pauseMenuTable.invalidateHierarchy();
        settingsTable.invalidateHierarchy();
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

