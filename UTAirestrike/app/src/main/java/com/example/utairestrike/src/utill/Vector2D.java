package com.example.utairestrike.src.utill;

public class Vector2D {
    private float x;
    private float y;

    public Vector2D(){
        this.x = 0;
        this.y = 0;
    }

    public Vector2D(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void add(Vector2D other) {
        this.x += other.x;
        this.y += other.y;
    }

    public void add(float x, float y) {
        this.x += x;
        this.y += y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vector2D)) return false;
        Vector2D v = (Vector2D) o;
        return Math.abs(v.x - x) < 1e-6f
                && Math.abs(v.y - y) < 1e-6f;
    }

    @Override
    public int hashCode() {
        int hx = Float.floatToIntBits(x);
        int hy = Float.floatToIntBits(y);
        return 31 * hx + hy;
    }

    @Override
    public String toString() {
        return String.format("Vector2D(%.3f, %.3f)", x, y);
    }

    public void multiply(float deltaTime) {
        this.x *= deltaTime;
        this.y *= deltaTime;
    }
}
