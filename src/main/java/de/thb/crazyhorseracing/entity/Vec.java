package de.thb.crazyhorseracing.entity;

import lombok.Getter;

import java.util.List;

public class Vec {
    @Getter
    private double x, y;

    public Vec(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // NOT Pure
    public void applyVelocity(Velocity velocity, double dt) { // the only non-pure function, used only for the horses
        this.x += velocity.getX() * dt;
        this.y += velocity.getY() * dt;
    }

    // Pure
    public double dist(Vec other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }

    // Pure
    public boolean isNear(Vec other, double epsilon) {
        return dist(other) <= epsilon;
    }

    // Pure
    public boolean isNear(Vec other) {
        return isNear(other, 0.01);
    }

    // Pure
    public Vec add(Vec other) {
        return new Vec(x + other.x, y + other.y);
    }

    // Pure
    public Vec normalized() {
        double len = Math.sqrt(x * x + y * y);
        if (len == 0) return new Vec(0, 0);
        return new Vec(x / len, y / len);
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
