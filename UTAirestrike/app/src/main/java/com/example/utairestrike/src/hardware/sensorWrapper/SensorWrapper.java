package com.example.utairestrike.src.hardware.sensorWrapper;

import lombok.Getter;
import lombok.Setter;

@Getter
public class SensorWrapper {
    float X;
    float Y;
    float Z;

    public SensorWrapper() {
    }

    public void denoise(float x, float y, float z) {
        X=x;
        Y=y;
        Z=z;
    }
}
