package com.example.utairestrike.src.hardware;

import com.example.utairestrike.src.utill.Vector2D;

import lombok.Getter;
import lombok.Setter;

@Getter
public class AircraftSpeed {
    Vector2D velocity;
    float rotationDelta;
    public AircraftSpeed(float x, float y, float rotation) {
        this.velocity = new Vector2D(x, y);
        this.rotationDelta = rotation;
    }
}
