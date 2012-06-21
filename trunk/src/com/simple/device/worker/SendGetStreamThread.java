package com.simple.device.worker;

import com.simple.device.service.IBaseService;

public class SendGetStreamThread extends Thread {
	private IBaseService baseservice;
	
	public SendGetStreamThread(IBaseService baseservice) {
		super();
		this.baseservice = baseservice;
	}

	@Override
	public void run() {
		int i = 0;
		byte[] buffer= "stream".getBytes();
		while(i<5){
			try {	
				System.out.println("·¢ËÍÖ¸ÁîÖÐ: "+i);
				Thread.sleep(2000);
				baseservice.write(buffer);
				i++;
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
}
