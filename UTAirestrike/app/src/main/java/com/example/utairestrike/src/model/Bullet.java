package com.example.utairestrike.src.model;

import android.graphics.Canvas;
import com.example.utairestrike.src.utill.Vector2D;

public class Bullet extends GameObject {
    public static Vector2D BULLET_SIZE = new Vector2D(1, 1);
    private float rotation;
    public Bullet(Vector2D position, Vector2D velocity, float rotation) {
        super(position, velocity, BULLET_SIZE);
        this.rotation = rotation;
    }

    @Override
    public void update(float deltaTime, Vector2D deltaVelocity, float rotationAngle) {
        updatePosition(deltaTime);
    }
}
