package com.baidu.selenium.errorspec.tools.hi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.selenium.errorspec.config.Config;
import com.baidu.selenium.errorspec.tools.Hi;
import com.baidu.selenium.errorspec.tools.Hi.Msg;

public class HiService extends UnicastRemoteObject implements IHiService {
	public static final Logger logger = LoggerFactory.getLogger(HiService.class);
	private static final long serialVersionUID = 9028458104259285627L;
	protected Hi hi;
	
	public HiService(Hi hi,int port)throws RemoteException {
		super(port);
		this.hi = hi;
	}

	/**
	 * UseAge
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		int port= Config.HiRemotePort;
		Hi hi = new Hi();
		hi.login("y3523", "13774203523");
		System.out.println(hi.sendMessage("sakyo_yang", Msg.msg("启动HI机器人")));
		try{
			HiService hiService = new HiService(hi,port);
			Registry registry = LocateRegistry.createRegistry(port);
			registry.bind(IHiService.SERVICE_NAME, hiService);
		}catch (Exception e) {
			logger.error("Failed to start remote HiService");
			return;
		}
		hi.listeners.add(new FanTuanListener(hi));
		hi.startRecive();
	}
	
	@Override
	public Boolean sendMessage(String hiName, Msg... message) throws RemoteException{
		return hi.sendMessage(hiName, message);
	}

	@Override
	public Boolean sendGroupMessage(String groupid, Msg... message)throws RemoteException {
		return hi.sendGroupMessage(groupid, message);
	}

}
