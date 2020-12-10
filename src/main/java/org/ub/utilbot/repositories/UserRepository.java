package org.ub.utilbot.repositories;

import org.springframework.data.repository.CrudRepository;
import org.ub.utilbot.entities.User;

public interface UserRepository extends CrudRepository<User, Integer> {

	User findById(String id);
	User findByDiscordId(String discordId);
	Iterable<User> findAll();
}
