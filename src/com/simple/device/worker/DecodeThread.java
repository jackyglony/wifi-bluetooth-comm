package com.simple.device.worker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.simple.device.codec.impl.Command;
import com.simple.device.codec.impl.CommandEnum;



public class DecodeThread implements Runnable {
	private final String LOGTAG = getClass().getSimpleName();
	
	private byte[] buffer;
	private Context context;
	
	public DecodeThread(byte[] buffer, Context context){
		this.buffer = buffer;
		this.context = context;
	}
	
    public void run() {
    	
		String bufferStr = new String(buffer);
		Log.d(LOGTAG, "run: " + bufferStr);
		String[] bufferArr = bufferStr.split("\\$");
		if(bufferArr.length >1){
			String commandName = bufferArr[0];
			String[] buffer = new String[bufferArr.length -1];
			System.arraycopy(bufferArr, 1, buffer, 0, buffer.length);
			CommandEnum commandEnum = CommandEnum.get(commandName);
			if(commandEnum != null){
				Command command = commandEnum.createInstance();
				command.setBuffer(buffer);
				command.setCommandName(commandName);
				command.setContext(context);
				command.decode();
			}
		}else{
			Log.e(LOGTAG, "bufferArr.length<=1: " + bufferStr);
			
//			Intent intent = new Intent(OBDData2PhoneBroadcastReceiver.OBDDATA2PHONE_GET_HISTORY);//异常处理获取历史数据失败发送广播通知
//			Bundle bundle=new Bundle();
//			bundle.putBoolean("result", false);
//			intent.putExtras(bundle);
//			context.sendBroadcast(intent);	
		}
    }
    
}
