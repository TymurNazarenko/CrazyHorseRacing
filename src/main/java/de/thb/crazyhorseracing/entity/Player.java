package de.thb.crazyhorseracing.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NonNull;

import java.util.Optional;

public class Player {
    @Getter
    @JsonIgnore
    private final String id;

    @Getter
    @JsonIgnore
    private Optional<HorseType> horseType;

    @Getter
    private final String username; // anonymous username to differentiate players

    public Player(String id, HorseType selectedHorseType) {
        this.id = id;
        this.horseType = Optional.ofNullable(selectedHorseType);
        this.username = "ducky momo"; // TODO generate randomly
    }

    public Player(String id) {
        this(id, null);
    }

    public void setHorseType(@NonNull HorseType horseType) {
        this.horseType = Optional.of(horseType);
    }
}
