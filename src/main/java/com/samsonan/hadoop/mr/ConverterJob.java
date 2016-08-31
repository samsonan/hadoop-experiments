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
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * Exchange Rates MapReduce Driver class.
 * @author asamsonov
 *
 */
public class ConverterJob extends Configured implements Tool {

	private final static String INSTANCE_NAME = "exchange rates converter";

	private final static String CONFIG_PARAM_NAME = "converter.configuration.name";
	private final static String CONFIG_PARAM_VALUE = "exchange_rates";
	
	private final static String CONF_PATH = "/user/cloudera/converter/";	
	
	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new ConverterJob(), args);
		System.exit(exitCode);
	}

	@Override
	public int run(String[] args) throws Exception {
		
		Configuration conf = getConf();
		
		conf.set(CONFIG_PARAM_NAME, CONFIG_PARAM_VALUE);
		
		Job job = new Job(conf, INSTANCE_NAME);
		job.setJarByClass(ConverterJob.class);
		job.setMapperClass(CsvToXmlMapper.class);
		job.setReducerClass(XsltReducer.class);
		job.setOutputKeyClass(LongWritable.class);
		job.setOutputValueClass(Text.class);

	    job.setOutputFormatClass(SequenceFileOutputFormat.class);
	    
	    //e.g. /user/cloudera/converter/exchange_rates.xsd#exchange_rates.xsd
		DistributedCache.addCacheFile(new URI(CONF_PATH + CONFIG_PARAM_VALUE + ".header#" + CONFIG_PARAM_VALUE + ".header"), job.getConfiguration());
		DistributedCache.addCacheFile(new URI(CONF_PATH + CONFIG_PARAM_VALUE + ".xsl#" + CONFIG_PARAM_VALUE + ".xsl"), job.getConfiguration());
		DistributedCache.addCacheFile(new URI(CONF_PATH + CONFIG_PARAM_VALUE + ".xsd#" + CONFIG_PARAM_VALUE + ".xsd"), job.getConfiguration());
	
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		int returnValue = job.waitForCompletion(true) ? 0 : 1;
		return returnValue;
	}

}