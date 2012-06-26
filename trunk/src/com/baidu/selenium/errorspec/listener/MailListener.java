package com.baidu.selenium.errorspec.listener;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.selenium.errorspec.Listener;
import com.baidu.selenium.errorspec.Policy;
import com.baidu.selenium.errorspec.PolicyResult;
import com.baidu.selenium.errorspec.config.Constants;
import com.baidu.selenium.errorspec.datasource.DataSource;
import com.baidu.selenium.errorspec.tools.MailHelper;
import com.baidu.selenium.errorspec.tools.html.HtmlBuilder;
import com.baidu.selenium.errorspec.tools.html.HtmlBuilderFactory;

/**
 * ErrorSpec的发邮件Action
 * 
 * @author sakyo
 * 
 */
public class MailListener extends HtmlBuilder implements Listener {
	private DataSource dataSource;

	public void afterAllMonitor(List<PolicyResult> lm) {
		List<HtmlBuilder> hbs = new ArrayList<HtmlBuilder>();
		hbs.add(new HtmlBuilder(Constants.HTML_VM) {
			@Override
			public String build() {
				StringBuffer sb = new StringBuffer();
				sb.append(new H(2, dataSource.getJobInfo().getJobName()));
				sb.append(new H(5, "JobURL:\t"
						+ dataSource.getJobInfo().getJobUrl()));
				return sb.toString();
			}
		});
		for (PolicyResult policyResult : lm) {
			if (policyResult.getLevel().ordinal() < com.baidu.selenium.errorspec.PolicyResult.Level.ERROR
					.ordinal()) {
				continue;
			}
			hbs.add(HtmlBuilderFactory.getBuilder(policyResult));
		}
		if (hbs.size() > 1) {
			new MailHelper().send(hbs, getSubject());
		}

	}

	public void beforeGetData(DataSource ds) {
		this.dataSource = ds;
	}

	private String getSubject() {
		return Constants.MAIL_SUBJECT + " "
				+ dataSource.getJobInfo().getJobName() + "_"
				+ dataSource.getJobInfo().getBuildNumber();
	}

	@Override
	public void beforeAllMonitor(List<Policy> lp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeMonitor(Policy py) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afteMonitor(PolicyResult mr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String build() {
		// TODO Auto-generated method stub
		return null;
	}
}
