package com.samsonan.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Methods used in XSL transformations
 * @author asamsonov
 *
 */
public class XmlUtil {

	public static String getCurrentDate() {
		
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date date = new Date();
		return dateFormat.format(date);
	
	}

}
