package com.simple.device.worker;

import java.lang.reflect.Method;
import java.util.Set;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.simple.device.IDevice;
import com.simple.device.service.BluetoothService;
import com.simple.device.util.DeviceTools;

public class BtDeviceConnectThread extends AbsoluteDeviceConnectThread {	
	
	private BluetoothService bluetoothService;
	private BluetoothSocket socket;
	private Set<BluetoothDevice> btDevices=null;//所有已匹配未连接的蓝牙设备列表
	
	public BtDeviceConnectThread(BluetoothService bluetoothService) {
		super();
		this.bluetoothService = bluetoothService;
	}
	
	public BtDeviceConnectThread(BluetoothService bluetoothService,
			Set<BluetoothDevice> btDevices) {
		super();
		this.bluetoothService = bluetoothService;
		this.btDevices = btDevices;
	}
	
	@Override
	public void run() {
		//在此处尝试自动连接
		//BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address) //address is mac address
		isRun = true;
		int tryMaxCount = 3;
		int tryCount = 0;
		
		if(null !=btDevices)
			Log.d(LOGTAG,"btDevices.size: "+btDevices.size());
		
		while(!stopFlag 
				&& DeviceTools.STATE_CONNECTED != bluetoothService.getDeviceState()
				&& tryCount<tryMaxCount
				&&null!=btDevices 
				&&btDevices.size()>0){	
			for(BluetoothDevice device : btDevices){
				try {
					executeConnect(device);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			tryCount++;
		}
		isRun = false;
	}
	
	/**
	 * 连接设备具体操作
	 * @param device
	 * @throws Exception
	 */
	public  void executeConnect(BluetoothDevice device) throws Exception{
		
		Log.d(LOGTAG,"[executeConnect]执行连接蓝牙设备, 设备信息: "+DeviceTools.getDeviceString(device));
		
		int state = bluetoothService.getDeviceState();
		
		/**
		 * 如果已经连接上 关掉连接
		 */
		if(state == DeviceTools.STATE_CONNECTED  || state == DeviceTools.STATE_CONNECTING ){
			Log.d(LOGTAG,"蓝牙连接处于已经连接或正在连接中状态,将先关掉该连接后重新尝试连接, 请稍等...");
			closeConnect();//先关掉SOCK句柄和输入/出流
			try {
				Thread.sleep(2000);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		/**
		 * 如果设备没有匹配直接返回.
		 * 即该处需要连接的设备已经做过配对工作
		 */
		if (BluetoothDevice.BOND_NONE ==device.getBondState())return;	
		
		bluetoothService.setDeviceState(DeviceTools.STATE_CONNECTING);//标识为正在连接中
		
		/**
		 * 方式一连接蓝牙
		 */
		Method localMethod = device.getClass().getMethod("createRfcommSocket", new Class[]{Integer.TYPE});
		socket = (BluetoothSocket)localMethod.invoke(device, new Object[]{1});
		
		try {	
			socket.connect();			
			Log.d(LOGTAG,"executeConnect() -> 本地蓝牙设备和远端蓝牙连接成功");
			bluetoothService.setDeviceState(DeviceTools.STATE_CONNECTED);	
			if(null==getCommuniThread()){
				DeviceCommuniThread communiThread = new DeviceCommuniThread(bluetoothService);
				setCommuniThread(communiThread);
			}
			
			if(null!=getCommuniThread() && !getCommuniThread().getIsRun()){
				getCommuniThread().setInStream(socket.getInputStream());//初始化 输入流
				getCommuniThread().setOutStream(socket.getOutputStream()); //初始化输出流
				getCommuniThread().start();//开始接收数据
			}
			
			/**
			 * 下面两行在商用时需要删除
			 */
			Log.d(LOGTAG, "开始发送读取行车数据流指令");
			new SendGetStreamThread(bluetoothService).start();//开始发送读取行车数据流的模拟指令
			
			//发送连接成功的广播
			bluetoothService.connectSuccess();
			
		} catch (Exception e) {
			Log.d(LOGTAG,"尝试连接失败,原因:"+e.getMessage());	
			Log.d(LOGTAG,"尝试连接失败,设备号:"+DeviceTools.getDeviceString(device));			
			e.printStackTrace();
			
			bluetoothService.setDeviceState(DeviceTools.STATE_NONE);//没有连接
			bluetoothService.connectFailed();//发送广播消息: 连接失败,失败原因在消息中
		}
	}
	
	/**
	 * 关掉本地设备和远端设备的连接
	 */
	@Override
	public void closeConnect(){
		super.closeConnect();//关掉输入/出流		
		if(null!=socket){
			try {
				socket.close();
				socket = null;
				bluetoothService.setSocket(null);	
				bluetoothService.setDeviceState(DeviceTools.STATE_NONE);
			} catch (Exception e) {
				Log.d(LOGTAG,"关闭通讯句柄失败,原因:"+e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 连接失败时关掉相应的操作
	 */
	@Override
	public void connectFailed()
	{
		super.connectFailed();
		Log.d(LOGTAG, "连接失败,做关闭名柄等操作");
		try {
			socket.close();
			socket = null;
			bluetoothService.setSocket(null);	
			bluetoothService.setDeviceState(DeviceTools.STATE_NONE);
		} catch (Exception e) {
			// TODO: handle exception
			Log.d(LOGTAG,"关闭通讯句柄失败,原因:"+e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * 连接蓝牙设备
	 */
	@Override
	public void connect(IDevice device) {
		super.connect(device);
		Log.d(LOGTAG, "执行连接蓝牙设备操作");
		BluetoothDevice btDevice= getBtDeviceByAddress(device.getMacAddress());
		if(null!=btDevice){
			try {
				executeConnect(btDevice);
			} catch (Exception e) {
				Log.d(LOGTAG,"连接蓝牙设备失败,原因: "+e.getMessage());
				e.printStackTrace();
			}
		} else {
			bluetoothService.setDeviceState(DeviceTools.STATE_NONE);
			bluetoothService.connectFailed();
		}
	}

	@Override
	public void reConnect(IDevice device) {
		super.reConnect(device);
	}
	
	/**
	 * 根据MAC地址获取蓝牙设备
	 */
	public BluetoothDevice getBtDeviceByAddress(String macAddress){
		BluetoothDevice btDevice = null;
		if(BluetoothAdapter.checkBluetoothAddress(macAddress)){	
			btDevice = (BluetoothAdapter.getDefaultAdapter().getRemoteDevice(macAddress));
		}
		return btDevice;
	}
	
}
