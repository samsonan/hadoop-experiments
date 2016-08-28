package com.samsonan.hadoop.mr;

import java.io.IOException;

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
	
	private ConverterService service;
	
	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		Injector injector = Guice.createInjector(new GuiceModule());
		service = injector.getInstance( ConverterService.class );
	}
	
	public void reduce(LongWritable key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {

		LOG.info("reducer: {key:" + key + "}");
		
		for (Text val : values) {
			try {
				LOG.info("  #" + key + " value:" + val);
				service.transformXml(val.toString(), null);  //TODO: XSL location
			} catch (ConverterException e) {
				LOG.error("Cannot transform given XML: "+ val, e);
			}
		}

		//TODO:
		//What to do with multiple XMLs for the one given key?!		
		//context.write(key, xmlResult);
	}
}
