package com.greatsoft.transq.core;

public interface Parameters {
	Object getValue(String paramName);

	void setValue(String paramName, Object value);

	int getItemCount();

	String[] listItemNames();
}
