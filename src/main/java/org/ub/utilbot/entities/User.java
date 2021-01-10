package org.ub.utilbot.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

@Entity(name = "User")
public class User {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(
		name = "UUID",
		strategy = "org.hibernate.id.UUIDGenerator"
	)
	@Column(name = "id", updatable = false, nullable = false)
	private String id;

	@Column(name = "DiscordId", nullable = false)
	private String discordId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDiscordId() {
		return discordId;
	}

	public void setDiscordId(String discordId) {
		this.discordId = discordId;
	}

	@Override
	public String toString() {
		return "Why?";//"User[id=%s, discordId=%s]".formatted(this.id, this.discordId);
	}

}
