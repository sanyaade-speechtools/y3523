package com.baidu.selenium.errorspec.config;


public class Config {
	/**
	 * 处理策略
	 */
	public static String Policy;
	/**
	 * 策略监听者
	 */
	public static String Listener;
	
	public static String ImageCompareXml;
	
	public static String MailTo;
	
	public static String TestNGXml;
	
	public static String TestNGPre;
	
	public static String TestNGEnd;
	
	public static String TestFailImagePath;
	
	public static String SvnUser;
	
	public static String SvnPassword;
	
	public static String HiName="y3523";
	
	public static String HiPassword="13774203523";
	
	public static String HiTo="sakyo_yang,songdragon1988";
	
	public static String HiRemoteServer ="10.237.2.161";
	
	public static int HiRemotePort = 8523;
	static {
		ConfigUtils.autowireConfig(Config.class,
				Constants.CONFIG_FILE);
	}
}
