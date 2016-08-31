package com.samsonan.service;

import java.io.File;

/**
 * Service defining "business logic" layer 
 * @author asamsonov
 *
 */
public interface ConverterService {

	/**
	 * Convert CSV String to plain XML 
	 * @param csvString Given CSV String
	 * @param rootName name of the root XML tag
	 * @param tagNames tag names to map CSV values
	 * @return XML as a String
	 * @throws ConverterException 
	 */
	String convertCsvToPlainXml (String csvString, String rootName, String [] tagNames) throws ConverterException;
	
	/**
	 * Validate XML against schema 
	 * @param xmlString XML to validate, as a String
	 * @param xsdPath Location of XSD schema
	 * @throws ConverterException
	 */
	void validateXmlString (String xmlString, File xsdFile) throws ConverterException;
	
	/**
	 * Apply XSL transformation to XML
	 * @param xmlString XML to transform, as a String
	 * @param xslPath Location of XSL file
	 * @return Result of transformation, as String
	 * @throws ConverterException
	 */
	String transformXml(String xmlString, File xslFile) throws ConverterException;
}
