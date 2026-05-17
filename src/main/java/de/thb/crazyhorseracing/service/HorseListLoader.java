package de.thb.crazyhorseracing.service;

import de.thb.crazyhorseracing.entity.Vec;
import de.thb.crazyhorseracing.entity.Hitbox;
import de.thb.crazyhorseracing.entity.HorseType;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class HorseListLoader {
    private List<HorseType> horseTypes;

    @PostConstruct
    public void init() {
        horseTypes = new ArrayList<>();
        // TODO load these from files instead
        Hitbox basichitbox = new Hitbox(List.of(new Vec(-1,-1), new Vec(-1,1), new Vec(1,1), new Vec(1,-1)));
        horseTypes.add(new HorseType(1, "/images/pink.jpg", basichitbox));
        horseTypes.add(new HorseType(2, "/images/cyan.jpg", basichitbox));
        horseTypes = Collections.unmodifiableList(horseTypes);
    }
    public Optional<HorseType> getHorseById(long id) {
        return horseTypes.stream().filter(h -> h.id() == id).findFirst();
    }

    public List<HorseType> copyHorses() { // returns a shallow copy of the horses array. We don't return the original array to prevent manipulation of the list
        return new ArrayList<>(horseTypes);
    }
}
