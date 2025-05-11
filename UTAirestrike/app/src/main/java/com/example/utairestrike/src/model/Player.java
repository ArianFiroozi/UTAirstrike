package com.example.utairestrike.src.model;

import com.example.utairestrike.src.utill.Vector2D;
import java.lang.Math;
public class Player extends GameObject {
    public static final float BULLET_SPEED = 5;
    private static Vector2D canvasSize ;

    public Player(Vector2D position, Vector2D velocity, Vector2D size, float rotation, Vector2D canvasSize) {
        super(position, velocity, size, rotation);
        Player.canvasSize = canvasSize;
    }

    public Player(Vector2D canvasSize) {
        super(new Vector2D(canvasSize.getX()/2, canvasSize.getY()/2), new Vector2D(0,0),
                new Vector2D(100,100), 0);
        Player.canvasSize = canvasSize;
    }

    public void takeBackToGameCanvas(){
        if (this.position.getX() < 50 )
            this.position.setX(50);
        else if (this.position.getX() > canvasSize.getX()-50)
            this.position.setX(canvasSize.getX()-50);
        if (this.position.getY() < 100)
            this.position.setY(100);
        else if (this.position.getY() > canvasSize.getY()-100)
            this.position.setY(canvasSize.getY()-100);
    }

    @Override
    public void update(float deltaTime, Vector2D deltaVelocity, float rotationAngle) {
        velocity.add(deltaVelocity);
        rotation += rotationAngle;
        updatePosition(deltaTime);
        takeBackToGameCanvas();
    }

    private Vector2D calculateShootingPosition(){
        float x = (float) (this.position.getX() + (this.size.getX())/2 * Math.sin(rotation));
        float y = (float) (this.position.getY() + (this.size.getX())/2 * Math.sin(rotation));
        return new Vector2D(x, y);
    }

    private Vector2D calculateBulletStartingVelocity(){
        return new Vector2D((float) (BULLET_SPEED * Math.sin(rotation)),
                -(float) (BULLET_SPEED * Math.cos(rotation)));
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
