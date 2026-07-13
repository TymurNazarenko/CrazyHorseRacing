package de.thb.crazyhorseracing.entity;

import lombok.NonNull;

public record Vec(double x, double y) {
    public Vec applyVelocity(Vec velocity, double dt) {
        return new Vec(x + velocity.x() * dt, y + velocity.y() * dt);
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
        return new Vec(x * m, y * m);
    }

    public Vec normalized() {
        double len = Math.sqrt(x * x + y * y);
        if (len == 0) return new Vec(0, 0);
        return new Vec(x / len, y / len);
    }

    public double dot(Vec other) {
        return x * other.x() + y * other.y();
    }

    @Override
    public @NonNull String toString() {
        return "(" + x + "," + y + ")";
    }
}
