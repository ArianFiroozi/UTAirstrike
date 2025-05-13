package com.example.utairestrike.src.model;

import android.graphics.Canvas;
import com.example.utairestrike.src.utill.Vector2D;
public class Enemy extends GameObject {
    private final boolean isflipped;
    public Enemy(Vector2D position, Vector2D velocity, Vector2D size,boolean isflipped) {
        super(position, velocity, size, 0);
        this.isflipped = isflipped;
    }
    public boolean Isflipped() {
        return isflipped;
    }
    @Override
    public void update(float deltaTime, Vector2D deltaVelocity, float rotationAngle) {
        velocity.add(deltaVelocity);
        updatePosition(deltaTime);
    }

}
