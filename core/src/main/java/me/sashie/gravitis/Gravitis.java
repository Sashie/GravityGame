package me.sashie.gravitis;

import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Gravitis extends Game {
    private PauseMenuScreen pauseMenuScreen;

    @Override
    public void create() {
        pauseMenuScreen = new PauseMenuScreen(this);
        setScreen(new GameScreen(this)); // Your main gameplay screen
    }

    public void pauseGame() {
        setScreen(pauseMenuScreen); // Switch to pause menu
    }

    public void resumeGame() {
        setScreen(new GameScreen(this)); // Resume the game
    }
}
