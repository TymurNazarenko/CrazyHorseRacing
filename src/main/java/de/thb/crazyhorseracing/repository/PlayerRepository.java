package de.thb.crazyhorseracing.repository;

import de.thb.crazyhorseracing.entity.Player;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.data.repository.query.Param;

@RepositoryDefinition(domainClass = Player.class, idClass = String.class)
public interface PlayerRepository extends CrudRepository<Player, String> {
}
