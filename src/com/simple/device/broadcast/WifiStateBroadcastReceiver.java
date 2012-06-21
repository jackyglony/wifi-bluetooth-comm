package com.simple.device.broadcast;

import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.simple.device.service.IBaseService;
import com.simple.device.service.WifiService;

/**
 * Wifi״̬�㲥������
 * 
 * @author Administrator
 * 
 */
public class WifiStateBroadcastReceiver extends BroadcastReceiver {

	private final String LOGTAG = getClass().getSimpleName();
	private WifiService wifiService;
	
	public WifiStateBroadcastReceiver(WifiService wifiService) {
		super();
		this.wifiService = wifiService;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(LOGTAG,"wifi ״̬�仯�㲥��������ʼ����");
		String action = intent.getAction();
		
		Log.d(LOGTAG,"wifi action name: "+ action);
		
		if(WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)){//WIFI״̬�Ѿ������仯
			int state_current = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,10);
			int state_prev = intent.getIntExtra(WifiManager.EXTRA_PREVIOUS_WIFI_STATE,11);			
			if(WifiManager.WIFI_STATE_ENABLED ==state_current){//�������			
				try {
					Log.d(LOGTAG,"��ʼ��������SOCK");
					wifiService.autoConnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else if(WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {//�л�WIFI
			System.out.println("ѡ��һ����������");
		}
	}
}
