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
 * Ӧ�ò���豸���Ӽ�ͨѶ�Ŀ��ƹ㲥������
 * 
 * 1. ����Ӧ�ò㷢�͵��豸��ָ��
 * 2. �ر��豸����
 * 3. �����Ѿ����ӵ��豸
 * 4. ��������
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
		
		if(DeviceTools.ACTION_DEVICE_DATA_SEND.equals(action)){//��ʼ��������
			String data = intent.getStringExtra(DeviceTools.EXTRA_DEVICE_SEND_COMMAND);
			Log.d(LOGTAG,"ControlBroadcastReceiver -> onReceive -> send to device: "+data);
			service.write(data.getBytes());
		}	else if(DeviceTools.ACTION_DEVICE_CONTROL_CONNECT.equals(action)){//��ʼ����ָ�����豸
			System.out.println("���յ������豸�Ĺ㲥ָ��");
			String macaddress = intent.getStringExtra(DeviceTools.EXTRA_DEVICE_CONNECT_MAC);
			String apname = intent.getStringExtra(DeviceTools.EXTRA_DEVICE_CONNECT_AP);
			System.out.println("�豸�ĵ�ַ:"+macaddress);
			
			if(null != macaddress){
				BtDevice btDevice = new BtDevice(macaddress);
				Log.d(LOGTAG,"ControlBroadcastReceiver -> onReceive -> connectDevice bluetooth device: "+macaddress);
				service.connectDevice(btDevice);
			} else if(null != apname){
				WifiDevice wifiDevice = new WifiDevice(apname);
				Log.d(LOGTAG,"ControlBroadcastReceiver -> onReceive -> connectDevice  wifi device: "+macaddress);
				service.connectDevice(wifiDevice);
			} else {
				Log.d(LOGTAG,"ControlBroadcastReceiver -> onReceive -> connectDevice device ��Ч�Ĳ���");
			}
		} else if(DeviceTools.ACTION_DEVICE_CONTROL_DISCONNECT.equals(action)){//�ص���ǰ���ӵ��豸
			Log.d(LOGTAG,"�ص���ǰ���ӵ��豸");
			service.closeDeviceConnect();
		}
	}
}
