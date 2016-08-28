package com.samsonan.util;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class XmlUtil {

	public static String getCurrentDate() {
		LocalDate localDate = new LocalDate();
		DateTimeFormatter formatOut = DateTimeFormat.forPattern("MM/dd/yyyy");
		return formatOut.print(localDate);
	}

}
