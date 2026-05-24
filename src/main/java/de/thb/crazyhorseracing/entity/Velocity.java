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

    public void reflectOnCollision(List<Vec> intersections, Vec origin) {
        if (intersections.isEmpty()) return;
        if (intersections.size() == 1) {
            // TODO this edge-case (is it even possible? hopefully not)
            return;
        }

        // Step 1: Find the nearest two collision points
        List<Vec> closestTwoIntersections = intersections.stream()
                                            .sorted(Comparator.comparingDouble(e -> e.dist(origin)))
                                            .limit(2)
                                            .toList();
        // Step 2: Reflect off the line made by those two collision points
        // TODO
        x = 0; y = 0; // This is placeholder code to just check if the collision checking even makes sense. And it does make sense! Yay!
    }
}
