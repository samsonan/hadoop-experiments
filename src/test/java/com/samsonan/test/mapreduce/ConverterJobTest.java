package com.samsonan.test.mapreduce;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.MapReduceDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.apache.hadoop.mrunit.types.Pair;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import com.samsonan.hadoop.mr.CsvToXmlMapper;
import com.samsonan.hadoop.mr.XsltReducer;
import com.samsonan.test.UseCases;

/**
 * Tests for MR methods
 * For Business Logic and Service layer tests refer to com.samsonan.test.service.*
 * 
 * TODO:
 *  - exception cases (e.g. params are not set, mandatory files are not accessible)
 * 
 * @author Andrey Samsonov (samsonan)
 *
 */
public class ConverterJobTest {

	private final static String CONFIG_PARAM_NAME = "converter.configuration.name";
	private final static String CONFIG_PARAM_VALUE = "exchange_rates";
	
	private final static String MAPRED_OUTPUT_DIR_PARAM = "mapred.output.dir";
	private final static String MAPRED_OUTPUT_DIR_VAL = "/output";
	
	MapDriver<LongWritable, Text, LongWritable, Text> mapDriver;
	ReduceDriver<LongWritable, Text, LongWritable, Text> reduceDriver;
	MapReduceDriver<LongWritable, Text, LongWritable, Text, LongWritable, Text> mapReduceDriver;

	Path headerLink;
	Path xslLink;
	Path xsdLink;

	@Before
	public void setUp() throws IOException {

		CsvToXmlMapper mapper = new CsvToXmlMapper();
		XsltReducer reducer = new XsltReducer();

		mapDriver = MapDriver.newMapDriver(mapper);
		reduceDriver = ReduceDriver.newReduceDriver(reducer);
		mapReduceDriver = MapReduceDriver.newMapReduceDriver(mapper, reducer);
		
		mapDriver.getConfiguration().set(CONFIG_PARAM_NAME, CONFIG_PARAM_VALUE);
		reduceDriver.getConfiguration().set(CONFIG_PARAM_NAME, CONFIG_PARAM_VALUE);
		mapReduceDriver.getConfiguration().set(CONFIG_PARAM_NAME, CONFIG_PARAM_VALUE);
		
		reduceDriver.getConfiguration().set(MAPRED_OUTPUT_DIR_PARAM, MAPRED_OUTPUT_DIR_VAL);
		mapReduceDriver.getConfiguration().set(MAPRED_OUTPUT_DIR_PARAM, MAPRED_OUTPUT_DIR_VAL);
		
		xsdLink = Paths.get("exchange_rates.xsd");
		Path xsdTarget = Paths.get("conf\\exchange_rates\\exchange_rates.xsd");
		xsdLink.toFile().delete();

		headerLink = Paths.get("exchange_rates.header");
		Path headerTarget = Paths.get("conf\\exchange_rates\\exchange_rates.header");
		headerLink.toFile().delete();

		xslLink = Paths.get("exchange_rates.xsl");
		Path xslTarget = Paths.get("conf\\exchange_rates\\exchange_rates.xsl");
		xslLink.toFile().delete();
		
		Files.createSymbolicLink(xslLink, xslTarget);
		Files.createSymbolicLink(headerLink, headerTarget);
		Files.createSymbolicLink(xsdLink, xsdTarget);
	}

	@After
	public void cleanUp() throws IOException {
		headerLink.toFile().delete();
		xsdLink.toFile().delete();
		xslLink.toFile().delete();
	}
	
	/**
	 * Header should be excluded
	 * @throws IOException
	 */
	@Test
	public void testMapperHeaderInput() throws IOException {
		mapDriver.withInput(new LongWritable(), new Text(UseCases.CASE1_HEADER_IN));
		final List<Pair<LongWritable, Text>> result = mapDriver.run();
		assertThat(result, hasSize(0));
	}

	@Test
	public void testMapper() throws IOException {
		mapDriver.withInput(new LongWritable(10), new Text(UseCases.CASE2_RT_LT20_IN));
		mapDriver.withOutput(new LongWritable(10), new Text(UseCases.CASE2_RT_LT20_XML));
		mapDriver.runTest();
	}	

	/**
	 * Exception in method (validation) wont throw exception, but output is empty
	 * @throws IOException
	 */
	@Test
	public void testMapperValidation() throws IOException {
		mapDriver.withInput(new LongWritable(5), new Text(UseCases.CASE9_VIOLATEXSD_XML));
		final List<Pair<LongWritable, Text>> result = mapDriver.run();
		assertThat(result, hasSize(0));
	}
	
	/**
	 * One valid and one invalid case on reducer with the same key
	 * Output is created, invalid case leads to the error but it wont break processing
	 * @throws IOException
	 */
	@Test
	public void testReducer() throws IOException {
		List<Text> values = new ArrayList<Text>();
	    values.add(new Text(UseCases.CASE3_RT_25_XML));
	    values.add(new Text(UseCases.CASE8_EMPTY_IN)); // empty XML -> SAXParseException -> OK!
		reduceDriver.withInput(new LongWritable(20), values);
		reduceDriver.withOutput(new LongWritable(20), new Text(MAPRED_OUTPUT_DIR_VAL + "/20.seq"));
		reduceDriver.runTest();
	}

	/**
	 * Two successful cases lead to one output
	 * @throws IOException
	 */
	@Test
	public void testMapReduce() throws IOException {
		
		mapReduceDriver.withInput(new LongWritable(10), new Text(UseCases.CASE4_RT_GT25_IN));
		mapReduceDriver.withInput(new LongWritable(10), new Text(UseCases.CASE3_RT_25_IN));
		mapReduceDriver.withOutput(new LongWritable(10), new Text(MAPRED_OUTPUT_DIR_VAL + "/10.seq"));
		mapReduceDriver.runTest();
	}

	
	
}
