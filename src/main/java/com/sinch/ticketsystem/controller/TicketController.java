package com.sinch.ticketsystem.controller;

import java.net.URISyntaxException;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.sinch.ticketsystem.controller.dto.CommentDto;
import com.sinch.ticketsystem.controller.dto.TicketDto;
import com.sinch.ticketsystem.controller.dto.TicketStatusWithCommentDto;
import com.sinch.ticketsystem.controller.util.PaginationUtil;
import com.sinch.ticketsystem.domain.Comment;
import com.sinch.ticketsystem.domain.Ticket;
import com.sinch.ticketsystem.exception.BadRequestException;
import com.sinch.ticketsystem.exception.EntityNotFoundException;
import com.sinch.ticketsystem.mapper.CommentMapper;
import com.sinch.ticketsystem.mapper.TicketMapper;
import com.sinch.ticketsystem.service.TicketSupportService;

@Validated
@RestController
@RequestMapping("/api/v1")
public class TicketController {

	private static final String DEFAULT_PAGE_VALUE = "0";
	private static final String DEFAULT_PAGE_SIZE = "5";
	private final TicketSupportService ticketSupportService;
	private final TicketMapper ticketMapper;
	private final CommentMapper commentMapper;

	public TicketController(TicketSupportService ticketSupportService, TicketMapper ticketMapper,
			CommentMapper commentMapper) {
		this.ticketSupportService = ticketSupportService;
		this.ticketMapper = ticketMapper;
		this.commentMapper = commentMapper;
	}

	@GetMapping("/tickets")
	public ResponseEntity<List<TicketDto>> getAllTickets(
			@RequestParam(defaultValue = DEFAULT_PAGE_VALUE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size) {
		Pageable paging = PageRequest.of(page, size);
		final Page<TicketDto> resultPage = ticketSupportService.getAllTickets(paging).map(ticketMapper::mapToTicketDto);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), resultPage);
		return new ResponseEntity<>(resultPage.getContent(), headers, HttpStatus.OK);
	}
	
	@GetMapping("/tickets/custom-query")
	public ResponseEntity<List<TicketDto>> getTicketsByCustomerId(
			@RequestParam(defaultValue = DEFAULT_PAGE_VALUE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size,
			@RequestParam("customerId") @Email String customerId) {
		Pageable paging = PageRequest.of(page, size);
		final Page<TicketDto> resultPage = ticketSupportService.getTicketsByCustomerId(customerId, paging)
				.map(ticketMapper::mapToTicketDto);
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), resultPage);
		return new ResponseEntity<>(resultPage.getContent(), headers, HttpStatus.OK);

	}
	
	@GetMapping("/tickets/{ticketId}")
	public ResponseEntity<TicketDto> getTicketInforamtionByTicketId(
			@RequestParam(defaultValue = DEFAULT_PAGE_VALUE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size,
			@PathVariable("ticketId")  Long ticketId) throws BadRequestException, EntityNotFoundException {
		Pageable paging = PageRequest.of(page, size);
		Ticket ticket = ticketSupportService.getTicketById(ticketId);
		final Page<CommentDto> ticketComments = ticketSupportService.getCommentsByTicket(ticket, paging)
				.map(commentMapper::mapToCommentDto);
		TicketDto resultTicketDto = ticketMapper.mapToTicketDto(ticket);
		resultTicketDto.setComments(ticketComments.getContent());
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), ticketComments);
		return new ResponseEntity<>(resultTicketDto, headers, HttpStatus.OK);

	}

	@PostMapping("/tickets")
	public ResponseEntity<TicketDto> createTicket(@Valid @RequestBody TicketDto ticketDto) throws URISyntaxException {
		Ticket newTicket = ticketSupportService.createTicket(ticketMapper.mapToTicket(ticketDto));
		return new ResponseEntity<>(ticketMapper.mapToTicketDto(newTicket), HttpStatus.CREATED);
	}
	
	@PostMapping("/tickets/{ticketId}/comments")
	public ResponseEntity<CommentDto> createComment(
			@PathVariable("ticketId") @Min(value = 1, message = "TciketId is Null") Long ticketId,
			@Valid @RequestBody CommentDto commentDto) {
		commentDto.setTicketId(ticketId);
		Comment comment = ticketSupportService.createComment(commentMapper.mapToComment(commentDto));
		return new ResponseEntity<>(commentMapper.mapToCommentDto(comment), HttpStatus.CREATED);
	}
	
	@PatchMapping("tickets/{ticketId}")
	public ResponseEntity<Void> updateTicket(
			@PathVariable("ticketId") @Min(value = 1, message = "TciketId is Null") Long ticketId,
			@Valid @RequestBody TicketStatusWithCommentDto ticketStatusWithCommentDto) throws BadRequestException {
		//To make sure that the right ticket will be updated
		ticketStatusWithCommentDto.setId(ticketId);
		if (ticketStatusWithCommentDto.getCommentDto() != null) {
			ticketSupportService.updateTicket(ticketStatusWithCommentDto.getId(),
					ticketStatusWithCommentDto.getTicketStatus(),
					commentMapper.mapToComment(ticketStatusWithCommentDto.getCommentDto()));
		} else {
			ticketSupportService.updateTicket(ticketStatusWithCommentDto.getId(),
					ticketStatusWithCommentDto.getTicketStatus(), null);
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

}
