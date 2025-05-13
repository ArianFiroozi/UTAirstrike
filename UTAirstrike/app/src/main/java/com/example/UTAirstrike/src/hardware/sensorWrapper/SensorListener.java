package com.example.UTAirstrike.src.hardware.sensorWrapper;

public interface SensorListener {
    void onAccelerometerUpdate(float x, float y, float z);

    void onGyroscopeUpdate(float x, float y, float z);

    void onGravimeterUpdate(float x, float y, float z);

    void onMagnetometerUpdate(float x, float y, float z);

    void onRollPitch(float roll, float pitch);
}
