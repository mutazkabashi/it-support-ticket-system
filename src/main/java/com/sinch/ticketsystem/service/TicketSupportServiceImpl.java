package com.sinch.ticketsystem.service;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sinch.ticketsystem.domain.Comment;
import com.sinch.ticketsystem.domain.Ticket;
import com.sinch.ticketsystem.exception.EntityNotFoundException;
import com.sinch.ticketsystem.repository.CommentRepository;
import com.sinch.ticketsystem.repository.TicketRepository;
import com.sinch.ticketsystem.valueobject.TicketStatus;

@Service
@Transactional
public class TicketSupportServiceImpl implements TicketSupportService {
	TicketRepository ticketRepository;
	CommentRepository commentRepository;

	public TicketSupportServiceImpl(TicketRepository ticketRepository, CommentRepository commentRepository) {
		super();
		this.ticketRepository = ticketRepository;
		this.commentRepository = commentRepository;
	}

	public Ticket getTicketById(Long id) throws EntityNotFoundException {
		return ticketRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Ticket not found for ticketId " + id));
	}

	public Page<Ticket> getAllTickets(Pageable pageable) {
		return ticketRepository.findAll(pageable);
	}

	public Page<Ticket> getTicketsByCustomerId(String customerId, Pageable pageable) {
		return ticketRepository.findByCreatedBy(customerId, pageable);
	}

	public Ticket createTicket(Ticket ticket) {
		return ticketRepository.save(ticket);
	}

	public void updateTicket(Long ticketId, TicketStatus ticketStatus, Comment comment) {
		ticketRepository.updateTicketStatus(ticketId, ticketStatus);
		if (comment != null) {
			Ticket ticket = new Ticket();
			ticket.setId(ticketId);
			comment.setTicket(ticket);
			commentRepository.save(comment);
		}
	}

	public Comment createComment(Comment comment) throws ConstraintViolationException {
		return commentRepository.save(comment);
	}

	public Page<Comment> getCommentsByTicket(Ticket ticket, Pageable pageable) {
		return commentRepository.findByTicketId(ticket.getId(), pageable);
	}
}
