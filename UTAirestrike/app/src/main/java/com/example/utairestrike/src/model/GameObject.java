package com.example.utairestrike.src.model;

import android.graphics.Canvas;
import com.example.utairestrike.src.utill.Vector2D;


public abstract class GameObject {
    protected Vector2D position;
    protected Vector2D velocity;
    protected Vector2D size;

    public GameObject(Vector2D position, Vector2D velocity, Vector2D size) {
        this.position = position;
        this.velocity = velocity;
        this.size = size;
    }

    public abstract void update(float deltaTime, Vector2D deltaVelocity, float rotationAngle);

    protected void updatePosition(float deltaTime){
        Vector2D delta = new Vector2D(this.velocity.getX(), this.velocity.getY());
        delta.multiply(deltaTime);
        this.position.add(delta);
    }



    // Getters and setters
    public Vector2D getPosition() { return position; }
    public void setPosition(Vector2D position) { this.position = position; }
    public Vector2D getVelocity() { return velocity; }
    public void setVelocity(Vector2D velocity) { this.velocity = velocity; }
}
