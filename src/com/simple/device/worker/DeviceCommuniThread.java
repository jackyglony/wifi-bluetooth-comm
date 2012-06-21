package com.simple.device.worker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.util.Log;

import com.simple.device.codec.MyProtocolDecoderListener;
import com.simple.device.codec.ProtocolDecoderAdapter;
import com.simple.device.service.IBaseService;


/**
 *  Wifi & 蓝牙　模块通讯使用的工作线程
 *  
 * @author simple
 *
 */
public class DeviceCommuniThread extends Thread {
	
	private final String LOGTAG = getClass().getSimpleName();
	
	private Boolean stopFlag = false;//是否要停掉工作线程
	private Boolean isRun = false;
	private IBaseService baseService;
	
	/**
	 * 从设备读取数据至应用层通道 
	 * 数据流向:	应用层<-蓝牙<-设备
	 */
	private InputStream	inStream;
	
	/**
	 * 向设备发送数据的通道
	 * 数据流向:	应用层->蓝牙->设备
	 */
	private OutputStream outStream;

	public DeviceCommuniThread(IBaseService baseService) {
		super();
		this.baseService = baseService;
	}
	
	/**
	 * 接收设备数据
	 */
	@Override
	public void run() {
		isRun = true;
		try {
			read();// 开始读数据
		} catch (Exception e) {
			// TODO: handle exception
			Log.d(LOGTAG,"读取数据失败,原因:"+e.getMessage());
			e.printStackTrace();
		}
		isRun = false;
	}
	
	/**
	 * 发送数据至设备
	 * @param data
	 */
	public synchronized void write(byte[] buffer){
		Log.d(LOGTAG,"synchronized write");
		
		if(outStream==null) return ;	
		byte[] msg = new byte[buffer.length+4];
		System.arraycopy(buffer, 0, msg, 2, buffer.length);
        msg[0] = msg[1] = '&';
        msg[msg.length -2] = '\r';
        msg[msg.length -1] = '\n';
        
        Log.d(LOGTAG, "[发送指令至设备]:"+new String(msg));
        
        try {
        	outStream.write(msg);
		} catch (Exception e) {
			Log.d(LOGTAG, "发送指令失败,原因: "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * 从设备读取数据
	 * @return
	 * @throws IOException 
	 */
	public void read() throws IOException{
		ProtocolDecoderAdapter adapter = new ProtocolDecoderAdapter();
		adapter.setProtocolDecoderListener(new MyProtocolDecoderListener(baseService));
		if(null != inStream){
			try {
				adapter.decode(inStream);
			} catch (Exception e) {
	            Log.d(LOGTAG, "read -> 读取数据失败,原因:"+e.getMessage());
	            e.printStackTrace();
			}
		} else {
			Log.d(LOGTAG,"用来读取数据的句柄为空");
		}
	}
		
	/**
	 * 解析从设备读取到的数据
	 * @param data
	 * @return
	 */
	 public void decodeBuffer(byte[] buffer){
		 Log.d(LOGTAG,"decodeBuffer() : "+ new String(buffer));
	 }
	 
	 /**
	  * 关掉输入/出流
	  */
	public void closeInOutStream() {
		Log.d(LOGTAG, "正在关闭输入/出流");
		try {
			Log.d(LOGTAG, "关闭输入/出流操作成功");
			inStream.close();
			inStream = null;
			outStream.close();
			outStream = null;
		} catch (Exception e) {
			Log.d(LOGTAG, "关闭输入/出流操作失败; 原因: " + e.getMessage());
			e.printStackTrace();
		}
	}

	//////////////////////////////////////////////////////////////////
	public Boolean getStopFlag() {
		return stopFlag;
	}
	
	public void setStopFlag(Boolean stopFlag) {
		this.stopFlag = stopFlag;
	}
	
	public Boolean getIsRun() {
		return isRun;
	}
	public void setIsRun(Boolean isRun) {
		this.isRun = isRun;
	}

	public InputStream getInStream() {
		return inStream;
	}

	public void setInStream(InputStream inStream) {
		this.inStream = inStream;
	}

	public OutputStream getOutStream() {
		return outStream;
	}

	public void setOutStream(OutputStream outStream) {
		this.outStream = outStream;
	}	
}
