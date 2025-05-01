package com.example.utairestrike.src.hardware.sensorWrapper;

import lombok.Getter;

@Getter
public class SensorWrapper {
    private float X;
    private float Y;
    private float Z;

    public SensorWrapper() {
    }

    public void denoise(float x, float y, float z) {
        X=x;
        Y=y;
        Z=z;
    }
}
