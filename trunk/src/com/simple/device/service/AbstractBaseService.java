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
	 * ����������ƹ㲥������
	 */
	private ControlBroadcastReceiver controlBroadcastReceiver;
	
	/**
	 * ���������豸�ľ��
	 */
	public IDeviceConnectThread deviceConnectThread;
		
	/**
	 *	�豸����״̬
	 */
	public int deviceState=0;
		
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		IntentFilter controlFilter = new IntentFilter();
		controlFilter.addAction(DeviceTools.ACTION_DEVICE_DATA_SEND);// �����������豸
		controlFilter.addAction(DeviceTools.ACTION_DEVICE_CONTROL_CONNECT);// ����ָ���豸
		controlFilter.addAction(DeviceTools.ACTION_DEVICE_CONTROL_DISCONNECT);// �ر�����
		
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
		Log.d(LOGTAG,"�����豸״̬�仯: "+deviceState +" -> "+ tdeviceState);
		this.deviceState = tdeviceState;
	}
	
	/**
	 * ���豸��������
	 */
	@Override
	public void write(byte[] buffer) {
		Log.d(LOGTAG, "׼������ָ�� AbstractBaseService->write");		
		if(null!=deviceConnectThread && null!= deviceConnectThread.getCommuniThread()){
			deviceConnectThread.getCommuniThread().write(buffer);
		}		
	}

	/**
	 * �������豸ȡ�õ�����
	 */
	@Override
	public void decodeBuffer(byte[] buffer) {
		if(buffer != null && buffer.length>0){
			/**
			 * ���͹㲥,�㲥�Ѿ����յ������� 
			 */
			Intent intent = new Intent(DeviceTools.ACTION_DEVICE_DATA_GET);
			intent.putExtra(DeviceTools.EXTRA_DEVICE_DATA, new String(buffer));
			getApplicationContext().sendBroadcast(intent);
			
			//��ʼ��������
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
	 * ȡ��service�������Ļ�������
	 */
	@Override
	public Context getExApplicationContext() {
		return getApplicationContext();
	}

	/**
	 * ȡ�õ�ǰ���еķ������
	 */
	@Override
	public Service getService() {
		// TODO Auto-generated method stub
		return this;
	}
	
	/**
	 * �ϵ��Ѿ����ӵ��豸
	 */
	@Override
	public void closeDeviceConnect() {
		IDeviceConnectThread deviceConnectThread  = getConnectHandler();
		if(null == deviceConnectThread){
			Log.d(LOGTAG,"�û������豸��SOCKδ��ʼ��.");
			return;
		}		
		deviceConnectThread.closeConnect();
	}

	/**
	 * ����ָ�����豸
	 */
	@Override
	public void connectDevice(IDevice device) {
		closeDeviceConnect();//ͣ����������,���Ѿ����ӳɹ����豸	
		deviceConnectThread.connect(device);
	}

	/**
	 * �����豸�ɹ����ͳɹ��㲥��Ϣ
	 */
	@Override
	public void connectSuccess() {
		Log.d(LOGTAG, "���������豸�ɹ��Ĺ㲥[DeviceTools.ACTION_DEVICE_CONNECT_SUCCESS]");
		Intent intent = new Intent(DeviceTools.ACTION_DEVICE_CONNECT_SUCCESS);
		getExApplicationContext().sendBroadcast(intent);
	}
	
	/**
	 * �����豸�ɹ�����ʧ�ܹ㲥��Ϣ
	 */
	@Override
	public void connectFailed() {
		Log.d(LOGTAG, "���������豸ʧ�ܵĹ㲥[DeviceTools.ACTION_DEVICE_CONNECT_FAILURE]");
		Intent intent = new Intent(DeviceTools.ACTION_DEVICE_CONNECT_FAILURE);
		getExApplicationContext().sendBroadcast(intent);
	}
	
	
	/**
	 * ȡ�õ�ǰ����ʹ�õ����Ӿ��
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
