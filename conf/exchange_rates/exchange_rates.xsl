<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:java="java:com.samsonan.util.XmlUtil"
	exclude-result-prefixes = "xs xsl xsi fn java">
	
	<xsl:output method="xml" encoding="UTF-8"/>

	<!-- deep copy, including attributes. just in case -->
	<xsl:template match="@*|node()">
        	<xsl:copy><xsl:apply-templates select="@*|node()"/></xsl:copy>
    	</xsl:template>
	
	<!-- don't copy bank_code -->
	<xsl:template match="bank_code"/>

	<!-- special case for buy_rate > 25 -->
	<xsl:template match="buy_rate[. > 25]"><xsl:copy-of select="self::node()"/><information date="{java:getCurrentDate()}">Buy rate more then 25</information></xsl:template>

</xsl:stylesheet>