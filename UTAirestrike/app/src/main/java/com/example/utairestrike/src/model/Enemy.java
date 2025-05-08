package com.example.utairestrike.src.model;

import android.graphics.Canvas;
import com.example.utairestrike.src.utill.Vector2D;
public class Enemy extends GameObject {
    public Enemy(Vector2D position, Vector2D velocity, Vector2D size) {
        super(position, velocity, size, 0);
    }

    @Override
    public void update(float deltaTime, Vector2D deltaVelocity, float rotationAngle) {
        velocity.add(deltaVelocity);
        updatePosition(deltaTime);
    }

}
