package com.sinch.ticketsystem.domain;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.sinch.ticketsystem.valueobject.TicketStatus;

@Entity
@Table(name = "ticket")
public class Ticket {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Instant creationDate = Instant.now();

	@Column(nullable = false)
	@NotNull(message = "Ticket's title field can not be null!")
	private String title;

	@Column(nullable = false)
	@NotNull(message = "Ticket's Description field can not be null!")
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TicketStatus ticketStatus;

	@Column(nullable = false)
	@NotNull(message = "User's field can not be null!")
	private String createdBy;

	@Column
	private String assignedTo;
	
	public Ticket() {
		
	}

	public Ticket(String title, String description, String createdBy, String assignedTo) {
		this.title = title;
		this.description = description;
		this.ticketStatus = TicketStatus.INQUEUE;
		this.createdBy = createdBy;
		this.assignedTo = assignedTo;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public TicketStatus getTicketStatus() {
		return ticketStatus;
	}

	public void setTicketStatus(TicketStatus ticketStatus) {
		this.ticketStatus = ticketStatus;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Ticket)) {
			return false;
		}
		return id != null && id.equals(((Ticket) o).id);
	}

	@Override
	public int hashCode() {
		// see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
		return getClass().hashCode();
	}

	@Override
	public String toString() {
		return "Ticket [id=" + id + ", dateCreated=" + creationDate + ", title=" + title + ", description="
				+ description + ", ticketStatus=" + ticketStatus + ", user=" + createdBy + ", agent=" + assignedTo
				+ "]";
	}
}
