package me.sashie.gravitis;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class SettingsScreen implements Screen {
    private final Gravitis game;
    private Stage stage;
    private Skin skin;

    private Slider volumeSlider;
    private CheckBox debugCheckBox;

    public SettingsScreen(Gravitis game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Create skin for UI components (requires a skin file, see below)
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Volume slider
        Label volumeLabel = new Label("Volume", skin);
        volumeLabel.setPosition(Gdx.graphics.getWidth() / 2 - 50, Gdx.graphics.getHeight() / 2 + 40);

        volumeSlider = new Slider(0, 1, 0.1f, false, skin);
        volumeSlider.setValue(1f); // default value 100%
        volumeSlider.setPosition(Gdx.graphics.getWidth() / 2 - 100, Gdx.graphics.getHeight() / 2);
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                float volume = volumeSlider.getValue();
                // Set game volume here
                // AudioManager.setVolume(volume);
            }
        });

        // Developer debug toggle
        debugCheckBox = new CheckBox("Enable Debug", skin);
        debugCheckBox.setPosition(Gdx.graphics.getWidth() / 2 - 70, Gdx.graphics.getHeight() / 2 - 60);
        debugCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Toggle developer debug options
                // DebugManager.setDebugEnabled(debugCheckBox.isChecked());
            }
        });

        // Back to Pause Menu button
        TextButton backButton = new TextButton("Back", skin);
        backButton.setSize(200, 50);
        backButton.setPosition(Gdx.graphics.getWidth() / 2 - 100, Gdx.graphics.getHeight() / 2 - 120);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new PauseMenuScreen(game)); // Go back to pause menu
            }
        });

        // Add UI elements to stage
        stage.addActor(volumeLabel);
        stage.addActor(volumeSlider);
        stage.addActor(debugCheckBox);
        stage.addActor(backButton);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
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
