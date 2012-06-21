package com.simple.device.worker;

import com.simple.device.IDevice;
import com.simple.device.service.IBaseService;


public interface IDeviceConnectThread {
	
	/**
	 * ���ø����Ӿ���ķ������
	 * @return
	 */
	IBaseService getService();
	
	/**
	 * ���ӳɹ�ʱ֪ͨ�ӿ�
	 */
	void connectSuccess();
	
	/**
	 * ����ʧ��ʱ֪ͨ�ӿ�
	 */
	void connectFailed();
	
	/**
	 * ����ָ�����豸
	 * @param device
	 */
	void connect(IDevice device);
	
	/**
	 * ��������ָ�����豸
	 * @param device
	 */
	void reConnect(IDevice device);
	
	/**
	 * �رյ�ǰ�Ѿ����ӵ��豸
	 */
	void closeConnect();
	
	/**
	 * ���ص�ǰ�̵߳��Ƿ���������״̬
	 * @return
	 */
	Boolean getIsRun();
	
	/**
	 * ȡ�õ�ǰ�߳��Ƿ���ֹͣ״̬
	 * @return
	 */
	Boolean getStopFlag();
	
	
	/**
	 * ȡ������ͨѶ�Ĺ����߳�
	 * @param flag
	 */
	DeviceCommuniThread getCommuniThread();
	void setIsRun(Boolean flag);	
	void setStopFlag(Boolean flag);
	
	
}
