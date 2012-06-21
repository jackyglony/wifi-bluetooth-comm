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
	 * 获取设备类型
	 * @return
	 */
	int getDeviceType();
	
	String getMacAddress();
	Boolean isBtDevice();
	Boolean isWifiDevice();
}
