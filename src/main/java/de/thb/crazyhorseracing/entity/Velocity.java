package de.thb.crazyhorseracing.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Velocity {
    @Getter
    @Setter
    private double x, y;

    public Velocity(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void addVelocity(Velocity vel) {
        this.x += vel.getX();
        this.y += vel.getY();
    }
}
