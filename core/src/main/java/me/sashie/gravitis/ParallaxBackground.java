package me.sashie.gravitis;

import com.sudoplay.joise.module.ModuleBasisFunction;
import com.sudoplay.joise.module.ModuleFractal;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class ParallaxBackground {
    private static final int STAR_LAYERS = 4; // Number of parallax layers
    private static final int STARS_PER_CHUNK = 100; // Stars per chunk
    private static final float CHUNK_SIZE = 2000f; // Size of a chunk in world units

    private final ModuleFractal module; // Joise noise module for procedural generation
    private final double[] layerSpeeds; // Speed multiplier for each layer
    private final double[] noiseOffsetsX; // Noise offsets for each layer
    private final double[] noiseOffsetsY; // Noise offsets for each layer

    private final int loadDistance = 3; // Number of chunks to load around the player

    public ParallaxBackground() {

        // Initialize Joise noise
        module = new ModuleFractal(ModuleFractal.FractalType.FBM, ModuleBasisFunction.BasisType.SIMPLEX, ModuleBasisFunction.InterpolationType.QUINTIC);
        ((ModuleFractal) module).setNumOctaves(3);
        module.setSeed(System.currentTimeMillis());


        layerSpeeds = new double[STAR_LAYERS];
        noiseOffsetsX = new double[STAR_LAYERS];
        noiseOffsetsY = new double[STAR_LAYERS];
        for (int i = 0; i < STAR_LAYERS; i++) {
            layerSpeeds[i] = 0.1 + i * 0.1; // Different speed per layer
            noiseOffsetsX[i] = Math.random() * 1000; // Add randomness to the layer noise
            noiseOffsetsY[i] = Math.random() * 1000;
        }
    }

    public void render(ShapeRenderer shapeRenderer, Vector2 cameraPosition) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Calculate the base chunk the camera is currently in
        int baseChunkX = (int) Math.floor(cameraPosition.x / CHUNK_SIZE);
        int baseChunkY = (int) Math.floor(cameraPosition.y / CHUNK_SIZE);

        // Render the chunks around the player
        for (int layer = 0; layer < STAR_LAYERS; layer++) {
            shapeRenderer.setColor(new Color(1f, 1f, 1f, 0.3f + 0.2f * layer));

            // Load and render chunks around the player
            for (int offsetX = -loadDistance; offsetX <= loadDistance; offsetX++) {
                for (int offsetY = -loadDistance; offsetY <= loadDistance; offsetY++) {
                    renderChunk(shapeRenderer, layer, baseChunkX + offsetX, baseChunkY + offsetY, cameraPosition);
                }
            }
        }

        shapeRenderer.end();
    }

    private void renderChunk(ShapeRenderer shapeRenderer, int layer, int chunkX, int chunkY, Vector2 cameraPosition) {
        // Offset noise inputs with chunk coordinates for deterministic star generation
        double noiseBaseX = chunkX * CHUNK_SIZE + noiseOffsetsX[layer];
        double noiseBaseY = chunkY * CHUNK_SIZE + noiseOffsetsY[layer];

        // Assign a unique color for debugging based on the chunk position
        //shapeRenderer.setColor(new Color(Math.abs(chunkX) % 1f, Math.abs(chunkY) % 1f, 0.5f, 0.7f));

        // Draw the chunk (square area) for debugging purposes
        //shapeRenderer.rect(chunkX * CHUNK_SIZE - cameraPosition.x, chunkY * CHUNK_SIZE - cameraPosition.y, CHUNK_SIZE, CHUNK_SIZE);

        // Generate stars within the chunk
        for (int i = 0; i < STARS_PER_CHUNK; i++) {
            // Generate star positions using the noise module
            float localStarX = (float) (module.get(noiseBaseX + i, noiseBaseY) * CHUNK_SIZE);
            float localStarY = (float) (module.get(noiseBaseX, noiseBaseY + i) * CHUNK_SIZE);

            // Calculate world positions for the star
            float worldStarX = localStarX + chunkX * CHUNK_SIZE;
            float worldStarY = localStarY + chunkY * CHUNK_SIZE;

            // Apply parallax effect based on the layer speed
            float parallaxOffsetX = (float) ((cameraPosition.x - worldStarX) * layerSpeeds[layer]);
            float parallaxOffsetY = (float) ((cameraPosition.y - worldStarY) * layerSpeeds[layer]);

            // Final position of the star, adjusted for parallax
            float renderX = worldStarX - parallaxOffsetX;
            float renderY = worldStarY - parallaxOffsetY;

            // Adjust star size based on layer index to make distant layers appear smaller
            float starSize = 2 + (layer * 6f) * .05f; // Smaller stars for further layers

            // Draw the star
            shapeRenderer.circle(renderX, renderY, starSize); // Layer affects size of the star

        }
    }
}
