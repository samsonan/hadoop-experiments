package com.samsonan.oozie;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

/**
 * Class for oozie workflow that checks if the input dir exists and that it is not empty.  
 * @author asamsonov
 *
 */
public class ConverterJobDirCheck {

	private static Logger LOG = Logger.getLogger(ConverterJobDirCheck.class);
	
	public static void main(String[] args) throws Exception {
        String inputDirPath = args[0];
        
        LOG.info("input dir path:" + inputDirPath);
        
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        Path hadoopDir = new Path(inputDirPath);
        
        if (fs.exists(hadoopDir)){
            FileStatus[] files = FileSystem.get(conf).listStatus(hadoopDir);
            int fileNum = files.length;
            if (fileNum == 0)
            	throw new Exception("Input dir " + inputDirPath + " is empty! Num of files: " + fileNum);
            else {
                LOG.info("Input dir file count:" + fileNum);
            }
        } else { 
        	throw new Exception("Input dir " + inputDirPath + " doesn't exist!");
        }
        
        
	}
	
}
