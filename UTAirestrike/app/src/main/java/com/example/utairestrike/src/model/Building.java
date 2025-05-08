package com.example.utairestrike.src.model;

import android.graphics.Canvas;
import com.example.utairestrike.src.utill.Vector2D;

public class Building extends GameObject {
    public Building(Vector2D position, Vector2D size) {
        super(position, new Vector2D(), size);
    }

    @Override
    public void update(float deltaTime, Vector2D deltaVelocity, float rotationAngle) {
        return ;
    }


}

