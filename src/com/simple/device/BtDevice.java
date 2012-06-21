package com.simple.device;

public class BtDevice extends AbsoluteDevice {

	public BtDevice(String macAddress) {
		super(macAddress);
		this.deviceType = 1;
	}
}
