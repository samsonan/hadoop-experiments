package com.samsonan.hadoop.mr;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;

import com.samsonan.service.ConverterException;
import com.samsonan.service.ConverterService;
import com.samsonan.service.GuiceModule;

/**
 * Mapper that does the following, given <id, CSV string as Text>
 * 1. Transform CSV to Plain XML 
 * 2. Validate XML with given schema
 * 3. Output <id, XML as Text>
 * 
 * @author Andrey Samsonov (samsonan)
 *
 */
public class CsvToXmlMapper extends Mapper<LongWritable, Text, LongWritable, Text> {

	private static Logger LOG = Logger.getLogger(CsvToXmlMapper.class);

	//TODO: get it from file
	public static String [] HEADER_LIST = {"pk","currency_code","buy_rate", "sell_rate"};

	public static String XML_ROOT_NAME = "exchange_rates";
	
	private ConverterService service;
	
	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		Injector injector = Guice.createInjector(new GuiceModule());
		service = injector.getInstance( ConverterService.class );
	}
	
	@Override
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException  {

		LOG.info("mapper: {key:" + key + "; value:" + value + "}");

		try {
			
			String strXmlResult = service.convertCsvToPlainXml(value.toString(), XML_ROOT_NAME, HEADER_LIST);
			
			service.validateXmlString(strXmlResult, null); //TODO: xsd location

			LOG.info("mapper: #" + key + " result:" + strXmlResult); 
			
			context.write(key, new Text(strXmlResult));
			
		} catch (ConverterException ex) {
			LOG.error("Cannot transform CSV string " + value + " to XML",ex);
		}
	}
}