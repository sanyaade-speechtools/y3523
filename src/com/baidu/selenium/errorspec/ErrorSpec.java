/**
 * 
 */
package com.baidu.selenium.errorspec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.LoggerFactory;

import com.baidu.selenium.errorspec.config.Config;
import com.baidu.selenium.errorspec.datasource.DataSource;
import com.baidu.selenium.errorspec.datasource.ErrorSpecDataSource;

/**
 * @author sakyo
 * 
 */
public class ErrorSpec extends Notifier {

	protected DataSource dataSource;
	protected List<PolicyResult> policyResultsList = Collections
			.synchronizedList(new ArrayList<PolicyResult>());
	protected List<Policy> policies = Collections
			.synchronizedList(new ArrayList<Policy>());
	protected List<Listener> listeners = Collections
			.synchronizedList(new ArrayList<Listener>());

	public ErrorSpec(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * 运行ErrorSpec
	 */
	public void run() {
		prepar();
		this.addListener(listeners);
		this.beforeGetData(dataSource);
		this.beforeAllMonitor(policies);
		for (Policy policy : policies) {
			logger.info("Begin Policy :" + policy.getClass().getSimpleName()
					+ "(" + policy.getName() + ")\r\n");
			this.beforeMonitor(policy);
			try {
				PolicyResult policyResult = policy.monitor(dataSource);
				policyResultsList.add(policyResult);
				this.afteMonitor(policyResult);
			} catch (Exception e) {
				logger.error("policy " + policy + " runn error.", e);
			}
			logger.info("End Policy.");
		}
		this.afterAllMonitor(policyResultsList);
	}

	public static void main(String[] args) {
		try {
			ErrorSpec errorspec = new ErrorSpec(new ErrorSpecDataSource());
			errorspec.run();
			System.exit(errorspec.getExitCode());
		} catch (Exception e) {
			LoggerFactory.getLogger(ErrorSpec.class).error(
					"UnException in main()", e);
			System.exit(0);
		}
	}

	/**
	 * 这里设置程序的返回值，目前强制为0，如果以后要根据程序处理，这里可以处理下结果
	 * @return 0
	 */
	public int getExitCode(){
		return 0;
	}
	
	/**
	 * 初始化所有的Listener和Policy
	 */
	private void prepar() {
		String[] policyName = Config.Policy.split(",");
		for (String pn : policyName) {
			Object policy = isExist(pn);
			if (policy == null) {
				policy = instantiate(pn.trim());
			}
			if (policy instanceof Policy) {
				policies.add((Policy) policy);
			} else {
				logger.error(pn + "isn't instanceof Policy.");
			}
		}
		String[] listenerName = Config.Listener.split(",");
		for (String ln : listenerName) {
			Object listener = isExist(ln);
			if (listener == null) {
				listener = instantiate(ln.trim());
			}
			if (listener instanceof Listener) {
				listeners.add((Listener) listener);
			} else {
				logger.error(ln + "isn't instanceof Listener.");
			}
		}
	}

	/**
	 * 取出系统中已经实例化的Listener和Policy，如果存在相同实例，则直接返回。这样Policy和Listener可同用一个文件
	 * 
	 * @param name
	 * @return
	 */
	private Object isExist(String name) {
		if (name.trim().equals(""))
			return null;
		for (Policy policy : policies) {
			if (name.trim().equalsIgnoreCase(policy.getClass().getName()))
				return policy;
		}
		for (Listener listener : listeners) {
			if (name.trim().equalsIgnoreCase(listener.getClass().getName()))
				return listener;
		}
		return null;
	}

	/**
	 * 实例化Listener和Policy
	 * 
	 * @param classname
	 * @return
	 */
	private Object instantiate(String classname) {
		try {
			Class<?> cla = Class.forName(classname);
			return cla.newInstance();
		} catch (Exception e) {
			logger.error("Can not create an instance:" + classname, e);
			return null;
		}
	}
}
