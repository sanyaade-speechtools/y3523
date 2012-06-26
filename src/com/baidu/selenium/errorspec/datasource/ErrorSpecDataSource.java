/**
 * 
 */
package com.baidu.selenium.errorspec.datasource;

import java.util.ArrayList;
import java.util.List;

import com.baidu.selenium.errorspec.config.Config;
import com.baidu.selenium.errorspec.datasource.entity.ImageCompare;
import com.baidu.selenium.errorspec.datasource.entity.JobInfo;
import com.baidu.selenium.errorspec.datasource.entity.TestNG;
import com.baidu.selenium.errorspec.datasource.entity.TestNG.TestMethod;

/**
 * ErrorSpec传入的数据源
 * 
 * @author sakyo
 * 
 */
public class ErrorSpecDataSource extends DataContext implements DataSource {

	public ImageCompare getImageCompare() {
		return parseImageCompare(Config.ImageCompareXml);
	}

	public TestNG getTestNG() {
		return parseTestNG(Config.TestNGXml);
	}

	public TestNG getDistributeTestNG(String pre, String end) {
		List<TestNG> distributeTestNGs = parseDistributeTestNG(pre, end);
		List<TestMethod> distributeTestMethod = new ArrayList<TestMethod>();
		TestNG totaltg = new TestNG();
		int total = 0;
		int skip = 0;
		int success=0;
		int fail = 0;
		for (TestNG testNG : distributeTestNGs) {
			try {
				total = total + Integer.parseInt(testNG.total);
			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				skip = skip + Integer.parseInt(testNG.skipped);
			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				fail = fail + Integer.parseInt(testNG.failed);
			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				success = success + Integer.parseInt(testNG.passed);
			} catch (Exception e) {
				// TODO: handle exception
			}
			if (testNG.testmethod != null) {
				for (TestMethod testMethod : testNG.testmethod) {
					distributeTestMethod.add(testMethod);
				}
			}
		}
		totaltg.total = total + "";
		totaltg.failed = fail + "";
		totaltg.skipped = skip + "";
		totaltg.passed = success +"";
		totaltg.testmethod = distributeTestMethod;
		return totaltg;
	}

	public JobInfo getJobInfo() {
		return new JobInfo();
	}


}
