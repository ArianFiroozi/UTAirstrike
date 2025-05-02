package com.example.utairestrike.src.model;

import android.graphics.Canvas;
import com.example.utairestrike.src.utill.Vector2D;


public abstract class GameObject {
    protected Vector2D position;
    protected Vector2D velocity;

    public GameObject(Vector2D position, Vector2D velocity) {
        this.position = position;
        this.velocity = velocity;
    }

    /** Update state based on elapsed time (seconds). */
    public abstract void update(float deltaTime);

    /** Draw the object onto the canvas. */
    public abstract void draw(Canvas canvas);

    // Getters and setters
    public Vector2D getPosition() { return position; }
    public void setPosition(Vector2D position) { this.position = position; }
    public Vector2D getVelocity() { return velocity; }
    public void setVelocity(Vector2D velocity) { this.velocity = velocity; }
}
