package de.thb.crazyhorseracing.entity;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import de.thb.crazyhorseracing.service.RandomNameGenerator;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Entity
@JsonIncludeProperties({"UUID", "username"})
public class Player {
    @Getter
    @Setter
    private static HorseType defaultHorseType;

    @Getter
    @Setter
    private String jid;

    @Getter
    @Setter
    @Id
    private String UUID;

    // The login+passwordHash combo is optional
    @Getter
    @Setter
    private String login;
    @Getter
    @Setter
    private String passwordHash;

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
        this.horseType = (selectedHorseType != null) ? selectedHorseType : defaultHorseType;
        this.username = RandomNameGenerator.generateRandomName();
        this.UUID = java.util.UUID.randomUUID().toString();
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
