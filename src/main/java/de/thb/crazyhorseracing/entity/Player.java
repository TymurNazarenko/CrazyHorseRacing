package de.thb.crazyhorseracing.entity;

import lombok.Getter;
import lombok.NonNull;

import java.util.Optional;

public class Player {
    private String id;

    @Getter
    private Optional<HorseType> horseType;

    public Player(String id, HorseType selectedHorseType) {
        this.id = id;
        this.horseType = Optional.ofNullable(selectedHorseType);
    }

    public Player(String id) {
        this(id, null);
    }

    public void setHorseType(@NonNull HorseType horseType) {
        this.horseType = Optional.of(horseType);
    }

    public String secretId() {
        return id;
    }
}
