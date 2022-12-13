package com.sinch.ticketsystem.exception;

public class BadRequestException extends Exception {
	static final long serialVersionUID = -3387516993334229948L;

	public BadRequestException(String message) {
		super(message);
	}

}
