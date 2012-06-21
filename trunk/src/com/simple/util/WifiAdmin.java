package com.simple.util;


import java.util.List;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;

/**
 * com.Aina.Android Pro_wifi
 * 
 * @author Aina.huang E-mail: 674023920@qq.com
 * @version 创建时间：2010 Jul 21, 2010 12:29:00 PM 类说明
 */
public class WifiAdmin {	

	private WifiManager mWifiManager=null;
	private WifiInfo mWifiInfo = null;
	private List<ScanResult> mWifiList = null;// 扫描出的网络连接列表
	private List<WifiConfiguration> mWifiConfiguration = null;// 网络连接列表
	private WifiLock mWifiLock = null;
	

	public WifiAdmin(Context mContext) {
		mWifiManager = (WifiManager) mContext	.getSystemService(mContext.WIFI_SERVICE);
		mWifiInfo = mWifiManager.getConnectionInfo();
	}

	public void OpenWifi() {
		if (!mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(true);// 打开wifi
		}
	}

	public void CloseWife() {
		if (mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(false);// 关闭wifi
		}
	}

	public void lockWifi() {
		mWifiLock.acquire();// 锁定wifi
	}

	public void rlockWifi() {
		if (mWifiLock.isHeld()) {
			mWifiLock.acquire();// 解锁wifi
		}
	}

	public void Createwifilock() {
		mWifiLock = mWifiManager.createWifiLock("Testss");// 创建一个wifilock
	}

	public List<WifiConfiguration> GetConfinguration() {
		return mWifiConfiguration;// 得到配置好的网络
	}

	public void ConnectConfiguration(int index) {
		if (index > mWifiConfiguration.size()) {
			return;
		}
		mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId, true);//连接配置好的指定ID的网络
	}
	public void StartScan()
	{
		mWifiManager.startScan();
		//得到扫描结果
		mWifiList = mWifiManager.getScanResults();
		//得到配置好的网络连接
		mWifiConfiguration = mWifiManager.getConfiguredNetworks();
	}
	//得到网络列表
	public List<ScanResult> GetWifiList()
	{
		return mWifiList;
	}
	//查看扫描结果
	public StringBuilder LookUpScan()
	{
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < mWifiList.size(); i++)
		{
			stringBuilder.append("Index_"+new Integer(i + 1).toString() + ":");
			//将ScanResult信息转换成一个字符串包
			//其中把包括：BSSID、SSID、capabilities、frequency、level
			stringBuilder.append((mWifiList.get(i)).toString());
			stringBuilder.append("\n");
		}
		return stringBuilder;
	}
	//得到MAC地址
	public String GetMacAddress()
	{
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
	}
	//得到接入点的BSSID
	public String GetBSSID()
	{
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
	}
	//得到IP地址
	public int GetIPAddress()
	{
		return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
	}
	//得到连接的ID
	public int GetNetworkId()
	{
		return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
	}
	//得到WifiInfo的所有信息包
	public String GetWifiInfo()
	{
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
	}
	//添加一个网络并连接
	public void AddNetwork(WifiConfiguration wcg)
	{
		int wcgID = mWifiManager.addNetwork(wcg); 
		mWifiManager.enableNetwork(wcgID, true); 
	}
	//断开指定ID的网络
	public void DisconnectWifi(int netId)
	{
		mWifiManager.disableNetwork(netId);
		mWifiManager.disconnect();
	}
}

