package com.greatsoft.transq.core.task;

import com.greatsoft.transq.core.message.Envelope;

public class TaskHelper {

	public static Task getNewTak(Envelope envelope, String nextHop) {
		return new TaskImp(envelope, nextHop);
	}
}
