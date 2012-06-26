/**
 * 
 */
package com.baidu.selenium.errorspec.tools.hi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.baidu.selenium.errorspec.tools.Hi.Msg;

/**
 * 远程调用服务
 * 
 * @author sakyo
 * 
 */
public interface IHiService extends Remote {
	public static final String SERVICE_NAME = "COM.BAIDU.ERRORSPEC.HI";

	public Boolean sendMessage(String hiName, Msg... message)throws RemoteException;

	public Boolean sendGroupMessage(String groupid, Msg... message)throws RemoteException;
}
