package de.thb.crazyhorseracing.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Horse implements AbsoluteHitboxObject {
    @Getter
    private static final long MOVE_DELAY_NS = 1_000_000_000; // 1 second
    @Getter
    private static final Vec UP_VEL = new Vec(0, -20);
    @Getter
    private static final Vec DOWN_VEL = new Vec(0, 20);
    @Getter
    private static final Vec LEFT_VEL = new Vec(-20, 0);
    @Getter
    private static final Vec RIGHT_VEL = new Vec(20, 0);

    @Getter
    private static final AtomicInteger idCounter = new AtomicInteger(0);
    @Getter
    private final int id;

    @Getter
    private final HorseType type;
    @Getter
    private final Player player;

    @Getter
    @Setter
    private Vec pos;
    @Getter
    @Setter
    private Vec velocity;

    @Getter
    @JsonIgnore
    private final AtomicLong lastPlayerMoveTime = new AtomicLong(0);

    private static final long COLLISION_DELAY_NS = 200_000_000; // 0.2 seconds
    private long lastCollisionTime = 0;
    private AbsoluteHitboxObject lastCollisionObj = null;

    public Horse(HorseType type, Player player, Vec pos) {
        this.type = type;
        this.player = player;
        this.pos = pos;
        this.velocity = new Vec(0,0);
        this.id = idCounter.getAndIncrement();
    }

    public Hitbox getAbsoluteHitbox() {
        return type.hitbox().withDisplacement(pos);
    }

    public void reflectIfColliding(AbsoluteHitboxObject obj) {
        if (obj == lastCollisionObj && System.nanoTime() - lastCollisionTime < COLLISION_DELAY_NS) return;

        List<Vec> intersections = getAbsoluteHitbox().getIntersections(obj.getAbsoluteHitbox());
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
