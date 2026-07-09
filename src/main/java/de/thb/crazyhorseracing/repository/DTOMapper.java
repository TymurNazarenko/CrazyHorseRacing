package de.thb.crazyhorseracing.repository;

public interface DTOMapper<F,T> {
    T toDomain(F from);
}
