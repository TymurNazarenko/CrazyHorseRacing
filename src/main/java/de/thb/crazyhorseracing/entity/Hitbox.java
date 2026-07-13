package de.thb.crazyhorseracing.entity;

import java.util.ArrayList;
import java.util.List;

public record Hitbox(List<Vec> vertices) {
    public Hitbox withDisplacement(Vec displacement) {
        ArrayList<Vec> new_vertices = new ArrayList<>();
        for (Vec vec : vertices) {
            new_vertices.add(vec.add(displacement));
        }
        return new Hitbox(new_vertices);
    }

    public Hitbox multiply(double size) {
        ArrayList<Vec> new_vertices = new ArrayList<>();
        for (Vec vec : vertices) {
            new_vertices.add(vec.multiply(size));
        }
        return new Hitbox(new_vertices);
    }

    public Vec getAlgebraicCenter() {
        double avgX = 0;
        double avgY = 0;
        for (Vec vec : vertices) {
            avgX += vec.x();
            avgY += vec.y();
        }
        avgX /= vertices.size();
        avgY /= vertices.size();
        return new Vec(avgX, avgY);
    }
    private static final double EPS = 1e-9;

    // Check if point q lies on segment pr
    private static boolean onSegment(Vec p, Vec q, Vec r) {
        return q.x() <= Math.max(p.x(), r.x()) && q.x() >= Math.min(p.x(), r.x()) &&
                q.y() <= Math.max(p.y(), r.y()) && q.y() >= Math.min(p.y(), r.y());
    }

    private static Vec getIntersection(Vec p1, Vec p2, Vec p3, Vec p4) {
        double A1 = p2.y() - p1.y();
        double B1 = p1.x() - p2.x();
        double C1 = A1 * p1.x() + B1 * p1.y();

        // Line 2: A2x + B2y = C2
        double A2 = p4.y() - p3.y();
        double B2 = p3.x() - p4.x();
        double C2 = A2 * p3.x() + B2 * p3.y();

        double determinant = A1 * B2 - A2 * B1;

        // If determinant is 0 then the lines are parallel/collinear
        if (Math.abs(determinant) < EPS) {
            // In that case there would be infinite intersection points, so we don't return anything at all.
            return null;
        }

        // Compute intersection point
        double x = (B2 * C1 - B1 * C2) / determinant;
        double y = (A1 * C2 - A2 * C1) / determinant;

        Vec intersection = new Vec(x, y);

        // Ensure the point lies within both segments
        if (onSegment(p1, intersection, p2) &&
                onSegment(p3, intersection, p4)) {
            return intersection;
        }

        return null;
    }

    public static List<Vec> getIntersections(Hitbox a, Hitbox b) {
        ArrayList<Vec> intersections = new ArrayList<>();
        for (int i = 0; i <= a.vertices().size() - 1; i++) {
            Vec a_p1 = a.vertices().get(i);
            Vec a_p2 = a.vertices().get((i+1) % a.vertices.size());
            for (int j = 0; j < b.vertices().size(); j++) {
                Vec b_p1 = b.vertices().get(j);
                Vec b_p2 = b.vertices().get((j+1) % b.vertices().size());
                Vec intersection = getIntersection(a_p1, a_p2, b_p1, b_p2);
                if (intersection == null) continue;
                intersections.add(intersection);
            }
        }
        return intersections;
    }
}
