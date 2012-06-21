package com.simple.device.codec.impl;

import android.util.Log;

public class GetStreamCommand extends AbstractDecode{
	private final String LOGTAG = getClass().getSimpleName();
	@Override
	public void decode() {
		/**
		 * 发送广播接收到数据
		 */
		Log.d(LOGTAG,"解析行车数据流");
		for(String s: buffer){
			Log.d(LOGTAG, "param:"+s);
		}
	}
}
