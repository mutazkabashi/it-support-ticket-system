package com.sinch.ticketsystem.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sinch.ticketsystem.domain.Comment;
import com.sinch.ticketsystem.domain.Ticket;

/**
 * Spring Data SQL repository for the Comment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

	@Query("SELECT c FROM Comment c WHERE c.ticket.id =:ticketId")
	Page<Comment> findByTicketId(@Param("ticketId") Long ticketId, Pageable pageable);
}
