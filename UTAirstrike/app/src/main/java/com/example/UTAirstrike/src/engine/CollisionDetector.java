package com.example.UTAirstrike.src.engine;

import com.example.UTAirstrike.src.model.GameObject;

public class CollisionDetector {
    public boolean isCollide(GameObject a, GameObject b) {
        // positions
        float ax = a.getPosition().getX();
        float ay = a.getPosition().getY();
        float bx = b.getPosition().getX();
        float by = b.getPosition().getY();

        // half‑sizes
        float ahw = a.getSize().getX() * 0.5f;
        float ahh = a.getSize().getY() * 0.5f;
        float bhw = b.getSize().getX() * 0.5f;
        float bhh = b.getSize().getY() * 0.5f;

        // compute deltas
        float dx = ax - bx;
        if (dx < 0) dx = -dx;
        if (dx > ahw + bhw)
            return false;

        float dy = ay - by;
        if (dy < 0) dy = -dy;
        if (dy > ahh + bhh)
            return false;

        return true;
    }
}
