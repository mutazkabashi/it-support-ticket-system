package com.sinch.ticketsystem.controller.dto;

import java.time.Instant;
import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.sinch.ticketsystem.domain.Ticket;
import com.sinch.ticketsystem.valueobject.TicketStatus;

public class TicketDto {

	private Long id;

	private Instant creationDate;

	@NotBlank
	private String title;

	@NotBlank
	private String description;

	private TicketStatus ticketStatus;

	@NotBlank
	@Email
	@Size(min = 5, max = 254)
	private String createdBy;

	@Email
	@Size(min = 5, max = 254)
	private String assignedTo;

	private List<CommentDto> Comments;

	public TicketDto() {
		// Empty constructor needed for Jackson.
	}
	
	public TicketDto(Long id, Instant creationDate, String title, String description,
			TicketStatus ticketStatus, String createdBy, String assignedTo, List<CommentDto> comments) {
		super();
		this.id = id;
		this.creationDate = creationDate;
		this.title = title;
		this.description = description;
		this.ticketStatus = ticketStatus;
		this.createdBy = createdBy;
		this.assignedTo = assignedTo;
		this.Comments =comments;
	}



	public TicketDto(Ticket ticket) {
		this.id = ticket.getId();
		this.creationDate = ticket.getCreationDate();
		this.title = ticket.getTitle();
		this.description = ticket.getDescription();
		this.ticketStatus = ticket.getTicketStatus();
		this.createdBy = ticket.getCreatedBy();
		this.assignedTo = ticket.getAssignedTo();
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

	public List<CommentDto> getComments() {
		return Comments;
	}

	public void setComments(List<CommentDto> comments) {
		Comments = comments;
	}
}
