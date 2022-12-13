package com.sinch.ticketsystem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.sinch.ticketsystem.Utils;
import com.sinch.ticketsystem.domain.Comment;
import com.sinch.ticketsystem.domain.Ticket;
import com.sinch.ticketsystem.exception.EntityNotFoundException;
import com.sinch.ticketsystem.repository.CommentRepository;
import com.sinch.ticketsystem.repository.TicketRepository;
import com.sinch.ticketsystem.valueobject.TicketStatus;

@ExtendWith(MockitoExtension.class)
public class TicketSupportServiceImplTest {

	@Mock
	private TicketRepository ticketRepository;

	@Mock
	private CommentRepository commentRepository;

	@InjectMocks
	private TicketSupportServiceImpl ticketSupportServiceImpl;

	@Test
	public void whenCallGetTicketByValidId_thenReturnObject() throws EntityNotFoundException {
		// given
		Ticket ticket = Utils.getTicket();
		ticket.setId(1L);

		// when
		when(ticketRepository.findById(any())).thenReturn(Optional.ofNullable(ticket));

		// then
		Ticket returnedTicket = ticketSupportServiceImpl.getTicketById(1L);
		assertThat(returnedTicket.equals(ticket));

		// verify
		verify(ticketRepository, times(1)).findById(any());
	}

	@Test
	public void whenCallGetTicketByInExistedId_thenReturnException() {
		// when
		when(ticketRepository.findById(any())).thenReturn(Optional.empty());
		Exception exception = assertThrows(EntityNotFoundException.class, () -> {
			ticketSupportServiceImpl.getTicketById(0L);
		});

		String expectedMessage = "Ticket not found for ticketId 0";
		String actualMessage = exception.getMessage();

		// then
		assertTrue(actualMessage.contains(expectedMessage));

		// verify
		verify(ticketRepository, times(1)).findById(any());
	}

	@Test
	public void whenCallGetAllTicketsWithEmptyDB_thenReturnNothing() {
		// given
		Pageable pageable = Utils.getPagable(0, 5);
		Page<Ticket> page = new PageImpl<>(List.of());

		// when
		when(ticketRepository.findAll(pageable)).thenReturn(page);

		// then
		Page<Ticket> returnedPage = ticketSupportServiceImpl.getAllTickets(pageable);
		assertThat(returnedPage.getContent()).size().isEqualTo(0);

		// verify
		verify(ticketRepository, times(1)).findAll(pageable);
	}

	@Test
	public void whenCallGetAllTicketsWithNonEmptyDB_thenReturnObject() {
		// given
		Ticket ticket = Utils.getTicket();
		Pageable pageable = Utils.getPagable(0, 5);
		Page<Ticket> page = Utils.getPageOfTickets(ticket);

		// when
		when(ticketRepository.findAll(pageable)).thenReturn(page);

		// then
		Page<Ticket> returnedPage = ticketSupportServiceImpl.getAllTickets(pageable);
		assertThat(returnedPage.getContent()).size().isEqualTo(1);
		assertThat(returnedPage.getContent().get(0)).isEqualTo(ticket);

		// verify
		verify(ticketRepository, times(1)).findAll(pageable);
	}

	@Test
	public void whenCallGetTicketByValidCustomerId_thenReturnObject() {
		// given
		Ticket ticket = Utils.getTicket();

		// when
		when(ticketRepository.findByCreatedBy(any(), any())).thenReturn(Utils.getPageOfTickets(ticket));

		// then
		Page<Ticket> page = ticketSupportServiceImpl.getTicketsByCustomerId(Utils.EMAIL, Utils.getPagable(0, 5));
		assertThat(page.getContent()).size().isEqualTo(1);
		assertThat(page.getContent().get(0).getCreatedBy().equals(ticket.getCreatedBy()));

		// verify
		verify(ticketRepository, times(1)).findByCreatedBy(any(), any());
	}

	@Test
	public void whenCallCreateTicketWithValidTicket_thenReturnObject() {
		// given
		Ticket ticket = Utils.getTicket();

		// when
		when(ticketRepository.save(any())).thenReturn(ticket);

		// then
		Ticket returnedTicket = ticketSupportServiceImpl.createTicket(ticket);
		assertThat(returnedTicket.equals(ticket));

		// verify
		verify(ticketRepository, times(1)).save(any());
	}

	@Test
	public void whenCallUpdateTicketWithValidTicketAndComment_thenReturnNothing() {
		// given
		Ticket ticket = Utils.getTicket();
		ticket.setId(1L);
		Comment comment = Utils.getComment(ticket);

		// when
		doNothing().when(ticketRepository).updateTicketStatus(any(), any());
		when(commentRepository.save(any())).thenReturn(comment);

		// then
		ticketSupportServiceImpl.updateTicket(1L, TicketStatus.INPROGRESS, comment);

		// verify
		verify(ticketRepository, times(1)).updateTicketStatus(any(), any());
		verify(commentRepository, times(1)).save(any());
	}

	@Test
	public void whenCallUpdateTciketWithValidTicketAndNoComment_thenReturnNothing() {
		// given
		Ticket ticket = Utils.getTicket();
		ticket.setId(1L);

		// when
		doNothing().when(ticketRepository).updateTicketStatus(any(), any());

		// then
		ticketSupportServiceImpl.updateTicket(1L, TicketStatus.INPROGRESS, null);

		// verify
		verify(ticketRepository, times(1)).updateTicketStatus(any(), any());
		verify(commentRepository, times(0)).save(any());
	}

	@Test
	public void whenCallCreateCommentWithValidTicketId_thenReturnSavedComment() {
		// given
		Ticket tikcet = Utils.getTicket();
		tikcet.setId(1L);
		Comment comment = Utils.getComment(tikcet);

		// when
		when(commentRepository.save(any())).thenReturn(comment);

		// then
		Comment returnedComment = ticketSupportServiceImpl.createComment(comment);
		assertThat(returnedComment.equals(comment));

		// verify
		verify(commentRepository, times(1)).save(any());
	}

	@Test
	public void whenCallCreateCommentWithInValidTicketId_thenReturnException() {
		// given
		Comment comment = Utils.getComment(null);

		// when
		when(commentRepository.save(any())).thenThrow(new DataIntegrityViolationException("ticket id is not valid"));
		Exception exception = assertThrows(DataIntegrityViolationException.class, () -> {
			ticketSupportServiceImpl.createComment(comment);
		});

		String expectedMessage = "ticket id is not valid";
		String actualMessage = exception.getMessage();

		// then
		assertTrue(actualMessage.contains(expectedMessage));

		// verify
		verify(commentRepository, times(1)).save(any());
	}

	@Test
	public void whenCallGetCommentsByTicketWithValidTicket_thenReturnObject() {
		// given
		Ticket ticket = Utils.getTicket();
		ticket.setId(1L);
		Comment comment = Utils.getComment(ticket);
		comment.setId(2L);
		Page<Comment> page = Utils.getPageOfComments(comment);
		Pageable pageable = Utils.getPagable(0, 5);

		// when
		when(commentRepository.findByTicketId(1L, pageable)).thenReturn(page);

		// then
		Page<Comment> returnedPage = ticketSupportServiceImpl.getCommentsByTicket(ticket, pageable);
		assertThat(returnedPage.getContent()).size().isEqualTo(1);
		assertThat(returnedPage.getContent().get(0).equals(comment));

		// verify
		verify(commentRepository, times(1)).findByTicketId(1L, pageable);
	}

	@Test
	public void whenCallGetCommentsByTicketWithInValidTicket_thenReturnException() {
		// given
		Ticket ticket = Utils.getTicket();
		ticket.setId(1L);
		Comment comment = Utils.getComment(ticket);
		comment.setId(2L);
		Page<Comment> page = Utils.getPageOfComments(comment);
		Pageable pageable = Utils.getPagable(0, 5);

		// when
		when(commentRepository.findByTicketId(1L, pageable))
				.thenThrow(new DataIntegrityViolationException("ticket id is not valid"));
		Exception exception = assertThrows(DataIntegrityViolationException.class, () -> {
			ticketSupportServiceImpl.getCommentsByTicket(ticket, pageable);
		});

		String expectedMessage = "ticket id is not valid";
		String actualMessage = exception.getMessage();

		// then
		assertTrue(actualMessage.contains(expectedMessage));

		// verify
		verify(commentRepository, times(1)).findByTicketId(1L, pageable);
	}

}
