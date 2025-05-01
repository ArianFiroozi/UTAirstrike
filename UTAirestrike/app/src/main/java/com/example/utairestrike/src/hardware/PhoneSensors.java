package com.example.utairestrike.src.hardware;

import com.example.utairestrike.src.hardware.sensorWrapper.SensorConnector;

public class PhoneSensors {
//    private final SensorConnector connector;

    public PhoneSensors(){
//        connector = new SensorConnector();
    }

    public AircraftSpeed getSpeed() {
        AircraftSpeed speed = new AircraftSpeed(0, 0);
        return speed;
    }

    public float getAngle() {
        return 0;
    }
}
