package com.simple.device.worker;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.bluetooth.BluetoothAdapter;
import android.util.Log;

import com.simple.device.IDevice;
import com.simple.device.service.IBaseService;

public class AbsoluteDeviceConnectThread extends Thread implements IDeviceConnectThread {
	
	protected final String LOGTAG = getClass().getSimpleName();	
	protected Boolean isRun = false;
	protected Boolean stopFlag = false;
	protected IDevice device;
	protected IBaseService iBaseService;
	protected DeviceCommuniThread communiThread;
		
	@Override
	public Boolean getIsRun() {
		return this.isRun;
	}

	@Override
	public Boolean getStopFlag() {
		return this.stopFlag;
	}

	@Override
	public void setIsRun(Boolean flag) {
		this.isRun = flag;
	}	

	@Override
	public void connect(IDevice device) {
	}

	@Override
	public void reConnect(IDevice device) {
	}

	@Override
	public void connectFailed() {
		closeConnect();
	}

	@Override
	public void setStopFlag(Boolean flag) {		
	}

	@Override
	public IBaseService getService() {
		return null;
	}

	@Override
	public void connectSuccess() {
		
	}	
	/**
	 * 关掉连接 先关掉输入/出流
	 */
	@Override
	public void closeConnect() {		
		if (null !=communiThread ){
			communiThread.closeInOutStream();
		}
	}

	/**********************************************************************************************************/
	/****************************************              get/set autrr     ***************************************/
	/**********************************************************************************************************/	
	
	@Override
	public DeviceCommuniThread getCommuniThread() {
		return communiThread;
	}

	public void setCommuniThread(DeviceCommuniThread communiThread) {
		this.communiThread = communiThread;
	}
	
}
