package com.example.utairestrike.src.logic.actors;

public class Aircraft extends Actor {
    public static float X = 0;
    public static float Y = 0;
    public static float speedX = 1;
    public static float speedY = 2;
    public static float angle = 0;

    public static void update() {
        X += speedX;
        Y += speedY;
        angle += 1;
    }
}

