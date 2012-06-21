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
 * Android监听网络状态实现(BroadcastReceiver + Service)
 * 文章来源于 http://blog.csdn.net/lvron/article/details/7098771
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
		
		if(WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)){//WIFI状态已经发生变化
			String ssid = intent. getStringExtra(WifiManager.EXTRA_BSSID);
			Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			if (null != parcelableExtra) {  
                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                System.out.println("networkInfo:"+networkInfo.getTypeName());
			}
			
			System.out.println("wifi 热点名称:"+ssid);			
			int state_current = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,10);
			int state_prev = intent.getIntExtra(WifiManager.EXTRA_PREVIOUS_WIFI_STATE,10);
			
			System.out.println("WIFI网状态发生变化,状态值: "+state_prev+" > "+state_current);
			
			if(WifiManager.WIFI_STATE_ENABLED == state_current){
				System.out.println("WIFI 网络可用");
				NetworkInfo info = (NetworkInfo)intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);				
				System.out.println("info:"+info);
			} else if(WifiManager.WIFI_STATE_DISABLED == state_current){
				System.out.println("WIFI 网络关掉");
			}
		} else if(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION.equals(action)){//检查连接
			//WIFI_STATE_CHANGED_ACTION
		}
		
		//网络状态发生变化
		if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION))   
		{
		  System.out.println("001");
		  NetworkInfo info = (NetworkInfo)intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);		  
		  String ssid = intent. getStringExtra(WifiManager.EXTRA_BSSID);
		  
		  System.out.println("info:"+info.getExtraInfo());
		  System.out.println("ssid:"+ssid);
		  
		  
		  if (info.getState().equals(NetworkInfo.State.CONNECTED))   
		  {   
			  System.err.println("网络可以使用2");
		    //do whatever you want when wifi is active and connected to a hotspot   
		  }   
		}
		
		// TODO Auto-generated method stub
		Log.d(LOGTAG, "接收到网络状态变化的广播");
		
		if(ConnectivityManager.CONNECTIVITY_ACTION.equals(action)){
			boolean success = false;
			// 获得网络连接服务
			ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			// State state = connManager.getActiveNetworkInfo().getState();
			
			// 获取WIFI网络连接状态
			State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
			
			// 判断是否正在使用WIFI网络
			if (State.CONNECTED == state) {
				success = true;
				Log.d(LOGTAG,"网络为WIFI");
			}
			
			// 获取GPRS网络连接状态
			state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();		
			// 判断是否正在使用GPRS网络
			if (State.CONNECTED == state) {
				success = true;
				Log.d(LOGTAG,"网络为gprs");
			}
						
			if (!success) {
				Log.d(LOGTAG,"网络未打开,或不可用");
			}		
		}
	}
	
	// 显示Wifi状态以及ip地址:
		public static String StringizeIp(int ip) {
			int ip4 = (ip >> 24) & 0x000000FF;
			int ip3 = (ip >> 16) & 0x000000FF;
			int ip2 = (ip >> 8) & 0x000000FF;
			int ip1 = ip & 0x000000FF;
			return Integer.toString(ip1) + "." + ip2 + "." + ip3 + "." + ip4;
		}
}
