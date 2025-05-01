package com.example.utairestrike.src.hardware;

import lombok.Getter;
import lombok.Setter;

@Getter
public class AircraftSpeed {
    public float X;
    public float Y;
    public AircraftSpeed(float x, float y) {
        X=x;
        Y=y;
    }
}
