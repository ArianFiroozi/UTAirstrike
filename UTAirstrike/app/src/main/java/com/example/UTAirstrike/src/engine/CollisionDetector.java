package com.example.UTAirstrike.src.engine;

import com.example.UTAirstrike.src.model.GameObject;

public class CollisionDetector {

    /**
     * Fast AABB overlap test:
     *   |x1 - x2| <= (w1/2 + w2/2)
     * && |y1 - y2| <= (h1/2 + h2/2)
     */
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
        if (dx < 0) dx = -dx;     // abs
        if (dx > ahw + bhw)      // quick X‑separation test
            return false;

        float dy = ay - by;
        if (dy < 0) dy = -dy;     // abs
        if (dy > ahh + bhh)      // quick Y‑separation test
            return false;

        return true;             // overlap on both axes
    }
}
