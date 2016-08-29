package com.samsonan.hadoop.mr;

import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class ConverterJob extends Configured implements Tool {

	private static String INSTANCE_NAME = "csv-xml-xslt converter";
	
	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new ConverterJob(), args);
		System.exit(exitCode);
	}

	@Override
	public int run(String[] args) throws Exception {
		
		Configuration conf = new Configuration();
		
		Job job = new Job(conf, INSTANCE_NAME);
		job.setJarByClass(ConverterJob.class);
		job.setMapperClass(CsvToXmlMapper.class);
		job.setReducerClass(XsltReducer.class);
		job.setOutputKeyClass(LongWritable.class);
		job.setOutputValueClass(Text.class);
		
		DistributedCache.addCacheFile(new URI("/user/cloudera/converter/exchange_rates.conf#exchange_rates.conf"), job.getConfiguration());
		DistributedCache.addCacheFile(new URI("/user/cloudera/converter/exchange_rates.xsd#exchange_rates.xsd"), job.getConfiguration());
		DistributedCache.addCacheFile(new URI("/user/cloudera/converter/exchange_rates.xsl#exchange_rates.xsl"), job.getConfiguration());
	
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		int returnValue = job.waitForCompletion(true) ? 0 : 1;
		return returnValue;
	}

}