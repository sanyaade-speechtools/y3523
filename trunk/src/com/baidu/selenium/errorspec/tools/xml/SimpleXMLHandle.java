/**
 * 
 */
package com.baidu.selenium.errorspec.tools.xml;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlInlineBinaryData;
import javax.xml.bind.annotation.XmlValue;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 简单的XML解析为对象的工具,只解析4种节点,并且只支持属性为String,或者list<>的节点,@see ImageCompare
 * 
 * just 4 me
 * 
 * @author sakyo
 * 
 */
public class SimpleXMLHandle {
	private Element root;
	private Object obj;
	private static String DEFAULT_NAME = "##default";

	public SimpleXMLHandle(Element document) {
		this.root = document;
	}

	/**
	 * 把当前节点转换为对象
	 * 
	 * @param cla
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends XMLElement> T parse(Class<T> cla) {
		try {
			obj = cla.newInstance();
		} catch (Exception e) {
			return null;
		}
		parse(root, obj);
		return (T) this.obj;
	}

	private void parse(Node ele, Object obj) {
		Class<?> cla = obj.getClass();
		while (cla != Object.class) {
			Field[] fields = cla.getDeclaredFields();
			for (Field field : fields) {
				try {
					porxyField(field, (Element) ele, obj);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			cla = cla.getSuperclass();
		}
	}

	private void porxyField(Field field, Element ele, Object obj) throws Exception {
		String name = "";
		XmlAttribute xa = field.getAnnotation(XmlAttribute.class);
		if (xa != null && field.getType() == String.class) {
			if (xa.name().equals(DEFAULT_NAME))
				name = field.getName();
			else
				name = xa.name();
			field.setAccessible(true);
			field.set(obj, ele.getAttribute(name));
		}
		XmlValue xv = field.getAnnotation(XmlValue.class);
		if (xv != null && field.getType() == String.class) {
			field.setAccessible(true);
			field.set(obj, ele.getNodeValue());
		}
		XmlInlineBinaryData xb = field.getAnnotation(XmlInlineBinaryData.class);
		if (xb != null && field.getType() == String.class) {
			field.setAccessible(true);
			field.set(obj, ele.getTextContent());
		}
		XmlElement xe = field.getAnnotation(XmlElement.class);
		if (xe != null) {
			if (xe.name().equals(DEFAULT_NAME))
				name = field.getName();
			else
				name = xe.name();
			if (field.getType() == List.class) {
				field.setAccessible(true);
				NodeList nl = ele.getElementsByTagName(name);
				if (nl == null)
					return;
				int size = nl.getLength();
				// 运行时解除泛型约束,所以生成Object的类型
				List<Object> ay = new ArrayList<Object>();
				for (int i = 0; i < size; i++) {
					Object sonField = new Object();
					Type type = field.getGenericType();
					if (type instanceof ParameterizedType) {
						sonField = ((Class<?>)((ParameterizedType)type).getActualTypeArguments()[0]).newInstance();
					}
					parse(nl.item(i), sonField);
					ay.add(sonField);
				}
				field.set(obj, ay);
			} else {
				field.setAccessible(true);
				NodeList nl = ele.getElementsByTagName(name);
				if (nl == null || nl.getLength() < 1)
					return;
				Object sonField = field.getType().newInstance();
				parse(nl.item(0), sonField);
				field.set(obj, sonField);
			}
		}
	}
}
