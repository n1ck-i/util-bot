package org.ub.utilbot.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import java.text.MessageFormat;

@Entity(name = "Professor")
public class Professor {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(
		name = "UUID",
		strategy = "org.hibernate.id.UUIDGenerator"
	)
	@Column(name = "id", updatable = false, nullable = false)
	private String id;

	@Column(name = "Name", nullable = false)
	private String name;

	@Column(name = "Subject", nullable = false)
	private String subject;

	@Column(name = "ChannelId", nullable = false)
	private String channelId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	@Override
	public String toString() {
		return MessageFormat.format("Professor[id={0}, name={1}, subject={2}, channelId={3}]" ,
				this.id, this.name, this.subject, this.channelId);
	}

}
