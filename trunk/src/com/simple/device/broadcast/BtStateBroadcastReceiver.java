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
 * 蓝牙状态广播接收器
 * @author Administrator
 *
 */
public class BtStateBroadcastReceiver extends BroadcastReceiver {
	private final String LOGTAG = getClass().getSimpleName();
	private BluetoothService bluetoothService;//蓝牙服务	
	
	public BtStateBroadcastReceiver(BluetoothService bluetoothService) {
		super();
		this.bluetoothService = bluetoothService;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		
		Log.d(LOGTAG,"onReceive action: "+action);
		
		if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {//允许连接
			Log.d(LOGTAG,"设备允许连接");	
			//BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);			
		}else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {//蓝牙不可连接
			Log.d(LOGTAG,"设备不可连接");			
			//BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		} else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){//蓝牙状态改变   STATE_OFF, STATE_TURNING_ON, STATE_ON, STATE_TURNING_OFF
			Log.d(LOGTAG,"设备开关状态发生变化");
			int cutent = intent.getExtras().getInt(BluetoothAdapter.EXTRA_STATE);						//当前设备的状态
			int privious = intent.getExtras().getInt(BluetoothAdapter.EXTRA_PREVIOUS_STATE);	//当前设备的上一个状态
			
			if(BluetoothAdapter.STATE_OFF == cutent && BluetoothAdapter.STATE_TURNING_OFF == privious){
				//两个都不可以连接
			} else if(BluetoothAdapter.STATE_ON == cutent && BluetoothAdapter.STATE_TURNING_ON == privious){
				Log.d(LOGTAG,"设备开始自动连接");
				bluetoothService.autoConnect();
			}			
		} else if(DeviceTools.ACTION_AUTO_CONNECT.equals(action)){//自动连接蓝牙指令
			Log.d(LOGTAG,"手工启动自动连接");
			bluetoothService.autoConnect();
		}
	}
}
