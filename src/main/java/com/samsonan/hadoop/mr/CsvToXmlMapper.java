package com.samsonan.hadoop.mr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
 * Current solution is generic
 * 
 * @author Andrey Samsonov (samsonan)
 *
 */
public class CsvToXmlMapper extends Mapper<LongWritable, Text, LongWritable, Text> {

	private static Logger LOG = Logger.getLogger(CsvToXmlMapper.class);

	private final static String CONFIG_PARAM_NAME = "configuration.name";
	
	private String confName;  // e.g. exchange_rates
	private String [] headerList; // e.g. pk, bank_code, currency_code, etc
	
	private File xsdFile;
	private ConverterService service;
	private boolean validateXML = true;
		
	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		Injector injector = Guice.createInjector(new GuiceModule());
		service = injector.getInstance( ConverterService.class );

		confName = context.getConfiguration().get(CONFIG_PARAM_NAME);
		
		String xsdFileName = confName+".xsd";
		xsdFile = new File(xsdFileName);

		LOG.info("mapper setup: configuration name:" + confName +"; xsd file exists:"+xsdFile.exists());
		
		if (!xsdFile.exists()) {
			LOG.error("Schema file " + xsdFileName + " doesn't exist! Validation will be skipped!");
			validateXML = false;
		}

		String headerFilename = confName+".header";
		
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(headerFilename))) {
	        headerList = bufferedReader.readLine().split(",");
		}catch(Exception ex){
			throw new IOException("Cannot read the mandatory header file "+headerFilename, ex);
		}

	}
	
	@Override
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException  {

		LOG.info("mapper: {key:" + key + "; value:" + value + "; xsdFile:"+ xsdFile +"}");

		try {
			
			if (key.get() != 0 && !value.toString().startsWith(headerList[0])) {

				String strXmlResult = service.convertCsvToPlainXml(value.toString(), confName, headerList);

				if (validateXML)
					service.validateXmlString(strXmlResult, xsdFile);

				LOG.info("mapper: #" + key + " result:" + strXmlResult);

				context.write(key, new Text(strXmlResult));
			}
			
		} catch (ConverterException ex) {
			LOG.error("Cannot convert CSV string " + value + " to valid XML",ex);
		}
	}
}