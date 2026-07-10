package de.thb.crazyhorseracing.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@JsonIncludeProperties({"id", "type", "player", "pos", "velocity"})
public class Horse implements AbsoluteHitboxObject {
    public static final long MOVE_DELAY_NS = 1_000_000_000; // 1 second
    public static final Vec UP_VEL = new Vec(0, -20);
    public static final Vec DOWN_VEL = new Vec(0, 20);
    public static final Vec LEFT_VEL = new Vec(-20, 0);
    public static final Vec RIGHT_VEL = new Vec(20, 0);
    public static final long COLLISION_DELAY_NS = 200_000_000; // 0.2 seconds

    public static final AtomicInteger idCounter = new AtomicInteger(0);
    public final int id;

    public final HorseType type;
    public final Player player;

    @Getter
    @Setter
    private double size = 1.0;

    @Getter
    @Setter
    private Vec pos;
    @Getter
    @Setter
    private Vec velocity;

    public final AtomicLong lastPlayerMoveTime = new AtomicLong(0);
    private long lastCollisionTime = 0;
    private AbsoluteHitboxObject lastCollisionObj = null;

    public Horse(HorseType type, Player player, Vec pos, double size) {
        this.type = type;
        this.size = size;
        this.player = player;
        this.pos = pos;
        this.velocity = new Vec(0,0);
        this.id = idCounter.getAndIncrement();
    }

    public Hitbox getAbsoluteHitbox() {
        return type.hitbox().multiply(size).withDisplacement(pos);
    }

    public void reflectIfColliding(AbsoluteHitboxObject obj) {
        if (obj == lastCollisionObj && System.nanoTime() - lastCollisionTime < COLLISION_DELAY_NS) return;

        List<Vec> intersections = Hitbox.getIntersections(getAbsoluteHitbox(), obj.getAbsoluteHitbox());
        if (intersections.isEmpty()) return;
        if (intersections.size() == 1) {
            // TODO this edge-case (is it even possible? hopefully not)
            return;
        }

        // Step 1: Find the nearest two collision points
        List<Vec> closestTwoIntersections = intersections.stream()
                .sorted(Comparator.comparingDouble(e -> e.dist(pos)))
                .limit(2)
                .toList();
        Vec first = closestTwoIntersections.get(0);
        Vec second = closestTwoIntersections.get(1);

        // Step 1.5: Check if velocity already points away from the intersection. We don't want to reflect the velocity in that case
        Vec intersectionMidpoint = first.add(second).multiply(0.5);
        Vec towardsIntersection = intersectionMidpoint.subtract(pos);
        double angleCos = velocity.normalized().dot(towardsIntersection.normalized());
        if (angleCos <= 0) return; // If the cos is lower than 0, the direction towards the intersection and the velocity don't point into the same general direction (and maybe point in completely different directions)

        // Step 2: Reflect off the line made by those two collision points
        Vec intersectionLine = first.subtract(second);
        Vec intersectionLineNormal = new Vec(-intersectionLine.getY(), intersectionLine.getX()).normalized();
        double dot = intersectionLineNormal.dot(velocity);
        velocity = velocity.subtract(intersectionLineNormal.multiply(2*dot)); // v′ = v − 2(v⋅n)n

        // TODO push the horse so far along the normal that it no longer collides?

        lastCollisionObj = obj;
        lastCollisionTime = System.nanoTime();
    }

    private void setMoveTime() {
        lastPlayerMoveTime.set(System.nanoTime());
    }

    public boolean hasEnoughTimeSinceLastMoveElapsed() {
        return System.nanoTime() - lastPlayerMoveTime.get() > MOVE_DELAY_NS;
    }

    public boolean Move(MoveType moveType) {
        if (!hasEnoughTimeSinceLastMoveElapsed()) return false;

        switch (moveType) {
            case MOVE_UP -> {
                velocity.addInPlace(UP_VEL);
                setMoveTime();
            }
            case MOVE_DOWN -> {
                velocity.addInPlace(DOWN_VEL);
                setMoveTime();
            }
            case MOVE_LEFT -> {
                velocity.addInPlace(LEFT_VEL);
                setMoveTime();
            }
            case MOVE_RIGHT -> {
                velocity.addInPlace(RIGHT_VEL);
                setMoveTime();
            }
        }
        return true;
    }
}
