package me.sashie.gravitis.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import me.sashie.gravitis.Player;
import me.sashie.gravitis.entities.pieces.CirclePiece;
import me.sashie.gravitis.entities.pieces.Piece;

public class GoldOre extends BreakableEntity {

    public GoldOre(Vector2 position, float radius) {
        super(position, radius);
    }

    @Override
    public Color getColor() {
        return Color.GOLDENROD;
    }

    @Override
    public Piece getPiece() {
        return new CirclePiece(getPosition().cpy().add((float) Math.random() * getRadius(), (float) Math.random() * getRadius()), Color.GOLD);
    }

    @Override
    public void onPickup(Piece piece, Player player) {

    }

    @Override
    public void renderShape(ShapeRenderer shapeRenderer) {
        shapeRenderer.circle(getPosition().x, getPosition().y, getRadius());
    }

}
