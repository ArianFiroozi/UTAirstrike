package com.example.utairestrike.src.model;

import com.example.utairestrike.src.util.Vector2D;

public class Bullet extends GameObject {
    public static Vector2D BULLET_SIZE = new Vector2D(1, 1);
    public Bullet(Vector2D position, Vector2D velocity) {
        super(position, velocity, BULLET_SIZE, 0);
    }

    @Override
    public void update(float deltaTime, Vector2D deltaVelocity, float rotationAngle) {
        updatePosition(deltaTime);
    }

    public boolean isInside (Vector2D canvasSize){
        boolean isInHorizentalBorder = this.position.getX() > 0 &&
                this.position.getX() < canvasSize.getX();
        boolean isInVerticalBorder = this.position.getY() > 0 &&
                this.position.getY() < canvasSize.getY();

        return isInHorizentalBorder && isInVerticalBorder;
    }
}
