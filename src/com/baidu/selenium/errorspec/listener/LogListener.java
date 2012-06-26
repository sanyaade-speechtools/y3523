package com.baidu.selenium.errorspec.listener;

import java.util.List;

import com.baidu.selenium.errorspec.PolicyResult;
import com.baidu.selenium.errorspec.PolicyResult.MonitorResult;

/**
 * hi的通知插件
 * 
 * @author sakyo
 * 
 */
public class LogListener extends ListenerBase {

	public void afterAllMonitor(List<PolicyResult> lm) {
		for (PolicyResult policyResult : lm) {
			System.out.println(policyResult.getPolicy().getName() + ":\r\n");
			for (MonitorResult mr : policyResult.getResults()) {
				System.out.println("\r" + mr.toString());
			}
		}
	}
	
}
