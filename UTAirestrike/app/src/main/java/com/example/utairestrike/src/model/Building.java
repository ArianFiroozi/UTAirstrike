package com.example.utairestrike.src.model;

import android.graphics.Canvas;
import com.example.utairestrike.src.utill.Vector2D;

public class Building extends GameObject {
    public Building(Vector2D position) {
        super(position, new Vector2D());
    }

    @Override
    public void update(float deltaTime) {
        // Buildings are static by default
    }

    @Override
    public void draw(Canvas canvas) {
        // TODO: draw building graphic
    }
}

