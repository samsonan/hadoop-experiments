package com.samsonan.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class XmlUtil {

	public static String getCurrentDate() {
		
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date date = new Date();
		return dateFormat.format(date);
	
	}

}
