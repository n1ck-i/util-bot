package org.ub.utilbot.repositories;

import java.sql.Time;

import org.springframework.data.repository.CrudRepository;
import org.ub.utilbot.entities.Meeting;

public interface MeetingRepository extends CrudRepository<Meeting, Integer> {

	Meeting findById(String id);
	Iterable<Meeting> findByWeekday(int weekday);
	Iterable<Meeting> findByStartTime(Time startTime);
	Iterable<Meeting> findAll();
}
