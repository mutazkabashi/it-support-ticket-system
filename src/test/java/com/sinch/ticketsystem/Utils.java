package com.sinch.ticketsystem;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.sinch.ticketsystem.controller.dto.CommentDto;
import com.sinch.ticketsystem.controller.dto.TicketDto;
import com.sinch.ticketsystem.domain.Comment;
import com.sinch.ticketsystem.domain.Ticket;
import com.sinch.ticketsystem.valueobject.TicketStatus;

public class Utils {

	public static final String EMAIL = "user@mail.com";

	public static Ticket getTicket() {
		return new Ticket("title", "description", EMAIL, null);
	}

	public static TicketDto getTicketDto() {
		return new TicketDto(1L, Instant.now(), "title", "", TicketStatus.INQUEUE, EMAIL, EMAIL, null);
	}

	public static Comment getComment(Ticket ticket) {
		return new Comment(null, "information", EMAIL, ticket);
	}

	public static CommentDto getCommentDto(String Information, String email, Long ticketId) {
		return new CommentDto(null, Instant.now(), Information, email, ticketId);
	}

	public static Pageable getPagable(int page, int size) {
		return PageRequest.of(page, size);
	}

	public static Page<Ticket> getPageOfTickets(Ticket ticket) {
		Page<Ticket> page = new PageImpl<>(List.of(ticket));
		return page;
	}

	public static Page<Comment> getPageOfComments(Comment comment) {
		Page<Comment> page = new PageImpl<>(List.of(comment));
		return page;
	}
}
