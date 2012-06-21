package com.simple.device.codec;

public interface ProtocolDecoderListener {
	
	public void start(byte[] data);

	public void finished(byte[] data);
}
