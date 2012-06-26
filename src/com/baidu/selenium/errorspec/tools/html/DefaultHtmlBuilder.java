/**
 * 
 */
package com.baidu.selenium.errorspec.tools.html;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.baidu.selenium.errorspec.PolicyResult;
import com.baidu.selenium.errorspec.PolicyResult.MonitorResult;
import com.baidu.selenium.errorspec.listener.MailListener;

/**
 * 提供默认的邮件
 * @author sakyo
 *
 */
public class DefaultHtmlBuilder extends HtmlBuilder {

	protected PolicyResult policyresult;
	
	public DefaultHtmlBuilder(PolicyResult pr, String vm) {
		super(vm);
		this.policyresult = pr;
	}

	public DefaultHtmlBuilder(PolicyResult pr) {
		super();
		this.policyresult = pr;
	}

	@Override
	public String build() {
		Set<String> titles = new HashSet<String>();
		List<Map<String, Object>> table = new ArrayList<Map<String,Object>>();
		for (MonitorResult result : policyresult.getResults()) {
			Map<String, Object> map = result.getAttribute(MailListener.class);
			if(map.size() == 0)
				continue;
			for (Object p: map.keySet()) {
				titles.add(p.toString());
			}
			table.add(map);
		}
		String desc = "";
		List<TR> trs = new ArrayList<TR>();
		List<TD> ths = new ArrayList<TD>();
		TR tr = new TR(ths);
		trs.add(tr);
		for (String th : titles) {
			ths.add(new TH(th));
		}
		for (Map<String, Object> tmptr : table) {
			List<TD> tds = new ArrayList<TD>();
			TR temptr = new TR(tds);
			for (String th : titles) {
				if(tmptr.get(th) == null)
					tds.add(new TD("null"));
				else
					tds.add(new TD(tmptr.get(th).toString()));
			}
			trs.add(temptr);
		}
		return new H(3,policyresult.getPolicy().getName()).toString()+new H(5,desc)+new Table(trs).toString();
	}
}
