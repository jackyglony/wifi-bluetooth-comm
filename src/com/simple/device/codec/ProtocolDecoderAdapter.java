package com.simple.device.codec;

import java.io.InputStream;

import android.util.Log;

public class ProtocolDecoderAdapter{
	private final String LOGTAG = getClass().getSimpleName();
	
	byte[] decodedData;
	byte[] decodeData;
	byte previous;
	byte current;
	
	private ProtocolDecoderListener protocolDecoderListener;
	
	public void decode(InputStream in) throws Exception {
        while (true) {
        	Log.d(LOGTAG, "read");
        	byte[] buffer = new byte[1024];
            int bytes = in.read(buffer);
        	Log.d(LOGTAG, "bytes: "+bytes);
            if(bytes>0){
            	byte[] dbuffer = new byte[bytes];
            	System.arraycopy(buffer, 0, dbuffer, 0, bytes);            	
            	Log.d(LOGTAG,"读到内容:"+new String(dbuffer)); 
            	doDecode(dbuffer);
            }
        }
	}

	public void finishDecode() throws Exception {
		decodedData = new byte[decodeData.length-2];
    	System.arraycopy(decodeData, 0, decodedData, 0, decodedData.length);
		startDecode();
		if(protocolDecoderListener != null) {
			protocolDecoderListener.finished(getDecodedData());
		}
	}

	public void startDecode() throws Exception {
		if(protocolDecoderListener != null) {
			protocolDecoderListener.start(decodeData);
		}
		decodeData = null;
	}
	
	private void doDecode(byte[] dbuffer) throws Exception {
		for(int i=0;i<dbuffer.length;i++){
			current = dbuffer[i];
			if(current != -1){
				addDecodeData(current);
				if(current == '#' && previous == '#'){
					Log.d(LOGTAG,"startDecode");
					startDecode();//清除数据
				}else if(current == '\n' && previous == '\r'){
					Log.d(LOGTAG,"finishDecode");
					finishDecode();
				}
			}
			previous = current;
		}
	}

	private void addDecodeData(byte current) throws Exception {
		if(decodeData == null){
			decodeData = new byte[]{current};
		}else{
			byte[] newDecodedData = new byte[decodeData.length + 1];
	    	System.arraycopy(decodeData, 0, newDecodedData, 0, decodeData.length);
	    	newDecodedData[newDecodedData.length - 1] = current;
	    	decodeData = newDecodedData;
		}
	}

	public byte[] getDecodedData() {
		return decodedData;
	}
	
	
	public static void main(String[] args){
		ProtocolDecoderAdapter protocolDecoderAdapter = new ProtocolDecoderAdapter(){
			@Override
			public void finishDecode(){
				try {
					super.finishDecode();
					byte[] dbuffer = getDecodedData();
					System.out.println();
					for(int i=0; i<dbuffer.length; i++){
						System.out.print(dbuffer[i]+", ");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}				
			}
		};
		byte[][] dbuffer = new byte[3][];
		
		dbuffer[0] = new byte[]{'#', '#', 1, 23, 4, 90, '\r', '\n'};
		dbuffer[1] = new byte[]{'#', '#', 22, 56, 77, 9, '\r', '\n'};
		dbuffer[2] = new byte[]{'&', 'f', 13, 11, 45, 67, 12, '\r', '\n'};
		try {
			for(int i=0; i<dbuffer.length; i++) {
				protocolDecoderAdapter.doDecode(dbuffer[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ProtocolDecoderListener getProtocolDecoderListener() {
		return protocolDecoderListener;
	}

	public void setProtocolDecoderListener(
			ProtocolDecoderListener protocolDecoderListener) {
		this.protocolDecoderListener = protocolDecoderListener;
	}
	
}
