package de.thb.crazyhorseracing.entity;

import lombok.Getter;
import lombok.Setter;

public class Vec {
    @Getter
    @Setter
    private double x, y;

    public Vec(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // NOT Pure
    public void applyVelocity(Vel vel, double dt) { // the only non-pure function, used only for the horses
        this.x += vel.getX() * dt;
        this.y += vel.getY() * dt;
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
    public Vec subtract(Vec other) {
        return new Vec(x - other.x, y - other.y);
    }

    // Pure
    public Vec multiply(double m) {
        return new  Vec(x * m, y * m);
    }

    // Pure
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
