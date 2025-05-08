package com.example.utairestrike.src.model;

import com.example.utairestrike.src.utill.Vector2D;
import java.lang.Math;
public class Player extends GameObject {
    private float rotation; // in degrees
    public static final float BULLET_SPEED = 5;

    public Player(Vector2D position, Vector2D velocity, Vector2D size, float rotation) {
        super(position, velocity, size);
        this.rotation = rotation;
    }

    @Override
    public void update(float deltaTime, Vector2D deltaVelocity, float rotationAngle) {
        velocity.add(deltaVelocity);
        rotation += rotationAngle;
        updatePosition(deltaTime);
    }

    private Vector2D calculateShootingPosition(){
        float x = (float) (this.position.getX() + (this.size.getX())/2 * Math.sin(rotation));
        float y = (float) (this.position.getY() + (this.size.getX())/2 * Math.sin(rotation));
        return new Vector2D(x, y);
    }

    private Vector2D calculateBulletStartingVelocity(){
        return new Vector2D((float) (BULLET_SPEED * Math.sin(rotation)),
                (float) (BULLET_SPEED * Math.cos(rotation)));
    }

    public Bullet shoot(){  //essential assumption is that the aircraft basit angle is the head to the right
        Vector2D bulletStartingPosition = calculateShootingPosition();
        Vector2D bulletVelocity = calculateBulletStartingVelocity();
        return new Bullet(bulletStartingPosition, bulletVelocity, this.rotation);
    }

    // Rotation getter/setter
    public float getRotation() { return rotation; }
    public void setRotation(float rotation) { this.rotation = rotation; }
}
