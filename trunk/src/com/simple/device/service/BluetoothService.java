package com.simple.device.service;

/**
 * 		工作模型
 * 
 *      Activity <== broadcast ==> Service  <== direct ==> WorkThread(含有服务)
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.simple.device.BtDevice;
import com.simple.device.broadcast.BtStateBroadcastReceiver;
import com.simple.device.util.DeviceTools;
import com.simple.device.worker.BtDeviceConnectThread;

public class BluetoothService extends AbstractBaseService{
	
	/**
	 * 本地蓝牙设备
	 */
	private BluetoothAdapter bt_device = DeviceTools.getAdapter();
	
	/**
	 * 蓝牙连接成功后的 socket
	 */
	private BluetoothSocket socket;
	
	/**
	 * 蓝牙状态变化广播监听器
	 */
	private BtStateBroadcastReceiver btStateBroadcastReceiver;
		
	/**
	 * 设备连接工作线程
	 */
	private BtDeviceConnectThread deviceConnectThread;
	
	
	public BluetoothAdapter getAdapter(){
		return bt_device;
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		System.out.println("BluetoothService -> onCreate");
		Log.d(LOGTAG,"BluetoothService -> onCreate");
		
		/**
		 * 1. 注册蓝牙状态变化时的广播
		 * 2. 注册 service 和 activity之间的广播
		 */
		if(bt_device == null){
			System.out.println("蓝牙设备不存在");
			return ;
		}
		
		DeviceTools.openBluetooth();
		
		IntentFilter btStateFilter = new IntentFilter();
		btStateFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		btStateFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		btStateFilter.addAction(BluetoothDevice.ACTION_FOUND);
		btStateFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//The state of the local Bluetooth adapter has been changed.	For example, Bluetooth has been turned on or off.
		btStateFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);	//蓝牙配对状态
		btStateFilter.addAction(DeviceTools.ACTION_AUTO_CONNECT);//自动连接设备的广播
			
		//注册BroadcastReceiver
		btStateBroadcastReceiver = new BtStateBroadcastReceiver(this);	
		registerReceiver(btStateBroadcastReceiver, btStateFilter);
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//服务运行逻辑在此		
		DeviceTools.openBluetooth();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		if(bt_device != null){
			unregisterReceiver(btStateBroadcastReceiver);
			DeviceTools.closeBluetooth();
		}
		super.onDestroy();
	}
	
	/**
	 * 自动连接蓝牙设备
	 */
	public void autoConnect() {
		// TODO Auto-generated method stub
		Set<BluetoothDevice> devices = this.getAdapter().getBondedDevices();
		if(devices==null || (devices!=null && devices.size()<1)) return ;
		
		for(BluetoothDevice device:devices){
			Log.d(LOGTAG,"BluetoothService -> autoConnect -> device: name: "+device.getName()+"; address: "+device.getAddress());			
		}
		
		if (deviceConnectThread == null) {
			//连接一个指定的设备
			deviceConnectThread = new BtDeviceConnectThread(this,devices);
			//deviceConnectThread = new BtDeviceConnectThread(this);
			setDeviceConnectThread(deviceConnectThread);//设置服务中所使用的连接工作线程
		}
		
		if(deviceConnectThread!=null &&!deviceConnectThread.getIsRun()){
			deviceConnectThread.start();
			//List<BluetoothDevice> devices_list = new ArrayList<BluetoothDevice>(devices); 
			//BtDevice btDevice = new BtDevice(devices_list.get(0).getAddress());
			//deviceConnectThread.connect(btDevice);
		}
	}
	

	/*
	 * 关掉蓝牙连接
	 * @see com.ecarinfo.device.service.AbstractBaseService#stopDeviceConnect()
	 */
	@Override
	public void closeDeviceConnect() {
		super.closeDeviceConnect();	
		if(null!=socket){
			try {
				Log.d(LOGTAG,"关闭蓝牙sock操作成功");
				socket.close();
			} catch (Exception e) {
				Log.d(LOGTAG,"关闭蓝牙sock操作失败; 原因: "+e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void setSocket(BluetoothSocket socket) {
		this.socket = socket;
	}
	
}
