package de.thb.crazyhorseracing.entity;

import java.util.ArrayList;
import java.util.List;

public record Hitbox(List<Vec> vertices) {
    // Returns the points where the hitboxes intersect
    public List<Vec> getIntersections(Hitbox hitbox) {
        ArrayList<Vec> intersections = new ArrayList<>();
        // TODO
        return intersections;
    }

    public Hitbox withDisplacement(Vec displacement) {
        ArrayList<Vec> new_vertices = new ArrayList<>();
        for (Vec vec : vertices) {
            new_vertices.add(vec.add(displacement));
        }
        return new Hitbox(new_vertices);
    }
}
