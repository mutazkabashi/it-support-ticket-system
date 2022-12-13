package com.sinch.ticketsystem.domain;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "comment")
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Instant creationDate = Instant.now();

	@Column(nullable = false)
	@NotNull(message = "Comment's information can not be null!")
	private String information;

	@Column(nullable = false)
	@NotNull(message = "CreatedBy can not be null!")
	private String createdBy;

	/*
	 * It is better to map @OneToMany relationship from the child side (performance wise)
	 * see https://vladmihalcea.com/the-best-way-to-map-a-onetomany-association-with-jpa-and-hibernate/
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ticket_id", nullable = false)
	private Ticket ticket;

	@SuppressWarnings("unused")
	private Comment() {
	}

	public Comment(Long id/*, Instant creationDate*/, String information, String createdBy, Ticket ticket) {
		super();
		this.id = id;
		this.creationDate = Instant.now();
		this.information = information;
		this.createdBy = createdBy;
		this.ticket = ticket;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Instant getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Instant creationDate) {
		this.creationDate = creationDate;
	}

	public String getInformation() {
		return information;
	}

	public void setInformation(String information) {
		this.information = information;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Ticket getTicket() {
		return ticket;
	}

	public void setTicket(Ticket ticket) {
		this.ticket = ticket;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Comment)) {
			return false;
		}
		return id != null && id.equals(((Comment) o).id);
	}

	@Override
	public int hashCode() {
		// see  https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
		return getClass().hashCode();
	}

	@Override
	public String toString() {
		return "Comment [id=" + id + ", creationDate=" + creationDate + ", information=" + information + ", createdBy="
				+ createdBy + ", ticket=" + ticket + "]";
	}
}
