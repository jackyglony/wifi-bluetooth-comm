package com.simple.device.service;

import android.app.Service;
import android.content.Context;

import com.simple.device.IDevice;
import com.simple.device.worker.IDeviceConnectThread;

public interface IBaseService {	
	
	void write(byte[] buffer);
	void decodeBuffer(byte[] buffer);
	
	void closeDeviceConnect();//�ص��������ӵ��豸
	void connectDevice(IDevice device);//����ָ�����豸
	
	void connectFailed();
	void connectSuccess();	
	
	Context getExApplicationContext();//��ȡcontext
	Service getService();//���ط������
	
	IDeviceConnectThread getConnectHandler();
}
