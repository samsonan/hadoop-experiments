# hadoop-experiments

This is a mall educational project using cloudera hadoop (cdh 5.5, hadoop 2.6), mapreduce, oozie

For simplicity everything (mr jobs, oozie) is packed into one jar file (hadoop-experiments-*.jar).

All paths and URLs are applicable for Cloudera Quickstart VM 5.5

- Oozie

  Checks if the input dir exists and is not empty. Starts MapReduce job (below) if everything is OK.

  Src:
  /oozie/workflow.xml
  /oozie/job.properties
  /src/*/ConverterJobDirCheck.java - class which performs the checks

  Web console: http://quickstart.cloudera:11000/oozie/

  Cmds:
  $ oozie job -oozie http://localhost:11000/oozie -config /local/path/to/job.properties -run
  $ oozie job -oozie http://localhost:11000/oozie -info 0000000-160819030053478-oozie-oozi-W
  $ oozie job -oozie http://localhost:11000/oozie -kill 0000003-160819030053478-oozie-oozi-W

- MapReduce

  1. Map:
    - Transform CSV to Plain XML 
    - Validate XML with given schema - if the XSD file exists, otherwise error is logged, process is not stopped
    - Output as <id, XML as Text>

  2. Reduce:
    - Apply XSL transformation to XMLs in the values
    - Write output to hadoop sequence file

  
  Dependencies & libs:
  /lib/saxon*.jar - cannot use maven repository, as saxon HE-edition doesnt allow to use java methods in XSLT

  Web console: http://quickstart.cloudera:19888/jobhistory/app
  
  Cmds:
  $ export LIBJARS=/local/path/to/saxon9-9.1.0.6.jar,/local/path/to/saxon9-dom-9.1.0.6.jar
  $ export HADOOP_CLASSPATH=/local/path/to/saxon9-9.1.0.6.jar:/local/path/to/saxon9-dom-9.1.0.6.jar
  $ hadoop jar hadoop-experiments-0.1.2-SNAPSHOT.jar -libjars $LIBJARS  /hdfs/input/path/ /hdfs/output/path/

 Map:  

Guide:

-- mapreduce


-- oozie

