package com.simple.device.worker;

import com.simple.device.IDevice;
import com.simple.device.service.IBaseService;


public interface IDeviceConnectThread {
	
	/**
	 * 调用该连接句柄的服务对象
	 * @return
	 */
	IBaseService getService();
	
	/**
	 * 连接成功时通知接口
	 */
	void connectSuccess();
	
	/**
	 * 连接失败时通知接口
	 */
	void connectFailed();
	
	/**
	 * 连接指定的设备
	 * @param device
	 */
	void connect(IDevice device);
	
	/**
	 * 重新连接指定的设备
	 * @param device
	 */
	void reConnect(IDevice device);
	
	/**
	 * 关闭当前已经连接的设备
	 */
	void closeConnect();
	
	/**
	 * 返回当前线程的是否正在运行状态
	 * @return
	 */
	Boolean getIsRun();
	
	/**
	 * 取得当前线程是否是停止状态
	 * @return
	 */
	Boolean getStopFlag();
	
	
	/**
	 * 取得用于通讯的工作线程
	 * @param flag
	 */
	DeviceCommuniThread getCommuniThread();
	void setIsRun(Boolean flag);	
	void setStopFlag(Boolean flag);
	
	
}
