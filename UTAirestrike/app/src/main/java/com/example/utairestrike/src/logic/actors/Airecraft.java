package com.example.utairestrike.src.logic.actors;

public class Airecraft extends Actor {
    public static float X = 500;
    public static float Y = 500;
    public static float speedX = 2;
    public static float speedY = 1;
    public static float angle = 0;

    public static void update() {
        X += speedX;
        Y += speedY;
        angle += 1;
    }
}

