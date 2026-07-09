package de.thb.crazyhorseracing.repository;

import de.thb.crazyhorseracing.entity.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.RepositoryDefinition;

@RepositoryDefinition(domainClass = Player.class, idClass = String.class)
public interface PlayerRepository extends CrudRepository<Player, String> {
}
