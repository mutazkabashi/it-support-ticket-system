package com.sinch.ticketsystem.service;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sinch.ticketsystem.domain.Comment;
import com.sinch.ticketsystem.domain.Ticket;
import com.sinch.ticketsystem.exception.EntityNotFoundException;
import com.sinch.ticketsystem.valueobject.TicketStatus;


/**
 * This service acts as a Facade service which provides Ticket and comment services
 * to the service client. instead of creating 2 services for Ticket and Comment,
 * @author mutaz
 *
 */
public interface TicketSupportService {

	/**
	 * Return A {@link Ticket} using ticket's id
	 * @param id ticket-id
	 * @return
	 * @throws EntityNotFoundException
	 */
	public Ticket getTicketById(Long id) throws EntityNotFoundException; 
	
	/**
	 * Returns All {@link tickets} from the Database/Data source
	 * @param pageable 
	 * @return
	 */
	public Page<Ticket> getAllTickets(Pageable pageable);
	
	/**
	 * Returns {@link Ticket}s using tickets createdBy field
	 * @param customerId email of the customer
	 * @param pageable
	 * @return
	 */
	//FIXME add excption notfound
	public Page<Ticket> getTicketsByCustomerId(String customerId, Pageable pageable); 
	
	/**
	 * Create a {@link Ticket} using full ticket object
	 * @param ticket Ticket object
	 * @return
	 */
	public Ticket createTicket(Ticket ticket);
	
	/**
	 * Update a {@link Ticket} using ticket's id, ticket's status and (optional) {@link Comment}
	 * @param ticketId ticket id
	 * @param ticketStatus ticket status
	 * @param comment   Comment Object
	 */
	public void updateTicket(Long ticketId, TicketStatus ticketStatus, Comment comment); 
	
	/**
	 * Create a {@link Comment} using Comment Object
	 * @param comment Comment object
	 * @return
	 */
	public Comment createComment(Comment comment) throws ConstraintViolationException; 
	
	/**
	 * Return {@link Comment}(s) using ticket 
	 * @param ticket
	 * @param pageable
	 * @return
	 */
	public Page<Comment> getCommentsByTicket(Ticket ticket, Pageable pageable);

}
