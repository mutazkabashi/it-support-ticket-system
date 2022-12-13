package com.sinch.ticketsystem.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;

import com.sinch.ticketsystem.Utils;
import com.sinch.ticketsystem.domain.Comment;
import com.sinch.ticketsystem.domain.Ticket;

@DataJpaTest
public class CommentRespositoryJpaUnitTest {

	@Autowired
	TestEntityManager entityManager;

	@Autowired
	CommentRepository repository;

	@Test
	public void whenCallSaveWithOrphanComment_thenThrowException() {
		// when
		Exception exception = assertThrows(DataIntegrityViolationException.class, () -> {
			Comment comment = Utils.getComment(null);
			repository.save(comment);
		});

		String expectedMessage = "ConstraintViolationException";
		String actualMessage = exception.getMessage();

		// then
		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	public void whenCallSaveWithValidComment_thenReturnData() {
		// given
		// Database populated from data.sql file on test startup

		// when
		Ticket ticket = new Ticket();
		ticket.setId(1L);
		Comment comment = repository.save(Utils.getComment(ticket));
		Comment savedComment = repository.findAll().get(0);

		// then
		assertThat(comment.getId() != null);
		assertThat(comment.equals(savedComment));
	}

	@Test
	public void whenCallFindByTicketId_thenReturnData() {
		// given
		// Database populated from data.sql file on test startup
		Ticket ticket = new Ticket();
		ticket.setId(1L);
		Comment comment = entityManager.persist(Utils.getComment(ticket));

		// when
		Page<Comment> page = repository.findByTicketId(ticket.getId(), Utils.getPagable(0, 5));
		Comment savedComment = page.getContent().get(0);
		// then
		assertThat(page.getContent()).isNotEmpty();
		assertThat(page.getContent()).size().isEqualTo(1);
		assertThat(savedComment.equals(comment));
		assertThat(savedComment.getTicket().equals(ticket));
	}
}
