package de.thb.crazyhorseracing.entity;

public record Wall(Hitbox hitbox) implements AbsoluteHitboxObject {
    public Hitbox getAbsoluteHitbox() {
        return hitbox;
    }
}
