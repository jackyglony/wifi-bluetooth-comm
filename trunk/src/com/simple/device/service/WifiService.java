package com.simple.device.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

import com.simple.device.broadcast.ControlBroadcastReceiver;
import com.simple.device.broadcast.WifiStateBroadcastReceiver;
import com.simple.device.util.DeviceTools;
import com.simple.device.worker.BtDeviceConnectThread;
import com.simple.device.worker.DecodeThread;
import com.simple.device.worker.DeviceCommuniThread;
import com.simple.device.worker.WifiDeviceConnectThread;

public class WifiService extends AbstractBaseService{	
		
	/**
	 * 用来做TCP通讯的SOCK句柄
	 */
	private Socket socket;
	
	/**
	 * wifi管理对象
	 */
	private WifiManager wifiManager;
	
	/**
	 * 最后一次连接过的设备
	 */
	private WifiInfo currentWifiInfo; 
		
	/**
	 * WIFI状态变化广播接收器
	 */
	private WifiStateBroadcastReceiver wifiStateBroadcastReceiver;
	
	/**
	 * 设备连接工作线程
	 */
	private WifiDeviceConnectThread deviceConnectThread;

	
	/**
	 * AP连接过记录清单  
	 * 该列表用来存放已经记住的SSID,在周边但没有连接过的SSID不会出现在该列表中
	 */
    private List<WifiConfiguration> wifiConfiguration; 
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		//注册WIFI系统状态广播
		wifiStateBroadcastReceiver = new WifiStateBroadcastReceiver(this);
		IntentFilter wifi_filter = new IntentFilter();
		wifi_filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);//接收WIFI 启用/禁用/未知 三种状态
		wifi_filter.addAction(WifiManager.ACTION_PICK_WIFI_NETWORK);
		wifi_filter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
		
		// Broadcast intent action indicating that the state of Wi-Fi connectivity has changed. One extra provides the new state in the form of a NetworkInfo object. If the new state is CONNECTED, a second extra may provide the BSSID of the access point, as a String.
		
		wifi_filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);//切换WIFI时系统会发送该广播
		wifi_filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);//切换WIFI时系统会发送该广播
		
		//Broadcast intent action indicating that a connection to the supplicant has been established (and it is now possible to perform Wi-Fi operations) or the connection to the supplicant has been lost.
		wifi_filter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
		
		wifi_filter.addAction(WifiManager.RSSI_CHANGED_ACTION);//信号强度发生变化时的广播
		
		registerReceiver(wifiStateBroadcastReceiver,wifi_filter);
		
		wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		openWifi();// 打开Wifi		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		currentWifiInfo = wifiManager.getConnectionInfo();
		System.out.println("当前 Wifi  AP name:" + currentWifiInfo.getSSID());
		
//		wifiConfiguration = wifiManager.getConfiguredNetworks();		
//		for(WifiConfiguration wificfg:wifiConfiguration){
//			System.out.println("wificfg->ssid:"+wificfg.SSID);
//			if(wificfg.SSID.equals("\"Ecarinfo\"") && !currentWifiInfo.getSSID().equals("\"Ecarinfo\"")){
//				wifiManager.enableNetwork(wificfg.networkId, false);
//			}
//		}
//		currentWifiInfo = wifiManager.getConnectionInfo();
//		System.out.println("2当前 Wifi  AP name:" + currentWifiInfo.getSSID());
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(wifiStateBroadcastReceiver);
		super.onDestroy();
	}
	
	/**
	 * 开始尝试连接当前有用WIFI对应的设备
	 * @throws IOException
	 */
	public void autoConnect() throws IOException{
		wifiConfiguration = wifiManager.getConfiguredNetworks();	//只有WIFI打开的状态下才可以获取已经连接过的WIFI列表
		
		Log.d(LOGTAG,"当前wifi 的 SSID:   "+currentWifiInfo.getSSID());
		
		if(deviceConnectThread == null){
			deviceConnectThread = new WifiDeviceConnectThread(this);
			setDeviceConnectThread(deviceConnectThread);
		}
		
		/**
		 * 检查当前热点是否合法
		 */
		if(checkWifiDeviceValid(currentWifiInfo.getSSID())
				&&deviceConnectThread!=null 
				&&!deviceConnectThread.getIsRun()){
			Log.d(LOGTAG,"开始执行连接设备操作");
			deviceConnectThread.start();
		}
	}
	
	/**
	 * 切换WIFI
	 * @param wifiCfg
	 */
	public void switchWifi(int networkId){	
		boolean b = wifiManager.enableNetwork(networkId, true);
		System.out.println("当前连接的WIFI信息:"+DeviceTools.getDeviceString(wifiManager.getConnectionInfo()));
	}
	
	/**
	 * 检查当前的WIFI AP是否为合法的
	 * @return
	 */
	public Boolean checkWifiDeviceValid(String ssid){	
		return ssid!=null && "xlw_1d9d".equals(ssid)?true:false;
	}
	
	public boolean getWifiStatus()
    {		
			return wifiManager.isWifiEnabled();        
    }
	
	public boolean openWifi() {
        if (!wifiManager.isWifiEnabled()) {
            return wifiManager.setWifiEnabled(true);
        } else {
            return false;
        }
    }

    public boolean closeWifi() {
        if (!wifiManager.isWifiEnabled()) {
            return true;
        } else {
            return wifiManager.setWifiEnabled(false);
        }
    }
	
	//////////////////////////////////////////////////////////////////////////////////////
	public WifiManager getWifiManager() {
		return wifiManager;
	}

	public void setWifiManager(WifiManager wifiManager) {
		this.wifiManager = wifiManager;
	}

	public List<WifiConfiguration> getWifiConfiguration() {
		return wifiConfiguration;
	}

	public void setWifiConfiguration(List<WifiConfiguration> wifiConfiguration) {
		this.wifiConfiguration = wifiConfiguration;
	}

	public WifiInfo getCurrentWifiInfo() {
		return currentWifiInfo;
	}

	public void setCurrentWifiInfo(WifiInfo currentWifiInfo) {
		this.currentWifiInfo = currentWifiInfo;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
}
