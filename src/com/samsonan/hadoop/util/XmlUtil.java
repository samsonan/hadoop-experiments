package com.samsonan.hadoop.util;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlUtil {

	/**
	 * Convert CSV string to XML Document
	 * @param csvString - CSV string with quoted or unquoted values 
	 * @return
	 */
	public Document convertCsvToXml(String csvString) {
		
		Document doc = null; 
				
		try{

				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

				doc = docBuilder.newDocument();

				Element rootElement = doc.createElement("exchange_rates");
				doc.appendChild(rootElement);
						
		} catch (Exception ex){
			ex.printStackTrace(); // TODO: logger
		}
		
		return doc; 
	}
	
	/**
	 * Validate given XML Document
	 * @param doc
	 */
	public void validateXml(Document doc) {
		
	}
	
	/**
	 * Apply XSL transformation
	 */
	public void transformXml(String xml) {
		
	}
	

}
