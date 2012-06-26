/**
 * 
 */
package com.baidu.selenium.errorspec.datasource.entity;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.baidu.selenium.errorspec.Attribute;
import com.baidu.selenium.errorspec.tools.xml.XMLElement;


/**
 * 图像对比结果的XML树
 * 
 * @author sakyo
 */
public class TestNG extends XMLElement {
	@XmlAttribute
	public String skipped;
	@XmlAttribute
	public String failed;
	@XmlAttribute
	public String total;
	@XmlAttribute
	public String passed;
	@XmlElement(name = "test-method")
	public List<TestMethod> testmethod;

	@Attribute
	public static class TestMethod extends XMLElement {
		@XmlAttribute(name = "started-at")
		public String start;
		@XmlAttribute(name = "finished-at")
		public String endtime;
		@Attribute("CASE名称")
		@XmlAttribute
		public String name;
		@XmlAttribute
		public String status;
		@Attribute("参数")
		@XmlElement
		public Params params;
		@XmlElement
		public Exception exception;
		@Attribute("失败截图")
		public String pic;
		
		public List<String> getParams() {
			ArrayList<String> result = new ArrayList<String>();
			if(params == null)
				return result;
			for (Param par : params.param) {
				result.add(par.value.data);
			}
			return result;
		}
	}

	public static class Params extends XMLElement {
		@XmlElement
		public List<Param> param;
	}

	public static class Param extends XMLElement {
		@XmlElement
		public XMLElement value;
	}
	
	public static class Exception extends XMLElement{
		@XmlElement
		public XMLElement message;
		@XmlElement(name = "full-stacktrace")
		public XMLElement trace;
	}
}
