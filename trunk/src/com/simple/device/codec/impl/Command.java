package com.simple.device.codec.impl;

import android.content.Context;

public interface Command {
	void setContext(Context context);
	void setBuffer(String[] buffer);
	void setCommandName(String commandName);
	void decode();
}
