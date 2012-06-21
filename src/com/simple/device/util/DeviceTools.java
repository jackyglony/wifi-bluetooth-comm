package com.simple.device.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.net.wifi.WifiInfo;


public class DeviceTools {	
	/**
	 * 本地蓝牙设备
	 */
	private static BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		
	/**
	 * 从手机发送指令至设备
	 */
	public static final String ACTION_DEVICE_DATA_SEND = "ACTION_DEVICE_DATA_SEND";
	
	/**
	 * 手机从设备获取数据
	 */
	public static final String ACTION_DEVICE_DATA_GET = "ACTION_DEVICE_DATA_GET";
		
	/**
	 * 连接设备
	 */
	public static final String ACTION_DEVICE_CONTROL_CONNECT = "ACTION_DEVICE_CONTROL_CONNECT";
	
	/**
	 * 广播自动连接设备
	 */
	public static final String ACTION_AUTO_CONNECT = "ACTION_AUTO_CONNECT";
		
	/**
	 * 关掉设备连接
	 */
	public static final String ACTION_DEVICE_CONTROL_DISCONNECT = "ACTION_DEVICE_CONTROL_DISCONNECT";
	
	/**
	 * 重新连接设备
	 */
	public static final String ACTION_DEVICE_CONTROL_RECONNECT = "ACTION_DEVICE_CONTROL_RECONNECT";
	
	/**
	 * 设备连接成功
	 */
	public static final String ACTION_DEVICE_CONNECT_SUCCESS = "ACTION_DEVICE_CONNECT_SUCCESS";
	
	/**
	 * 设备连接失败
	 */
	public static final String ACTION_DEVICE_CONNECT_FAILURE = "ACTION_DEVICE_CONNECT_FAILURE";
	
	/**
	 * 打开蓝牙功能
	 */
	public static void openBluetooth() {
		if(!adapter.isEnabled())
			adapter.enable();
	}
	
	/**
	 * 设备连接状态定义
	 */
	public static final int STATE_NOT_AVAILABLE = -1;	// 没有设备
    public static final int STATE_NONE = 0;       				// 没有连接
    public static final int STATE_CONNECTING = 1; 			// now initiating an outgoing connection
    public static final int STATE_CONNECTED = 2;  			// now connected to a remote device   
    
    public static final int BT=1;
    public static final int WIFI=2;
    
    
    public static final String EXTRA_DEVICE_SEND_COMMAND = "EXTRA_DEVICE_SEND_COMMAND";
    public static final String EXTRA_DEVICE_CONNECT_MAC = "EXTRA_DEVICE_CONNECT_MAC";
    public static final String EXTRA_DEVICE_CONNECT_AP = "EXTRA_DEVICE_CONNECT_AP";
    public static final String EXTRA_DEVICE_DATA="EXTRA_DEVICE_DATA";//ACTION_DEVICE_DATA_GET
        
	/**
	 * 关闭蓝牙功能
	 */
	public static void closeBluetooth() {
		if(adapter.isEnabled())
			adapter.disable();
	}
	
	public static BluetoothAdapter getAdapter(){
		return adapter;
	}
	
	public static String getDeviceString(BluetoothDevice device){
		return "bluetooth device: [name:"+device.getName()+"; mac address:"+device.getAddress()+"; ]";				
	}
	
	public static String getDeviceString(WifiInfo device){		
		return "Wifi device: [ssid:"+device.getSSID()+"; ipaddress:"+device.getIpAddress()+"; mac:"+device.getMacAddress()+"]";
	}
}