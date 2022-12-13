package com.sinch.ticketsystem.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sinch.ticketsystem.domain.Ticket;
import com.sinch.ticketsystem.valueobject.TicketStatus;

/**
 * Spring Data SQL repository for the Ticket entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

	Page<Ticket> findByCreatedBy(String createdBy, Pageable pageable);

	@Modifying
	@Query("update Ticket t set t.ticketStatus =:status where t.id =:id")
	void updateTicketStatus(@Param("id") Long id, @Param("status") TicketStatus ticketStatus);
}
