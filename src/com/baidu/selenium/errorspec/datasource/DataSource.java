/**
 * 
 */
package com.baidu.selenium.errorspec.datasource;

import com.baidu.selenium.errorspec.datasource.entity.ImageCompare;
import com.baidu.selenium.errorspec.datasource.entity.JobInfo;
import com.baidu.selenium.errorspec.datasource.entity.TestNG;

/**
 * 数据源接口
 * 
 * @author sakyo
 */
public interface DataSource {
	/**
	 * 获取图片对比信息
	 * @return
	 */
	ImageCompare getImageCompare();
	/**
	 * 获取TestNG的数据
	 * @return
	 */
	TestNG getTestNG();
	/**
	 * 获取分布式的TestNG数据
	 * @return
	 */
	TestNG getDistributeTestNG(String pre, String end);
	/**
	 * 获取Job运行环境
	 * @return
	 */
	JobInfo getJobInfo();
}
