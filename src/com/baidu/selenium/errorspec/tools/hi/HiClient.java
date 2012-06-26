package com.baidu.selenium.errorspec.tools.hi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import com.baidu.selenium.errorspec.config.Config;
import com.baidu.selenium.errorspec.tools.Hi.Msg;

public class HiClient {

	private static IHiService getService(String host, String port)
			throws RemoteException, MalformedURLException, NotBoundException {

		IHiService service = null;
		service = (IHiService) Naming.lookup(String.format("//%s:%s/%s",
				host, port, IHiService.SERVICE_NAME));
		return service;
	}
	
	public static IHiService getService()throws RemoteException, MalformedURLException, NotBoundException {
		return getService(Config.HiRemoteServer, Config.HiRemotePort+"");
	}
	
	public static boolean sendMessage(String hi, Msg... message){
		Boolean isOk = false;
		try{
			IHiService hiService = getService(Config.HiRemoteServer, Config.HiRemotePort+"");
			long groupid = 0;
			try{
				groupid = Long.valueOf(hi);
				isOk = hiService.sendGroupMessage(groupid+"", message);
			}
			catch (Exception e) {
				if(groupid ==0)
					isOk = hiService.sendMessage(hi, message);
			}
		}
		catch (Exception e) {
			isOk = false;
		}
		return isOk;
	}
	
	public static void main(String[] args) {
		if(args.length != 2){
			System.out.println("send hi to someone:");
			System.out.println("\tjava -jar hi.jar sakyo_yang \"你好\"");
			System.out.println("send hi to group:");
			System.out.println("\tjava -jar hi.jar 1360213 \"你好\"");
			System.exit(-1);		
		}
		Boolean isOk = false;
		try{
			IHiService hiService = getService(Config.HiRemoteServer, Config.HiRemotePort+"");
			long groupid = 0;
			try{
				groupid = Long.valueOf(args[0]);
				isOk = hiService.sendGroupMessage(groupid+"", Msg.msg(args[1]));
			}
			catch (Exception e) {
				if(groupid ==0)
					isOk = hiService.sendMessage(args[0], Msg.msg(args[1]));
			}
		}
		catch (Exception e) {
			isOk = false;
		}
		if(isOk) {
			System.exit(0);
			System.out.print("send hi message success.");
		}
		System.out.print("send hi message failed.");
		System.exit(-1);
	}
	
}
