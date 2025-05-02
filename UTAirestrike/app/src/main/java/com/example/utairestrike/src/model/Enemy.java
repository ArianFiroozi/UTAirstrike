package com.example.utairestrike.src.model;

import android.graphics.Canvas;
import com.example.utairestrike.src.utill.Vector2D;

public class Enemy extends GameObject {
    public Enemy(Vector2D position, Vector2D velocity) {
        super(position, velocity);
    }

    @Override
    public void update(float deltaTime) {
//        this.position = this.position.add(this.velocity.multiply(deltaTime));
        // TODO: optionally change direction or behavior
    }

    @Override
    public void draw(Canvas canvas) {
        // TODO: draw enemy sprite
    }
}
