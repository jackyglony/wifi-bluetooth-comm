package com.simple.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.util.Log;

/**
 * Android��������״̬ʵ��(BroadcastReceiver + Service)
 * ������Դ�� http://blog.csdn.net/lvron/article/details/7098771
 * 
 * @author Administrator
 *
 */
public class ConnectionChangeReceiver extends BroadcastReceiver {
	private final String LOGTAG = getClass().getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();   
		 
		System.out.println("action: "+action);
		
		if(WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)){//WIFI״̬�Ѿ������仯
			String ssid = intent. getStringExtra(WifiManager.EXTRA_BSSID);
			Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			if (null != parcelableExtra) {  
                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                System.out.println("networkInfo:"+networkInfo.getTypeName());
			}
			
			System.out.println("wifi �ȵ�����:"+ssid);			
			int state_current = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,10);
			int state_prev = intent.getIntExtra(WifiManager.EXTRA_PREVIOUS_WIFI_STATE,10);
			
			System.out.println("WIFI��״̬�����仯,״ֵ̬: "+state_prev+" > "+state_current);
			
			if(WifiManager.WIFI_STATE_ENABLED == state_current){
				System.out.println("WIFI �������");
				NetworkInfo info = (NetworkInfo)intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);				
				System.out.println("info:"+info);
			} else if(WifiManager.WIFI_STATE_DISABLED == state_current){
				System.out.println("WIFI ����ص�");
			}
		} else if(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION.equals(action)){//�������
			//WIFI_STATE_CHANGED_ACTION
		}
		
		//����״̬�����仯
		if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION))   
		{
		  System.out.println("001");
		  NetworkInfo info = (NetworkInfo)intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);		  
		  String ssid = intent. getStringExtra(WifiManager.EXTRA_BSSID);
		  
		  System.out.println("info:"+info.getExtraInfo());
		  System.out.println("ssid:"+ssid);
		  
		  
		  if (info.getState().equals(NetworkInfo.State.CONNECTED))   
		  {   
			  System.err.println("�������ʹ��2");
		    //do whatever you want when wifi is active and connected to a hotspot   
		  }   
		}
		
		// TODO Auto-generated method stub
		Log.d(LOGTAG, "���յ�����״̬�仯�Ĺ㲥");
		
		if(ConnectivityManager.CONNECTIVITY_ACTION.equals(action)){
			boolean success = false;
			// ����������ӷ���
			ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			// State state = connManager.getActiveNetworkInfo().getState();
			
			// ��ȡWIFI��������״̬
			State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
			
			// �ж��Ƿ�����ʹ��WIFI����
			if (State.CONNECTED == state) {
				success = true;
				Log.d(LOGTAG,"����ΪWIFI");
			}
			
			// ��ȡGPRS��������״̬
			state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();		
			// �ж��Ƿ�����ʹ��GPRS����
			if (State.CONNECTED == state) {
				success = true;
				Log.d(LOGTAG,"����Ϊgprs");
			}
						
			if (!success) {
				Log.d(LOGTAG,"����δ��,�򲻿���");
			}		
		}
	}
	
	// ��ʾWifi״̬�Լ�ip��ַ:
		public static String StringizeIp(int ip) {
			int ip4 = (ip >> 24) & 0x000000FF;
			int ip3 = (ip >> 16) & 0x000000FF;
			int ip2 = (ip >> 8) & 0x000000FF;
			int ip1 = ip & 0x000000FF;
			return Integer.toString(ip1) + "." + ip2 + "." + ip3 + "." + ip4;
		}
}
