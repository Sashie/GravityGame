package me.sashie.gravitis;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class PauseMenuScreen implements Screen {
    private final Gravitis game;
    private Stage stage;
    private Skin skin;

    public PauseMenuScreen(Gravitis game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Create skin for UI components (requires a skin file, see below)
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Create resume button
        TextButton resumeButton = new TextButton("Resume", skin);
        resumeButton.setSize(200, 50);
        resumeButton.setPosition(Gdx.graphics.getWidth() / 2 - 100, Gdx.graphics.getHeight() / 2 + 60);
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.resumeGame(); // Calls the resume method in your game logic
            }
        });

        // Create settings button
        TextButton settingsButton = new TextButton("Settings", skin);
        settingsButton.setSize(200, 50);
        settingsButton.setPosition(Gdx.graphics.getWidth() / 2 - 100, Gdx.graphics.getHeight() / 2);
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SettingsScreen(game)); // Show settings screen
            }
        });

        // Create quit button
        TextButton quitButton = new TextButton("Quit", skin);
        quitButton.setSize(200, 50);
        quitButton.setPosition(Gdx.graphics.getWidth() / 2 - 100, Gdx.graphics.getHeight() / 2 - 60);
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit(); // Exit the game
            }
        });

        // Add buttons to stage
        stage.addActor(resumeButton);
        stage.addActor(settingsButton);
        stage.addActor(quitButton);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw the menu background (optional)
        // batch.begin();
        // batch.draw(backgroundTexture, 0, 0);
        // batch.end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f)); // Keep frame rate consistent
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        stage.dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

}
