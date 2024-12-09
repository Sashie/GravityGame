package me.sashie.gravitis.entities.pieces;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import me.sashie.gravitis.ClientPlayer;
import me.sashie.gravitis.Player;

public class CirclePiece extends Piece {

    public CirclePiece(String id, Vector2 position, Color color) {
        super(id, position, color);
    }

    @Override
    public boolean renderShape(ShapeRenderer shapeRenderer, Player player) {
        return false;
    }

    @Override
    public boolean updateShape(Player player) {
        return false;
    }

    @Override
    public void onHit(Player player) {

    }

    @Override
    public void onDeath(Player player) {

    }
}
