package com.baidu.selenium.errorspec;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 策略的处理结果
 * 
 * @author sakyo
 * 
 */
public class PolicyResult {

	private Policy policy;
	private List<MonitorResult> results = Collections
			.synchronizedList(new ArrayList<MonitorResult>());

	public PolicyResult(Policy policy) {
		super();
		this.policy = policy;
	}

	public void addResult(MonitorResult result) {
		if (result == null)
			return;
		results.add(result);
	}

	public void removeResult(MonitorResult result) {
		results.remove(result);
	}

	public Policy getPolicy() {
		return policy;
	}

	public List<MonitorResult> getResults() {
		return results;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(policy.getName() + "\r\n");
		for (MonitorResult result : results) {
			sb.append("\t" + result.toString() + "\r\n");
		}
		return sb.toString();
	}

	/**
	 * 返回策略结果的处理级别
	 * 
	 * @return 取的策略所有处理结果的最大log级别
	 */
	public Level getLevel() {
		Level level = Level.INFO;
		for (MonitorResult result : results) {
			if (result.getLevel().ordinal() > level.ordinal())
				level = result.getLevel();
		}
		return level;
	}

	/**
	 * 处理结果的级别
	 * 
	 * @author sakyo
	 * 
	 */
	public static enum Level {
		Log, INFO, WARNING, ERROR, BUG;
	}

	/**
	 * 每个监控的处理结果
	 * 
	 * @author sakyo
	 */
	public static class MonitorResult {

		private Level level;
		private Object obj;

		/**
		 * 每一条监控结果
		 * 
		 * @param level
		 *            结果级别
		 * @param obj
		 *            监控结果自定义对象
		 */
		public MonitorResult(Level level, Object obj) {
			super();
			this.level = level;
			this.obj = obj;
		}

		public Level getLevel() {
			return level;
		}

		public Object getObj() {
			return obj;
		}

		public void setLevel(Level level) {
			this.level = level;
		}

		public void setObj(Object obj) {
			this.obj = obj;
		}

		@Override
		public String toString() {
			return level.toString() + ": " + obj.toString();
		}
		
		/**
		 * 辅助方法，提供对注解方式的Obj的dispatch处理，获取属于某Listener的属性和值集合,
		 * @see com.baidu.selenium.errorspec.Attribute
		 * 
		 * @param listener 如果传入NULL，表示获取全部
		 * @return
		 */
		public Map<String, Object> getAttribute(Class<?> listener) {
			Map<String, Object> result = new HashMap<String, Object>();
			if (obj == null)
				return result;
			Class<?> cla = obj.getClass();
			Attribute att = cla.getAnnotation(Attribute.class);
			if (att != null && !checkType(listener,att.listener()))
				return result;
			while (cla != Object.class) {
				Field[] fields = cla.getDeclaredFields();
				for (Field field : fields) {
					att = field.getAnnotation(Attribute.class);
					if (att != null && checkType(listener,att.listener())) {
						try {
							field.setAccessible(true);
							String key = att.value();
							if(key.equals("")) key = field.getName();
							result.put(key, field.get(obj));
						} catch (Exception e) {
							// TODO
						}
					}
				}
				cla = cla.getSuperclass();
			}
			return result;
		}

		private boolean checkType(Class<?> listener, Class<?>[] classes) {
			if(listener == null)
				return true;
			if (classes.length == 0)
				return true;
			for (int i = 0; i < classes.length; i++) {
				if (classes[i] == listener)
					return true;
			}
			return false;
		}
	}
}
