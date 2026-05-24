package de.thb.crazyhorseracing.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Horse {
    @Getter
    private static final long MOVE_DELAY_NS = 1_000_000_000;
    @Getter
    private static final Vel UP_VEL = new Vel(0, -20);
    @Getter
    private static final Vel DOWN_VEL = new Vel(0, 20);
    @Getter
    private static final Vel LEFT_VEL = new Vel(-20, 0);
    @Getter
    private static final Vel RIGHT_VEL = new Vel(20, 0);

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
    private Vec vec;
    @Getter
    @Setter
    private Vel vel;

    @Getter
    @JsonIgnore
    private final AtomicLong lastPlayerMoveTime = new AtomicLong(0);

    public Horse(HorseType type, Player player, Vec vec) {
        this.type = type;
        this.player = player;
        this.vec = vec;
        this.vel = new Vel(0,0);
        this.id = idCounter.getAndIncrement();
    }

    public Hitbox getAbsoluteHitbox() {
        return type.hitbox().withDisplacement(vec);
    }

    public void reflectOnCollision(List<Vec> intersections) {
        if (intersections.isEmpty()) return;
        if (intersections.size() == 1) {
            // TODO this edge-case (is it even possible? hopefully not)
            return;
        }

        // Step 1: Find the nearest two collision points
        List<Vec> closestTwoIntersections = intersections.stream()
                .sorted(Comparator.comparingDouble(e -> e.dist(vec)))
                .limit(2)
                .toList();
        Vec first = closestTwoIntersections.get(0);
        Vec second = closestTwoIntersections.get(1);

        // Step 2: Reflect off the line made by those two collision points
        Vec intersectionLine = first.subtract(second);
        Vec intersectionLineNormal = new Vec(-intersectionLine.getY(), intersectionLine.getX()).normalized();
        double dot = intersectionLineNormal.dot(vel);
        vel = new Vel(vel.subtract(intersectionLineNormal.multiply(2*dot))); // v′ = v − 2(v⋅n)n
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
                vel.addVelocity(UP_VEL);
                setMoveTime();
            }
            case MOVE_DOWN -> {
                vel.addVelocity(DOWN_VEL);
                setMoveTime();
            }
            case MOVE_LEFT -> {
                vel.addVelocity(LEFT_VEL);
                setMoveTime();
            }
            case MOVE_RIGHT -> {
                vel.addVelocity(RIGHT_VEL);
                setMoveTime();
            }
        }
        return true;
    }
}
