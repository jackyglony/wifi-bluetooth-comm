package com.simple.device.codec;

import com.simple.device.service.IBaseService;

public class MyProtocolDecoderListener implements ProtocolDecoderListener{
	private final String LOGTAG = getClass().getSimpleName();
	private IBaseService baseService;
    
	public MyProtocolDecoderListener(IBaseService baseService){
		this.baseService = baseService;
	}
	@Override
	public void finished(byte[] buffer) {
		if(buffer != null){
			baseService.decodeBuffer(buffer);
		}
	}
	
	@Override
	public void start(byte[] data) {
		
	}	
}
