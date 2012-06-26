/**
 * 
 */
package com.baidu.selenium.errorspec.policy;

import com.baidu.selenium.errorspec.Policy;
import com.baidu.selenium.errorspec.PolicyResult;
import com.baidu.selenium.errorspec.PolicyResult.Level;
import com.baidu.selenium.errorspec.PolicyResult.MonitorResult;
import com.baidu.selenium.errorspec.datasource.DataSource;
import com.baidu.selenium.errorspec.datasource.entity.ImageCompare;
import com.baidu.selenium.errorspec.datasource.entity.ImageCompare.Pic;

/**
 * @author sakyo
 *
 */
public class PicPolicy extends PolicyBase implements Policy {

	/* (non-Javadoc)
	 * @see com.baidu.selenium.errorspec.Policy#getName()
	 */
	@Override
	public String getName() {
		return "截图对比";
	}

	/* (non-Javadoc)
	 * @see com.baidu.selenium.errorspec.Policy#getDescription()
	 */
	@Override
	public String getDescription() {
		return "截图对比服务的结果显示";
	}

	/* (non-Javadoc)
	 * @see com.baidu.selenium.errorspec.Policy#monitor(com.baidu.selenium.errorspec.datasource.DataSource)
	 */
	@Override
	public PolicyResult monitor(DataSource ds) {
		ImageCompare ic = ds.getImageCompare();
		for (Pic p : ic.pic) {
			if (!"PASS".equals(p.status))
				policyResult.addResult(new MonitorResult(Level.ERROR, p));
		}
		policyResult.addResult(new MonitorResult(Level.INFO, ic));
		return policyResult;
	}

}
