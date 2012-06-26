/**
 * 
 */
package com.baidu.selenium.errorspec.tools.xml;

import javax.xml.bind.annotation.XmlInlineBinaryData;
import javax.xml.bind.annotation.XmlValue;

/**
 * @author sakyo
 *
 */
public class XMLElement {
	@XmlValue
	public String value="";
	
	@XmlInlineBinaryData
	public String data;
	
	@Override
	public String toString(){
		return value;
	}
}
