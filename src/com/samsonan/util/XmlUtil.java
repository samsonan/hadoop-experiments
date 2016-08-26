package com.samsonan.util;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlUtil {

	public final static String CSV_SPLIT_BY = ",";

	private static Logger logger = Logger.getLogger(XmlUtil.class);

	/**
	 * Convert CSV string to XML Document
	 * 
	 * @param csvString
	 *            - CSV string with quoted or unquoted values
	 * @return XML Document
	 */
	public static Document convertCsvToPlainXml(String csvString, String rootName, String[] tagNames)
			throws XmlException {

		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();

			Element rootElement = doc.createElement(rootName);
			doc.appendChild(rootElement);

			String[] fieldList = csvString.split(CSV_SPLIT_BY);
			for (int i = 0; i < fieldList.length; i++) {
				Element pkElement = doc.createElement(tagNames[i]);
				pkElement.appendChild(doc.createTextNode(fieldList[i]));
				rootElement.appendChild(pkElement);
			}

			return doc;

		} catch (Exception ex) {
			throw new XmlException("Error converting to plain XML. CSV:" + csvString, ex);
		}
	}

	public static String xmlToString(Document doc) throws XmlException {

		try {

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();

			StringWriter outWriter = new StringWriter();
			StreamResult result = new StreamResult(outWriter);

			DOMSource source = new DOMSource(doc);
			transformer.transform(source, result);

			return outWriter.toString();

		} catch (Exception ex) {
			throw new XmlException("Cannot convert XML to string", ex);
		}

	}

	/**
	 * Validate given XML Document
	 * 
	 * @param doc
	 *            Document to Validate
	 */
	public static void validateXml(Document doc) {

	}

	/**
	 * Apply XSL transformation
	 */
	public static String transformXml(String xml) throws XmlException {
		TransformerFactory tFactory = TransformerFactory.newInstance();
		try {
			Transformer transformer = tFactory.newTransformer(new StreamSource());
			transformer.transform(new StreamSource(), new StreamResult());
			return "";
		} catch (Exception ex) {
			throw new XmlException("Cannot transform XML string: " + xml, ex);
		}
	}

}
