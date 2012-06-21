package com.simple;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.ecarinfo.R;
import com.simple.broadcast.ConnectionChangeReceiver;
import com.simple.device.service.BluetoothService;
import com.simple.device.service.WifiService;
import com.simple.device.util.DeviceTools;

/**
 * 
 * 	����״̬�仯���
 * http://blog.csdn.net/lvron/article/details/7098771
 * 
 * Wifi�㲥״̬
 * http://wang-peng1.iteye.com/blog/649500
 * 
 * 
 * androidѧϰ֮wifimanager
 * http://daixj110.iteye.com/blog/1133039
 * 
 * @author Administrator
 *
 */
public class MainActivity extends Activity implements OnClickListener {
	private Button startBtn;
	private Button stopBtn;
	
	private Button startWifi;
	private Button stopWifi;	
	private Button swifiBtn;
	private WifiManager wm;
	private ConnectionChangeReceiver connectionChangeReceiver;
	private Intent bt_intent;
	private Intent wifi_intent;
	private EditText logtext;
	
	private WifiManager wifiManager;
	private List<WifiConfiguration> wifiConfiguration; 
	private WifiInfo currentWifiInfo; 
	
	
	private Button connectDeviceBtn;
	
	private BroadcastReceiver myReceiver = new BroadcastReceiver() {		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(DeviceTools.ACTION_DEVICE_DATA_GET.equals(action)){
				//��������,������Ϣ,��handler���½���
				String data = intent.getStringExtra(DeviceTools.EXTRA_DEVICE_DATA);
				Bundle bdata = new Bundle();
				bdata.putString("text", data);
				Message msg = myHandler.obtainMessage();				
				msg.setData(bdata);
				msg.what =1;
				myHandler.sendMessage(msg);
			}
		}
	};
	
	private Handler myHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				String tmp_str = msg.getData().getString("text")+"\r\n"+logtext.getText().toString();				
				logtext.setText(tmp_str);
				break;
			default:
				break;
			}
		}		
	};
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testservice);
        
        startBtn = (Button) findViewById(R.id.start);
        stopBtn = (Button) findViewById(R.id.stop);
        
        startWifi = (Button) findViewById(R.id.startwifi);
        stopWifi = (Button) findViewById(R.id.stopwifi);
        
        startBtn.setOnClickListener(this);
        stopBtn.setOnClickListener(this);
        
        startWifi.setOnClickListener(this);
        stopWifi.setOnClickListener(this); 
        
        bt_intent = new Intent(this,BluetoothService.class);
        wifi_intent = new Intent(this,WifiService.class);
        
        
        connectDeviceBtn = (Button) findViewById(R.id.connectDevice);
//        connectDeviceBtn.setOnClickListener(this);
        
        logtext = (EditText) findViewById(R.id.log);
        
        swifiBtn = (Button) findViewById(R.id.swifi);
        swifiBtn.setOnClickListener(this);
        
        //ע��㲥
        IntentFilter myfilter = new IntentFilter();
        myfilter.addAction(DeviceTools.ACTION_DEVICE_DATA_GET);
        registerReceiver(myReceiver, myfilter);
        
        
        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub		
		switch (v.getId()) {
		case R.id.start:
			System.out.println(" click start bluetooth service");	
			startService(bt_intent);
			break;
		case R.id.stop:
			System.out.println(" click stop bluetooth service");	
			stopService(bt_intent);
			break;
		case R.id.startwifi:
			startService(wifi_intent);
			break;
		case R.id.stopwifi:
			stopService(wifi_intent);
			break;
		case R.id.connectDevice:
			System.out.println("��ʼ���з��͹㲥������ָ�����豸");
				Intent intent = new Intent(DeviceTools.ACTION_DEVICE_CONTROL_CONNECT);
				intent.putExtra(DeviceTools.EXTRA_DEVICE_CONNECT_MAC, "00:19:5D:26:2E:27");
				getApplicationContext().sendBroadcast(intent);
			break;
		case R.id.swifi:
			currentWifiInfo = wifiManager.getConnectionInfo();
			System.out.println("��ǰWIFI AP name:"+currentWifiInfo.getSSID());
			for(WifiConfiguration wificfg:wifiConfiguration){				
				if(currentWifiInfo.getSSID().equals("xlw_1d9d")
						&&wificfg.SSID.equals("\"Ecarinfo\"") 
						&& !currentWifiInfo.getSSID().equals("\"Ecarinfo\"")
						){
					System.out.println(" WIFI �л� xlw_1d9d->>> Ecarinfo");
					switchWifi(wificfg.networkId);
				} else if(currentWifiInfo.getSSID().equals("Ecarinfo")
						&&wificfg.SSID.equals("\"xlw_1d9d\"") 
						&& !currentWifiInfo.getSSID().equals("\"xlw_1d9d\"")){
					switchWifi(wificfg.networkId);
					System.out.println(" WIFI �л� xlw_1d9d <<<- Ecarinfo");
				}
			}
			break;
		default:
			break;
		}
	}	
	
	/**
	 * �л�WIFI
	 * @param wifiCfg
	 */
	public void switchWifi(int networkId){	
		boolean b = wifiManager.enableNetwork(networkId, true);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent startMain = new Intent(Intent.ACTION_MAIN);
			startMain.addCategory(Intent.CATEGORY_HOME);
			startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(startMain);
			System.exit(0);	
		}
		return false;
	}

	@Override
	protected void onStart() {
		currentWifiInfo = wifiManager.getConnectionInfo();
		wifiConfiguration = wifiManager.getConfiguredNetworks();		
		System.out.println("activity  Wifi  AP name:" + currentWifiInfo.getSSID());		
		super.onStart();
	}

	@Override
    public void onConfigurationChanged(Configuration newConfig) {
		System.out.println("--onConfigurationChanged--");
		System.out.println("�����ڴ˴��л�UI�����ļ�");
		
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            System.out.println("����");
        }else{
            System.out.println("����");
        }        
    }	
	
	@Override
	protected void onDestroy() {
		
		stopService(bt_intent);
		stopService(wifi_intent);
		unregisterReceiver(myReceiver);
		
		super.onDestroy();
	}
}