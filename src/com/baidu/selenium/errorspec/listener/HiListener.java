package com.baidu.selenium.errorspec.listener;

import java.util.List;

import com.baidu.selenium.errorspec.PolicyResult;
import com.baidu.selenium.errorspec.PolicyResult.Level;
import com.baidu.selenium.errorspec.config.Config;
import com.baidu.selenium.errorspec.policy.TestNGPolicy;
import com.baidu.selenium.errorspec.tools.Hi;
import com.baidu.selenium.errorspec.tools.Hi.Msg;
import com.baidu.selenium.errorspec.tools.hi.HiClient;

/**
 * ErrorSpec的日志系统插件
 * 
 * @author sakyo
 * 
 */
public class HiListener extends ListenerBase {

	public void afterAllMonitor(List<PolicyResult> lm) {
		Hi hi = new Hi();
		hi.login(Config.HiName, Config.HiPassword);
		boolean isFailed = false;
		for (PolicyResult policyResult : lm) {
			if (policyResult != null && policyResult.getResults() != null && policyResult.getResults().size() > 0) {
				if (policyResult.getPolicy().getClass() == TestNGPolicy.class) {
					if (policyResult.getLevel().ordinal() >= Level.ERROR.ordinal()) {
						isFailed = true;
					}
				}
			}
		}
		if (isFailed) {
			for (String to : Config.HiTo.split(",")) {
				HiClient.sendMessage(to.trim(),Msg.msg("Pay attention to Job: "+dataSource.getJobInfo().getJobName()+"\r\n"), Msg.link(dataSource.getJobInfo().getBuildUrl()));
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
	}

}
