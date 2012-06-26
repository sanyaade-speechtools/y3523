/**
 * 
 */
package com.baidu.selenium.errorspec.tools.html;

import java.util.ArrayList;
import java.util.List;

import com.baidu.selenium.errorspec.PolicyResult;
import com.baidu.selenium.errorspec.PolicyResult.MonitorResult;
import com.baidu.selenium.errorspec.datasource.entity.TestNG;
import com.baidu.selenium.errorspec.datasource.entity.TestNG.TestMethod;

/**
 * @author sakyo
 * 
 */
public class TestNGHtmlBuilder extends HtmlBuilder {

	protected PolicyResult policyresult;
	
	public TestNGHtmlBuilder(PolicyResult pr, String vm) {
		super(vm);
		this.policyresult = pr;
	}

	public TestNGHtmlBuilder(PolicyResult pr) {
		super();
		this.policyresult = pr;
	}

	@Override
	public String build() {
		List<TR> trs = new ArrayList<TR>();
		List<TD> ths = new ArrayList<TD>();
		String desc = "";
		TR tr = new TR(ths);
		trs.add(tr);
		ths.add(new TH("Case名称"));
		ths.add(new TH("参数"));
		ths.add(new TH("失败原因"));
		ths.add(new TH("日志"));
		ths.add(new TH("失败截图"));
		for (MonitorResult mr : policyresult.getResults()) {
			try {
				if (mr.getObj() instanceof TestNG) {
					desc = "Total:" + ((TestNG) mr.getObj()).total + " Success:" + ((TestNG) mr.getObj()).passed + " Fail:" + ((TestNG) mr.getObj()).failed
							+ " Skip:" + ((TestNG) mr.getObj()).skipped;
					continue;
				}
				TestMethod tm = (TestMethod) mr.getObj();
				List<TD> tds = new ArrayList<TD>();
				TR temptr = new TR(tds);
				tds.add(new TD(tm.name));
				StringBuffer sb = new StringBuffer();
				for (String s : tm.getParams()) {
					sb.append(s).append(";");
				}
				tds.add(new TD(sb.toString()));
				tds.add(new TD(tm.exception.message.data));
				tds.add(new TD(tm.exception.trace.data));
				tds.add(new TD(pic(tm.pic)));
				trs.add(temptr);
			} catch (Exception e) {
				logger.error("parse result for html error.", e);
				continue;
			}
		}
		return new H(3, policyresult.getPolicy().getName()).toString() + new H(5, desc) + new Table(trs).toString();
	}

	public static class NoWrapTD extends TD {
		public NoWrapTD(String td) {
			super(td);
		}

		public String toString() {
			return "<td nowrap class=\"alt\">" + td + "</td>";
		}
	}
}
