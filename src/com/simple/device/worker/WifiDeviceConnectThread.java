package com.simple.device.worker;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Set;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.simple.device.IDevice;
import com.simple.device.service.BluetoothService;
import com.simple.device.service.WifiService;
import com.simple.device.util.DeviceTools;

/**
 * Android ��ȡwifi��IP��ַ
 * http://www.cnblogs.com/error404/archive/2012/05/03/2481958.html
 * @author simple
 *
 */
public class WifiDeviceConnectThread extends AbsoluteDeviceConnectThread {	
	private Socket socket;	
	private WifiService wifiService;	
		
	public WifiDeviceConnectThread(WifiService pwifiService) {		
		this.wifiService = pwifiService;
	}

	@Override
	public void run() {
		System.out.println("WifiDeviceConnectThread-> run...");
		Log.d(LOGTAG,"wifi connect thread run...");
		//�ڴ˴������Զ�����
		setIsRun(true);
		try {
			connect();
		} catch (Exception e) {
			// TODO: handle exception
		}
		setIsRun(false);
	}
	
	/**
	 * �����豸�������
	 * @param device
	 * @throws Exception
	 */
	public synchronized void connect() throws Exception{
		/**
		 * ����豸û��ƥ��ֱ�ӷ���.
		 * ���ô���Ҫ���ӵ��豸�Ѿ�������Թ���
		 */
		try {
//			socket = new Socket("192.168.1.163", 60000);
			socket = new Socket("192.168.2.3", 5000);
			wifiService.setDeviceState(DeviceTools.STATE_CONNECTED);
			
			if(null==getCommuniThread()){
				DeviceCommuniThread communiThread = new DeviceCommuniThread(wifiService);
				setCommuniThread(communiThread);
			}
			
			if(null!=getCommuniThread()){
				getCommuniThread().setInStream(socket.getInputStream());
				getCommuniThread().setOutStream(socket.getOutputStream());
				getCommuniThread().start();//��ʼ��������
			}
			
			Log.d(LOGTAG, "��ʼ���Ͷ�ȡ�г�������ָ��");
			new SendGetStreamThread(wifiService).start();//��ʼ���Ͷ�ȡ�г���������ģ��ָ��
			
			wifiService.connectSuccess();
		} catch (Exception e) {
			Log.d(LOGTAG,"��������ʧ��,ԭ��:"+e.getMessage());
			e.printStackTrace();
			wifiService.connectFailed();
		}
	}
	
	/**
	 * ���չ㲥Ҫ�������豸������
	 */
	@Override
	public void connect(IDevice device) {
		super.connect(device);
		try {
			Log.d(LOGTAG,"���չ㲥������WIFI�豸");
			connect();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * ���չ㲥Ҫ�����������豸������
	 */
	@Override
	public void reConnect(IDevice device) {
		super.reConnect(device);
	}
	
	/**
	 * ִ������WIFI�豸�ľ������
	 */
	public void executeConnect() throws Exception{
		
	}

	/**
	 * �ص������豸��Զ���豸������
	 */
	@Override
	public void closeConnect(){
		super.closeConnect();//�ص�SOCK ������/����		
		if(null!=socket){
			try {
				socket.close();
				socket = null;
				wifiService.setSocket(null);
				wifiService.setDeviceState(DeviceTools.STATE_NONE);
			} catch (Exception e) {
				Log.d(LOGTAG,"�ر�ͨѶ���ʧ��,ԭ��:"+e.getMessage());
				e.printStackTrace();
			}
		}
	}

	@Override
	public void connectFailed() {
		super.connectFailed();
		try {
			if(null!=socket)
				socket.close();
				socket = null;
				wifiService.setSocket(null);
				wifiService.setDeviceState(DeviceTools.STATE_NONE);
		} catch (Exception e) {
			Log.d(LOGTAG,"�ر�ͨѶ���ʧ��,ԭ��:"+e.getMessage());
			e.printStackTrace();
		}
	}
}
