package com.example.utairestrike.src.model;

import android.graphics.Canvas;
import com.example.utairestrike.src.utill.Vector2D;
public class Player extends GameObject {
    private float rotation; // in degrees

    public Player(Vector2D position, Vector2D velocity, float rotation) {
        super(position, velocity);
        this.rotation = rotation;
    }

    @Override
    public void update(float deltaTime) {
        // Move based on velocity
//        this.position = this.position.add(this.velocity.multiply(deltaTime));
        // TODO: clamp within screen bounds or apply inertia
    }

    @Override
    public void draw(Canvas canvas) {
        // TODO: use canvas.save(); canvas.rotate(rotation, position.x, position.y);
        //       draw bitmap, then canvas.restore();
    }

    // Rotation getter/setter
    public float getRotation() { return rotation; }
    public void setRotation(float rotation) { this.rotation = rotation; }
}
