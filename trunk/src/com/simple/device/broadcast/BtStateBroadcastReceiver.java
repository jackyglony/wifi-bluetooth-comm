package com.simple.device.broadcast;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.simple.device.service.BluetoothService;
import com.simple.device.util.DeviceTools;

/**
 * ����״̬�㲥������
 * @author Administrator
 *
 */
public class BtStateBroadcastReceiver extends BroadcastReceiver {
	private final String LOGTAG = getClass().getSimpleName();
	private BluetoothService bluetoothService;//��������	
	
	public BtStateBroadcastReceiver(BluetoothService bluetoothService) {
		super();
		this.bluetoothService = bluetoothService;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		
		Log.d(LOGTAG,"onReceive action: "+action);
		
		if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {//��������
			Log.d(LOGTAG,"�豸��������");	
			//BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);			
		}else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {//������������
			Log.d(LOGTAG,"�豸��������");			
			//BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		} else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){//����״̬�ı�   STATE_OFF, STATE_TURNING_ON, STATE_ON, STATE_TURNING_OFF
			Log.d(LOGTAG,"�豸����״̬�����仯");
			int cutent = intent.getExtras().getInt(BluetoothAdapter.EXTRA_STATE);						//��ǰ�豸��״̬
			int privious = intent.getExtras().getInt(BluetoothAdapter.EXTRA_PREVIOUS_STATE);	//��ǰ�豸����һ��״̬
			
			if(BluetoothAdapter.STATE_OFF == cutent && BluetoothAdapter.STATE_TURNING_OFF == privious){
				//����������������
			} else if(BluetoothAdapter.STATE_ON == cutent && BluetoothAdapter.STATE_TURNING_ON == privious){
				Log.d(LOGTAG,"�豸��ʼ�Զ�����");
				bluetoothService.autoConnect();
			}			
		} else if(DeviceTools.ACTION_AUTO_CONNECT.equals(action)){//�Զ���������ָ��
			Log.d(LOGTAG,"�ֹ������Զ�����");
			bluetoothService.autoConnect();
		}
	}
}
