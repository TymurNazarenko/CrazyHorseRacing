package de.thb.crazyhorseracing.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.thb.crazyhorseracing.repository.HorseTypeListLoader;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Entity
public class Player {
    @Getter
    @Setter
    @Id
    @JsonIgnore
    private String jid;

    @Getter
    @Setter
    private String username; // anonymous username to differentiate players

    @Getter
    @JsonIgnore
    @Transient
    private HorseType horseType;

    @Getter
    @Setter
    @JsonIgnore
    private int wins = 0;
    @Getter
    @Setter
    @JsonIgnore
    private int playedGames = 0;

    public Player(String jid, HorseType selectedHorseType) {
        this.jid = jid;
        if (selectedHorseType != null) {
            this.horseType = selectedHorseType;
        } else {
            this.horseType = HorseTypeListLoader.getHorseById(1);
        }
        this.username = "ducky momo"; // TODO generate randomly
    }

    public Player(String jid) {
        this(jid, null);
    }

    protected Player() {}

    public void setHorseType(@NonNull HorseType horseType) {
        this.horseType = horseType;
    }

    public void addWin() {
        wins++;
    }

    public void addGame() {
        playedGames++;
    }
}
