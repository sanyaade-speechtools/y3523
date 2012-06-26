package com.baidu.selenium.errorspec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.selenium.errorspec.datasource.DataSource;

/**
 * 各种监听者的Notify
 * 
 * @author sakyo
 * 
 */
public class Notifier {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	protected final List<Listener> fListeners = Collections
			.synchronizedList(new ArrayList<Listener>());

	public void addListener(Listener listener) {
		if (listener != null)
			fListeners.add(listener);
	}

	protected void addListener(List<Listener> listeners) {
		for (Listener policyListener : listeners) {
			addListener(policyListener);
		}
	}

	protected void removeListener(Listener listener) {
		fListeners.remove(listener);
	}

	/**
	 * 这里统一循环遍历的过程，不需要每个事件都遍历listener (from
	 * junit4.9,没有使用EventFiringWebDriver的做法是因为反射效率低)
	 * 
	 * @author sakyo
	 * 
	 */
	private abstract class SafeNotifier {
		void run() {
			synchronized (fListeners) {
				for (Iterator<Listener> all = fListeners.iterator(); all
						.hasNext();)
					try {
						notifyListener(all.next());
					} catch (Exception e) {
						logger.warn("Listener Exception,remove All Listeners",
								e);
						all.remove(); // Remove the offending listener first to
										// avoid an infinite loop
					}
			}
		}

		abstract protected void notifyListener(Listener each) throws Exception;
	}

	/**
	 * 在获取数据源之前进行操作
	 */
	public void beforeGetData(final DataSource ds) {
		new SafeNotifier() {
			@Override
			protected void notifyListener(Listener each) throws Exception {
				logger.info("BeforeGetData: " + each.getClass().getName());
				each.beforeGetData(ds);
			};
		}.run();
	}

	/**
	 * 在所有策略开始之前
	 */
	public void beforeAllMonitor(final List<Policy> lp) {
		new SafeNotifier() {
			@Override
			protected void notifyListener(Listener each) throws Exception {
				logger.info("BeforeAllMonitor: " + each.getClass().getName());
				each.beforeAllMonitor(lp);
			};
		}.run();
	}

	/**
	 * 在每一个策略开始之前
	 */
	public void beforeMonitor(final Policy py) {
		new SafeNotifier() {
			@Override
			protected void notifyListener(Listener each) throws Exception {
				logger.info("BeforeMonitor: " + each.getClass().getName());
				each.beforeMonitor(py);
			};
		}.run();
	}

	/**
	 * 在每一个策略结束之后
	 */
	public void afteMonitor(final PolicyResult mr) {
		new SafeNotifier() {
			@Override
			protected void notifyListener(Listener each) throws Exception {
				logger.info("AfteMonitor: " + each.getClass().getName());
				each.afteMonitor(mr);
			};
		}.run();
	}

	/**
	 * 在所有策略结束之后
	 */
	public void afterAllMonitor(final List<PolicyResult> lm) {
		new SafeNotifier() {
			@Override
			protected void notifyListener(Listener each) throws Exception {
				logger.info("AfterAllMonitor: " + each.getClass().getName());
				each.afterAllMonitor(lm);
			};
		}.run();
	}
}
