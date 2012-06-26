/**
 * 
 */
package com.baidu.selenium.errorspec;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Listener处理PolicyResult方式，用在MonitorResult的Object属性上，listener指定被哪个Listerner处理，
 * value代表处理的附加属性
 * listener可以放在类上和属性上，作为分发到那个listener处理，默认是分发到全部listener。如果类和属性上都有listerner限定
 * ，只有类和属性都包含时才分配到此listener处理
 * 类上默认不用修饰，代表全部，属性必须使用此注解，并且指定value，如果不指定vaule，默认取此属性的名称
 * 
 * @author sakyo
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE })
public @interface Attribute {

	/**
	 * 设置被那个listener处理，默认是空，代表全部
	 * 
	 * @return
	 */
	Class<? extends Listener>[] listener() default {};

	/**
	 * 被listener处理时的属性字段名称
	 * 
	 * @return
	 */
	String value() default "";
}
