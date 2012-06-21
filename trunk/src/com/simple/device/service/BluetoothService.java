package com.simple.device.service;

/**
 * 		����ģ��
 * 
 *      Activity <== broadcast ==> Service  <== direct ==> WorkThread(���з���)
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
	 * ���������豸
	 */
	private BluetoothAdapter bt_device = DeviceTools.getAdapter();
	
	/**
	 * �������ӳɹ���� socket
	 */
	private BluetoothSocket socket;
	
	/**
	 * ����״̬�仯�㲥������
	 */
	private BtStateBroadcastReceiver btStateBroadcastReceiver;
		
	/**
	 * �豸���ӹ����߳�
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
		 * 1. ע������״̬�仯ʱ�Ĺ㲥
		 * 2. ע�� service �� activity֮��Ĺ㲥
		 */
		if(bt_device == null){
			System.out.println("�����豸������");
			return ;
		}
		
		DeviceTools.openBluetooth();
		
		IntentFilter btStateFilter = new IntentFilter();
		btStateFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		btStateFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		btStateFilter.addAction(BluetoothDevice.ACTION_FOUND);
		btStateFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//The state of the local Bluetooth adapter has been changed.	For example, Bluetooth has been turned on or off.
		btStateFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);	//�������״̬
		btStateFilter.addAction(DeviceTools.ACTION_AUTO_CONNECT);//�Զ������豸�Ĺ㲥
			
		//ע��BroadcastReceiver
		btStateBroadcastReceiver = new BtStateBroadcastReceiver(this);	
		registerReceiver(btStateBroadcastReceiver, btStateFilter);
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//���������߼��ڴ�		
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
	 * �Զ����������豸
	 */
	public void autoConnect() {
		// TODO Auto-generated method stub
		Set<BluetoothDevice> devices = this.getAdapter().getBondedDevices();
		if(devices==null || (devices!=null && devices.size()<1)) return ;
		
		for(BluetoothDevice device:devices){
			Log.d(LOGTAG,"BluetoothService -> autoConnect -> device: name: "+device.getName()+"; address: "+device.getAddress());			
		}
		
		if (deviceConnectThread == null) {
			//����һ��ָ�����豸
			deviceConnectThread = new BtDeviceConnectThread(this,devices);
			//deviceConnectThread = new BtDeviceConnectThread(this);
			setDeviceConnectThread(deviceConnectThread);//���÷�������ʹ�õ����ӹ����߳�
		}
		
		if(deviceConnectThread!=null &&!deviceConnectThread.getIsRun()){
			deviceConnectThread.start();
			//List<BluetoothDevice> devices_list = new ArrayList<BluetoothDevice>(devices); 
			//BtDevice btDevice = new BtDevice(devices_list.get(0).getAddress());
			//deviceConnectThread.connect(btDevice);
		}
	}
	

	/*
	 * �ص���������
	 * @see com.ecarinfo.device.service.AbstractBaseService#stopDeviceConnect()
	 */
	@Override
	public void closeDeviceConnect() {
		super.closeDeviceConnect();	
		if(null!=socket){
			try {
				Log.d(LOGTAG,"�ر�����sock�����ɹ�");
				socket.close();
			} catch (Exception e) {
				Log.d(LOGTAG,"�ر�����sock����ʧ��; ԭ��: "+e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void setSocket(BluetoothSocket socket) {
		this.socket = socket;
	}
	
}
