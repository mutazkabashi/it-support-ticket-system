package com.sinch.ticketsystem.controller.dto;

import java.time.Instant;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CommentDto {

	private Long id;

	private Instant creationDate;

	@NotBlank(message = "can not be null!")
	private String information;

	@NotBlank
	@Email
	@Size(min = 5, max = 254)
	private String createdBy;

	@NotNull
	@Min(value = 0L, message = "TicketId must be positive")
	private Long ticketId;

	public CommentDto() {
		// Empty constructor needed for Jackson.
	}

	public CommentDto(Long id, Instant creationDate, String information, String createdBy, Long ticketId) {
		this.id = id;
		this.creationDate = creationDate;
		this.information = information;
		this.createdBy = createdBy;
		this.ticketId = ticketId;
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

	public Long getTicketId() {
		return ticketId;
	}

	public void setTicketId(Long ticketId) {
		this.ticketId = ticketId;
	}
}
