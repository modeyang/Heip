package com.greatsoft.transq.utils;

import java.util.List;
import java.util.Map;

import com.greatsoft.transq.core.Address;
import com.greatsoft.transq.core.RouterRecord;

/** 配置类 */
public class Config {
	/**存取路由记录*/
	public static List<RouterRecord> routerRecordList = null;
	/** 存储业务地址与具体详细地址对象的 */
	public static Map<String, Address> addressMap = null;
	/**存储业务处理系统映射关系*/
	public static Map<String, String> processorNameMap = null;
	/**本节点的HIEP交换端接收消息的SERVER地址*/
	public static Address[] localServerAddress = null;
	
/**********************HIEP地址信息配置小节********************************/	
	public static String HIEP_HOME_PATH = null;
	/** 本节点的HIEP交换端地址名字 */
	public static String LOCAL_ADDRESS = null;
	/**本节点的HIEP交换端接收消息的SERVER的个数，可配置为1到10之间的任意数字*/
	public static int LOCAL_SERVER_ADDRESS_NUM=-1;
	/**本节点的HIEP交换端接收消息的SERVER的名字，多个接收消息的SERVER的地址之间以“;”隔开*/
	public static String LOCAL_SERVER_ADDRESS = null;
	
/**********************任务记录队列配置小节********************/
	/** 为任务记录队列管理容器的名字,需要安装数据库Sqlite时由安装人员给定*/
	public static String TASK_QUEUE_CONTAINER_NAME="TaskQueueContainer";
	/**为router使用的存放任务记录队列的个数*/
	public static int TASK_QUEUE_COUNT = 2;
	/**为当TASK_QUEUE_TABLE_COUNT=1时,任务记录队列的名字*/
	public static String TASK_QUEUE_NAME = "taskQueue";
	/**为当TASK_QUEUE_TABLE_COUNT=1时,本地任务记录队列的名字*/
	public static String TASK_QUEUE_LOCAL_NAME = "taskQueueLocal";
	/**为当TASK_QUEUE_TABLE_COUNT=1时,远程任务记录队列的名字*/
	public static String TASK_QUEUE_REMOTE_NAME = "taskQueueRemote";
	/** 为列举任务记录的个数，作为分页的最大上限控制*/
	public static int MAX_TASK_LIST_COUNT = 1000;
	
/**********************消息队列目录配置小节********************/
	/**为接收信息队列的目录*/
	public static String RECEIVED_MESSAGE_FILE_DIRECTORY = null;
	/**为新消息队列的目录*/
	public static String NEW_MESSAGE_FILE_DIRECTORY= null;
	/**为超时消息队列的目录*/
	public static String EXPIRED_TIME_MESSAGE_FILE_DIRECTORY= null;
	/**错误消息队列的目录*/
	public static String ERROR_MESSAGE_FILE_DIRECTORY= null;
	/**为发送信息队列的目录*/
	public static String SEND_MESSAGE_FILE_DIRECTORY= null;
	
/**********************系统运行硬件环境配置小节********************/
	/**为项目启动时最小的磁盘空间（单位为GB）*/
	public static long MIN_DISK_SPACE = 5* 1024 * 1024*1024;
	/** 项目运行时最小剩余磁盘空间*/
	public static long MIN_FREE_DISK_SPACE = 100 * 1024 * 1024;
	/**线程并发上限的个数*/
	public static int THREAD_NUMBER = 10;
	/**为远程关闭HIEP交换端工作的Router模块socket监听端口，必须配置为1025至65535之间的任意数字*/
	public static int ROUTER_PORT = 56789;
	/**为远程关闭HIEP交换端工作的Dispatcher模块socket监听端口，必须配置为1025至65535之间的任意数字*/
	public static int DISPATCHER_PORT = 56780;
	
/**********************系统轮循时间配置小节********************/
	/** 为ROUTER线程的轮循时间间隔（单位为毫秒） */
	public static int ROUTER_PROCESSING_INTERVAL = 50;
	/** 为DISPARTER线程的轮循时间间隔（单位为毫秒） */
	public static int DISPARTER_PROCESSING_INTERVAL = 50;
	
/**********************消息处理配置小节********************/
	/**为接收信息的优先级模式，1代表按照进入任务队列的顺序处理消息，2代表按照消息优先级顺序处理消息 ，3代表按照失效时间最短的顺序处理消息*/
	public static int RECEIVE_MESSAGE_PRIORITY_MODE=1;
	/**为处理本地信息的优先级模式,1代表按照进入任务队列的顺序处理消息，2代表按照消息优先级顺序处理消息 ，3代表按照失效时间最短的顺序处理消息*/
	public static int LOCAL_MESSAGE_PROCESS_PRIORITY_MODE=1;
	/**为处理远程信息的优先级模式,1代表按照进入任务队列的顺序处理消息，2代表按照消息优先级顺序处理消息 ，3代表按照失效时间最短的顺序处理消息*/
	public static int REMOTE_MESSAGE_PROCESS_PRIORITY_MODE=1;
	/**为缺省的消息的相对失效时间（单位为分钟）*/
	public static int RELATIVE_EXPIRED_TIME=60;
	/**为HIEP的交换回执开关，1代表无回执，2代表有回执*/
	public static int EXCHANGE_TRACE_MODE = 1;
	
	public static String DELETE_MESSAGE_FILE_DIRECTORY = null;
}
