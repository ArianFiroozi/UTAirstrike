package com.example.utairestrike.src.hardware;

import com.example.utairestrike.src.hardware.sensorWrapper.SensorWrapper;

public class PhoneSensors {
    public PhoneSensors(){
    }

    public AircraftSpeed getSpeed() {
        AircraftSpeed speed = new AircraftSpeed(0, 0);
        return speed;
    }

    public float getAngle() {
        return 0;
    }
}
