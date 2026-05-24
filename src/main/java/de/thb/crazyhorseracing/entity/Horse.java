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
    private static final Velocity UP_VELOCITY = new Velocity(0, -20);
    @Getter
    private static final Velocity DOWN_VELOCITY = new Velocity(0, 20);
    @Getter
    private static final Velocity LEFT_VELOCITY = new Velocity(-20, 0);
    @Getter
    private static final Velocity RIGHT_VELOCITY = new Velocity(20, 0);

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
    private Velocity velocity;

    @Getter
    @JsonIgnore
    private final AtomicLong lastPlayerMoveTime = new AtomicLong(0);

    public Horse(HorseType type, Player player, Vec vec) {
        this.type = type;
        this.player = player;
        this.vec = vec;
        this.velocity = new Velocity(0,0);
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
        // Step 2: Reflect off the line made by those two collision points
        // TODO
        velocity.setX(0); velocity.setY(0); // This is placeholder code to just check if the collision checking even makes sense.
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
                velocity.addVelocity(UP_VELOCITY);
                setMoveTime();
            }
            case MOVE_DOWN -> {
                velocity.addVelocity(DOWN_VELOCITY);
                setMoveTime();
            }
            case MOVE_LEFT -> {
                velocity.addVelocity(LEFT_VELOCITY);
                setMoveTime();
            }
            case MOVE_RIGHT -> {
                velocity.addVelocity(RIGHT_VELOCITY);
                setMoveTime();
            }
        }
        return true;
    }
}
