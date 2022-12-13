package com.sinch.ticketsystem.controller.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.sinch.ticketsystem.valueobject.TicketStatus;

public class TicketStatusWithCommentDto {

	@Min(value = 1L, message = "can not be null!")
	private Long id;

	@NotNull
	private TicketStatus ticketStatus;

	private CommentDto commentDto;
	
	public TicketStatusWithCommentDto() {
		// Empty constructor needed for Jackson.
	}

	public TicketStatusWithCommentDto(Long id, TicketStatus ticketStatus, CommentDto commentDto) {
		super();
		this.id = id;
		this.ticketStatus = ticketStatus;
		this.commentDto = commentDto;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TicketStatus getTicketStatus() {
		return ticketStatus;
	}

	public void setTicketStatus(TicketStatus ticketStatus) {
		this.ticketStatus = ticketStatus;
	}

	public CommentDto getCommentDto() {
		return commentDto;
	}

	public void setCommentDto(CommentDto commentDto) {
		this.commentDto = commentDto;
	}
}
