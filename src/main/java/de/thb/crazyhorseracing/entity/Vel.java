package de.thb.crazyhorseracing.entity;

import lombok.Getter;
import lombok.Setter;

public class Vel extends Vec {
    public Vel(double x, double y) {
        super(x,y);
    }

    public Vel(Vec vec) {
        super(vec.getX(),vec.getY());
    }

    public void addVelocity(Vel vel) {
        this.setX(this.getX() + vel.getX());
        this.setY(this.getY() + vel.getY());
    }
}
