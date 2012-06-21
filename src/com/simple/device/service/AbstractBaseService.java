package com.simple.device.service;

import java.io.InputStream;
import java.io.OutputStream;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.simple.device.IDevice;
import com.simple.device.broadcast.ControlBroadcastReceiver;
import com.simple.device.util.DeviceTools;
import com.simple.device.worker.DecodeThread;
import com.simple.device.worker.DeviceCommuniThread;
import com.simple.device.worker.IDeviceConnectThread;

public abstract class AbstractBaseService extends Service implements IBaseService {
	
	public final String LOGTAG = getClass().getSimpleName();
	
	/**
	 * 蓝牙服务控制广播接收器
	 */
	private ControlBroadcastReceiver controlBroadcastReceiver;
	
	/**
	 * 用于连接设备的句柄
	 */
	public IDeviceConnectThread deviceConnectThread;
		
	/**
	 *	设备工作状态
	 */
	public int deviceState=0;
		
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		IntentFilter controlFilter = new IntentFilter();
		controlFilter.addAction(DeviceTools.ACTION_DEVICE_DATA_SEND);// 发送数据至设备
		controlFilter.addAction(DeviceTools.ACTION_DEVICE_CONTROL_CONNECT);// 连接指定设备
		controlFilter.addAction(DeviceTools.ACTION_DEVICE_CONTROL_DISCONNECT);// 关闭连接
		
		controlBroadcastReceiver = new ControlBroadcastReceiver(this);
		registerReceiver(controlBroadcastReceiver, controlFilter);
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(controlBroadcastReceiver);
		super.onDestroy();
	}
	
		
	public int getDeviceState() {
		return deviceState;
	}

	public synchronized void setDeviceState(int tdeviceState) {
		Log.d(LOGTAG,"连接设备状态变化: "+deviceState +" -> "+ tdeviceState);
		this.deviceState = tdeviceState;
	}
	
	/**
	 * 向设备发送数据
	 */
	@Override
	public void write(byte[] buffer) {
		Log.d(LOGTAG, "准备发送指令 AbstractBaseService->write");		
		if(null!=deviceConnectThread && null!= deviceConnectThread.getCommuniThread()){
			deviceConnectThread.getCommuniThread().write(buffer);
		}		
	}

	/**
	 * 解析从设备取得的数据
	 */
	@Override
	public void decodeBuffer(byte[] buffer) {
		if(buffer != null && buffer.length>0){
			/**
			 * 发送广播,广播已经接收到的数据 
			 */
			Intent intent = new Intent(DeviceTools.ACTION_DEVICE_DATA_GET);
			intent.putExtra(DeviceTools.EXTRA_DEVICE_DATA, new String(buffer));
			getApplicationContext().sendBroadcast(intent);
			
			//开始解析数据
    		DecodeThread decodeThread = new DecodeThread(buffer, getExApplicationContext());
    		decodeThread.run(); 
    	}
	}

	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 取得service的上下文环境变量
	 */
	@Override
	public Context getExApplicationContext() {
		return getApplicationContext();
	}

	/**
	 * 取得当前运行的服务对象
	 */
	@Override
	public Service getService() {
		// TODO Auto-generated method stub
		return this;
	}
	
	/**
	 * 断掉已经连接的设备
	 */
	@Override
	public void closeDeviceConnect() {
		IDeviceConnectThread deviceConnectThread  = getConnectHandler();
		if(null == deviceConnectThread){
			Log.d(LOGTAG,"用户连接设备的SOCK未初始化.");
			return;
		}		
		deviceConnectThread.closeConnect();
	}

	/**
	 * 连接指定的设备
	 */
	@Override
	public void connectDevice(IDevice device) {
		closeDeviceConnect();//停掉正在连接,或已经连接成功的设备	
		deviceConnectThread.connect(device);
	}

	/**
	 * 连接设备成功后发送成功广播消息
	 */
	@Override
	public void connectSuccess() {
		Log.d(LOGTAG, "发送连接设备成功的广播[DeviceTools.ACTION_DEVICE_CONNECT_SUCCESS]");
		Intent intent = new Intent(DeviceTools.ACTION_DEVICE_CONNECT_SUCCESS);
		getExApplicationContext().sendBroadcast(intent);
	}
	
	/**
	 * 连接设备成功后发送失败广播消息
	 */
	@Override
	public void connectFailed() {
		Log.d(LOGTAG, "发送连接设备失败的广播[DeviceTools.ACTION_DEVICE_CONNECT_FAILURE]");
		Intent intent = new Intent(DeviceTools.ACTION_DEVICE_CONNECT_FAILURE);
		getExApplicationContext().sendBroadcast(intent);
	}
	
	
	/**
	 * 取得当前正在使用的连接句柄
	 */
	@Override
	public IDeviceConnectThread getConnectHandler() {
		return deviceConnectThread;
	}
	
	public IDeviceConnectThread getDeviceConnectThread() {
		return deviceConnectThread;
	}

	public void setDeviceConnectThread(IDeviceConnectThread deviceConnectThread) {
		this.deviceConnectThread = deviceConnectThread;
	}
		
}
