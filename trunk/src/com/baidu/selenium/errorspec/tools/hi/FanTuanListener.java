package com.baidu.selenium.errorspec.tools.hi;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bitoflife.chatterbean.AliceBot;
import bitoflife.chatterbean.AliceBotMother;

import com.baidu.selenium.errorspec.tools.Hi;
import com.baidu.selenium.errorspec.tools.Hi.MessageListener;
import com.baidu.selenium.errorspec.tools.Hi.Msg;
import com.baidu.selenium.errorspec.tools.Hi.ReceiveMsg;

public class FanTuanListener implements MessageListener {
	public static final Logger logger = LoggerFactory.getLogger(FanTuanListener.class);
	private static final long PERIOD_DAY = 24 * 60 * 60 * 1000;
	private String groupid = "1360213";
	private Hi hi;
	private AliceBot bot;
	private Queue<ReceiveMsg> sendList = new LinkedList<ReceiveMsg>();
	private static long lastsendtime = new Date().getTime();

	public FanTuanListener(Hi hi) {
		this.hi = hi;
		timerManager();
		startSend();
		try {
			AliceBotMother mother = new AliceBotMother();
			bot = mother.newInstance();
			bot.respond("welcome");
			mother.setUp();
			
		} catch (Exception e) {
			logger.error("Start Alice Error.", e);
		}
	}

	/**
	 * 定时任务
	 */
	private void timerManager() {
		Calendar calendar = Calendar.getInstance();
		/*** 定制每日11:40执行方法 ***/
		calendar.set(Calendar.HOUR_OF_DAY, 11);
		calendar.set(Calendar.MINUTE, 40);
		calendar.set(Calendar.SECOND, 0);
		Date date = calendar.getTime(); // 第一次执行定时任务的时间
		if (date.before(new Date())) {
			date = this.addDay(date, 1);
		}
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				ReceiveMsg msg = new ReceiveMsg();
				msg.groupid = groupid;
				msg.msg = "去吃饭了\r\n人是铁饭是钢，一顿不吃饿的慌.";
				send(msg);
			}
		}, date, PERIOD_DAY);
	}

	private void startSend() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					if(new Date().getTime()-lastsendtime<1000)
						return;
					synchronized (sendList) {
						if (sendList != null && sendList.size() > 0) {
							ReceiveMsg msg = sendList.poll();
							if (msg.groupid != null) {
								hi.sendGroupMessage(msg.groupid, Msg.msg(msg.msg));
							} else {
								hi.sendMessage(msg.from, Msg.msg(msg.msg));
							}
							lastsendtime = new Date().getTime();
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}, 100,50);
	}

	private void send(ReceiveMsg msg) {
		synchronized (sendList) {
			sendList.add(msg);
		}
	}

	// 增加或减少天数
	private Date addDay(Date date, int num) {
		Calendar startDT = Calendar.getInstance();
		startDT.setTime(date);
		startDT.add(Calendar.DAY_OF_MONTH, num);
		return startDT.getTime();
	}

	@Override
	public void getTextMessage(Hi hi, ReceiveMsg message) {
		String content = message.msg;
		ReceiveMsg msg = message.clone();
		if (content.startsWith("y3523 ") && content.length() > 6) {
			String result = "机器人貌似挂了啊，杨忠伟咧？";
			try {
				result = bot.respond(toAlice(content.substring(6).split("\\r\\n")[0]));
				result = fromAlice(result);
			} catch (Exception e) {
				logger.error("Alice Exception: ",e);
			}
			msg.msg = result;
			send(msg);
		}
	}

	private String toAlice(String input) {
		StringBuffer sb = new StringBuffer();
		for (char c : input.trim().toCharArray()) {
			int v = (int) c;
			if (v >= 19968 && v <= 171941) {
				sb.append(c).append(" ");
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	private String fromAlice(String input){
		StringBuffer sb = new StringBuffer();
		char[] ins = input.trim().toCharArray();
		int i =0;
		while (i<ins.length) {
			sb.append(ins[i]);
			int v = (int) ins[i];
			if (v >= 19968 && v <= 171941 && i<ins.length-1 && ins[i+1]==' ') {
				i+=2;
			} else {
				i++;
			}
		}
		return sb.toString();
	}
}
