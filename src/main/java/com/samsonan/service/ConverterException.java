package com.samsonan.service;

/**
 * Generic exception for Converter Service
 * @author Andrey Samsonov (samsonan)
 *
 */
public class ConverterException extends Exception {

	private static final long serialVersionUID = -5510553777939925245L;

    public ConverterException(String message, Throwable throwable) {
        super(message, throwable);
    }	
	
}
