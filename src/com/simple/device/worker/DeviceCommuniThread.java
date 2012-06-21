package com.simple.device.worker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.util.Log;

import com.simple.device.codec.MyProtocolDecoderListener;
import com.simple.device.codec.ProtocolDecoderAdapter;
import com.simple.device.service.IBaseService;


/**
 *  Wifi & ������ģ��ͨѶʹ�õĹ����߳�
 *  
 * @author simple
 *
 */
public class DeviceCommuniThread extends Thread {
	
	private final String LOGTAG = getClass().getSimpleName();
	
	private Boolean stopFlag = false;//�Ƿ�Ҫͣ�������߳�
	private Boolean isRun = false;
	private IBaseService baseService;
	
	/**
	 * ���豸��ȡ������Ӧ�ò�ͨ�� 
	 * ��������:	Ӧ�ò�<-����<-�豸
	 */
	private InputStream	inStream;
	
	/**
	 * ���豸�������ݵ�ͨ��
	 * ��������:	Ӧ�ò�->����->�豸
	 */
	private OutputStream outStream;

	public DeviceCommuniThread(IBaseService baseService) {
		super();
		this.baseService = baseService;
	}
	
	/**
	 * �����豸����
	 */
	@Override
	public void run() {
		isRun = true;
		try {
			read();// ��ʼ������
		} catch (Exception e) {
			// TODO: handle exception
			Log.d(LOGTAG,"��ȡ����ʧ��,ԭ��:"+e.getMessage());
			e.printStackTrace();
		}
		isRun = false;
	}
	
	/**
	 * �����������豸
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
        
        Log.d(LOGTAG, "[����ָ�����豸]:"+new String(msg));
        
        try {
        	outStream.write(msg);
		} catch (Exception e) {
			Log.d(LOGTAG, "����ָ��ʧ��,ԭ��: "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * ���豸��ȡ����
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
	            Log.d(LOGTAG, "read -> ��ȡ����ʧ��,ԭ��:"+e.getMessage());
	            e.printStackTrace();
			}
		} else {
			Log.d(LOGTAG,"������ȡ���ݵľ��Ϊ��");
		}
	}
		
	/**
	 * �������豸��ȡ��������
	 * @param data
	 * @return
	 */
	 public void decodeBuffer(byte[] buffer){
		 Log.d(LOGTAG,"decodeBuffer() : "+ new String(buffer));
	 }
	 
	 /**
	  * �ص�����/����
	  */
	public void closeInOutStream() {
		Log.d(LOGTAG, "���ڹر�����/����");
		try {
			Log.d(LOGTAG, "�ر�����/���������ɹ�");
			inStream.close();
			inStream = null;
			outStream.close();
			outStream = null;
		} catch (Exception e) {
			Log.d(LOGTAG, "�ر�����/��������ʧ��; ԭ��: " + e.getMessage());
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
