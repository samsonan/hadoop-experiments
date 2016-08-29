package com.samsonan.util;

import java.io.File;

import org.apache.log4j.Logger;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.samsonan.service.ConverterException;
import com.samsonan.service.ConverterService;
import com.samsonan.service.GuiceModule;

public class Starter {

	public static String[] HEADER_LIST = { "pk", "bank_code", "currency_code", "buy_rate", "sell_rate" };

	public static String XML_ROOT_NAME = "exchange_rates";

	private static Logger LOG = Logger.getLogger(Starter.class);

	public static void main(String[] args) {
		try {

			Injector injector = Guice.createInjector(new GuiceModule() );
			ConverterService service = injector.getInstance( ConverterService.class );
			
			System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");

			String csvString = "1, Credit_Agricode, usd, 25.5000, 25.9000";
			long key = 1;

			String strXmlResult = service.convertCsvToPlainXml(csvString, XML_ROOT_NAME, HEADER_LIST);

			LOG.info("mapper: #" + key + " transformation: OK");
			LOG.info("mapper: #" + key + " result:" + strXmlResult);
			
			String xsdFile = "C:\\working.folder\\workspace-sts\\hadoop-experiments\\task\\exchange_rates.xsd"; 
			service.validateXmlString(strXmlResult, new File(xsdFile));

			LOG.info("mapper: #" + key + " validation: OK");

			String xslFile = "C:\\working.folder\\workspace-sts\\hadoop-experiments\\task\\exchange_rates.xsl";
			String transformResult = service.transformXml(strXmlResult, new File(xslFile));

			LOG.info("reducer: #" + key + " transformResult:" + transformResult);

		} catch (ConverterException ex) {
			LOG.error("Cannot transform CSV string to XML", ex);
		}
	}

}
