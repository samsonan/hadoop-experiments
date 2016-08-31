package com.samsonan.hadoop.mr;

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.SequenceFile.Writer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.log4j.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.samsonan.service.ConverterException;
import com.samsonan.service.ConverterService;
import com.samsonan.service.GuiceModule;

/**
 * Apply XSL transformation to XMLs in the values
 * 
 * @author Andrey Samsonov (samsonan)
 *
 */
public class XsltReducer extends Reducer<LongWritable, Text, LongWritable, Text> {

	private static Logger LOG = Logger.getLogger(CsvToXmlMapper.class);

	private final static String CONFIG_PARAM_NAME = "converter.configuration.name";

	private String confName; // e.g. exchange_rates

	private File xslFile;
	private ConverterService service;

	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		Injector injector = Guice.createInjector(new GuiceModule());
		service = injector.getInstance(ConverterService.class);

		System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");

		confName = context.getConfiguration().get(CONFIG_PARAM_NAME);

		if (confName == null)
			throw new IOException("Mandatory parameter " + CONFIG_PARAM_NAME +" is undefined");
		
		String xslFileName = confName + ".xsl";
		xslFile = new File(xslFileName);

		LOG.info("reducer setup: configuration name:" + confName + "; xsl file exists:" + xslFile.exists());

		if (!xslFile.exists())
			throw new IOException("XSL file " + xslFileName + " doesn't exist!");
	}

	@Override
	public void reduce(LongWritable key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {

		Path outputFilePath = new Path(FileOutputFormat.getOutputPath(context).toUri()+"/"+key+".seq");
		LOG.info("reducer: {key:" + key + ". output file:"+outputFilePath+"}");
		
		SequenceFile.Writer writer = null;
		
		try {

			writer = SequenceFile.createWriter(context.getConfiguration(), 
					Writer.file(outputFilePath), 
					Writer.keyClass(key.getClass()),
					Writer.valueClass(Text.class));

//					Writer.bufferSize(fs.getConf().getInt("io.file.buffer.size", 4096)),
//					Writer.replication(fs.getDefaultReplication()), Writer.blockSize(1073741824),
//					Writer.compression(SequenceFile.CompressionType.BLOCK, new DefaultCodec()),
//					Writer.progressable(null), Writer.metadata(new Metadata()));

			LOG.info("reducer. SequenceFile writer is created");
			
			for (Text val : values) {
				try {
					LOG.info("  #" + key + " value:" + val);
					String outputXML = service.transformXml(val.toString(), xslFile);
					writer.append(key, new Text(outputXML));
				} catch (ConverterException e) {
					LOG.error("Cannot transform given XML: " + val, e);
				}
			}
		} finally {
			IOUtils.closeStream(writer);
		}
	}



}
