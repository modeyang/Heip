package com.greatsoft.transq.utils;

import java.nio.ByteOrder;

public class ConstantValue {

	/** Main中，线程组的名字 */
	public static final String threadGroupName = "HiepThreadGroup";
	/** Dispatcher用户日志的名字 */
	public static final String DISPATCHER_USER_LOG = "userLogDis";
	
	/** Router用户日志的名字 */
	public static final String ROUTER_USER_LOG = "userLogRouter";

	/** 操作系统对应的编号：未知操作系统=0 */
	public static final int OS_UNKNOW = 0;
	/** 操作系统对应的编号：WINDOWS 操作系统=1 */
	public static final int OS_WINDOWS = 1;
	/** 操作系统对应的编号：UNIX 操作系统=2 */
	public static final int OS_UNIX = 2;

	public static final String COLON = ":";
	public static final String EQUAL_OPERATOR = "=";
	public static final String NULL_STRING = "";
	public static final String NUMBER_STRING = "[0-9]*";
	public static final String MORE_PATH_STRING = "//";
	public static final String TAB_OR_SPACE = "(" + (char) 32 + "|" + (char) 9
			+ ")+";

	/** 信息地址分隔符 */
	public static final String ADDRESS_SEPARATOR = ";|；";
	/** 信息地址分隔符 */
	public static final String ADDRESS_SEPARATOR_ONE = ";";
	/** 一条地址内的数据部门分隔符 */
	public static final String SINGLE_ADDRESS_SPLIT_STRING = ",|，";
	/** 一条地址内的数据处理部门和HIEP交换节点地址分隔符 */
	public static final String PARSE_SINGLE_ADDRESS_STRING = "@";
	/** HIEP节点相关的所有数据处理部门 */
	public static final String ALL_DEPARTMENT_STRING = "*";

	public static final String UUID_SEPERATER = "_";

	public static final String TLQ_ADDRESS_BEGIN_STRING = "tlq://";
	public static final String TLQ = "tlq:";

	/** 获取注册表HIEP_HOME_PATH的相关字段 */
	public static final String SOFTWARE = "SOFTWARE";
	public static final String HIEP = "HIEP";
	public static final String CURRENT_VERSION = "CurrentVersion";
	public static final String HIEP_HOME_PATH = "hiepHomePath";
	public static final String HIEP_HOME = "HIEP_HOME";

	/** 本地信息 */
	public static final String LOCAL = "本地";
	/** 远程信息 */
	public static final String REMOTE = "远程";

	/** 消息处理优先级，MESSAGE_PROCESS_NO_PRIORITY=按照先进先出进行处理 */
	public static final int MESSAGE_PROCESS_NO_PRIORITY = 1;
	/** 消息处理优先级，MESSAGE_PROCESS_NO_PRIORITY=按照消息优先级进行处理 */
	public static final int MESSAGE_PROCESS_PRIORITY = 2;
	/** 消息处理优先级，MESSAGE_PROCESS_NO_PRIORITY=按照最快失效消息进行处理 */
	public static final int MESSAGE_PROCESS_TIME_PRIORITY = 3;

	/** 未处理的任务记录 */
	public static final int TASK_UNPROCESSED = 0x00000001;
	/** 正在处理的任务记录 */
	public static final int TASK_PROCESSING = 0x00000002;
	/** 处理异常中断的任务记录 */
	public static final int TASK_INTERRUPT = 0x00000003;
	/** 超时的任务记录 */
	public static final int TASK_EXPIRED_TIME = 0x00000004;

	/** 发送消息结果代码：发送成功 */
	public static final int PROCESS_SUCCESS = 0x00000000;
	public static final int PROCESS_FAILED = 0x00000001;

	public static final String PROCESS_SUCCESS_INFO = "消息发送成功";
	public static final String PROCESS_FAILED_INFO = "消息发送失败";

	public static final String MESSAGE_VERSION = "01.01";

	public static final String LITTLE_FILE = "0000";

	public static final String DATEFORMAT = "yyyyMMddHHmmss";

	public static final String HIEP_MESSAGE_ENCODING = "UTF_16";

	public static final String HIEP_MESSAGE_ID_STRING = "HIEP_MESSAGE";

	public static final ByteOrder HIEP_MESSAGE_BYTEORDER = ByteOrder.LITTLE_ENDIAN;

	public static final String HIEP_MESSAGE_TO_BYTEBUFFER_BYTEORDER_STRING = "LITTLE_ENDIAN";

	public static final String CONNECTION_URL = "jdbc:sqlite:";

	public static final String JDBC_URL = "org.sqlite.JDBC";

	public static final int HIEP_STOP_TIMEOUT = 10000;

	/** 数据库监听锁 */
	public static final int DB_PORT = 5909;

	public static final String DISPATCHER_COMMAND_PROCESSOR_NAME = "dispatcherCommandProcessor";

	public static final String DELETE_FILE_WORKER_NAME = "deleteFileWorker";

	public static final String DISPATCHER_SERVER_PROCESS_NAME = "dispatcherProcess";

	public static final String DISPATCHER_SERVER_ROUTE_NAME = "dispatcherRoute";

	public static final String ROUTER_SERVER_NAME = "routerServerThread";

	public static final String ROUTER_COMMAND_PROCESS_NAME = "routerCommandProcessThread";
	// /** 临时删除目录 */
	// public static final String DELETE_MESSAGE_DIR =
	// "data/message/deleteMessage/";

	/** HIEP_config.ini */
	public static final String HIEP_CONFIG_FILE_NAME = "HIEP_config.ini";
	/** HIEP_config_custom.ini */
	public static final String HIEP_CONFIG_CUSTOM_FILE_NAME = "HIEP_config_ad.ini";
	/** HIEP_routerTable.ini：为路由表配置文件 */
	public static final String ROUTER_TABLE_FILE_NAME = "HIEP_routerTable.ini";
	/** 为数据处理部门与业务系统映射规则的配置文档 */
	public static final String PROCESSOR_MAP_FILE_NAME = "HIEP_processorMap_syn.ini";
	/** 为传输层物理映射规则配置文档 */
	public static final String ADDRESS_MAP_FILE_NAME = "HIEP_addressMap.ini";
	/** 为Router模块的日志配置文件 */
	public static final String ROUTER_LOG4J_PROPERTIES_FILE_NAME = "routerlog4j.properties";
	/** 为Dispatcher模块的日志配置文件 */
	public static final String DISPARTER_LOG4J_PROPERTIES_FILE_NAME = "dispatcherlog4j.properties";
	/** 为Router模块的PID文件 */
	public static final String ROUTER_PID_FILE_NAME = "router.pid";
	/** 为Dispatcher模块的PID文件 */
	public static final String DISPATCHER_PID_FILE_NAME = "dispatcher.pid";

	/** 为配置文件的相对目录 */
	public static String CONFIG_DIRECTORY = "config/";
	/** 第三方JAR包存放相对路径 */
	public static final String EXT_DIRECTORY = "ext/";
	/** 记录日志的相对目录 */
	public static final String LOG_DIRECTORY = "data/log/";
	/** 为接收信息队列的相对目录 */
	public static final String RELATIVE_RECEIVED_MESSAGE_FILE_DIRECTORY = "data/message/received/";
	/** 为新消息队列的相对目录 */
	public static final String RELATIVE_NEW_MESSAGE_FILE_DIRECTORY = "data/message/new/";
	/** 为超时消息队列的相对目录 */
	public static final String RELATIVE_EXPIRED_TIME_MESSAGE_FILE_DIRECTORY = "data/message/expiredTime/";
	/** 错误消息队列的相对目录 */
	public static final String RELATIVE_ERROR_MESSAGE_FILE_DIRECTORY = "data/message/error/";
	/** 为发送信息队列的相对目录 */
	public static final String RELATIVE_SEND_MESSAGE_FILE_DIRECTORY = "data/message/send/";
	/** 为删除消息队列的相对目录 */
	public static final String DELETE_MESSAGE_FILE_DIRECTORY = "data/message/delete/";

	public static final String ENTER = "\n";
	public static final String PATH = ":path=";
	public static final String LINE = ":line=";
	public static final String ADDURL = "addURL";
	public static final String ADDRESS = "，address=";
	public static final String CLASSNAME = "className=";
	public static final String DATANAME_CONNECTION = "./";
	public static final String TYPE = "，type=";
	public static final String SUBADRESS = "，subAddress=";
	public static final String JAR = ".jar";
	public static final String USE_DEFAULT_CONFIGURATION = "使用默认配置：";
	public static final String THREAD_NAME = "的routerServer线程";
	public static final String THREAD_GROUP_NAME = "路由处理模块线程家族";
	public static final long THRED_GET_WRITE_DATABASE_INTERVAL = 20;

	/** 发送消息结果，成功 */
	public static final int PUT_MESSAGE_SUCCESS = 0x00000000;
	/** 发送消息结果，失败 */
	public static final int PUT_MESSAGE_FAILED = 0x00000001;
	/** 从消息中间件获取消息的有效等待时间 */
	public static final int WAIT_TIME = 30000;

	/** webservice常用配置 **/
	public static final String PROCESSOR_MAP_FILE = "config/HIEP_processorMap_ws.ini";

	public static final String WS_CONFIG_FILE = "config/HIEP_WebServiceConfig.ini";
	
	public static final String WS_LOG_FILE = "config/HIEP_WebServiceLog4j.properties";
	
	public static String CONNECTION_TIME = "";
	public static String WSSERVER_WAITINT_TIME = "";

}
