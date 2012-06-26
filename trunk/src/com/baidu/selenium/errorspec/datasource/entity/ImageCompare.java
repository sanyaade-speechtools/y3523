/**
 * 
 */
package com.baidu.selenium.errorspec.datasource.entity;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlInlineBinaryData;

import com.baidu.selenium.errorspec.tools.xml.XMLElement;


/**
 * 图像对比结果的XML树
 * 
 * @author sakyo
 */
public class ImageCompare extends XMLElement{
	@XmlAttribute
	public String baseImageRootPath;
	@XmlAttribute
	public String compareImageRootPath;
	@XmlAttribute(name="started-at")
	public String starttime;
	@XmlAttribute(name="finished-at")
	public String endtime;
	@XmlAttribute
	public String folderlevel;
	@XmlAttribute
	public String total;
	@XmlAttribute
	public String failed;
	@XmlAttribute
	public String passed;
	@XmlAttribute
	public String skip;
	@XmlAttribute
	public String name;
	@XmlElement
	public List<Pic> pic;
	
	public static class Pic extends XMLElement{
		@XmlAttribute(name="started-at")
		public String starttime;
		@XmlAttribute(name="finished-at")
		public String endtime;
		@XmlAttribute
		public String filesize;
		@XmlAttribute
		public String basepath;
		@XmlAttribute
		public String path;
		@XmlAttribute
		public String name;
		@XmlAttribute
		public String status;
		@XmlElement
		public CompareResult CompareResult;
	}
	
	public static class CompareResult extends XMLElement{
		@XmlAttribute
		public String method;
		//find all children if name =""
		@XmlInlineBinaryData
		public String trace;
		
	}
	
}
