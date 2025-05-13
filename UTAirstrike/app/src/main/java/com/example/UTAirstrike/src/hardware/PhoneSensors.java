package com.example.UTAirstrike.src.hardware;

public class PhoneSensors {
    public PhoneSensors(){
    }

    public AircraftSpeed getSpeed() {
        AircraftSpeed speed = new AircraftSpeed(0, 0, 0);
        return speed;
    }

    public float getAngle() {
        return 0;
    }
}
