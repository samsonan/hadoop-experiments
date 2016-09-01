package com.samsonan.test.service

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.inject.Guice;
import com.google.inject.Injector
import com.samsonan.service.ConverterException;
import com.samsonan.service.ConverterService;
import com.samsonan.service.GuiceModule
import com.samsonan.test.UseCases;
import com.samsonan.util.XmlUtil;

import spock.lang.Shared;
import spock.lang.Specification;

class ConverterServiceSpec extends Specification {


	static final ROOT_NAME = "exchange_rates"
	static final HEADER_LIST = ["pk", "bank_code", "currency_code", "buy_rate", "sell_rate" ] as String[]

	static final XSD_PATH = "\\conf\\exchange_rates\\exchange_rates.xsd" 
	static final XSL_PATH = "\\conf\\exchange_rates\\exchange_rates.xsl"
	
	@Shared service
	@Shared xsdFile
	@Shared xslFile
	
	def setupSpec() {
		Injector injector = Guice.createInjector(new GuiceModule())
		service = injector.getInstance( ConverterService.class )
		
		xsdFile = new File(System.getProperty("user.dir") + XSD_PATH)
		xslFile = new File(System.getProperty("user.dir") + XSL_PATH)
	}

	def "CSV to plain XML"() {
		expect:
		service.convertCsvToPlainXml (csvString,ROOT_NAME,HEADER_LIST) == xmlString

		where:
		csvString       		      | xmlString
		UseCases.CASE2_RT_LT20_IN     | UseCases.CASE2_RT_LT20_XML
		UseCases.CASE3_RT_25_IN       | UseCases.CASE3_RT_25_XML
		UseCases.CASE4_RT_GT25_IN     | UseCases.CASE4_RT_GT25_XML
		UseCases.CASE7_QUOTED_IN      | UseCases.CASE7_QUOTED_XML
	}

	def "CSV too little values"() {
		when:
			service.convertCsvToPlainXml(UseCases.CASE5_VALS_LT_HDR_IN,ROOT_NAME,HEADER_LIST)
		then:
			def ex = thrown(ConverterException)
			ex.message == 'Error converting to plain XML. CSV:'+UseCases.CASE5_VALS_LT_HDR_IN
	}

	def "CSV too many values"() {
		when:
			service.convertCsvToPlainXml(UseCases.CASE6_VALS_GT_HDR_IN,ROOT_NAME,HEADER_LIST)
		then:
			def ex = thrown(ConverterException)
			ex.message == 'Error converting to plain XML. CSV:'+UseCases.CASE6_VALS_GT_HDR_IN
	}
	
	def "Empty CSV"() {
		when:
			service.convertCsvToPlainXml(UseCases.CASE8_EMPTY_IN,ROOT_NAME,HEADER_LIST)
		then:
			thrown(ConverterException)
	}

	def "valid XML"() {
		when:
			service.validateXmlString(UseCases.CASE3_RT_25_XML,xsdFile)
		then:
			notThrown();
	}

	def "not valid XML"() {
		when:
			service.validateXmlString(UseCases.CASE9_VIOLATEXSD_XML,xsdFile)
		then:
			thrown(ConverterException)
	}
	
	def "not valid XML with quoted values"() {
		when:
			service.validateXmlString(UseCases.CASE7_QUOTED_XML,xsdFile)
		then:
			thrown(ConverterException)
	}
	
	def "Transform XML"() {

		setup:
			//Spock can only mock static methods implemented in Groovy
			// so that doesnt work. have to use actual current date
			//GroovyMock(XmlUtil, global: true)
			//XmlUtil.getCurrentDate() >> "12/12/2008"
		
		expect:
			def date = new Date()
			def xmlWithCurrentDate = String.format(xmlOutString, date.format("dd/MM/yyy"))
			service.transformXml(xmlInString,xslFile) == xmlWithCurrentDate
		
		where:
		xmlInString       		       | xmlOutString
		UseCases.CASE2_RT_LT20_XML     | UseCases.CASE2_RT_LT20_XSLT_XML
		UseCases.CASE3_RT_25_XML       | UseCases.CASE3_RT_25_XSLT_XML
		UseCases.CASE4_RT_GT25_XML     | UseCases.CASE4_RT_GT25_XSLT_XML
	}

	def "transformation error"() {
		when:
			service.validateXmlString(UseCases.CASE8_EMPTY_IN,xsdFile)
		then:
			thrown(ConverterException)
	}

	
}
