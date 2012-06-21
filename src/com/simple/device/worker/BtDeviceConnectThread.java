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
	private Set<BluetoothDevice> btDevices=null;//������ƥ��δ���ӵ������豸�б�
	
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
		//�ڴ˴������Զ�����
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
	 * �����豸�������
	 * @param device
	 * @throws Exception
	 */
	public  void executeConnect(BluetoothDevice device) throws Exception{
		
		Log.d(LOGTAG,"[executeConnect]ִ�����������豸, �豸��Ϣ: "+DeviceTools.getDeviceString(device));
		
		int state = bluetoothService.getDeviceState();
		
		/**
		 * ����Ѿ������� �ص�����
		 */
		if(state == DeviceTools.STATE_CONNECTED  || state == DeviceTools.STATE_CONNECTING ){
			Log.d(LOGTAG,"�������Ӵ����Ѿ����ӻ�����������״̬,���ȹص������Ӻ����³�������, ���Ե�...");
			closeConnect();//�ȹص�SOCK���������/����
			try {
				Thread.sleep(2000);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		/**
		 * ����豸û��ƥ��ֱ�ӷ���.
		 * ���ô���Ҫ���ӵ��豸�Ѿ�������Թ���
		 */
		if (BluetoothDevice.BOND_NONE ==device.getBondState())return;	
		
		bluetoothService.setDeviceState(DeviceTools.STATE_CONNECTING);//��ʶΪ����������
		
		/**
		 * ��ʽһ��������
		 */
		Method localMethod = device.getClass().getMethod("createRfcommSocket", new Class[]{Integer.TYPE});
		socket = (BluetoothSocket)localMethod.invoke(device, new Object[]{1});
		
		try {	
			socket.connect();			
			Log.d(LOGTAG,"executeConnect() -> ���������豸��Զ���������ӳɹ�");
			bluetoothService.setDeviceState(DeviceTools.STATE_CONNECTED);	
			if(null==getCommuniThread()){
				DeviceCommuniThread communiThread = new DeviceCommuniThread(bluetoothService);
				setCommuniThread(communiThread);
			}
			
			if(null!=getCommuniThread() && !getCommuniThread().getIsRun()){
				getCommuniThread().setInStream(socket.getInputStream());//��ʼ�� ������
				getCommuniThread().setOutStream(socket.getOutputStream()); //��ʼ�������
				getCommuniThread().start();//��ʼ��������
			}
			
			/**
			 * ��������������ʱ��Ҫɾ��
			 */
			Log.d(LOGTAG, "��ʼ���Ͷ�ȡ�г�������ָ��");
			new SendGetStreamThread(bluetoothService).start();//��ʼ���Ͷ�ȡ�г���������ģ��ָ��
			
			//�������ӳɹ��Ĺ㲥
			bluetoothService.connectSuccess();
			
		} catch (Exception e) {
			Log.d(LOGTAG,"��������ʧ��,ԭ��:"+e.getMessage());	
			Log.d(LOGTAG,"��������ʧ��,�豸��:"+DeviceTools.getDeviceString(device));			
			e.printStackTrace();
			
			bluetoothService.setDeviceState(DeviceTools.STATE_NONE);//û������
			bluetoothService.connectFailed();//���͹㲥��Ϣ: ����ʧ��,ʧ��ԭ������Ϣ��
		}
	}
	
	/**
	 * �ص������豸��Զ���豸������
	 */
	@Override
	public void closeConnect(){
		super.closeConnect();//�ص�����/����		
		if(null!=socket){
			try {
				socket.close();
				socket = null;
				bluetoothService.setSocket(null);	
				bluetoothService.setDeviceState(DeviceTools.STATE_NONE);
			} catch (Exception e) {
				Log.d(LOGTAG,"�ر�ͨѶ���ʧ��,ԭ��:"+e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * ����ʧ��ʱ�ص���Ӧ�Ĳ���
	 */
	@Override
	public void connectFailed()
	{
		super.connectFailed();
		Log.d(LOGTAG, "����ʧ��,���ر������Ȳ���");
		try {
			socket.close();
			socket = null;
			bluetoothService.setSocket(null);	
			bluetoothService.setDeviceState(DeviceTools.STATE_NONE);
		} catch (Exception e) {
			// TODO: handle exception
			Log.d(LOGTAG,"�ر�ͨѶ���ʧ��,ԭ��:"+e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * ���������豸
	 */
	@Override
	public void connect(IDevice device) {
		super.connect(device);
		Log.d(LOGTAG, "ִ�����������豸����");
		BluetoothDevice btDevice= getBtDeviceByAddress(device.getMacAddress());
		if(null!=btDevice){
			try {
				executeConnect(btDevice);
			} catch (Exception e) {
				Log.d(LOGTAG,"���������豸ʧ��,ԭ��: "+e.getMessage());
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
	 * ����MAC��ַ��ȡ�����豸
	 */
	public BluetoothDevice getBtDeviceByAddress(String macAddress){
		BluetoothDevice btDevice = null;
		if(BluetoothAdapter.checkBluetoothAddress(macAddress)){	
			btDevice = (BluetoothAdapter.getDefaultAdapter().getRemoteDevice(macAddress));
		}
		return btDevice;
	}
	
}
