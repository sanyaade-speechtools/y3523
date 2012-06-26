/**
 * 
 */
package com.baidu.selenium.errorspec.policy;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.baidu.selenium.errorspec.Policy;
import com.baidu.selenium.errorspec.PolicyResult;
import com.baidu.selenium.errorspec.PolicyResult.Level;
import com.baidu.selenium.errorspec.PolicyResult.MonitorResult;
import com.baidu.selenium.errorspec.config.Config;
import com.baidu.selenium.errorspec.datasource.DataSource;
import com.baidu.selenium.errorspec.datasource.entity.TestNG;
import com.baidu.selenium.errorspec.datasource.entity.TestNG.TestMethod;

/**
 * @author sakyo
 *
 */
public class TestNGPolicy extends PolicyBase implements Policy {

	/* (non-Javadoc)
	 * @see com.baidu.selenium.errorspec.Policy#getName()
	 */
	@Override
	public String getName() {
		return "TestNG Report";
	}

	/* (non-Javadoc)
	 * @see com.baidu.selenium.errorspec.Policy#getDescription()
	 */
	@Override
	public String getDescription() {
		return "TestNG的错误分析结果显示";
	}

	/* (non-Javadoc)
	 * @see com.baidu.selenium.errorspec.Policy#monitor(com.baidu.selenium.errorspec.datasource.DataSource)
	 */
	@Override
	public PolicyResult monitor(DataSource ds) {
		TestNG tn  = new TestNG();
		if(Config.TestNGPre != null && Config.TestNGEnd != null){
			tn = ds.getDistributeTestNG(Config.TestNGPre, Config.TestNGEnd);
		}
		else {
			tn = ds.getTestNG();
		}
		for (TestMethod tm : tn.testmethod) {
			if (!"PASS".equals(tm.status))
			{
				/**
				 * 这里在result里插入对应的图片地址
				 */
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
				try {
					tm.pic = Config.TestFailImagePath+File.separator+tm.name+"_"+sdf.parse(tm.start).getTime()/1000+".png";
				} catch (ParseException e) {
					tm.pic = "Not Find";
				}
				policyResult.addResult(new MonitorResult(Level.ERROR, tm));
			}
		}
		policyResult.addResult(new MonitorResult(Level.INFO, tn));
		return policyResult;
	}

}
