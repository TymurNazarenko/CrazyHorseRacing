package de.thb.crazyhorseracing.entity;

import lombok.Getter;

// A pure class
public class Vec {
    @Getter
    private final double x, y;

    public Vec(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vec applyVelocity(Vec velocity, double dt) {
        return new Vec(x + velocity.getX() * dt, y + velocity.getY() * dt);
    }

    public double dist(Vec other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }

    public boolean isNear(Vec other, double epsilon) {
        return dist(other) <= epsilon;
    }

    public boolean isNear(Vec other) {
        return isNear(other, 0.01);
    }

    public Vec add(Vec other) {
        return new Vec(x + other.x, y + other.y);
    }

    public Vec subtract(Vec other) {
        return new Vec(x - other.x, y - other.y);
    }

    public Vec multiply(double m) {
        return new  Vec(x * m, y * m);
    }

    public Vec normalized() {
        double len = Math.sqrt(x * x + y * y);
        if (len == 0) return new Vec(0, 0);
        return new Vec(x / len, y / len);
    }

    public double dot(Vec other) {
        return x * other.getX() + y * other.getY();
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
