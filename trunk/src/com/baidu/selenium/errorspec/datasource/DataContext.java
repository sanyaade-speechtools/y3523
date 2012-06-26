/**
 * 
 */
package com.baidu.selenium.errorspec.datasource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.baidu.selenium.errorspec.ErrorSpec;
import com.baidu.selenium.errorspec.datasource.entity.ImageCompare;
import com.baidu.selenium.errorspec.datasource.entity.TestNG;
import com.baidu.selenium.errorspec.tools.xml.SimpleXMLHandle;

/**
 * 数据源获取的上下文环境
 * 
 * @author sakyo
 * 
 */
public class DataContext {

	public static final Logger logger = LoggerFactory.getLogger(ErrorSpec.class);
	private static DocumentBuilderFactory xmlparserfactiory;
	static {
		try {
			xmlparserfactiory = DocumentBuilderFactory.newInstance();
		} catch (Exception e) {
			logger.error("Cannot initialize a DocumentBuilderFactory\n", e);
		}
	}

	protected DocumentBuilder getDocumentBuilder() {
		try {
			return xmlparserfactiory.newDocumentBuilder();
		} catch (Exception e) {
			logger.error("Cannot initialize a DocumentBuilder\n", e);
			return null;
		}
	}

	protected ImageCompare parseImageCompare(String path) {
		try {
			File file = new File(path);
			Element project = (Element) getDocumentBuilder().parse(file).getDocumentElement().getFirstChild();
			SimpleXMLHandle sx = new SimpleXMLHandle(project);
			return sx.parse(ImageCompare.class);

		} catch (Exception e) {
			logger.error("Cannot parse a parseImageCompare XML File " + path, e);
			return null;
		}
	}

	protected TestNG parseTestNG(String path) {
		try {
			File file = new File(path);
			Element project = (Element) getDocumentBuilder().parse(file).getDocumentElement();
			SimpleXMLHandle sx = new SimpleXMLHandle(project);
			return sx.parse(TestNG.class);

		} catch (Exception e) {
			logger.error("Cannot parse a parseTestNG XML File " + path, e);
			return null;
		}
	}

	protected List<TestNG> parseDistributeTestNG(String pre, String end) {
		List<TestNG> distributeTestNGs = new ArrayList<TestNG>();
		File dir = new File(pre);
		if (!dir.exists()) {
			logger.warn("Dir doesn't exist: "+dir.getAbsolutePath());
			return distributeTestNGs;
		}
		File[] dirs = dir.listFiles();
		if (dirs == null) {
			logger.warn("Couldn't find folders in "+dir.getAbsolutePath());
			return distributeTestNGs;
		}
		for (File file : dirs) {
			if (!file.isDirectory())
				break;
			String path = file.getAbsolutePath()+File.separator+end;
			try {
				distributeTestNGs.add(parseTestNG(path));
			} catch (Exception e) {
				logger.error("Parse distribute testng-results.xml fail.", e);
				continue;
			}
		}
		return distributeTestNGs;
	}
}
