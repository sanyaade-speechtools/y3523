/**
 * 
 */
package com.baidu.selenium.errorspec.tools;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.text.translate.UnicodeUnescaper;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.selenium.errorspec.tools.hi.FanTuanListener;

/**
 * Hi的包装类，封装了对于HI的调用，请不要在一秒内频繁发送消息，长度限制900
 * 
 * @author sakyo
 * 
 */
public class Hi {

	public static final Logger logger = LoggerFactory.getLogger(Hi.class);
	protected HiHttpHelper httpHelper;
	protected Thread reciveThread;
	public List<MessageListener> listeners = new ArrayList<Hi.MessageListener>();
	/**
	 * 定义一些常量
	 */
	protected static final String MSG_URL = "http://web.im.baidu.com/message";
	protected static final String GROUP_URL = "http://web.im.baidu.com/groupmessage";
	protected static final String PICK_URL = "http://web.im.baidu.com/pick";
	protected static final String LOGIN_TOKEN = "https://passport.baidu.com/v2/api/?getapi&class=login&tpl=mn&tangram=false";
	protected static final String BAIDU_LOGIN = "https://passport.baidu.com/v2/api/?login";
	protected static final String PIC_URL = "http://file.im.baidu.com/put/file/content/old_image/00000000000000000000000000000000?from=page";
	protected static final String CHECK_URL = "http://web.im.baidu.com/check?callback=_nbc_.f1&v=30";// &auth=4c1cdf66a17428323dda75e5c5e0a0feb26f11faccef9cf2a346bcc6e377746fbed16db9f932";
	protected static final String WEL_URL = "http://web.im.baidu.com/welcome";
	protected static final String INT_URL = "http://web.im.baidu.com/init";

	private String user = "y3523";
	private Long seq = 354L;

	public Hi() {
		this.httpHelper = new HiHttpHelper();
	}

	/**
	 * 设置代理服务器 有些Hudson没有外网权限，设置下代理
	 * 
	 * @param host
	 *            代理服务器地址
	 * @param port
	 *            代理服务器端口
	 */
	public void setProxy(String host, int port) {
		httpHelper.setProxy(host, port);
	}

	/**
	 * 设置hi用户Cookie，如果不设置使用默认的HI账号
	 * 
	 * @param cookie
	 *            需要使用的账号的Cookie
	 */
	public void setCookie(CookieStore cookieStore) {
		httpHelper.setCookie(cookieStore);
	}

	/**
	 * 接收消息，目前只是显示出来，可以加入事件机制
	 */
	public void startRecive() {
		if (reciveThread != null)
			return;
		reciveThread = new Thread(new Runnable() {
			private String ack = "";

			@Override
			public void run() {
				Map<String, Object> parms = new HashMap<String, Object>();
				parms.put("v", "30");
				parms.put("session", "");
				parms.put("source", "22");
				parms.put("guid", "h3bgqyya");
				parms.put("type", "23");
				parms.put("flag", "1");
				parms.put("ack", "");
				String result = httpHelper.get(parms, PICK_URL);
				getAct(result);
				if (ack == null || ack.equals("")) {
					logger.error("Get Ack From pick fail.");
					return;
				}
				while (true) {
					parms.put("seq", seq + "");
					parms.put("ack", ack);
					result = httpHelper.get(parms, PICK_URL);
					getAct(result);
					seq++;
					logger.info(getMessage(result)+"");
					if (listeners != null && getMessage(result).msg != null) {
						synchronized (listeners) {
							for (Iterator<MessageListener> all = listeners.iterator(); all.hasNext();)
								try {
									all.next().getTextMessage(Hi.this, getMessage(result));
								} catch (Exception e) {
									logger.error("Hi listener Error.", e);
								}
						}
					}
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}

			private void getAct(String result) {
				try {
					ack = result.split("\"ack\": \"")[1].split("\"")[0];
				} catch (Exception e) {
					return;
				}
			}

			// 简单分析下文本
			private ReceiveMsg getMessage(String result) {
				ReceiveMsg rmsg = new ReceiveMsg();
				try {
					rmsg.from = result.split("\"from\": \"")[1].split("\"")[0];
					rmsg.msg = new UnicodeUnescaper().translate(result.split("\"type\": \"text\", \"c\": \"")[1].split("\"")[0]);
				} catch (Exception e) {
					//todo
				}
				try {
					rmsg.groupid = result.split("\"gid\": \"")[1].split("\"")[0];
				} catch (Exception e) {
					// TODO: handle exception
				}
				return rmsg;
			}
		});
		reciveThread.start();
	}

	/**
	 * 发送消息之前必须登陆，1.使用cookie绕过百度的单点登陆,这个 不太人性化2.想象下鸿媒体就知道单点登录过程了
	 * 现在passport里面取token，前提必须在cookie里面有个baiduID，
	 * 用用户名、密码、token到passport登陆，
	 * 登陆成功后，使用welcome的请求激活webhi就可以了
	 * 
	 * @return 是否登陆成功
	 */
	public boolean login(String username, String password) {
		this.user = username;
		String result = "";
		String token = "";
		result = httpHelper.get(null, LOGIN_TOKEN);
		try {
			token = result.split("bdPass.api.params.login_token='")[1].split("'")[0];
		} catch (Exception e) {
			return false;
		}

		Map<String, Object> parms = new HashMap<String, Object>();
		parms.put("callback", "parent.bdPass.api.login._postCallback");
		parms.put("charset", "UTF-8");
		parms.put("codestring", "");
		parms.put("index", "0");
		parms.put("isPhone", "false");
		parms.put("mem_pass", "on");
		parms.put("password", password);
		parms.put("safeflg", "0");
		parms.put("staticpage", "http://web.im.baidu.com/popup/src/v2Jump.html");
		parms.put("token", token);
		parms.put("tpl", "mn");
		parms.put("u", "");
		parms.put("username", username);
		parms.put("verifycode", "");
		result = httpHelper.post(parms, BAIDU_LOGIN);
		// 单点登陆
		// httpHelper.get(null, LOGIN_URL);
		// 设置cookie的请求
		httpHelper.get(null, CHECK_URL);
		parms = new HashMap<String, Object>();
		parms.put("v", "30");
		parms.put("seq", seq + "");
		parms.put("session", "");
		parms.put("source", "22");
		parms.put("guid", "h38ff" + seq);// qcb
		parms.put("force", true);
		parms.put("from", "0");
		result = httpHelper.post(parms, WEL_URL);
		seq += 1;

		// 这个是设置状态的请求，被证实不需要
		parms.remove("force");
		parms.remove("from");
		parms.put("seq", seq + "");
		parms.put("status", "online");
		// 设置状态的请求
		result = httpHelper.post(parms, INT_URL);
		return isOK(result);
	}

	/**
	 * 给好友发送消息
	 * 
	 * @param hiName
	 *            好友hi号
	 * @param message
	 *            消息体
	 * @return 是否发送成功
	 */
	public boolean sendMessage(String hiName, Msg... message) {
		boolean result =  sendMessage(hiName, false, message);
		if(result){
			String content = "send message ["+ hiName+":";
			for (Msg msg : message) {
				content += msg.toString()+";";
			}
			content += "]";
			logger.info(content);
			return true;
		}
		return false;
	}

	/**
	 * 给好友发送图片，不验证是否是图片，不验证是否是好友
	 * 
	 * @param hiName
	 *            好友hi号
	 * @param pic
	 *            图片路径
	 * @return 是否发送成功
	 */
	protected boolean sendPic(String hiName, String pic) {
		File picFile = new File(pic);
		if (!picFile.exists()) {
			logger.error(pic + " doesn't exist.");
			return false;
		}
		// 使用单独的请求发送图片
		String result = httpHelper.post(picFile, PIC_URL);
		if (!isOK(result))
			return false;
		String md5 = "";
		try {
			md5 = result.split("\"md5\":\"")[1].split("\"")[0];
		} catch (Exception e) {
			logger.error("split file result fail.", e);
			return false;
		}
		Map<String, Object> parms = new HashMap<String, Object>();
		parms.put("v", "30");
		parms.put("seq", seq + "");
		parms.put("session", "");
		parms.put("source", "22");
		parms.put("guid", "h38ff" + seq);
		parms.put("from", user);
		parms.put("body", buildMessage(buildPic(md5)));
		parms.put("to", hiName);
		parms.put("friend", "true");
		result = httpHelper.post(parms, MSG_URL);
		seq += 1;
		return isOK(result);
	}

	/**
	 * 给所有人发送消息
	 * 
	 * @param hiName
	 *            hi号
	 * @param message
	 *            消息
	 * @param isFriend
	 *            是否是好友
	 * @return
	 */
	protected boolean sendMessage(String hiName, boolean isFriend, Msg... msg) {
		if (msg == null || msg.length == 0)
			return false;
		String message = "";
		for (Msg tmpmsg : msg) {
			if (tmpmsg.getType() == "pic") {
				if (msg.length == 1) {
					return sendPic(hiName, tmpmsg.toString());
				} else {
					logger.error("send one pic alone");
					return false;
				}
			}
			message += tmpmsg.toString();
		}
		if (message == null || message.length() > 900)
			return false;
		Map<String, Object> parms = new HashMap<String, Object>();
		parms.put("v", "30");
		parms.put("seq", seq + "");
		parms.put("session", "");
		parms.put("source", "22");
		parms.put("guid", "h38ff" + seq);
		parms.put("from", user);
		parms.put("body", buildMessage(message));
		parms.put("to", hiName);
		parms.put("friend", isFriend + "");
		String result = httpHelper.post(parms, MSG_URL);
		seq += 1;
		return isOK(result);
	}

	/**
	 * 给群发消息
	 * 
	 * @param groupId
	 *            群id
	 * @param message
	 *            消息
	 * @return 是否成功
	 */
	public boolean sendGroupMessage(String groupId, Msg... msg) {
		if (msg == null || msg.length == 0)
			return false;
		String message = "";
		for (Msg tmpmsg : msg) {
			if (tmpmsg.getType() == "pic") {
				if (msg.length == 1) {
					return sendGroupPic(groupId, tmpmsg.toString());
				} else {
					logger.error("send one pic alone");
					return false;
				}
			}
			message += tmpmsg.toString();
		}
		if (message == null || message.length() > 900)
			return false;
		Map<String, Object> parms = new HashMap<String, Object>();
		parms.put("v", "30");
		parms.put("seq", seq + "");
		parms.put("session", "");
		parms.put("source", "22");
		parms.put("guid", "h38ff" + seq);
		parms.put("from", user);
		parms.put("body", buildMessage(message));
		parms.put("gid", groupId);
		parms.put("messageid", "");
		String result = httpHelper.post(parms, GROUP_URL);
		seq += 1;
		boolean isok =  isOK(result);
		if(isok){
			String content = "send group message ["+ groupId+":";
			for (Msg mg : msg) {
				content += mg.toString()+";";
			}
			content += "]";
			logger.info(content);
			return true;
		}
		return false;
	}

	/**
	 * 发送群组图片
	 * 
	 * @param groupId
	 *            群id
	 * @param pic
	 *            图片
	 * @return
	 */
	protected boolean sendGroupPic(String groupId, String pic) {
		File picFile = new File(pic);
		if (!picFile.exists()) {
			logger.error(pic + " doesn't exist.");
			return false;
		}
		// 使用单独的请求发送图片
		String result = httpHelper.post(picFile, PIC_URL);
		if (!isOK(result))
			return false;
		String md5 = "";
		try {
			md5 = result.split("\"md5\":\"")[1].split("\"")[0];
		} catch (Exception e) {
			logger.error("split file result fail.", e);
			return false;
		}
		Map<String, Object> parms = new HashMap<String, Object>();
		parms.put("v", "30");
		parms.put("seq", seq + "");
		parms.put("session", "");
		parms.put("source", "22");
		parms.put("guid", "h38ff" + seq);
		parms.put("from", user);
		parms.put("body", buildMessage(buildPic(md5)));
		parms.put("gid", groupId);
		parms.put("messageid", "");
		result = httpHelper.post(parms, GROUP_URL);
		seq += 1;
		return isOK(result);
	}

	/**
	 * 判断返回结果是否OK
	 * 
	 * @param result
	 */
	protected boolean isOK(String result) {
		return result != null && (result.contains("\"result\": \"ok\"") || result.contains("\"result\":\"ok\""));
	}

	/**
	 * 构造带字体的消息体
	 * 
	 * @param message
	 *            消息
	 * @return 带字体的消息体
	 */
	protected String buildMessage(String message) {
		String result = "<msg><font n=\"微软雅黑\" s=\"18\" b=\"0\" i=\"0\" ul=\"0\" c=\"255\"/>";
		// String encodeMsg = StringEscapeUtils.escapeHtml4(message);
		return result + message + "</msg>";
	}

	protected String buildPic(String md5) {
		String result = "<img n=\"9BA5EC29F8\" md5=\"";
		return result + md5 + "\" t=\"png\"/>";
	}

	/**
	 * 不同hi类型消息的封装
	 * 
	 * @author sakyo
	 * 
	 */
	public abstract static class Msg implements Serializable {

		private static final long serialVersionUID = 7528435281036292913L;

		public abstract String toString();

		protected abstract String getType();

		// 文本消息
		public static Msg msg(final String message) {
			return new Msg() {
				private static final long serialVersionUID = 5045046831385531326L;

				@Override
				public String toString() {
					String result = "<text c=\"";
					String encodeMsg = StringEscapeUtils.escapeHtml4(message);
					return result + encodeMsg + "\"/>";
				}

				@Override
				protected String getType() {
					return "msg";
				}
			};
		}

		// 链接消息
		public static Msg link(final String link) {
			return new Msg() {

				private static final long serialVersionUID = 317895869444575909L;

				@Override
				public String toString() {
					String result = "<text c=\"\"/><url ref=\"";
					String encodeMsg = StringEscapeUtils.escapeHtml4(link);
					return result + encodeMsg + "\"/><text c=\"\"/>";
				}

				@Override
				protected String getType() {
					return "link";
				}
			};
		}

		// 图片，图片只能单独发，不要和其他消息一起发送
		public static Msg pic(final String file) {
			return new Msg() {

				private static final long serialVersionUID = -8111203185230945267L;

				@Override
				public String toString() {
					return file;
				}

				@Override
				protected String getType() {
					return "pic";
				}
			};
		}
	}

	public static class ReceiveMsg{
		public String groupid;
		public String from;
		public String msg;
		@Override
		public String toString() {
			return "ReceiveMsg [groupid=" + groupid + ", from=" + from + ", msg=" + msg + "]";
		}
		@Override
		public ReceiveMsg clone(){
			ReceiveMsg msg = new ReceiveMsg();
			msg.groupid = this.groupid;
			msg.from = this.from;
			msg.msg = this.msg;
			return msg;
		}
	}
	/**
	 * 处理hi返回消息的事件
	 * 
	 * @author sakyo
	 */
	public static interface MessageListener {
		public void getTextMessage(Hi hi, ReceiveMsg message);
	}

	/**
	 * 简单供HI使用的http类 封装了client伪装，post方法，cookie伪装登陆等等
	 * 
	 * @author sakyo
	 */
	@SuppressWarnings("deprecation")
	protected class HiHttpHelper {
		private DefaultHttpClient httpClient = new DefaultHttpClient();
		CookieStore cookieStore = new BasicCookieStore();

		public HiHttpHelper() {
			httpSupprt();
			setDefaultCookie();
			setCookie(this.cookieStore);
		}

		/**
		 * java的httpclient实在太难用了，手工支持gizp，https无证书认证 我就想不明白了，为啥非得让我定制，你默认加上不得了
		 */
		private void httpSupprt() {
			try {
				KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
				trustStore.load(null, null);

				SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
				sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

				HttpParams params = new BasicHttpParams();
				HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
				HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

				SchemeRegistry registry = new SchemeRegistry();
				registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
				registry.register(new Scheme("https", sf, 443));

				ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
				httpClient = new DefaultHttpClient(ccm, params);
			} catch (Exception e) {
				httpClient = new DefaultHttpClient();
			}
			httpClient.addResponseInterceptor(new HttpResponseInterceptor() {
				public void process(final HttpResponse response, final HttpContext context) throws HttpException, IOException {
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						Header ceheader = entity.getContentEncoding();
						if (ceheader != null) {
							HeaderElement[] codecs = ceheader.getElements();
							for (int i = 0; i < codecs.length; i++) {
								if (codecs[i].getName().equalsIgnoreCase("gzip")) {
									response.setEntity(new GzipDecompressingEntity(response.getEntity()));
									return;
								}
							}
						}
					}
				}
			});
		}

		public void setProxy(String host, int port) {
			HttpHost proxy = new HttpHost(host, port);
			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}

		public void setCookie(CookieStore cookieStore) {
			this.cookieStore = cookieStore;
			httpClient.setCookieStore(cookieStore);
		}

		/**
		 * 针对hi发送post请求，强制转换返回的为String
		 * 
		 * @param parms
		 *            参数
		 * @param url
		 *            url
		 * @return 返回的String，若不为String，则返回null
		 */
		public String post(Map<String, Object> parms, String url) {
			try {
				HttpPost httpPost = new HttpPost(url);
				String argc = "";
				if (parms != null) {
					for (Entry<String, Object> parm : parms.entrySet()) {
						argc += parm.getKey() + "=" + URLEncoder.encode(parm.getValue().toString(), "UTF-8") + "&";
					}
					argc = argc.substring(0, argc.length() - 1);
				}
				httpPost.setEntity(new StringEntity(argc));
				HttpResponse response = request(httpPost);
				if (response.getStatusLine().getStatusCode() != 200) {
					logger.info("StatusCode: " + response.getStatusLine().getStatusCode());
					return null;
				}
				String result = EntityUtils.toString(response.getEntity());
				logger.info("result: " + result);
				return result;
			} catch (Exception e) {
				logger.error("HTTP POST ERROR", e);
				return null;
			}
		}

		public String post(File file, String url) {
			try {
				HttpPost httpPost = new HttpPost(url);
				FileEntity entity = new FileEntity(file, ContentType.create("text/plain"));
				httpPost.setEntity(entity);
				HttpResponse response = request(httpPost);
				if (response.getStatusLine().getStatusCode() != 200) {
					logger.info("StatusCode: " + response.getStatusLine().getStatusCode());
					return null;
				}
				String result = EntityUtils.toString(response.getEntity());
				logger.info("result: " + result);
				return result;
			} catch (Exception e) {
				logger.error("HTTP POST File ERROR", e);
				return null;
			}
		}

		/**
		 * 针对hi发送get请求，强制转换返回的为String
		 * 
		 * @param parms
		 *            参数
		 * @param url
		 *            url
		 * @return 返回的String，若不为String，则返回null
		 */
		public String get(Map<String, Object> parms, String url) {
			try {
				if (parms != null) {
					url += "?";
					for (Entry<String, Object> parm : parms.entrySet()) {
						if (parm.getKey().toString().equalsIgnoreCase("ack")) {
							url += parm.getKey() + "=" + parm.getValue().toString() + "&";
						} else {
							url += parm.getKey() + "=" + URLEncoder.encode(parm.getValue().toString(), "UTF-8") + "&";
						}

					}
					url = url.substring(0, url.length() - 1);
				}
				HttpGet httpGet = new HttpGet(url);
				HttpResponse response = request(httpGet);
				if (response.getStatusLine().getStatusCode() != 200) {
					return null;
				}
				String result = EntityUtils.toString(response.getEntity());
				// EntityUtils.toString(response.getEntity(),"");
				logger.info("result: " + result);
				return result;
			} catch (Exception e) {
				logger.error("HTTP GET ERROR", e);
				return null;
			}
		}

		/**
		 * 伪装的请求包装 Chrome19，win7,32，GBK，Web登陆hi
		 * 
		 * @param parms
		 *            参数
		 * @param httpmethod
		 *            请求方式
		 * @return
		 * @throws Exception
		 */
		protected HttpResponse request(HttpUriRequest httpmethod) throws Exception {
			// httpmethod.setHeader("Cookie", cookie);
			httpmethod.setHeader("Accept", "*/*");
			httpmethod.setHeader("Accept-Charset", "GBK,utf-8;q=0.7,*;q=0.3");
			httpmethod.setHeader("Accept-Encoding", "gzip,deflate,sdch");
			httpmethod.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
			httpmethod.setHeader("Content-Type", "application/x-www-form-urlencoded");
			if (httpmethod instanceof HttpPost) {
				HttpEntity entry = ((HttpPost) httpmethod).getEntity();
				if (entry instanceof FileEntity) {
					httpmethod.setHeader("Content-Type", "multipart/form-data");
				}
			}

			httpmethod.setHeader("Host", httpmethod.getURI().getHost());
			httpmethod.setHeader("Proxy-Connection", "keep-alive");
			httpmethod.setHeader("Referer", "http://web.im.baidu.com/");
			httpmethod.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/536.5 (KHTML, like Gecko) Chrome/19.0.1084.52 Safari/536.5");
			// httpmethod.setHeader("X-Requested-With", "XMLHttpRequest");
			// httpmethod.setHeader("Origin", "http://web.im.baidu.com");
			logger.info("URI: " + httpmethod.getURI());
			return httpClient.execute(httpmethod);
		}

		/**
		 * 所有百度的使用必须要有个百度id，你懂的，否者出错
		 * 其他几个域的不需要使用
		 * 所以可以固定使用
		 */
		protected void setDefaultCookie() {
			cookieStore.addCookie(createCookie("BAIDUID", "568FBA5E944F7C8499F5D999D3307A7F:FG=1", ".baidu.com"));
			// cookieStore
			// .addCookie(createCookie(
			// "BDUSS",
			// "dvSndQdH5sMDFwcG5xSkZLd0l-NXJEUXpYcndwckxjZTVIOUlGOVpEcFBkY0JRQVFBQUFBJCQAAAAAAAAAAAoqxg3LJbIAeTM1MjMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACAYIArMAAAAOD6z5YqAAAALWdCAAAAAAAxMC4yNi4yMk8n009PJ9NPdU",
			// ".baidu.com"));
			// cookieStore.addCookie(createCookie("BDUT",
			// "l7vh568FBA5E944F7C8499F5D999D3307A7F137ccdcd8880",
			// ".baidu.com"));
			// cookieStore.addCookie(createCookie("IM_", "1", ".baidu.com"));
			// cookieStore.addCookie(createCookie("PTOKEN",
			// "998aa5ec1e69dac40097cc382a3dd5fe", ".passport.baidu.com"));
			// cookieStore.addCookie(createCookie("SAVEUSERID",
			// "fbb709ec3f2881d4a5", ".passport.baidu.com"));
			// cookieStore.addCookie(createCookie("STOKEN",
			// "71393ecfa21f22a8aeecafaa2d366ab7", ".passport.baidu.com"));
			// cookieStore.addCookie(createCookie("stoken",
			// "4c1cdf66a17428323dda75e5c5e0a0feb26f11faccef9cf2a346bcc6e377746fbed16db9f932",
			// ".im.baidu.com"));
		}

		private BasicClientCookie createCookie(String key, String value, String domain) {
			BasicClientCookie cookie = new BasicClientCookie(key, value);
			cookie.setVersion(0);
			cookie.setDomain(domain);
			cookie.setPath("/");
			return cookie;
		}
	}

	protected class MySSLSocketFactory extends SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};

			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}

}
