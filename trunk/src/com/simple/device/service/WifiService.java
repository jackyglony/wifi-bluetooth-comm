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
	 * ������TCPͨѶ��SOCK���
	 */
	private Socket socket;
	
	/**
	 * wifi�������
	 */
	private WifiManager wifiManager;
	
	/**
	 * ���һ�����ӹ����豸
	 */
	private WifiInfo currentWifiInfo; 
		
	/**
	 * WIFI״̬�仯�㲥������
	 */
	private WifiStateBroadcastReceiver wifiStateBroadcastReceiver;
	
	/**
	 * �豸���ӹ����߳�
	 */
	private WifiDeviceConnectThread deviceConnectThread;

	
	/**
	 * AP���ӹ���¼�嵥  
	 * ���б���������Ѿ���ס��SSID,���ܱߵ�û�����ӹ���SSID��������ڸ��б���
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
		//ע��WIFIϵͳ״̬�㲥
		wifiStateBroadcastReceiver = new WifiStateBroadcastReceiver(this);
		IntentFilter wifi_filter = new IntentFilter();
		wifi_filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);//����WIFI ����/����/δ֪ ����״̬
		wifi_filter.addAction(WifiManager.ACTION_PICK_WIFI_NETWORK);
		wifi_filter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
		
		// Broadcast intent action indicating that the state of Wi-Fi connectivity has changed. One extra provides the new state in the form of a NetworkInfo object. If the new state is CONNECTED, a second extra may provide the BSSID of the access point, as a String.
		
		wifi_filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);//�л�WIFIʱϵͳ�ᷢ�͸ù㲥
		wifi_filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);//�л�WIFIʱϵͳ�ᷢ�͸ù㲥
		
		//Broadcast intent action indicating that a connection to the supplicant has been established (and it is now possible to perform Wi-Fi operations) or the connection to the supplicant has been lost.
		wifi_filter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
		
		wifi_filter.addAction(WifiManager.RSSI_CHANGED_ACTION);//�ź�ǿ�ȷ����仯ʱ�Ĺ㲥
		
		registerReceiver(wifiStateBroadcastReceiver,wifi_filter);
		
		wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		openWifi();// ��Wifi		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		currentWifiInfo = wifiManager.getConnectionInfo();
		System.out.println("��ǰ Wifi  AP name:" + currentWifiInfo.getSSID());
		
//		wifiConfiguration = wifiManager.getConfiguredNetworks();		
//		for(WifiConfiguration wificfg:wifiConfiguration){
//			System.out.println("wificfg->ssid:"+wificfg.SSID);
//			if(wificfg.SSID.equals("\"Ecarinfo\"") && !currentWifiInfo.getSSID().equals("\"Ecarinfo\"")){
//				wifiManager.enableNetwork(wificfg.networkId, false);
//			}
//		}
//		currentWifiInfo = wifiManager.getConnectionInfo();
//		System.out.println("2��ǰ Wifi  AP name:" + currentWifiInfo.getSSID());
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(wifiStateBroadcastReceiver);
		super.onDestroy();
	}
	
	/**
	 * ��ʼ�������ӵ�ǰ����WIFI��Ӧ���豸
	 * @throws IOException
	 */
	public void autoConnect() throws IOException{
		wifiConfiguration = wifiManager.getConfiguredNetworks();	//ֻ��WIFI�򿪵�״̬�²ſ��Ի�ȡ�Ѿ����ӹ���WIFI�б�
		
		Log.d(LOGTAG,"��ǰwifi �� SSID:   "+currentWifiInfo.getSSID());
		
		if(deviceConnectThread == null){
			deviceConnectThread = new WifiDeviceConnectThread(this);
			setDeviceConnectThread(deviceConnectThread);
		}
		
		/**
		 * ��鵱ǰ�ȵ��Ƿ�Ϸ�
		 */
		if(checkWifiDeviceValid(currentWifiInfo.getSSID())
				&&deviceConnectThread!=null 
				&&!deviceConnectThread.getIsRun()){
			Log.d(LOGTAG,"��ʼִ�������豸����");
			deviceConnectThread.start();
		}
	}
	
	/**
	 * �л�WIFI
	 * @param wifiCfg
	 */
	public void switchWifi(int networkId){	
		boolean b = wifiManager.enableNetwork(networkId, true);
		System.out.println("��ǰ���ӵ�WIFI��Ϣ:"+DeviceTools.getDeviceString(wifiManager.getConnectionInfo()));
	}
	
	/**
	 * ��鵱ǰ��WIFI AP�Ƿ�Ϊ�Ϸ���
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
