package com.samsonan.test;

public class UseCases {
	
	//case 1. processing header
	// ignored by mapper
	public static String CASE1_HEADER_IN = "pk, bank_code, currency_code, buy_rate, sell_rate";
	
	//case 2. correct input. rate < 25
	public static String CASE2_RT_LT20_IN = "3, Praveks, usd, 24.0000, 26.5000";
	public static String CASE2_RT_LT20_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><exchange_rates><pk>3</pk><bank_code>Praveks</bank_code><currency_code>usd</currency_code><buy_rate>24.0000</buy_rate><sell_rate>26.5000</sell_rate></exchange_rates>";
	public static String CASE2_RT_LT20_XSLT_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><exchange_rates><pk>3</pk><currency_code>usd</currency_code><buy_rate>24.0000</buy_rate><sell_rate>26.5000</sell_rate></exchange_rates>";

	//case 3. correct input. rate == 25
	public static String CASE3_RT_25_IN = "2, OTPBank, usd, 25.0000, 27.0000";
	public static String CASE3_RT_25_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><exchange_rates><pk>2</pk><bank_code>OTPBank</bank_code><currency_code>usd</currency_code><buy_rate>25.0000</buy_rate><sell_rate>27.0000</sell_rate></exchange_rates>";
	public static String CASE3_RT_25_XSLT_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><exchange_rates><pk>2</pk><currency_code>usd</currency_code><buy_rate>25.0000</buy_rate><sell_rate>27.0000</sell_rate></exchange_rates>";
	
	//case 4. correct input. rate > 25 - additional tag added
	public static String CASE4_RT_GT25_IN = "6, Credit_Agricole, eur, 26.0000, 29.5000";
	public static String CASE4_RT_GT25_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><exchange_rates><pk>6</pk><bank_code>Credit_Agricole</bank_code><currency_code>eur</currency_code><buy_rate>26.0000</buy_rate><sell_rate>29.5000</sell_rate></exchange_rates>";
	public static String CASE4_RT_GT25_XSLT_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><exchange_rates><pk>6</pk><currency_code>eur</currency_code><buy_rate>26.0000</buy_rate><information date=\"%s\">Buy rate more then 25</information><sell_rate>29.5000</sell_rate></exchange_rates>";

	//case 5. less values than in header - exception 
	public static String CASE5_VALS_LT_HDR_IN = "8, Praveks, eur, 28.5000";
	
	//case 6. more values than in header - exception
	public static String CASE6_VALS_GT_HDR_IN = "8, Praveks, eur, 27.0000, 28.5000, extra value";

	//case 7. quoted values! currently not processed correctly! TODO: will have to use reg exp for the split 
	public static String CASE7_QUOTED_IN = "\"9\", \"PrivatBank\", \"eur\", \"23.0000\", \"28.9855\"";
	public static String CASE7_QUOTED_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><exchange_rates><pk>\"9\"</pk><bank_code>\"PrivatBank\"</bank_code><currency_code>\"eur\"</currency_code><buy_rate>\"23.0000\"</buy_rate><sell_rate>\"28.9855\"</sell_rate></exchange_rates>";
	
	//case 8. empty csv - exception
	public static String CASE8_EMPTY_IN = "";

	//case 9. XML violates XSD
	public static String CASE9_VIOLATEXSD_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><exchange_rates><pk>6</pk><bank_code>Credit_Agricole</bank_code><currency_code>eur</currency_code><buy_rate>26.0000</buy_rate><sell_rate>29.50000000001</sell_rate></exchange_rates>";
	
	
}
