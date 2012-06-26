/**
 * 
 */
package com.baidu.selenium.errorspec.policy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.selenium.errorspec.Policy;
import com.baidu.selenium.errorspec.PolicyResult;

/**
 * 默认的基础策略
 * @author sakyo
 * 
 */
public abstract class PolicyBase implements Policy {
	protected PolicyResult policyResult;
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	public PolicyBase() {
		policyResult = new PolicyResult(this);
	}

	public String getName() {
		return this.getClass().getSimpleName();
	}

	public String getDescription() {
		return this.getClass().getName();
	}
}
