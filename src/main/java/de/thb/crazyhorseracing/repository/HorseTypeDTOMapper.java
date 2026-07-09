package de.thb.crazyhorseracing.repository;

import de.thb.crazyhorseracing.entity.*;

import java.util.List;

public class HorseTypeDTOMapper implements DTOMapper<HorseTypeDTO,HorseType> {
    public HorseType toDomain(HorseTypeDTO dto) {
        return new HorseType(
                dto.id(),
                dto.imagePath(),
                new Hitbox(mapVecList(dto.hitbox()))
        );
    }

    private static List<Vec> mapVecList(List<List<Double>> points) {
        return points.stream()
                .map(p -> new Vec(p.get(0), p.get(1)))
                .toList();
    }
}
