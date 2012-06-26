/**
 * 
 */
package com.baidu.selenium.errorspec.tools.html;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.selenium.errorspec.config.Constants;

/**
 * @author sakyo
 * 
 */
public abstract class HtmlBuilder {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private Map<String, String> attachment = new HashMap<String, String>();
	protected String title = Constants.MAIL_SUBJECT;
	
	protected String vm;

	public HtmlBuilder() {
		this(Constants.HTML_VM);
	}

	public HtmlBuilder(String vm) {
		this.vm = vm;
	}

	/**
	 * 页面的主体部分
	 * @return
	 */
	public abstract String build();

	public Map<String, String> getAttachment() {
		return attachment;
	}

	public String addAttachment(String file) {
		if (attachment.containsKey(file))
			return attachment.get(file);
		else {
			File f = new File(file);
			if (!f.exists()) {
				logger.warn("File: " + file + "doesn't exist.");
				return file;
			}
			String name = f.getName();
			while (attachment.containsValue(name)) {
				name = "d1" + name;
			}
			attachment.put(file, name);
			return name;
		}
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	/**
	 * 页面加上样式源码
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		try {
			String line;
			BufferedReader reader = null;
			reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(vm), "UTF-8"));
			while ((line = reader.readLine()) != null) {
				if (line.contains("$body"))
					line = line.replace("$body", build());
				sb.append(line);
			}
		} catch (Exception e) {
			return build();
		}
		return sb.toString();
	}

	/**
	 * 生成pic节点
	 * 
	 * @param pic
	 * @return
	 */
	public String pic(String pic) {
		File f = new File(pic);
		if (!f.exists()) {
			logger.warn("Pic: " + pic + "doesn't exist.");
			return pic;
		}
		return "<img src=\"" + addAttachment(pic) + "\">";
	}

	public static class TD {
		protected String td = "";

		public TD(String td) {
			this.td = td;
		}

		public String toString() {
			return "<td class=\"alt\">" + td + "</td>";
		}
	}

	public static class TH extends TD {
		public TH(String td) {
			super(td);
		}

		public String toString() {
			return "<th nowrap scope=\"co\">" + td + "</th>";
		}
	}

	public static class TR {
		protected List<TD> tds;

		public TR(List<TD> tds) {
			this.tds = tds;
		}

		public String toString() {
			StringBuffer sb = new StringBuffer("<tr>");
			for (TD td : tds) {
				sb.append(td.toString());
			}
			return sb.append("</tr>").toString();
		}
	}

	public static class Table {
		protected List<TR> trs;

		public Table(List<TR> trs) {
			this.trs = trs;
		}

		public String toString() {
			StringBuffer sb = new StringBuffer(
					"<table id=\"mytable\" cellspacing=\"0\" summary=\"The technical specifications of the Apple PowerMac G5 series\">");
			for (TR td : trs) {
				sb.append(td.toString());
			}
			return sb.append("</table>").toString();
		}
	}

	public static class H {
		protected int level = 2;
		protected String title = "";

		public H(int i, String title) {
			this.level = i;
			this.title = title;
		}

		public String toString() {
			return new StringBuffer("<H" + level + ">").append(title).append("</H" + level + ">").toString();
		}
	}

	/**
	 * 合并多个html为一个
	 * 
	 * @param hbs
	 *            多个html
	 * @param vm
	 *            使用的模板
	 */
	public static HtmlBuilder buildHtmls(final List<HtmlBuilder> hbs, String vm) {
		return new HtmlBuilder(vm) {
			@Override
			public String build() {
				StringBuffer sb = new StringBuffer();
				for (HtmlBuilder hb : hbs) {
					sb.append(hb.build()).append("<br>");
				}
				return sb.toString();
			}

			@Override
			public HashMap<String, String> getAttachment() {
				HashMap<String, String> s = new HashMap<String, String>();
				for (HtmlBuilder hb : hbs) {
					s.putAll(hb.getAttachment());
				}
				return s;
			}
		};
	}
}
