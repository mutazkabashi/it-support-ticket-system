package com.sinch.ticketsystem.mapper;

import org.springframework.stereotype.Service;

import com.sinch.ticketsystem.controller.dto.TicketDto;
import com.sinch.ticketsystem.domain.Ticket;

@Service
public class TicketMapper {

	public Ticket mapToTicket(TicketDto ticketDto) {
		return new Ticket(ticketDto.getTitle(), ticketDto.getDescription(), ticketDto.getCreatedBy(),
				ticketDto.getAssignedTo());
	}

	public TicketDto mapToTicketDto(Ticket ticket) {
		return new TicketDto(ticket.getId(), ticket.getCreationDate(), ticket.getTitle(), ticket.getDescription(),
				ticket.getTicketStatus(), ticket.getCreatedBy(), ticket.getAssignedTo(), null);
	}
}
