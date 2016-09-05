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
import org.apache.log4j.Logger;

/**
 * Exchange Rates MapReduce Driver class.
 * @author asamsonov
 *
 */
public class ConverterJob extends Configured implements Tool {

	private static Logger LOG = Logger.getLogger(ConverterJob.class);
	
	private final static String PARAM_INSTANCE_NAME = "instance.name";
	private final static String PARAM_CONFIG_NAME = "converter.configuration.name";
	private final static String PARAM_CONFIG_PATH = "converter.configuration.path";	
	
	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new ConverterJob(), args);
		System.exit(exitCode);
	}

	@Override
	public int run(String[] args) throws Exception {
		
		Configuration conf = getConf();
		conf.addResource("converter-configuration.xml");
		
		String configurationName = conf.get(PARAM_CONFIG_NAME);
		String configurationPath = conf.get(PARAM_CONFIG_PATH);
		
		LOG.info("Converter Job: {configurationName:" + configurationName + "; "
				+ "configurationPath:" + configurationPath + "}");
		
		if (configurationName == null)
			throw new Exception ("Mandatory parameter " + PARAM_CONFIG_NAME + " is not set");
		
		if (configurationPath == null)
			throw new Exception ("Mandatory parameter " + PARAM_CONFIG_PATH + " is not set");
		
		Job job = new Job(conf, conf.get(PARAM_INSTANCE_NAME));
		job.setJarByClass(ConverterJob.class);
		job.setMapperClass(CsvToXmlMapper.class);
		job.setReducerClass(XsltReducer.class);
		job.setOutputKeyClass(LongWritable.class);
		job.setOutputValueClass(Text.class);

	    job.setOutputFormatClass(SequenceFileOutputFormat.class);
	    
	    //e.g. /user/cloudera/converter/exchange_rates.xsd#exchange_rates.xsd
	    //putting files in the  cache to use later in map/reduce jobs
		DistributedCache.addCacheFile(new URI(configurationPath + configurationName + ".header#" + configurationName + ".header"), job.getConfiguration());
		DistributedCache.addCacheFile(new URI(configurationPath + configurationName + ".xsl#" + configurationName + ".xsl"), job.getConfiguration());
		DistributedCache.addCacheFile(new URI(configurationPath + configurationName + ".xsd#" + configurationName + ".xsd"), job.getConfiguration());
	
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		int returnValue = job.waitForCompletion(true) ? 0 : 1;
		return returnValue;
	}

}