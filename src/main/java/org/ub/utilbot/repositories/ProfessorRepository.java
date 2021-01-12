package org.ub.utilbot.repositories;

import org.springframework.data.repository.CrudRepository;
import org.ub.utilbot.entities.Professor;

public interface ProfessorRepository extends CrudRepository<Professor, Integer> {

	Professor findById(String id);
	Iterable<Professor> findAll();
	Professor findBySubject(String subject);
	Professor findByChannelId(String channelId);
}
