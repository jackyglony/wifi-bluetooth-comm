package com.simple.device.service;

import android.app.Service;
import android.content.Context;

import com.simple.device.IDevice;
import com.simple.device.worker.IDeviceConnectThread;

public interface IBaseService {	
	
	void write(byte[] buffer);
	void decodeBuffer(byte[] buffer);
	
	void closeDeviceConnect();//关掉正在连接的设备
	void connectDevice(IDevice device);//连接指定的设备
	
	void connectFailed();
	void connectSuccess();	
	
	Context getExApplicationContext();//获取context
	Service getService();//返回服务对象
	
	IDeviceConnectThread getConnectHandler();
}
