package org.ub.utilbot.entities;

import java.sql.Time;
import java.text.MessageFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

@Entity(name = "Meeting")
public class Meeting {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(
		name = "UUID",
		strategy = "org.hibernate.id.UUIDGenerator"
	)
	@Column(name = "id", updatable = false, nullable = false)
	private String id;

	@Column(name = "refTutorId", nullable = false)
	private String refTutorId;

	@Column(name = "refProfId", nullable = false)
	private String refProfId;

	@Column(name = "GroupNumber", nullable = false)
	private int groupNumber;

	@Column(name = "Link", nullable = false)
	private String link;

	@Column(name = "Weekday", nullable = false)
	private Integer weekday;

	@Column(name = "StartTime", nullable = false)
	private Time startTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRefTutorId() {
		return refTutorId;
	}

	public void setRefTutorId(String refTutorId) {
		this.refTutorId = refTutorId;
	}

	public String getRefProfId() {
		return refProfId;
	}

	public void setRefProfId(String refProfId) {
		this.refProfId = refProfId;
	}

	public int getGroupNumber() {
		return groupNumber;
	}

	public void setGroupNumber(int groupNumber) {
		this.groupNumber = groupNumber;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Integer getWeekday() {
		return weekday;
	}

	public void setWeekday(Integer weekday) {
		this.weekday = weekday;
	}

	public Time getStartTime() {
		return startTime;
	}

	public void setStartTime(Time startTime) {
		this.startTime = startTime;
	}

	@Override
	public String toString() {
		return MessageFormat.format("Meeting[id={0}, refTutorId={1}, refProfId={2}, " +
						"GroupNumber={3}, Link={4}, weekday={5}, starttime={6}]",
				this.id,
				this.refTutorId,
				this.refProfId,
				this.groupNumber,
				this.link,
				this.weekday,
				this.startTime);
	}

}
