/**
 * 
 */
package com.baidu.selenium.errorspec.tools;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.selenium.errorspec.config.Config;
import com.baidu.selenium.errorspec.config.Constants;
import com.baidu.selenium.errorspec.tools.html.HtmlBuilder;

/**
 * 发邮件辅助类，传入一个html文件
 * 
 * @author sakyo
 * 
 */
public class MailHelper {
	public static final Logger logger = LoggerFactory.getLogger(MailHelper.class);
	// 邮箱服务器
	private String host = Constants.MAIL_HOST;

	/**
	 * 发送一个html
	 * 
	 * @param hb
	 */
	public void send(HtmlBuilder hb, String subject) {
		if (subject == null || subject.trim().equals(""))
			subject = Constants.MAIL_SUBJECT;
		//logger.info(hb.toString());
		send(Constants.MAIL_FROM, Constants.MAIL_FROM, Config.MailTo, subject, hb.toString(), hb.getAttachment());
		logger.info("Send Email "+subject+" to "+Config.MailTo);
	}

	/**
	 * 发送一系列html
	 */
	public void send(List<HtmlBuilder> hbs, String subject) {
		if (hbs == null || hbs.size() == 0) {
			logger.info("No Error, wouldn't send Email.");
			return;
		}
		send(HtmlBuilder.buildHtmls(hbs, Constants.HTML_VM), subject);
	}

	public void send(String from, String personalName, String to, String subject, String body, Map<String, String> attchement) {
		try {
			Properties props = new Properties();
			Authenticator auth = new Email_Autherticator(); // 进行邮件服务器用户认证
			props.put("mail.smtp.host", host);
			props.put("mail.smtp.auth", "true");
			Session session = Session.getDefaultInstance(props, auth);

			// 设置session,和邮件服务器进行通讯。
			MimeMessage message = new MimeMessage(session);

			message.setSubject(MimeUtility.encodeText(subject, "utf-8", "b"));
			message.setHeader(subject, subject); // 设置邮件标题
			message.setSentDate(new Date()); // 设置邮件发送日期
			Address address = new InternetAddress(from, personalName);
			message.setFrom(address); // 设置邮件发送者的地址
			Address toAddress;
			for (String mail : to.split(",")) {
				toAddress = new InternetAddress(mail.trim()); // 设置邮件接收方的地址
				message.addRecipient(Message.RecipientType.TO, toAddress);
			}
			Multipart multipart = new MimeMultipart();
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			multipart.addBodyPart(messageBodyPart);
			messageBodyPart.setContent(body, "text/html; charset=utf-8");
			if (attchement != null) {
				for (Entry<String, String> name : attchement.entrySet()) {
					try {
						MimeBodyPart filePart = new MimeBodyPart();
						FileDataSource fileds = new FileDataSource(name.getKey());
						filePart.setDataHandler(new DataHandler(fileds));
						filePart.setFileName(name.getValue());
						// filePart.attachFile(name);
						multipart.addBodyPart(filePart);
					} catch (Exception e) {
						logger.warn("attchement not Found:" + name, e);
						continue;
					}
				}
			}
			message.setContent(multipart, "text/html;charset=utf-8");
			Transport.send(message); // 发送邮件
		} catch (Exception e) {
			logger.error("Error to send Mail.", e);
		}
	}

	/**
	 * 用来进行服务器对用户的认证
	 */
	public static class Email_Autherticator extends Authenticator {
		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(Constants.MAIL_USERNAME, Constants.MAIL_PASSWORD);
		}
	}
}
