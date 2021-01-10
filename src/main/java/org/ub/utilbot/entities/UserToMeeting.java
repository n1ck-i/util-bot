package org.ub.utilbot.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

@Entity(name = "UserToMeeting")
public class UserToMeeting {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(
		name = "UUID",
		strategy = "org.hibernate.id.UUIDGenerator"
	)
	@Column(name = "id", updatable = false, nullable = false)
	private String id;

	@Column(name = "refUserId", nullable = false)
	private String refUserId;

	@Column(name = "refMeetingId", nullable = false)
	private String refMeetingId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRefUserId() {
		return refUserId;
	}

	public void setRefUserId(String refUserId) {
		this.refUserId = refUserId;
	}

	public String getRefMeetingId() {
		return refMeetingId;
	}

	public void setRefMeetingId(String refMeetingId) {
		this.refMeetingId = refMeetingId;
	}

	@Override
	public String toString() {
		return "Stuff";//"UserToMeeting[id=%s, refUserId=%s, refMeetingId=%s]"
			//.formatted(this.id, this.refUserId, this.refMeetingId);
	}

}
