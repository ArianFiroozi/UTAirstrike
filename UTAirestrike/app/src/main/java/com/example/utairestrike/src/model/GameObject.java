package com.example.utairestrike.src.model;

import android.graphics.Canvas;
import com.example.utairestrike.src.utill.Vector2D;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class GameObject {
    protected Vector2D position;
    protected Vector2D velocity;
    protected Vector2D size;

    protected float rotation;

    public GameObject(Vector2D position, Vector2D velocity, Vector2D size, float rotation) {
        this.position = position;
        this.velocity = velocity;
        this.size = size;
        this.rotation = rotation;
    }

    public abstract void update(float deltaTime, Vector2D deltaVelocity, float rotationAngle);

    protected void updatePosition(float deltaTime){
        Vector2D delta = new Vector2D(this.velocity.getX(), this.velocity.getY());
        delta.multiply(deltaTime);
        this.position.add(delta);
    }
}
