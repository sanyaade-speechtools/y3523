/**
 * 
 */
package com.baidu.selenium.errorspec.tools.html;

import java.util.ArrayList;
import java.util.List;

import com.baidu.selenium.errorspec.PolicyResult;
import com.baidu.selenium.errorspec.PolicyResult.MonitorResult;
import com.baidu.selenium.errorspec.datasource.entity.ImageCompare;

/**
 * @author sakyo
 *
 */
public class ImageCompareHtmlBuilder extends HtmlBuilder {

	protected PolicyResult policyresult;
	
	public ImageCompareHtmlBuilder(PolicyResult pr, String vm) {
		super(vm);
		this.policyresult = pr;
	}

	public ImageCompareHtmlBuilder(PolicyResult pr) {
		super();
		this.policyresult = pr;
	}

	@Override
	public String build() {
		String desc = "";
		List<TR> trs = new ArrayList<TR>();
		List<TD> ths = new ArrayList<TD>();
		TR tr = new TR(ths);
		trs.add(tr);
		ths.add(new TH("图片名称"));
		ths.add(new TH("对比结果"));
		ths.add(new TH("对比方式"));
		ths.add(new TH("日志"));
		ths.add(new TH("截图"));
		ths.add(new TH("基准图片"));
		for (MonitorResult mr : policyresult.getResults()) {
			if(mr.getObj() instanceof ImageCompare){
				desc = "Total:"+((ImageCompare)mr.getObj()).total+" Success:"+((ImageCompare)mr.getObj()).passed+" Fail:"+((ImageCompare)mr.getObj()).failed+" Skip:"+((ImageCompare)mr.getObj()).skip;
				continue;
			}
			ImageCompare.Pic p= (ImageCompare.Pic)mr.getObj();
			List<TD> tds = new ArrayList<TD>();
			TR temptr = new TR(tds);
			tds.add(new TD(p.name));
			tds.add(new TD(p.status));
			tds.add(new TD(p.CompareResult.method));
			tds.add(new TD(p.CompareResult.trace));
			tds.add(new TD(pic(p.path)));
			tds.add(new TD(pic(p.basepath)));
			trs.add(temptr);
		}
		return new H(3,policyresult.getPolicy().getName()).toString()+new H(5,desc)+new Table(trs).toString();
	}
	
	public static class NoWrapTD extends TD{
		public NoWrapTD(String td) {
			super(td);
		}
		public String toString() {
			return "<td nowrap class=\"alt\">" + td + "</td>";
		}
	}
}
