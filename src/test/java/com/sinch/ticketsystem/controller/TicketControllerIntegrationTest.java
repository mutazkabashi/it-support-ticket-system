package com.sinch.ticketsystem.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinch.ticketsystem.Utils;
import com.sinch.ticketsystem.controller.dto.CommentDto;
import com.sinch.ticketsystem.controller.dto.TicketDto;
import com.sinch.ticketsystem.controller.dto.TicketStatusWithCommentDto;
import com.sinch.ticketsystem.domain.Comment;
import com.sinch.ticketsystem.domain.Ticket;
import com.sinch.ticketsystem.mapper.CommentMapper;
import com.sinch.ticketsystem.mapper.TicketMapper;
import com.sinch.ticketsystem.service.TicketSupportService;
import com.sinch.ticketsystem.valueobject.TicketStatus;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = TicketController.class)
@AutoConfigureMockMvc
public class TicketControllerIntegrationTest {

	@MockBean
	private TicketSupportService ticketSupportService;

	@MockBean
	private TicketMapper ticketMapper;
	
	@MockBean
	private CommentMapper commentMapper;

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
    ObjectMapper objectMapper;
	
	private static final String CREATE_COMMENT_URL = "/api/v1/tickets/1/comments";
	private static final String VALIDATION_ERROR_MESSAGE = "Input Validation Error, Check details field for more info";
	private static final String ERROR_MESSAGE_LABEL = "$.message";
	private static final String ERROR_MESSAGE_DETAILS_LABEL ="$.details";

    ///////////////////////////////////////////////////
    // Test Cases for @GetMapping("/tickets") end-point
    ///////////////////////////////////////////////////
	@Test
    public void whenGetRequestToTickets_thenCorrectResponse() 
    		throws Exception {
		//when
		Ticket ticket = new Ticket("title", "description", "mutaz@gmail.com", null);
		TicketDto ticketDto = new TicketDto(ticket);
		Page<Ticket> serviceResultPage = new PageImpl<>(List.of(ticket));

		Pageable paging = PageRequest.of(0, 5);
		//given
        when(ticketSupportService.getAllTickets(paging)).thenReturn(serviceResultPage);
        when(ticketMapper.mapToTicketDto(ticket)).thenReturn(ticketDto);
        
        //then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tickets")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].title").value("title"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].description").value("description"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].createdBy").value("mutaz@gmail.com"));
    }
	
	///////////////////////////////////////////////////
	// Test Cases for @PostMapping("/tickets") end-point
	///////////////////////////////////////////////////
	@Test
    public void whenPostRequestToCreateTicket_thenTicketCreatedWithCorrectResponse() 
    		throws Exception {
		//when
		Ticket ticket = Utils.getTicket();
		TicketDto ticketDto = new TicketDto(ticket);
		
		//given
        when(ticketMapper.mapToTicket(any())).thenReturn(ticket);
        when(ticketSupportService.createTicket(any())).thenReturn(ticket);
        when(ticketMapper.mapToTicketDto(any())).thenReturn(ticketDto);
        
        //then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tickets")
                .contentType(MediaType.APPLICATION_JSON)/*.characterEncoding("utf-8")*/
                .content(objectMapper.writeValueAsString((ticket)))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.createdBy").value(Utils.EMAIL));
    
    }
	
	@Test
    public void whenPostRequestToCreateTicketWithEmptyTitle_thenRequestFailedWithCorrectResponse() 
    		throws Exception {
		//when
		TicketDto ticketDto = new TicketDto(null, Instant.now(), "", "description", TicketStatus.INQUEUE,
				"mutaz@gmail.com", null, null);
        
		// then
		simuatePostBadRequest("/api/v1/tickets", ticketDto, "Input Validation Error, Check details field for more info",
				"[title-must not be blank]");
    
    }
	
	@Test
    public void whenPostRequestToCreateTicketWithInvalidEmail_thenRequestFailedWithCorrectResponse() 
    		throws Exception {
		//when
		TicketDto ticketDto = new TicketDto(null, Instant.now(), "title", "description", TicketStatus.INQUEUE, "mutaz",
				null, null);
        
		// then
		simuatePostBadRequest("/api/v1/tickets", ticketDto,
				VALIDATION_ERROR_MESSAGE,
				"[createdBy-must be a well-formed email address]");
    
    }
	
	@Test
    public void whenPostRequestToCreateTicketWithEmptyEmail_thenRequestFailedWithCorrectResponse() 
    		throws Exception {
		//when
		TicketDto ticketDto = new TicketDto(null, Instant.now(), "title", "description", TicketStatus.INQUEUE, "",
				null, null);
        
        //then
		simuatePostBadRequest("/api/v1/tickets", ticketDto,
				VALIDATION_ERROR_MESSAGE,
				"createdBy-must not be blank");
    }
	
	@Test
    public void whenPostRequestToCreateTicketWithEmptyDescription_thenRequestFailedWithCorrectResponse() 
    		throws Exception {
		//when
		TicketDto ticketDto = new TicketDto(null, Instant.now(), "title", "", TicketStatus.INQUEUE, Utils.EMAIL,
				null, null);
        
        //then
		simuatePostBadRequest("/api/v1/tickets", ticketDto,
				VALIDATION_ERROR_MESSAGE,
				"[description-must not be blank]");
    }
	
	@Test
    public void whenPostRequestToCreateTicketWithNullEmail_thenRequestFailedWithCorrectResponse() 
    		throws Exception {
		//when
		TicketDto ticketDto = new TicketDto(null, Instant.now(), "title", "description", TicketStatus.INQUEUE, null,
				null, null);
        
        //then
		simuatePostBadRequest("/api/v1/tickets", ticketDto,
				VALIDATION_ERROR_MESSAGE,
				"[createdBy-must not be blank]");
    }
	
	//////////////////////////////////////////////////////////////
	// Test Cases for @GetMapping("/tickets/custom-query") end-point
	///////////////////////////////////////////////////////////////
	@Test
	public void whenGetRequestToFindTicketsByValidCustomerId_thenRequestSuccessWithCorrectResponse() 
			throws Exception {
		// when
		Ticket ticket = new Ticket();
		ticket.setId(1L);
		TicketDto ticketDto = Utils.getTicketDto();
		Page<Ticket> ticketsPage = new PageImpl<>(List.of(ticket));

		// given
		when(ticketSupportService.getTicketsByCustomerId(any(), any())).thenReturn(ticketsPage);
		when(ticketMapper.mapToTicketDto(ticket)).thenReturn(ticketDto);

		// then
		mockMvc.perform(get("/api/v1/tickets/custom-query?customerId=user@mail.com")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.[0].id").value(1L))
				.andExpect(jsonPath("$.[0].title").value("title"))
				.andExpect(jsonPath("$.[0].ticketStatus",containsString(TicketStatus.INQUEUE.toString())))
				.andExpect(jsonPath("$.[0].createdBy").value(Utils.EMAIL))
				.andExpect(jsonPath("$.[0].assignedTo").value(Utils.EMAIL));

	}

	@Test
	public void whenGetRequestToFindTicketsByInValidCustomerId_thenRequestFailWithCorrectResponse() 
			throws Exception{
		//then
		mockMvc.perform(get("/api/v1/tickets/custom-query?customerId=1")
				.contentType(MediaType.APPLICATION_JSON))
		        .andExpect(status().isBadRequest())
		        .andExpect(jsonPath(ERROR_MESSAGE_LABEL).value("customerId: must be a well-formed email address"));
	}
	
	///////////////////////////////////////////////////////////////////////
	// Test Cases for @PostMapping("/tickets/{ticketId}/comments") end-point
	///////////////////////////////////////////////////////////////////////
	@Test
	public void whenPostRequestToCreateCommentWithNullInformation_thenRequestFailedWithCorrectResponse()
			throws Exception {
		// when
		CommentDto commentDto = Utils.getCommentDto(null, Utils.EMAIL, 1L);

		// then
		simuatePostBadRequest(CREATE_COMMENT_URL, commentDto, VALIDATION_ERROR_MESSAGE,
				"[information-can not be null!]");
	}
	
	@Test
	public void whenPostRequestToCreateCommentWithInvalidEmail_thenRequestFailedWithCorrectResponse() 
			throws Exception {
		// when
		CommentDto commentDto = Utils.getCommentDto("information", "mutaz", null);

		// then
		simuatePostBadRequest(CREATE_COMMENT_URL, commentDto, VALIDATION_ERROR_MESSAGE,
				"createdBy-must be a well-formed email address");
	}
	
	@Test
    public void whenPostRequestToCreateCommentWithNullEmail_thenRequestFailedWithCorrectResponse() 
    		throws Exception {
		// when
		CommentDto commentDto = Utils.getCommentDto("information", null, 1L);

		// then
		simuatePostBadRequest(CREATE_COMMENT_URL, commentDto, VALIDATION_ERROR_MESSAGE,
				"[createdBy-must not be blank]");
    
    }
	
	@Test
    public void whenPostRequestToCreateCommentWithNullTicket_thenRequestFailedWithCorrectResponse() 
    		throws Exception {
		// when
		CommentDto commentDto = Utils.getCommentDto("information", Utils.EMAIL, null);

		// then
		simuatePostBadRequest(CREATE_COMMENT_URL, commentDto, VALIDATION_ERROR_MESSAGE, "[ticketId-must not be null]");
    
    }
	
	@Test
	public void whenPostRequestToCreateCommentWithCorrectFormat_thenRequestSuccessWithCorrectResponse()
			throws Exception {
		// when
		CommentDto commentDto = Utils.getCommentDto("information", Utils.EMAIL, 1L);
		Ticket ticket = new Ticket();
		ticket.setId(1L);
		Comment comment = new Comment(1L, "information", "user@mail.com", ticket);

		// given
		when(commentMapper.mapToComment(any())).thenReturn(comment);
		when(ticketSupportService.createComment(any())).thenReturn(comment);
		when(commentMapper.mapToCommentDto(any())).thenReturn(commentDto);

		// then
		mockMvc.perform(MockMvcRequestBuilders.post(CREATE_COMMENT_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString((commentDto)))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.information").value("information"))
				.andExpect(jsonPath("$.ticketId").value(1L))
				.andExpect(jsonPath("$.createdBy").value("user@mail.com"));

	}
	
	/////////////////////////////////////////////////////////////
	// Test Cases for @GetMapping("/tickets/{ticketId}") end-point
	/////////////////////////////////////////////////////////////
	@Test
	public void whenGetRequestToFindATicketByValidId_thenRequestSuccessWithCorrectResponse()
			throws Exception {
		// when
		CommentDto commentDto = new CommentDto(1L, Instant.now(), "information", Utils.EMAIL, 1L);
		// FIXME
		Ticket ticket = new Ticket();
		ticket.setId(1L);
		Comment comment = new Comment(1L, "information", "user@mail.com", ticket);
		TicketDto ticketDto = new TicketDto(1L, Instant.now(), "title", "", TicketStatus.INQUEUE, "user@mail.com",
				"user@mail.com", null);
		Page<Comment> commentsPage = new PageImpl<>(List.of(comment));

		// given
		when(ticketSupportService.getTicketById(any())).thenReturn(ticket);
		when(ticketSupportService.getCommentsByTicket(any(), any())).thenReturn(commentsPage);
		when(commentMapper.mapToCommentDto(any())).thenReturn(commentDto);
		when(ticketMapper.mapToTicketDto(ticket)).thenReturn(ticketDto);

		// then
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tickets/"+1)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value(1L))
				.andExpect(jsonPath("$.title").value("title"))
				.andExpect(jsonPath("$.ticketStatus",containsString(TicketStatus.INQUEUE.toString())))
				.andExpect(jsonPath("$.createdBy").value("user@mail.com"))
				.andExpect(jsonPath("$.assignedTo").value("user@mail.com"))
				.andExpect(jsonPath("$.comments[0].information").value("information"))
				.andExpect(jsonPath("$.comments[0].id").value(1L))
				.andExpect(jsonPath("$.comments[0].createdBy").value("user@mail.com"));

	}
	
	@Test
	public void whenGetRequestToFindATicketByInvalidId_thenRequestFailWithCorrectResponse() 
			throws Exception{
		//then
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tickets/one")
				.contentType(MediaType.APPLICATION_JSON))
		        .andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath(ERROR_MESSAGE_LABEL)
						.value("Url Path parameter is not in the right format/Type , Check details field for more info"))
				.andExpect(MockMvcResultMatchers.jsonPath(ERROR_MESSAGE_DETAILS_LABEL, containsString(
						"parameter name-ticketId, parameter type-class java.lang.Long passed Value-one")));
		
	}
	
	/////////////////////////////////////////////////////////////
	// Test Cases for @PatchMapping("tickets/{ticketId}") end-point
	/////////////////////////////////////////////////////////////
	@Test
	public void whenPatchRequestToUpdateTicketByValidId_thenRequestSuccessWithCorrectResponse() 
			throws Exception {
		// when
		Ticket ticket = new Ticket();
		ticket.setId(1L);
		CommentDto commentDto = new CommentDto(1L, Instant.now(), "information", Utils.EMAIL, 1L);
		Comment comment = new Comment(1L, "information", Utils.EMAIL, ticket);
		TicketStatusWithCommentDto ticketStatusWithCommentDto = new TicketStatusWithCommentDto(1L,
				TicketStatus.RESOLVED, commentDto);

		// given
		doNothing().when(ticketSupportService).updateTicket(any(), any(), any());
		when(commentMapper.mapToComment(any())).thenReturn(comment);

		// then
		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/tickets/" + 1)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString((ticketStatusWithCommentDto))))
				.andExpect(status().isCreated());
	}
	
	@Test
	public void whenPatchRequestToUpdateTicketByValidIdAndNoComment_thenRequestSuccessWithCorrectResponse()
			throws Exception {
		// when
		TicketStatusWithCommentDto ticketStatusWithCommentDto = new TicketStatusWithCommentDto(1L,
				TicketStatus.RESOLVED, null);

		// given
		doNothing().when(ticketSupportService).updateTicket(any(), any(), any());

		// then
		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/tickets/" + 1)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString((ticketStatusWithCommentDto))))
				.andExpect(status().isCreated());
	}
	
	@Test
	public void whenPatchRequestToUpdateTicketByInValidId_thenRequestFailWithCorrectResponse() 
			throws Exception {
		// when
		CommentDto commentDto = new CommentDto(1L, Instant.now(), "information", Utils.EMAIL, 1L);
		TicketStatusWithCommentDto ticketStatusWithCommentDto = new TicketStatusWithCommentDto(null,
				TicketStatus.RESOLVED, commentDto);
		
		//then
		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/tickets/"+"test")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString((ticketStatusWithCommentDto))))
		        .andExpect(status().isBadRequest());
	}
	
	@Test
	public void whenPatchRequestToUpdateTicketByInValidTicketStatus_thenRequestFailWithCorrectResponse() 
			throws Exception {
		// when
		CommentDto commentDto = new CommentDto(1L, Instant.now(), "information", Utils.EMAIL, 1L);
		TicketStatusWithCommentDto ticketStatusWithCommentDto = new TicketStatusWithCommentDto(1L, null, commentDto);
		
		//then
		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/tickets/"+"1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString((ticketStatusWithCommentDto))))
		        .andExpect(status().isBadRequest());
	}
	
    //Util Methods
	private void simuatePostBadRequest(String url,Object body, String message, String details) 
			throws Exception, JsonProcessingException {
		mockMvc.perform(MockMvcRequestBuilders.post(url)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString((body)))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath(ERROR_MESSAGE_LABEL).value(message))
				.andExpect(jsonPath(ERROR_MESSAGE_DETAILS_LABEL, containsString(details)));
	}	
}
