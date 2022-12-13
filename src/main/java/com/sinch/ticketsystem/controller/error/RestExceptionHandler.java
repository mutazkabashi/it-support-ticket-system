package com.sinch.ticketsystem.controller.error;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.sinch.ticketsystem.exception.EntityNotFoundException;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler{
	
	@ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorDetails> handleAllExceptions(Exception exception, WebRequest request) {
		ErrorDetails errorDetails = handleBasicExceptions(exception, request);
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

	@ExceptionHandler(EntityNotFoundException.class)
    public final ResponseEntity<ErrorDetails> handleUserNotFoundException(EntityNotFoundException entityNotFoundException, WebRequest request) {
		ErrorDetails errorDetails = handleBasicExceptions(entityNotFoundException, request);
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }
	
	@ExceptionHandler(DataIntegrityViolationException.class)
	public final ResponseEntity<ErrorDetails> handleDataIntegrityViolationException(
			DataIntegrityViolationException exception, WebRequest request) {
		String errorMessage = "Comment is not Valid , Make sure Comment is in right Format and it belong to a ticket";
		ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), errorMessage, "Comment dosen't have a tikcet");
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ConstraintViolationException.class)
	public final ResponseEntity<ErrorDetails> handleConstraintViolationException(
			ConstraintViolationException constraintViolationException, WebRequest request) {
		String errorMessage = constraintViolationException.getMessage();
		if(errorMessage.contains("must be a well-formed email address")) {
			errorMessage = errorMessage.substring(errorMessage.indexOf(".")+1);
		}
		ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), errorMessage,
                request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
	
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public final ResponseEntity<ErrorDetails> handleMethodArgumentTypeMismatchException(
			MethodArgumentTypeMismatchException methodArgumentTypeMismatchException, WebRequest webRequest) {
		String errorMessage = "Url Path parameter is not in the right format/Type , Check details field for more info";
		ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), errorMessage,
				"parameter name-" + methodArgumentTypeMismatchException.getName() + ", parameter type-"
						+ methodArgumentTypeMismatchException.getRequiredType() + " passed Value-"
						+ methodArgumentTypeMismatchException.getValue());
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}
	
	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		String errorMessage = "Url Path parameter is missing , Check details field for more info";
		ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), errorMessage, ex.getMessage());
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		String errorMessage = "Input Validation Error, Check details field for more info";
		List<String> validationList = ex.getBindingResult().getFieldErrors().stream()
				.map(fieldError -> fieldError.getField() + "-" + fieldError.getDefaultMessage())
				.collect(Collectors.toList());
		ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), errorMessage, validationList.toString());
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}
	
	private ErrorDetails handleBasicExceptions(Exception exception, WebRequest request) {
		ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), exception.getMessage(),
                request.getDescription(false));
		return errorDetails;
	}
}
