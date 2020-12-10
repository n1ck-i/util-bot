package org.ub.utilbot.repositories;

import org.springframework.data.repository.CrudRepository;
import org.ub.utilbot.entities.UserToMeeting;

public interface UserToMeetingRepository extends CrudRepository<UserToMeeting, Integer> {

	UserToMeeting findById(String id);
	Iterable<UserToMeeting> findAll();
	Iterable<UserToMeeting> findByRefUserId(String refUserId);
	Iterable<UserToMeeting> findByRefMeetingId(String refMeetingId);
}
