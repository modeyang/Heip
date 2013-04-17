package com.greatsoft.transq.core.task;

public interface TaskQueueProperties {
	//扩展（EG：先进先出，生效时间等等机制控制，或者对message的内容的扩展）
	Object getProperty(String propName);

	void setProperty(String propName, Object value);

	Object getProperty(int index);

	void setProperty(int index, Object value);

}
