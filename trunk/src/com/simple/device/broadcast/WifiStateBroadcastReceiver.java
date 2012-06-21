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
 * Wifi状态广播接收器
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
		Log.d(LOGTAG,"wifi 状态变化广播接收器开始工作");
		String action = intent.getAction();
		
		Log.d(LOGTAG,"wifi action name: "+ action);
		
		if(WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)){//WIFI状态已经发生变化
			int state_current = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,10);
			int state_prev = intent.getIntExtra(WifiManager.EXTRA_PREVIOUS_WIFI_STATE,11);			
			if(WifiManager.WIFI_STATE_ENABLED ==state_current){//网络可用			
				try {
					Log.d(LOGTAG,"开始尝试连接SOCK");
					wifiService.autoConnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else if(WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {//切换WIFI
			System.out.println("选择一个网络连接");
		}
	}
}
