package com.baidu.selenium.errorspec.tools.html;

import com.baidu.selenium.errorspec.PolicyResult;
import com.baidu.selenium.errorspec.policy.PicPolicy;
import com.baidu.selenium.errorspec.policy.TestNGPolicy;

public class HtmlBuilderFactory {
	public static HtmlBuilder getBuilder(PolicyResult pr) {
		if (pr != null && pr.getResults() != null && pr.getResults().size() > 0) {
			if(pr.getPolicy().getClass()==PicPolicy.class){
				return new ImageCompareHtmlBuilder(pr);
			}
			if(pr.getPolicy().getClass()==TestNGPolicy.class){
				return new TestNGHtmlBuilder(pr);
			}
		} 
		return new DefaultHtmlBuilder(pr);
	}

}
