package de.thb.crazyhorseracing.entity;

import lombok.Getter;

public class Vec {
    @Getter
    private double x, y;

    public Vec(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void applyVelocity(Velocity velocity, double dt) { // the only non-pure function, used only for the horses
        this.x += velocity.getX() * dt;
        this.y += velocity.getY() * dt;
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
}
