package com.greatsoft.transq.core.task;

public interface TaskQueueProperties {
	//��չ��EG���Ƚ��ȳ�����Чʱ��ȵȻ��ƿ��ƣ����߶�message�����ݵ���չ��
	Object getProperty(String propName);

	void setProperty(String propName, Object value);

	Object getProperty(int index);

	void setProperty(int index, Object value);

}
