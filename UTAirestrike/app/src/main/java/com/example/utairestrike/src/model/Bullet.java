package com.example.utairestrike.src.model;

import android.graphics.Canvas;
import com.example.utairestrike.src.utill.Vector2D;

public class Bullet extends GameObject {
    public Bullet(Vector2D position, Vector2D velocity) {
        super(position, velocity);
    }

    @Override
    public void update(float deltaTime) {
//        this.position = this.position.add(this.velocity.multiply(deltaTime));
    }

    @Override
    public void draw(Canvas canvas) {
        // TODO: draw a small circle or bullet sprite at position
    }
}
