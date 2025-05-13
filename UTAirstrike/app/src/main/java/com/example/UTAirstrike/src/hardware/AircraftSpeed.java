package com.example.UTAirstrike.src.hardware;

import com.example.UTAirstrike.src.util.Vector2D;

import lombok.Getter;

@Getter
public class AircraftSpeed {
    Vector2D velocity;
    float rotationDelta;
    public AircraftSpeed(float x, float y, float rotation) {
        this.velocity = new Vector2D(x, y);
        this.rotationDelta = rotation;
    }
}
