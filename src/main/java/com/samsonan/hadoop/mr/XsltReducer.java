package com.samsonan.hadoop.mr;

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.log4j.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.samsonan.service.ConverterException;
import com.samsonan.service.ConverterService;
import com.samsonan.service.GuiceModule;

/**
 * Apply XSL transformation to XMLs in the values
 * @author Andrey Samsonov (samsonan)
 *
 */
public class XsltReducer extends Reducer<LongWritable, Text, LongWritable, Text> {

	private static Logger LOG = Logger.getLogger(CsvToXmlMapper.class);
	private static String XSL_FILE_NAME = "exchange_rates.xsl";
	
	private ConverterService service;
	private File xslFile;
	
	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		Injector injector = Guice.createInjector(new GuiceModule());
		service = injector.getInstance( ConverterService.class );

		xslFile = new File(XSL_FILE_NAME); //read from cache
		
		/*
	   	try{
	   		
    		Path[] cacheFiles = DistributedCache.getLocalCacheFiles(context.getConfiguration());

    		if(cacheFiles != null && cacheFiles.length > 0) {
    			for(Path file : cacheFiles) {
    				LOG.info("cache file found: "+file+"; name:"+file.getName());
    				if (file.getName().equals("exchange_rates.xsl"))
    					xslFile = new File(file.toString());
    			}
    		} else throw new IOException("no cached files found in mapper");
    		
    	} catch(IOException ex) {
    		LOG.error("Error reading distributed cache in mapper", ex);
    	}*/		
	}
	
	public void reduce(LongWritable key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {

		LOG.info("reducer: {key:" + key + "}");
		
		for (Text val : values) {
			try {
				LOG.info("  #" + key + " value:" + val);
				service.transformXml(val.toString(), xslFile);
			} catch (ConverterException e) {
				LOG.error("Cannot transform given XML: "+ val, e);
			}
		}

		//TODO:
		//What to do with multiple XMLs for the one given key?!		
		//context.write(key, xmlResult);
	}
}
