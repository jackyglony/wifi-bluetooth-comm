package com.simple.device;

public interface IDevice {	
	/**
	 * wifi is ap name
	 * bt is mac address
	 * @return
	 */
	String getName();	
//	Object getDevice();
	
	/**
	 * ��ȡ�豸����
	 * @return
	 */
	int getDeviceType();
	
	String getMacAddress();
	Boolean isBtDevice();
	Boolean isWifiDevice();
}
