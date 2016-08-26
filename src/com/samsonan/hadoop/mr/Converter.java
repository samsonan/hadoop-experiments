package com.samsonan.hadoop.mr;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.samsonan.util.XmlException;
import com.samsonan.util.XmlUtil;

public class Converter {

	private static Logger LOG = Logger.getLogger(Converter.class);
	
	//TODO: get it from file
	public static String [] HEADER_LIST = {"pk","currency_code","buy_rate", "sell_rate"};

	public static String XML_ROOT_NAME = "exchange_rates";
		
	public static class XmlMapper extends Mapper<Object, Text, Text, Text> {

		public void map(Object key, Text value, Context context) throws IOException, InterruptedException  {

			LOG.info("mapper: {key:" + key + "; value:" + value + "}");

			try {
				Document xmlResult;
				xmlResult = XmlUtil.convertCsvToPlainXml(value.toString(), XML_ROOT_NAME, HEADER_LIST);
			
				LOG.info("mapper: #" + key + " transformation: OK"); 

				XmlUtil.validateXml(xmlResult);

				LOG.info("mapper: #" + key + " validation: OK"); 
				
				String strXmlResult = XmlUtil.xmlToString(xmlResult);

				LOG.info("mapper: #" + key + " result:" + strXmlResult); 
				
				context.write(new Text(key.toString()), new Text(strXmlResult));
				
			} catch (XmlException ex) {
				LOG.error("Cannot transform CSV string to XML",ex);
			}
		}
	}

	public static class XsltReducer extends Reducer<Text, Text, Text, Text> {

		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {

			LOG.info("reducer: {key:" + key + "}");
			
			for (Text val : values) {
				LOG.info("  #" + key + " value:" + val);
			}
			
			Text xmlResult = new Text("<result>");
			context.write(key, xmlResult);
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		
		Job job = Job.getInstance(conf, "xml converter");
		job.setJarByClass(Converter.class);
		job.setMapperClass(XmlMapper.class);
		job.setReducerClass(XsltReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}