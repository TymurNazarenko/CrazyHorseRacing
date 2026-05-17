package de.thb.crazyhorseracing.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Vector;

public class Velocity {
    @Setter
    @Getter
    private double x, y;

    public Velocity(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void addVelocity(Velocity vel) {
        this.x += vel.getX();
        this.y += vel.getY();
    }

    public void invertAlongDirection() { // TODO this function
        this.x *= -1;
        this.y *= -1;
    }
}
