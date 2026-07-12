package de.thb.crazyhorseracing.entity;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import de.thb.crazyhorseracing.service.RandomNameGenerator;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Entity
@JsonIncludeProperties({"UUID", "nickname"})
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

    // The login+passwordHash combo is an optional way to log in
    @Getter
    @Setter
    private String login;
    @Getter
    @Setter
    private String passwordHash;

    @Getter
    @Setter
    private String nickname; // The name which is shown to other users

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
        this.nickname = RandomNameGenerator.generateRandomName();
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
