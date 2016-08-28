package com.samsonan.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.MapReduceDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.junit.Before;
import org.junit.Test;

import com.samsonan.hadoop.mr.CsvToXmlMapper;
import com.samsonan.hadoop.mr.XsltReducer;

//http://m-mansur-ashraf.blogspot.ru/2013/02/testing-mapreduce-with-mrunit.html
public class ConverterJobTest {

	MapDriver<LongWritable, Text, LongWritable, Text> mapDriver;
	ReduceDriver<LongWritable, Text, LongWritable, Text> reduceDriver;
	MapReduceDriver<LongWritable, Text, LongWritable, Text, LongWritable, Text> mapReduceDriver;

	@Before
	public void setUp() {

		CsvToXmlMapper mapper = new CsvToXmlMapper();
		XsltReducer reducer = new XsltReducer();

		mapDriver = MapDriver.newMapDriver(mapper);
		reduceDriver = ReduceDriver.newReduceDriver(reducer);
		mapReduceDriver = MapReduceDriver.newMapReduceDriver(mapper, reducer);
	}

	@Test
	public void testMapper() throws IOException {
		mapDriver.withInput(new LongWritable(1), new Text("1, Credit_Agricode, usd, 25.5000, 25.9000"));
		mapDriver.withOutput(new LongWritable(6), new Text("ass"));
		mapDriver.runTest();
	}

	@Test
	public void testReducer() throws IOException {
		List<Text> values = new ArrayList<Text>();
		values.add(new Text("1"));
		values.add(new Text("1"));
		reduceDriver.withInput(new LongWritable(6), values);
		reduceDriver.withOutput(new LongWritable(6), new Text("result"));
		reduceDriver.runTest();
	}

	@Test
	public void testMapReduce() throws IOException {
		mapReduceDriver.withInput(new LongWritable(), new Text("655209;1;796764372490213;804422938115889;6"));
		mapReduceDriver.withOutput(new LongWritable(6), new Text("2"));
		mapReduceDriver.runTest();
	}

}
