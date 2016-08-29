package com.samsonan.service;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class ConverterServiceImpl implements ConverterService {

	@SuppressWarnings("unused")
	private static Logger LOG = Logger.getLogger(ConverterServiceImpl.class);

	public static String CSV_SPLIT_BY = ",";

	@Override
	public String convertCsvToPlainXml(String csvString, String rootName, String[] tagNames) throws ConverterException {
		
		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement(rootName);

			doc.appendChild(rootElement);

			String[] fieldList = csvString.split(CSV_SPLIT_BY);
			for (int i = 0; i < fieldList.length; i++) {
				Element pkElement = doc.createElement(tagNames[i]);
				pkElement.appendChild(doc.createTextNode(fieldList[i].trim()));
				rootElement.appendChild(pkElement);
			}

			return xmlToString(doc);

		} catch (Exception ex) {
			throw new ConverterException("Error converting to plain XML. CSV:" + csvString, ex);
		}
	}

	@Override
	public void validateXmlString(String xmlString, File xsdFile) throws ConverterException {
		
		try {
			
			LOG.info("validate XML:"+xmlString+"; xsdFile:"+xsdFile); 
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = factory.newSchema(xsdFile);
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(new StringReader(xmlString)));
			
		} catch (SAXException | IOException ex) {
			throw new ConverterException("Error validating XML using given xsd file:" + xsdFile, ex);
		}
	}

	private String xmlToString(Document doc) throws ConverterException {

		try {

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();

			StringWriter outWriter = new StringWriter();

			transformer.transform(new DOMSource(doc), new StreamResult(outWriter));

			return outWriter.toString();

		} catch (Exception ex) {
			throw new ConverterException("Cannot convert XML to string", ex);
		}
	}

	public String transformXml(String xmlString, File xslFile) throws ConverterException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		
		try {
			
			StringWriter outWriter = new StringWriter();

			Transformer transformer = transformerFactory.newTransformer(new StreamSource(xslFile));
			transformer.transform(new StreamSource(new StringReader(xmlString)), new StreamResult(outWriter));
			
			return outWriter.toString();
			
		} catch (Exception ex) {
			throw new ConverterException("Cannot transform XML string: " + xmlString + " using given xsl file:" + xslFile, ex);
		}
	}

}
