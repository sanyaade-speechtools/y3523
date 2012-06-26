/**
 * 
 */
package com.baidu.selenium.errorspec.listener;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.selenium.errorspec.Listener;
import com.baidu.selenium.errorspec.Policy;
import com.baidu.selenium.errorspec.PolicyResult;
import com.baidu.selenium.errorspec.datasource.DataSource;

/**
 * 监听者抽象基类
 * 
 * @author sakyo
 */
public abstract class ListenerBase implements Listener {

	protected DataSource dataSource;
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	public void beforeGetData(DataSource ds) {
		this.dataSource = ds;
	}

	public void beforeAllMonitor(List<Policy> lp) {
		// TODO Auto-generated method stub

	}

	public void beforeMonitor(Policy py) {
		// TODO Auto-generated method stub

	}

	public void afteMonitor(PolicyResult mr) {
		// TODO Auto-generated method stub
	}
}
