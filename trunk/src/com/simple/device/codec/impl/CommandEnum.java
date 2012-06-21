package com.simple.device.codec.impl;

public enum CommandEnum {
	GET_RUNNING("stream", GetStreamCommand.class, "行车数据解析"),	
	;
	
	private String command;
	private Class<? extends Command> clazz;
	private String desc;

	CommandEnum(String command, Class<? extends Command> clazz, String desc) {
		this.command = command;
		this.clazz = clazz;
		this.setDesc(desc);
	}

	public static CommandEnum get(String value) {
		for (CommandEnum command : values()) {
			if (command.getCommand().equals(value)) {
				return command;
			}
		}
		return null;
	}
	
	public String getCommand() {
		return command;
	}
	
	public Command createInstance(){
		if (clazz == null){
			return null;
		}
		Command c = null;
		
		try {
			c = clazz.newInstance();
		} catch (Exception e) {
		}
		return c;
		
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
