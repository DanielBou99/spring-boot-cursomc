package com.danielsouza.cursomc.services.exceptions;

public class DataIntegrityException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DataIntegrityException(String mensagem) {
		super(mensagem);
	}
}
