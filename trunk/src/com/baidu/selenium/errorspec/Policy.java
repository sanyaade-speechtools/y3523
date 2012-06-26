/**
 * 
 */
package com.baidu.selenium.errorspec;

import com.baidu.selenium.errorspec.datasource.DataSource;

/**
 * 策略接口
 * 
 * @author sakyo
 *
 */
public interface Policy {
	/**
	 * 策略名称
	 * @return 策略名称
	 */
	public String getName();
	
	/**
	 * 策略描述信息
	 * @return 策略的描述信息
	 */
	public String getDescription();
	
	/**
	 * 对数据源进行监控操作
	 */
	public PolicyResult monitor(DataSource datasource);
}
