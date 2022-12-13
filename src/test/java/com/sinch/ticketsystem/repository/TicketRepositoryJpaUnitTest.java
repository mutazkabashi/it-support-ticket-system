package com.sinch.ticketsystem.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sinch.ticketsystem.Utils;
import com.sinch.ticketsystem.domain.Ticket;
import com.sinch.ticketsystem.valueobject.TicketStatus;

@DataJpaTest
public class TicketRepositoryJpaUnitTest {

	@Autowired
	TestEntityManager entityManager;

	@Autowired
	TicketRepository repository;

	@Test
	public void whenCallFindAll_thenReturnAll() {
		// given
		// Database populated from data.sql file on test startup

		// when
		Pageable paging = Utils.getPagable(0, 6);
		Page<Ticket> page = repository.findAll(paging);
		Ticket result = page.getContent().get(0);

		// then
		assertThat(page.getContent()).isNotEmpty();
		assertThat(page.getContent().size()).isEqualTo(6);
		assertThat(result.getId() != null);
		assertThat(result.getCreatedBy().equals(Utils.EMAIL));
	}

	@Test
	public void whenCallFindById_thenReturnData() {
		// given
		// Database populated from data.sql file on test startup

		// when
		Ticket savedTicked = repository.findById(1L).orElseThrow();

		// then
		assertThat(savedTicked != null);
		assertThat(savedTicked.getId().equals(1L));
	}

	@Test
	public void whenCallFindByCreatedyWithNotEmptyRepository_thenReturnData() {
		// given
		// Database populated from data.sql file on test startup

		// when
		Pageable paging = Utils.getPagable(0, 6);
		Page<Ticket> page = repository.findByCreatedBy(Utils.EMAIL, paging);
		Ticket savedTicket = page.getContent().get(0);

		// then
		assertThat(page.getContent()).isNotEmpty();
		assertThat(page.getContent().size()).isEqualTo(3);
		assertThat(savedTicket.getId() != null);
		assertThat(savedTicket.getCreatedBy().equals(Utils.EMAIL));

	}

	@Test
	public void whenCallUpdateTicketStatus_thenReturnUpdatedData() {
		// given
		// Database populated from data.sql file on test startup

		// when
		repository.updateTicketStatus(1L, TicketStatus.INPROGRESS);
		Ticket updatedTicket = repository.findById(1L).orElseThrow();

		// then
		assertThat(updatedTicket.getId().equals(1L));
		assertThat(updatedTicket.getTicketStatus().equals(TicketStatus.INPROGRESS));
	}
}
