package com.simple.device;

import com.simple.device.util.DeviceTools;

public class AbsoluteDevice implements IDevice{
		
	protected String name;
	protected String macAddress;
	protected int deviceType=0;		
		
	public AbsoluteDevice(String macAddress) {
		super();
		this.macAddress = macAddress;
	}
	
	public AbsoluteDevice(String macAddress, int deviceType) {
		super();
		this.macAddress = macAddress;
		this.deviceType = deviceType;
	}
	
	public AbsoluteDevice(String name, String macAddress, int deviceType) {
		super();
		this.name = name;
		this.macAddress = macAddress;
		this.deviceType = deviceType;
	}
	
	@Override
	public String getName() {
		return name;
	}
	@Override
	public int getDeviceType() {
		return deviceType;
	}
	@Override
	public String getMacAddress() {
		return macAddress;
	}
	
	@Override
	public Boolean isBtDevice() {
		return (deviceType == DeviceTools.BT)?true:false;
	}
	
	@Override
	public Boolean isWifiDevice() {
		return (deviceType == DeviceTools.WIFI)?true:false;
	}
	
}
