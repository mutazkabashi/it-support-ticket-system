package com.sinch.ticketsystem.mapper;

import org.springframework.stereotype.Component;

import com.sinch.ticketsystem.controller.dto.CommentDto;
import com.sinch.ticketsystem.domain.Comment;
import com.sinch.ticketsystem.domain.Ticket;

@Component
public class CommentMapper {

	public Comment mapToComment(CommentDto commentDto) {
		Ticket ticket = new Ticket();
		ticket.setId(commentDto.getTicketId());
		return new Comment(commentDto.getId(), commentDto.getInformation(), commentDto.getCreatedBy(), ticket);
	}

	public CommentDto mapToCommentDto(Comment comment) {
		return new CommentDto(comment.getId(), comment.getCreationDate(), comment.getInformation(),
				comment.getCreatedBy(), comment.getTicket().getId());
	}
}
