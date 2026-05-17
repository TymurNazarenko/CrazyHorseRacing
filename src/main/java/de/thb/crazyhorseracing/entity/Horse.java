package de.thb.crazyhorseracing.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicLong;

public class Horse {
    @Getter
    private static final long MOVE_DELAY_NS = 1_000_000_000;
    @Getter
    private static final double MOVE_VELOCITY = 1.0;
    @Getter
    private static final Velocity UP_VELOCITY = new  Velocity(0, 1);
    @Getter
    private static final Velocity DOWN_VELOCITY = new  Velocity(0, -1);
    @Getter
    private static final Velocity LEFT_VELOCITY = new  Velocity(-1, 0);
    @Getter
    private static final Velocity RIGHT_VELOCITY = new  Velocity(1, 0);

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
    private final AtomicLong lastPlayerMoveTime = new AtomicLong(0);

    public Horse(HorseType type, Player player, Vec vec) {
        this.type = type;
        this.player = player;
        this.vec = vec;
        this.velocity = new Velocity(0,0);
    }

    private void setMoveTime() {
        lastPlayerMoveTime.set(System.nanoTime());
    }

    public boolean hasEnoughTimeSinceLastMoveElapsed() {
        return System.nanoTime() - lastPlayerMoveTime.get() > MOVE_DELAY_NS;
    }

    public boolean Move(MoveType moveType) {
        if (!hasEnoughTimeSinceLastMoveElapsed()) return false;

        return switch (moveType) {
            case MOVE_UP -> {
                velocity.addVelocity(UP_VELOCITY);
                setMoveTime();
                yield true;
            }
            case MOVE_DOWN -> {
                velocity.addVelocity(DOWN_VELOCITY);
                setMoveTime();
                yield true;
            }
            case MOVE_LEFT -> {
                velocity.addVelocity(LEFT_VELOCITY);
                setMoveTime();
                yield true;
            }
            case MOVE_RIGHT -> {
                velocity.addVelocity(RIGHT_VELOCITY);
                setMoveTime();
                yield true;
            }
            default -> false;
        };
    }
}
