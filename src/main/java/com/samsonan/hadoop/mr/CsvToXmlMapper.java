package com.samsonan.hadoop.mr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
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
	private static String [] HEADER_LIST = {"pk", "bank_code", "currency_code","buy_rate", "sell_rate"};
	private static String XML_ROOT_NAME = "exchange_rates";
	private static String XSD_FILE_NAME = "exchange_rates.xsd";
	
	private File xsdFile;
	private ConverterService service;
	
	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		Injector injector = Guice.createInjector(new GuiceModule());
		service = injector.getInstance( ConverterService.class );

		xsdFile = new File(XSD_FILE_NAME); // read from cache
		
//		if (!xsdFile.exists())
//			throw new IOException("Mandatory file " + XSD_FILE_NAME + " is not found in mapper!");
		
//		try{
//			LOG.info("xsd file 2 exists:"+xsdFile2.exists());
//			LOG.info("xsd file 2 can read:"+xsdFile2.canRead());
//		}catch(Exception e){
//			LOG.error("cannot read xsd file:"+e.getMessage());
//		}
		
		
		try{
			Properties prop = new Properties();
			FileInputStream fis = new FileInputStream("exchange_rates.conf");
			prop.load(fis);
			LOG.info("root:"+prop.getProperty("root"));
			LOG.info("tags:"+prop.getProperty("tags"));
			fis.close();
		}catch(Exception e){
			LOG.error("cannot read properties file:"+e.getMessage());
		}
		
		/*
	   	try{
	   		
    		Path[] cacheFiles = DistributedCache.getLocalCacheFiles(context.getConfiguration());

    		if(cacheFiles != null && cacheFiles.length > 0) {
    			for(Path file : cacheFiles) {
    				LOG.info("cache file found: "+file+"; name:"+file.getName());
    				if (file.getName().equals("exchange_rates.xsd")) {
    					xsdFile = new File(file.toString());
    					LOG.info("xsdFile: " + xsdFile.exists());
    				}
    			}
    		} else throw new IOException("no cached files found in mapper");
    		
    	} catch(IOException ex) {
    		LOG.error("Error reading distributed cache in mapper", ex);
    	}*/		
	}
	
	@Override
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException  {

		LOG.info("mapper: {key:" + key + "; value:" + value + "; xsdFile:"+ xsdFile +"}");

		try {
			
			if (key.get() != 0 && !value.toString().startsWith(HEADER_LIST[0])) {

				String strXmlResult = service.convertCsvToPlainXml(value.toString(), XML_ROOT_NAME, HEADER_LIST);

				service.validateXmlString(strXmlResult, xsdFile);

				LOG.info("mapper: #" + key + " result:" + strXmlResult);

				context.write(key, new Text(strXmlResult));
			}
			
		} catch (ConverterException ex) {
			LOG.error("Cannot transform CSV string " + value + " to XML",ex);
		}
	}
}