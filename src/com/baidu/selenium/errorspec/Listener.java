package com.baidu.selenium.errorspec;

import java.util.List;

import com.baidu.selenium.errorspec.datasource.DataSource;

/**
 * 对于监控策略的监听者，在策略执行的各个阶段进行事件处理
 * 
 * @author sakyo
 *
 */
public interface Listener {
	
	/**
	 * 在获取数据源之前进行操作
	 */
	public void beforeGetData(DataSource ds);
	
	/**
	 * 在所有策略开始之前
	 */
	public void beforeAllMonitor(List<Policy> lp);
	
	/**
	 * 在每一个策略开始之前
	 */
	public void beforeMonitor(Policy py);
	
	/**
	 * 在每一个策略结束之后
	 */
	public void afteMonitor(PolicyResult mr);
	
	/**
	 * 在所有策略结束之后
	 */
	public void afterAllMonitor(List<PolicyResult> lm);
}
