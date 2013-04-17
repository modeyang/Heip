package com.greatsoft.transq.core.task;

import com.greatsoft.transq.exception.TaskQueueException;
import com.greatsoft.transq.utils.ConstantValue;

public class TaskQueueHelper {

	public static Task getFirstOne(TaskQueue taskQueue, int processPriorityMode)
			throws TaskQueueException {
		switch (processPriorityMode) {
		case ConstantValue.MESSAGE_PROCESS_NO_PRIORITY:
			return taskQueue.getFirstOne(ConstantValue.TASK_UNPROCESSED);
		case ConstantValue.MESSAGE_PROCESS_PRIORITY:
			return taskQueue.getFirstOneByPriority(ConstantValue.TASK_UNPROCESSED);
		case ConstantValue.MESSAGE_PROCESS_TIME_PRIORITY:
			return taskQueue.getFirstOneByDeadTime(ConstantValue.TASK_UNPROCESSED);
		}
		return null;
	}
}
