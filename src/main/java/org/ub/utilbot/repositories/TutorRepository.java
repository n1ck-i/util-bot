package org.ub.utilbot.repositories;

import org.springframework.data.repository.CrudRepository;
import org.ub.utilbot.entities.Tutor;

public interface TutorRepository extends CrudRepository<Tutor, Integer> {

	Tutor findById(String id);
	Tutor findByName(String name);
	Iterable<Tutor> findAll();
}
