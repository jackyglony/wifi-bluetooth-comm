package com.simple.device.codec.impl;

import android.content.Context;

public abstract class AbstractDecode implements Command {
	
	protected Context context;
	protected String[] buffer;
	protected String commandName;
	
	@Override
	public void setCommandName(String commandName) {
		this.commandName = commandName;
	}

	@Override
	public void setContext(Context context) {
		this.context = context;
	}

	@Override
	public void setBuffer(String[] buffer) {
		this.buffer = buffer;
	}

	@Override
	public abstract void decode();

}
