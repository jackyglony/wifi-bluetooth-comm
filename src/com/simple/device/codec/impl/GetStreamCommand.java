package com.simple.device.codec.impl;

import android.util.Log;

public class GetStreamCommand extends AbstractDecode{
	private final String LOGTAG = getClass().getSimpleName();
	@Override
	public void decode() {
		/**
		 * ���͹㲥���յ�����
		 */
		Log.d(LOGTAG,"�����г�������");
		for(String s: buffer){
			Log.d(LOGTAG, "param:"+s);
		}
	}
}
