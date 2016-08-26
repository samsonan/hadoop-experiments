package com.samsonan.util;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.samsonan.hadoop.mr.Converter;

public class Starter {

	public static String[] HEADER_LIST = { "pk", "bank_code", "currency_code", "buy_rate", "sell_rate" };

	public static String XML_ROOT_NAME = "exchange_rates";

	private static Logger LOG = Logger.getLogger(Starter.class);
	
	public static void main(String[] args) {
		try {

			String csvString = "1, Credit_Agricode, usd, 25.5000, 25.9000";

			String key = "0";
			Document xmlResult;
			xmlResult = XmlUtil.convertCsvToPlainXml(csvString, XML_ROOT_NAME, HEADER_LIST);

			LOG.info("mapper: #" + key + " transformation: OK");

			XmlUtil.validateXml(xmlResult);

			LOG.info("mapper: #" + key + " validation: OK");

			String strXmlResult = XmlUtil.xmlToString(xmlResult);

			LOG.info("mapper: #" + key + " result:" + strXmlResult);

			String transformResult = XmlUtil.transformXml(strXmlResult);

			LOG.info("reducer: #" + key + " transformResult:" + transformResult);

		} catch (XmlException ex) {
			LOG.error("Cannot transform CSV string to XML", ex);
		}
	}

}
