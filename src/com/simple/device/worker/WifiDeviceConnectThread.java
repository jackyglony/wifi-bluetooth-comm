package com.simple.device.worker;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Set;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.simple.device.IDevice;
import com.simple.device.service.BluetoothService;
import com.simple.device.service.WifiService;
import com.simple.device.util.DeviceTools;

/**
 * Android 获取wifi的IP地址
 * http://www.cnblogs.com/error404/archive/2012/05/03/2481958.html
 * @author simple
 *
 */
public class WifiDeviceConnectThread extends AbsoluteDeviceConnectThread {	
	private Socket socket;	
	private WifiService wifiService;	
		
	public WifiDeviceConnectThread(WifiService pwifiService) {		
		this.wifiService = pwifiService;
	}

	@Override
	public void run() {
		System.out.println("WifiDeviceConnectThread-> run...");
		Log.d(LOGTAG,"wifi connect thread run...");
		//在此处尝试自动连接
		setIsRun(true);
		try {
			connect();
		} catch (Exception e) {
			// TODO: handle exception
		}
		setIsRun(false);
	}
	
	/**
	 * 连接设备具体操作
	 * @param device
	 * @throws Exception
	 */
	public synchronized void connect() throws Exception{
		/**
		 * 如果设备没有匹配直接返回.
		 * 即该处需要连接的设备已经做过配对工作
		 */
		try {
//			socket = new Socket("192.168.1.163", 60000);
			socket = new Socket("192.168.2.3", 5000);
			wifiService.setDeviceState(DeviceTools.STATE_CONNECTED);
			
			if(null==getCommuniThread()){
				DeviceCommuniThread communiThread = new DeviceCommuniThread(wifiService);
				setCommuniThread(communiThread);
			}
			
			if(null!=getCommuniThread()){
				getCommuniThread().setInStream(socket.getInputStream());
				getCommuniThread().setOutStream(socket.getOutputStream());
				getCommuniThread().start();//开始接收数据
			}
			
			Log.d(LOGTAG, "开始发送读取行车数据流指令");
			new SendGetStreamThread(wifiService).start();//开始发送读取行车数据流的模拟指令
			
			wifiService.connectSuccess();
		} catch (Exception e) {
			Log.d(LOGTAG,"尝试连接失败,原因:"+e.getMessage());
			e.printStackTrace();
			wifiService.connectFailed();
		}
	}
	
	/**
	 * 接收广播要求连接设备的请求
	 */
	@Override
	public void connect(IDevice device) {
		super.connect(device);
		try {
			Log.d(LOGTAG,"接收广播后连接WIFI设备");
			connect();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * 接收广播要求重新连接设备的请求
	 */
	@Override
	public void reConnect(IDevice device) {
		super.reConnect(device);
	}
	
	/**
	 * 执行连接WIFI设备的具体操作
	 */
	public void executeConnect() throws Exception{
		
	}

	/**
	 * 关掉本地设备和远端设备的连接
	 */
	@Override
	public void closeConnect(){
		super.closeConnect();//关掉SOCK 的输入/出流		
		if(null!=socket){
			try {
				socket.close();
				socket = null;
				wifiService.setSocket(null);
				wifiService.setDeviceState(DeviceTools.STATE_NONE);
			} catch (Exception e) {
				Log.d(LOGTAG,"关闭通讯句柄失败,原因:"+e.getMessage());
				e.printStackTrace();
			}
		}
	}

	@Override
	public void connectFailed() {
		super.connectFailed();
		try {
			if(null!=socket)
				socket.close();
				socket = null;
				wifiService.setSocket(null);
				wifiService.setDeviceState(DeviceTools.STATE_NONE);
		} catch (Exception e) {
			Log.d(LOGTAG,"关闭通讯句柄失败,原因:"+e.getMessage());
			e.printStackTrace();
		}
	}
}
