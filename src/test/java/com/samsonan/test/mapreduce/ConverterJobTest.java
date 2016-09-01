package com.samsonan.test.mapreduce;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.MapReduceDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.apache.hadoop.mrunit.testutil.TemporaryPath;
import org.apache.hadoop.mrunit.types.Pair;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import com.samsonan.hadoop.mr.CsvToXmlMapper;
import com.samsonan.hadoop.mr.XsltReducer;
import com.samsonan.test.UseCases;

public class ConverterJobTest {

	private final static String CONFIG_PARAM_NAME = "converter.configuration.name";
	
	//TODO: thats the hack here
	//in map/reduce we get file by symlink name 
	//where symlink name = <CONFIG_PARAM_VALUE>.XSD
	private final static String CONFIG_PARAM_VALUE = "exchange_rates";
	
	MapDriver<LongWritable, Text, LongWritable, Text> mapDriver;
	ReduceDriver<LongWritable, Text, LongWritable, Text> reduceDriver;
	MapReduceDriver<LongWritable, Text, LongWritable, Text, LongWritable, Text> mapReduceDriver;

	@Rule
	public TemporaryPath tmpDir = new TemporaryPath();	
	
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
		
		Path xsdLink = Paths.get("exchange_rates.xsd");
		Path xsdTarget = Paths.get("conf\\exchange_rates\\exchange_rates.xsd");
		xsdLink.toFile().delete();

		Path headerLink = Paths.get("exchange_rates.header");
		Path headerTarget = Paths.get("conf\\exchange_rates\\exchange_rates.header");
		headerLink.toFile().delete();

		Path xslLink = Paths.get("exchange_rates.xsl");
		Path xslTarget = Paths.get("conf\\exchange_rates\\exchange_rates.xsl");
		xslLink.toFile().delete();
		
		Files.createSymbolicLink(xslLink, xslTarget);
		Files.createSymbolicLink(headerLink, headerTarget);
		Files.createSymbolicLink(xsdLink, xsdTarget);
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
	
	@Test
	public void testReducer() throws IOException {
		//TODO
	}

	@Test
	public void testMapReduce() throws IOException {
		
		//TODO
		org.apache.hadoop.fs.Path output = tmpDir.getPath("output");
		
		mapReduceDriver.withInput(new LongWritable(10), new Text(UseCases.CASE4_RT_GT25_IN));
		mapReduceDriver.withOutput(new LongWritable(10), new Text("10.seq"));
		mapReduceDriver.runTest();
	}

}
