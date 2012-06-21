package com.simple.device.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.simple.device.BtDevice;
import com.simple.device.WifiDevice;
import com.simple.device.service.IBaseService;
import com.simple.device.util.DeviceTools;
/**
 * 应用层对设备连接及通讯的控制广播接收器
 * 
 * 1. 接收应用层发送到设备的指令
 * 2. 关闭设备连接
 * 3. 连接已经连接的设备
 * 4. 重新连接
 * 
 * @author Administrator
 *
 */
public class ControlBroadcastReceiver extends BroadcastReceiver {
	
	public final String LOGTAG = getClass().getSimpleName();
	
	private IBaseService service;

	public ControlBroadcastReceiver(IBaseService service) {
		super();
		this.service = service;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.d(LOGTAG,"--onReceive--");
		
		if(DeviceTools.ACTION_DEVICE_DATA_SEND.equals(action)){//开始发送数据
			String data = intent.getStringExtra(DeviceTools.EXTRA_DEVICE_SEND_COMMAND);
			Log.d(LOGTAG,"ControlBroadcastReceiver -> onReceive -> send to device: "+data);
			service.write(data.getBytes());
		}	else if(DeviceTools.ACTION_DEVICE_CONTROL_CONNECT.equals(action)){//开始连接指定的设备
			System.out.println("接收到连接设备的广播指令");
			String macaddress = intent.getStringExtra(DeviceTools.EXTRA_DEVICE_CONNECT_MAC);
			String apname = intent.getStringExtra(DeviceTools.EXTRA_DEVICE_CONNECT_AP);
			System.out.println("设备的地址:"+macaddress);
			
			if(null != macaddress){
				BtDevice btDevice = new BtDevice(macaddress);
				Log.d(LOGTAG,"ControlBroadcastReceiver -> onReceive -> connectDevice bluetooth device: "+macaddress);
				service.connectDevice(btDevice);
			} else if(null != apname){
				WifiDevice wifiDevice = new WifiDevice(apname);
				Log.d(LOGTAG,"ControlBroadcastReceiver -> onReceive -> connectDevice  wifi device: "+macaddress);
				service.connectDevice(wifiDevice);
			} else {
				Log.d(LOGTAG,"ControlBroadcastReceiver -> onReceive -> connectDevice device 无效的参数");
			}
		} else if(DeviceTools.ACTION_DEVICE_CONTROL_DISCONNECT.equals(action)){//关掉当前连接的设备
			Log.d(LOGTAG,"关掉当前连接的设备");
			service.closeDeviceConnect();
		}
	}
}
