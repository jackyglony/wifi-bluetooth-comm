package com.simple.device;

public class WifiDevice extends AbsoluteDevice {

	public WifiDevice(String macAddress) {
		super(macAddress);
		this.deviceType = 2;
	}
	
}
