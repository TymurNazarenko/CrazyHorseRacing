package de.thb.crazyhorseracing.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import de.thb.crazyhorseracing.repository.HorseTypeProvider;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Entity
@JsonIncludeProperties({"username", "horseType"})
public class Player {
    @Getter
    @Setter
    private static HorseType defaultHorseType;

    @Getter
    @Setter
    @Id
    private String jid;

    @Getter
    @Setter
    private String username; // anonymous username to differentiate players

    @Getter
    @Transient
    private HorseType horseType;

    @Getter
    @Setter
    private int wins = 0;
    @Getter
    @Setter
    private int playedGames = 0;

    public Player(String jid, HorseType selectedHorseType) {
        this.jid = jid;
        if (selectedHorseType != null) {
            this.horseType = selectedHorseType;
        } else {
            this.horseType = defaultHorseType;
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
