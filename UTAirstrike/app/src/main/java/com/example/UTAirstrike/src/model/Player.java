package com.example.UTAirstrike.src.model;

import com.example.UTAirstrike.src.util.Vector2D;
import java.lang.Math;
public class Player extends GameObject {
    public static final float BULLET_SPEED = 70;
    public static final float PLAYER_VELOCITY_LIMIT = 30;
    private static Vector2D canvasSize ;
    private long lastShotTime = 0;
    private static final long SHOOT_COOLDOWN = 200;

    public Player(Vector2D position, Vector2D velocity, Vector2D size, float rotation, Vector2D canvasSize) {
        super(position, velocity, size, rotation);
        Player.canvasSize = canvasSize;
    }

    public Player(Vector2D canvasSize) {
        super(new Vector2D(canvasSize.getX()/2, canvasSize.getY()/2), new Vector2D(0,0),
                new Vector2D(100,100), 0);
        Player.canvasSize = canvasSize;
    }

    public void takeBackToGameCanvas(Vector2D deltaVelocity){
        if (this.position.getX() < 50 ) {
            this.position.setX(50);
            if (deltaVelocity.getX() > 0)
                this.velocity.setX(deltaVelocity.getX());
        }
        else if (this.position.getX() > canvasSize.getX()-50) {
            this.position.setX(canvasSize.getX() - 50);
            if (deltaVelocity.getX() < 0)
                this.velocity.setX(deltaVelocity.getX());
        }
        if (this.position.getY() < 100) {
            this.position.setY(100);
            if (deltaVelocity.getY() > 0)
                this.velocity.setY(deltaVelocity.getY());
        }
        else if (this.position.getY() > canvasSize.getY()-100) {
            this.position.setY(canvasSize.getY() - 100);
            if (deltaVelocity.getY() < 0)
                this.velocity.setY(deltaVelocity.getY());
        }
    }

    @Override
    public void update(float deltaTime, Vector2D deltaVelocity, float rotationAngle) {
        velocity.add(deltaVelocity);
        velocity.trimLimit(PLAYER_VELOCITY_LIMIT);
        rotation += rotationAngle /2;
        updatePosition(deltaTime);
        takeBackToGameCanvas(deltaVelocity);
    }

    private Vector2D calculateShootingPosition(){
        float x = (float) (this.position.getX() + (this.size.getX())/2 * Math.sin(Math.toRadians(rotation)));
        float y = (float) (this.position.getY() - (this.size.getX())/2 * Math.cos(Math.toRadians(rotation)));
        return new Vector2D(x, y);
    }

    private Vector2D calculateBulletStartingVelocity(){
        return new Vector2D((float) (BULLET_SPEED * Math.sin(Math.toRadians(rotation))), -(float)(BULLET_SPEED * Math.cos(Math.toRadians(rotation))));
    }

    public Bullet shoot(){  //essential assumption is that the aircraft basit angle is the head to the right
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShotTime < SHOOT_COOLDOWN) {
            return null;
        }
        lastShotTime = currentTime;
        Vector2D bulletStartingPosition = calculateShootingPosition();
        Vector2D bulletVelocity = calculateBulletStartingVelocity();
        return new Bullet(bulletStartingPosition, bulletVelocity);
    }

    public float getRotation() { return rotation; }
    public void setRotation(float rotation) { this.rotation = rotation; }
}
