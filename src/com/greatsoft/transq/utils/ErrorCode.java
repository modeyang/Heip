package com.greatsoft.transq.utils;

public class ErrorCode {
	public static String getErrorMessage(int errorCode){
		if(errorCode>=0 && errorCode<ErrorInfo.length){ 
			return ErrorInfo[errorCode];
		}
		return ErrorInfo[UNKNOWN_ERROR];
	}
	public static final String[] ErrorInfo={
		/**NO_ERROR*/"没有错误",
		/**UNKNOWN_ERROR*/"未定义错误信息",
		/**LOAD_HIEP_HOME_PATH_ERROR*/"获取HIEP_HOME_PATH失败",
		/**HIEP_HOME_PATH_ERROR */"HIEP_HOME_PATH路径有误",
		/**GET_WINDOWS_HIEP_PATH_ERROR*/"获取HIEP_HOME_PATH异常",
		/**HIEP_HOME_PATH_DIRECTORY_ERROR*/"HIEP_HOME_PATH目录不存在",
		/**CONFIG_DIRECTORY_ERROR*/"config目录不存在",
		/**EXT_DIRECTORY_ERROR*/"ext目录不存在",
		/**HIEP_CONFIG_FILE_ERROR*/"cinfig目录下缺少配置文件: HIEP_config.ini",
		/**HIEP_CONFIG_CUSTOM_FILE_ERROR*/"cinfig目录下缺少配置文件: HIEP_config_custom.ini",
		/**DISPATCHER_LOG_4J_FILE_ERROR*/"cinfig目录下缺少配置文件: dispatcherlog4j.properties",
		/**ROUTER_LOG_4J_FILE_ERROR*/"cinfig目录下缺少配置文件: routerlog4j.properties",
		/**ADDRESS_MAP_FILE_ERROR*/"cinfig目录下缺少配置文件:HIEP_addressMap.ini",
		/**PROCESSOR_MAP_FILE_ERROR */"cinfig目录下缺少配置文件:HIEP_processorMap.ini",
		/**ROUTER_TABLE_FILE_ERROR*/"cinfig目录下缺少配置文件:HIEP_routerTable.ini",
		/**CREATE_LOG_ERROR*/"日志初始化失败",
		/**HIEP_CONFIG_FILE_CHECK_ERROR*/"HIEP配置文件检查错误",
		/**FILE_NOT_FOUND_ERROR*/"没有找到指定路径的文件",
		/**LOAD_HIEP_CONFIG_INI_ERRO*/"HIEP_config.ini文件加载失败",
		/**LOAD_HIEP_CONFIG_CUSTOM_INI_ERROR*/"HIEP_config_custom.ini文件加载失败",
		/**LINE_INFORMATION_NOT_STARDAND_ERROR*/"配置文件中消息配置不标准",
		/**LOCAL_ADDRESS_ERROR*/"LOCAL_ADDRESS配置内容有误",
		/**LOCAL_SERVER_ADDRESS_NUM_ERROR*/"LOCAL_SERVER_ADDRESS_NUM配置有误",
		/**ADDRESS_PARSE_ERROR*/"字符串转换为地址失败",
		/**LOCAL_SERVER_ADDRESS_PARSE_ERROR*/"LOCAL_SERVER_ADDRESS内容配置有误",
		/**LOCAL_SERVER_ADDRESS_EQUAL_ERROR*/"LOCAL_SERVER_ADDRESS配置的接收消息的SERVER名字的个数与LOCAL_SERVER_ADDRESS_NUM不相等",
		/**TASK_QUEUE_CONTAINER_NAME_ERROR*/"TASK_QUEUE_CONTAINER_NAME配置有误",
		/**TASK_QUEUE_COUNT_ERROR*/"TASK_QUEUE_TABLE_COUNT只能设置为数字1或者数字2",
		/**TASK_QUEUE_NAME_ERROR*/"HIEP_config.ini配置文件TASK_QUEUE_NAME配置有误",
		/**TASK_QUEUE_LOCAL_NAME_ERROR*/"HIEP_config.ini配置文件TASK_QUEUE_LOCAL_NAME配置有误",
		/**TASK_QUEUE_REMOTE_NAME_ERROR*/"HIEP_config.ini配置文件TASK_QUEUE_REMOTE_NAME配置有误",
		/**RECEIVED_MESSAGE_FILE_DIRECTORY_ERROR*/"RECEIVED_MESSAGE_FILE_DIRECTORY配置文件目录的路径有误",
		/**NEW_MESSAGE_FILE_DIRECTORY_ERROR*/"NEW_MESSAGE_FILE_DIRECTORY_ERROR配置文件目录的路径有误",
		/**EXPIRED_TIME_MESSAGE_FILE_DIRECTORY_ERROR*/"EXPIRED_TIME_MESSAGE_FILE_DIRECTORY配置文件目录的路径有误",
		/**ERROR_MESSAGE_FILE_DIRECTORY_ERROR*/"ERROR_MESSAGE_FILE_DIRECTORY配置文件目录的路径有误",
		/**SEND_MESSAGE_FILE_DIRECTORY_ERROR*/"SEND_MESSAGE_FILE_DIRECTORY配置文件目录的路径有误",
		/**MIN_DISK_SPACE_CONFIG_ERROR*/"MIN_DISK_SPACE必须配置为大于1的任意数字",
		/**MIN_DISK_SPACE_ERROR*/"运行系统的最小硬盘空间不足够",
		/**ROUTER_PORT_ERROR*/"ROUTER_PORT只能配置为1025至65535之间的任意数字",
		/**DISPATCHER_PORT_ERROR*/"DISPATCHER_PORT只能配置为1025至65535之间的任意数字",
		/**DISPATCHER_PORT_EQUAL_ROUTER_PORT_ERROR*/"ROUTER_PORT和DISPATCHER_PORT不能配置为相同的数字",
		/**THREAD_NUMBER_ERROR*/"THREAD_NUMBER必须配置为5至20的任意数字",
		/**ROUTER_PROCESSING_INTERVAL_ERROR*/"ROUTER_PROCESSING_INTERVAL必须配置为1至65535的任意数字",
		/**DISPARTER_PROCESSING_INTERVAL_ERROR*/"DISPARTER_PROCESSING_INTERVAL必须配置为1至65535的任意数字",
		/**RECEIVE_MESSAGE_PRIORITY_MODE_ERROR*/"RECEIVE_MESSAGE_PRIORITY_MODE必须配置为1至3的任意数字",
		/**LOCAL_MESSAGE_PROCESS_PRIORITY_MODE_ERROR*/"LOCAL_MESSAGE_PROCESS_PRIORITY_MODE必须配置为1至3的任意数字",
		/**REMOTE_MESSAGE_PROCESS_PRIORITY_MODE_ERROR*/"REMOTE_MESSAGE_PROCESS_PRIORITY_MODE必须配置为1至3的任意数字",
		/**RELATIVE_EXPIRED_TIME_ERROR*/"RELATIVE_EXPIRED_TIME必须配置为非负整数",
		/**EXCHANGE_TRACE_MODE_ERROR*/"EXCHANGE_TRACE_MODE只能设置数字为1或者2",
		/**DUPLICATE_TARGET*/"路由记录的目的地址已经在之前的记录中出现",
		/**LOAD_ROUTER_TABLE_CONFIG_ERROR*/"HIEP_routerTable.ini文件加载失败",
		/**LOAD_ADDRESS_MAP_CONFIG_ERROR*/"HIEP_addressMap.ini文件加载失败",
		/**LOAD_PROCESSOR_MAP_CONFIG_ERROR*/"HIEP_processorMap.ini文件加载失败",
		/**ROUTER_NEXT_ADDRESS_MISSING_ADDRESS_ERROR*/"路由记录中的下一跳地址没有在HIEP_addressMap.ini文件中配置对应的地址记录",
		/**CREATE_DATABASE_ERROR*/"任务记录管理容器初始化失败",
		/**CONNECT_DATABASE_ERROR*/"任务记录管理容器连接失败",
		/**PID_FILE_EXIST_ERROR*/"PID文件存在",
		/**CREATE_PID_FILE_ERROR*/"PID文件创建失败",
		/**DUPLICATE_PROCESSOR_NAME*/"相同的数据处理部门和数据类型的记录已经在之前的记录中出现",
		/**LOAD_THIRD_JAR_ERROR*/"EXT目录下的JAR包加载失败",
		/**FILE_BYTEBUFFER_READ_IO_ERROR*/"文件在读取过程中出错：",
		/**FILE_BYTEBUFFER_CLOSE_ERROR*/"文件的BYTEBUFFER流关闭失败：",
		/**THREAD_SLEEP_INTERRUPT_ERROR*/"线程睡眠被异常中断",
		/**TASK_QUEUE_GET_ERROR*/"任务记录队列获取失败",
		/**TASK_QUEUE_EXCEPTION_ERROR*/"任务记录队列访问异常",
		/**CONNECTION_ERROR*/"与消息中间件建立连接失败",
		/**PUT_MESSAGE_COMMUNICATION_ERROR*/"远程消息处理模块发送信息失败",
		/**FAILED_PUT_TASK*/"将任务记录放入任务记录队列失败",
		/**EXIST_SAME_ID_TASK_ERROR*/"相同ID的任务记录存在",
		/**OPEN_TASK_QUEUE_ERROR*/"任务记录队列连接创建失败",
		/**OPEN_TASK_QUEUE_SQLEXCEPTION_ERROR*/"任务记录队列打开失败",
		/**PARSE_TARGRT_ADDRESS_ERROR*/"信息的目的地址解析错误",
		/**ROUTER_GROUP_ERROR*/"信息的目的地址路由解析失败",
		/**PUT_ONE_TASK_INTO_TASK_QUEUE_ERROR*/"放进一个任务记录到任务队列失败",
		/**GET_DATA_ERROR*/"从从消息中间件获取信息失败",
		/**MESSAGE_FORMAT_ERROR*/"消息解析过程中出现格式错误",
		/**FILE_IO_ERROR*/"文件读写过程中出错",
		/**TLQ_CONNECTION_URL_ERROR*/"与TONGLINGQUEUE建立连接失败，url错误",
		/**CLOSE_TLQ_CONNECTION_ERROR*/"关闭与TLQ的连接失败",
		/**TLQ_RECEIVER_MESSAGE_ERROR*/"TLQ的消息接收端连接异常",
		/**TLQ_SENDER_CONNECTION_ERROR*/"TLQ的消息发送端连接异常",
		/**PUT_MESSAGE_ERROR*/"发送信息失败",
		/**CREATE_TLQ_MESSAGE_ERROR*/"创建TLQ信息失败",
		/**TLQ_ACKNOWLEDGE_ERROR*/"TLQ确认接收消息异常",
		/**TLQ_INITIAL_CONTEXT_ERROR*/"TLQ上下文初始化异常",
		/**TLQ_LOOKUP_ERROR*/"TLQ寻找连接工厂和队列异常",
		/**TLQ_CREATE_CONNECTION_ERROR*/"TLQ创建连接异常",
		/**TLQ_CREATE_SENDER_ERROR*/"TLQ创建消息发送端异常",
		/**TLQ_CREATE_RECEIVER_ERROR*/"TLQ创建消息接收端异常",
	};
	
	public static final int NO_ERROR = 0;
	public static final String NO_ERROR_MESSAGE = "没有错误";
	
	public static final int UNKNOWN_ERROR = 1;
	public static final String UNKNOWN_ERROR_MESSAGE = "未定义错误信息";
	
	public static final int LOAD_HIEP_HOME_PATH_ERROR = 2;
	public static final String LOAD_HIEP_HOME_PATH_ERROR_INFO = "获取HIEP_HOME_PATH失败";
	
	public static final int HIEP_HOME_PATH_ERROR = 3;
	public static final String HIEP_HOME_PATH_ERROR_INFO = "HIEP_HOME_PATH路径有误";
	
	public static final int GET_WINDOWS_HIEP_PATH_ERROR = 4;
	public static final String GET_WINDOWS_HIEP_PATH_ERROR_INFO="获取HIEP_HOME_PATH异常";
	
	public static final int HIEP_HOME_PATH_DIRECTORY_ERROR = 5;
	public static final String HIEP_HOME_PATH_DIRECTORY_ERROR_INFO = "HIEP_HOME_PATH目录不存在";
	
	public static final int CONFIG_DIRECTORY_ERROR = 6;
	public static final String CONFIG_DIRECTORY_ERROR_INFO = "config目录不存在";
	
	public static final int EXT_DIRECTORY_ERROR = 7;
	public static final String EXT_DIRECTORY_ERROR_INFO = "ext目录不存在";
	
	public static final int HIEP_CONFIG_FILE_ERROR = 8;
	public static final String HIEP_CONFIG_FILE_ERROR_INFO = "cinfig目录下缺少配置文件: HIEP_config.ini";
	
	public static final int HIEP_CONFIG_CUSTOM_FILE_ERROR = 9;
	public static final String HIEP_CONFIG_CUSTOM_FILE_ERROR_INFO = "cinfig目录下缺少配置文件: HIEP_config_custom.ini";
	
	public static final int DISPATCHER_LOG_4J_FILE_ERROR = 10;
	public static final String DISPATCHER_LOG_4J_FILE_ERROR_INFO = "cinfig目录下缺少配置文件: dispatcherlog4j.properties";
	
	public static final int ROUTER_LOG_4J_FILE_ERROR = 11;
	public static final String ROUTER_LOG_4J_FILE_ERROR_INFO = "cinfig目录下缺少配置文件: routerlog4j.properties";
	
	public static final int ADDRESS_MAP_FILE_ERROR = 12;
	public static final String ADDRESS_MAP_FILE_ERROR_INFO = "cinfig目录下缺少配置文件:HIEP_addressMap.ini";

	public static final int PROCESSOR_MAP_FILE_ERROR = 13;
	public static final String PROCESSOR_MAP_FILE_ERROR_INFO = "cinfig目录下缺少配置文件:HIEP_processorMap.ini";

	public static final int ROUTER_TABLE_FILE_ERROR = 14;
	public static final String ROUTER_TABLE_FILE_ERROR_INFO = "cinfig目录下缺少配置文件:HIEP_routerTable.ini";
	
	public static final int CREATE_LOG_ERROR = 15;
	public static final String CREATE_LOG_ERROR_INFO = "日志初始化失败";
	
	public static final int HIEP_CONFIG_FILE_CHECK_ERROR = 16;
	public static final String HIEP_CONFIG_FILE_CHECK_ERROR_MESSAGE="HIEP配置文件检查错误";
	
	public static final int FILE_NOT_FOUND_ERROR = 17;
	public static final String FILE_NOT_FOUND_ERROR_MESSAGE="没有找到指定路径的文件";
	
	public static final int LOAD_HIEP_CONFIG_INI_ERROR = 18;
	public static final String LOAD_HIEP_CONFIG_INI_ERROR_INFO = "HIEP_config.ini文件加载失败";
	
	public static final int LOAD_HIEP_CONFIG_CUSTOM_INI_ERROR = 19;
	public static final String LOAD_HIEP_CONFIG_CUSTOM_INI_ERROR_INFO = "HIEP_config_custom.ini文件加载失败";
	
	public static final int LINE_INFORMATION_NOT_STARDAND_ERROR = 20;
	public static final String LINE_INFORMATION_NOT_STARDAND_ERROR_INFO = "配置文件中消息配置不标准";
	
	public static final int LOCAL_ADDRESS_ERROR =21;
	public static final String LOCAL_ADDRESS_ERROR_INFO = "LOCAL_ADDRESS配置内容有误";
	
	public static final int LOCAL_SERVER_ADDRESS_NUM_ERROR =22;
	public static final String LOCAL_SERVER_ADDRESS_NUM_ERROR_INFO = "LOCAL_SERVER_ADDRESS_NUM配置有误";
	
	public static final int LOCAL_SERVER_ADDRESS_NUM_ZERO =90;
	public static final String LOCAL_SERVER_ADDRESS_NUM_ZERO_INFO = "LOCAL_SERVER_ADDRESS_NUM配置为0，没有配置消息中间件";
	
	
	public static final int ADDRESS_PARSE_ERROR =23;
	public static final String ADDRESS_PARSE_ERROR_INFO = "字符串转换为地址失败";
	
	public static final int LOCAL_SERVER_ADDRESS_PARSE_ERROR = 24;
	public static final String LOCAL_SERVER_ADDRESS_PARSE_ERROR_INFO = "LOCAL_SERVER_ADDRESS内容配置有误";
	
	public static final int LOCAL_SERVER_ADDRESS_EQUAL_ERROR = 25;
	public static final String LOCAL_SERVER_ADDRESS_EQUAL_ERROR_INFO = "LOCAL_SERVER_ADDRESS配置的接收消息的SERVER名字的个数与LOCAL_SERVER_ADDRESS_NUM不相等";
	
	public static final int TASK_QUEUE_CONTAINER_NAME_ERROR = 26;
	public static final String TASK_QUEUE_CONTAINER_NAME_ERROR_INFO ="TASK_QUEUE_CONTAINER_NAME配置有误";
	
	public static final int TASK_QUEUE_COUNT_ERROR = 27;
	public static final String TASK_QUEUE_COUNT_ERROR_INFO="TASK_QUEUE_TABLE_COUNT只能设置为数字1或者数字2";
	
	public static final int TASK_QUEUE_NAME_ERROR = 28;
	public static final String TASK_QUEUE_NAME_ERROR_INFO="HIEP_config.ini配置文件TASK_QUEUE_NAME配置有误";
	
	public static final int TASK_QUEUE_LOCAL_NAME_ERROR = 29;
	public static final String TASK_QUEUE_LOCAL_NAME_ERROR_INFO="HIEP_config.ini配置文件TASK_QUEUE_LOCAL_NAME配置有误";
	
	public static final int TASK_QUEUE_REMOTE_NAME_ERROR = 30;
	public static final String TASK_QUEUE_REMOTE_NAME_ERROR_INFO="HIEP_config.ini配置文件TASK_QUEUE_REMOTE_NAME配置有误";
	
	public static final int RECEIVED_MESSAGE_FILE_DIRECTORY_ERROR = 31;
	public static final String RECEIVED_MESSAGE_FILE_DIRECTORY_ERROR_INFO = "RECEIVED_MESSAGE_FILE_DIRECTORY配置文件目录的路径有误";
	
	public static final int NEW_MESSAGE_FILE_DIRECTORY_ERROR = 32;
	public static final String NEW_MESSAGE_FILE_DIRECTORY_ERROR_INFO = "NEW_MESSAGE_FILE_DIRECTORY_ERROR配置文件目录的路径有误";
	
	public static final int EXPIRED_TIME_MESSAGE_FILE_DIRECTORY_ERROR = 33;
	public static final String EXPIRED_TIME_MESSAGE_FILE_DIRECTORY_ERROR_INFO = "EXPIRED_TIME_MESSAGE_FILE_DIRECTORY配置文件目录的路径有误";
	
	public static final int ERROR_MESSAGE_FILE_DIRECTORY_ERROR = 34;
	public static final String ERROR_MESSAGE_FILE_DIRECTORY_ERROR_INFO = "ERROR_MESSAGE_FILE_DIRECTORY配置文件目录的路径有误";
	
	public static final int SEND_MESSAGE_FILE_DIRECTORY_ERROR = 35;
	public static final String SEND_MESSAGE_FILE_DIRECTORY_ERROR_INFO = "SEND_MESSAGE_FILE_DIRECTORY配置文件目录的路径有误";
	
	public static final int MIN_DISK_SPACE_CONFIG_ERROR = 36;
	public static final String MIN_DISK_SPACE_CONFIG_ERROR_INFO = "MIN_DISK_SPACE必须配置为大于1的任意数字";
	
	public static final int MIN_DISK_SPACE_ERROR = 37;
	public static final String MIN_DISK_SPACE_ERROR_INFO = "运行系统的最小硬盘空间不足够";
	
	public static final int ROUTER_PORT_ERROR = 38;
	public static final String ROUTER_PORT_ERROR_INFO ="ROUTER_PORT只能配置为1025至65535之间的任意数字";

	public static final int DISPATCHER_PORT_ERROR = 39;
	public static final String DISPATCHER_PORT_ERROR_INFO ="DISPATCHER_PORT只能配置为1025至65535之间的任意数字";

	public static final int DISPATCHER_PORT_EQUAL_ROUTER_PORT_ERROR =40;
	public static final String DISPATCHER_PORT_EQUAL_ROUTER_PORT_ERROR_INFO ="ROUTER_PORT和DISPATCHER_PORT不能配置为相同的数字";
	
	public static final int THREAD_NUMBER_ERROR = 41;
	public static final String THREAD_NUMBER_ERROR_INFO ="THREAD_NUMBER必须配置为5至20的任意数字";
	
	public static final int ROUTER_PROCESSING_INTERVAL_ERROR = 42;
	public static final String ROUTER_PROCESSING_INTERVAL_ERROR_INFO ="ROUTER_PROCESSING_INTERVAL必须配置为1至65535的任意数字";

	public static final int DISPARTER_PROCESSING_INTERVAL_ERROR = 43;
	public static final String DISPARTER_PROCESSING_INTERVAL_ERROR_INFO ="DISPARTER_PROCESSING_INTERVAL必须配置为1至65535的任意数字";
	
	public static final int RECEIVE_MESSAGE_PRIORITY_MODE_ERROR = 44;
	public static final String RECEIVE_MESSAGE_PRIORITY_MODE_ERROR_INFO ="RECEIVE_MESSAGE_PRIORITY_MODE必须配置为1至3的任意数字";
	
	public static final int LOCAL_MESSAGE_PROCESS_PRIORITY_MODE_ERROR = 45;
	public static final String LOCAL_MESSAGE_PROCESS_PRIORITY_MODE_ERROR_INFO ="LOCAL_MESSAGE_PROCESS_PRIORITY_MODE必须配置为1至3的任意数字";
	
	public static final int REMOTE_MESSAGE_PROCESS_PRIORITY_MODE_ERROR = 46;
	public static final String REMOTE_MESSAGE_PROCESS_PRIORITY_MODE_ERROR_INFO ="REMOTE_MESSAGE_PROCESS_PRIORITY_MODE必须配置为1至3的任意数字";
	
	public static final int RELATIVE_EXPIRED_TIME_ERROR = 47;
	public static final String RELATIVE_EXPIRED_TIME_ERROR_INFO ="RELATIVE_EXPIRED_TIME必须配置为非负整数";
	
	public static final int EXCHANGE_TRACE_MODE_ERROR = 48;
	public static final String EXCHANGE_TRACE_MODE_ERROR_INFO ="EXCHANGE_TRACE_MODE只能设置数字为1或者2";
	
	public static final int DUPLICATE_TARGET=49;
	public static final String DUPLICATE_TARGET_INFO= "路由记录的目的地址已经在之前的记录中出现";
	
	public static final int LOAD_ROUTER_TABLE_CONFIG_ERROR = 50;
	public static final String LOAD_ROUTER_TABLE_CONFIG_ERROR_INFO = "HIEP_routerTable.ini文件加载失败";
	
	public static final int LOAD_ADDRESS_MAP_CONFIG_ERROR = 51;
	public static final String LOAD_ADDRESS_MAP_CONFIG_ERROR_INFO = "HIEP_addressMap.ini文件加载失败";
	
	public static final int LOAD_PROCESSOR_MAP_CONFIG_ERROR = 52;
	public static final String LOAD_PROCESSOR_MAP_CONFIG_ERROR_INFO = "HIEP_processorMap.ini文件加载失败";
	
	public static final int ROUTER_NEXT_ADDRESS_MISSING_ADDRESS_ERROR = 53;
	public static final String ROUTER_NEXT_ADDRESS_MISSING_ADDRESS_ERROR_INFO = "路由记录中的下一跳地址没有在HIEP_addressMap.ini文件中配置对应的地址记录";
	
	public static final int CREATE_DATABASE_ERROR = 54;
	public static final String CREATE_DATABASE_ERROR_INFO ="任务记录管理容器初始化失败";
	
	public static final int CONNECT_DATABASE_ERROR = 55;
	public static final String CONNECT_DATABASE_ERROR_INFO ="任务记录管理容器连接失败";
	
	public static final int PID_FILE_EXIST_ERROR = 56;
	public static final String PID_FILE_EXIST_ERROR_INFO = "PID文件存在";
	
	public static final int CREATE_PID_FILE_ERROR = 57;
	public static final String CREATE_PID_FILE_ERROR_INFO = "PID文件创建失败";
	
	public static final int DUPLICATE_PROCESSOR_NAME = 58;
	public static final String DUPLICATE_PROCESSOR_NAME_INFO = "相同的数据处理部门和数据类型的记录已经在之前的记录中出现";
	
	public static final int LOAD_THIRD_JAR_ERROR = 59;
	public static final String LOAD_THIRD_JAR_ERROR_INFO = "EXT目录下的JAR包加载失败";
	
	public static final int FILE_BYTEBUFFER_READ_IO_ERROR = 60;
	public static final String FILE_BYTEBUFFER_READ_IO_ERROR_MESSAGE="文件在读取过程中出错：";
	
	public static final int FILE_BYTEBUFFER_CLOSE_ERROR = 61;
	public static final String FILE_BYTEBUFFER_CLOSE_ERROR_INFO = "文件的BYTEBUFFER流关闭失败：";
	
	public static final int THREAD_SLEEP_INTERRUPT_ERROR = 62;
	public static final String THREAD_SLEEP_INTERRUPT_ERROR_INFO = "线程睡眠被异常中断";
	
	public static final int TASK_QUEUE_GET_ERROR = 63;
	public static final String TASK_QUEUE_GET_ERROR_INFO = "任务记录队列获取失败";
	
	public static final int TASK_QUEUE_EXCEPTION_ERROR = 64;
	public static final String TASK_QUEUE_EXCEPTION_ERROR_INFO = "任务记录队列访问异常";
	
	public static final int CONNECTION_ERROR = 65; 
	public static final String CONNECTION_ERROR_INFO = "与消息中间件建立连接失败";
	
	public static final int PUT_MESSAGE_COMMUNICATION_ERROR = 66;
	public static final String PUT_MESSAGE_COMMUNICATION_ERROR_INFO = "远程消息处理模块发送信息失败";
	
	public static final int FAILED_PUT_TASK = 67;
	public static final String FAILED_PUT_TASK_INFO="将任务记录放入任务记录队列失败";
	
	public static final int EXIST_SAME_ID_TASK_ERROR = 68;
	public static final String EXIST_SAME_ID_TASK_ERROR_INFO="相同ID的任务记录存在";
	
	public static final int OPEN_TASK_QUEUE_ERROR = 69;
	public static final String OPEN_TASK_QUEUE_ERROR_INFO="任务记录队列连接创建失败";
	
	public static final int OPEN_TASK_QUEUE_SQLEXCEPTION_ERROR = 70;
	public static final String OPEN_TASK_QUEUE_SQLEXCEPTION_ERROR_INFO="任务记录队列打开失败";
	
	public static final int PARSE_TARGRT_ADDRESS_ERROR = 71;
	public static final String PARSE_TARGRT_ADDRESS_ERROR_INFO = "信息的目的地址解析错误";
	
	public static final int ROUTER_GROUP_ERROR = 72;
	public static final String ROUTER_GROUP_ERROR_INFO = "信息的目的地址路由解析失败";
	
	public static final int PUT_ONE_TASK_INTO_TASK_QUEUE_ERROR = 73;
	public static final String PUT_ONE_TASK_INTO_TASK_QUEUE_ERROR_INFO = "放进一个任务记录到任务队列失败";
	
	public static final int GET_DATA_ERROR = 74;
	public static final String GET_DATA_ERROR_INFO = "从从消息中间件获取信息失败";
	
	public static final int MESSAGE_FORMAT_ERROR = 75;
	public static final String MESSAGE_FORMAT_ERROR_INFO = "消息解析过程中出现格式错误";
	
	public static final int FILE_IO_ERROR = 76;
	public static final String FILE_IO_ERROR_INFO = "文件读写过程中出错";
	
	public static final int TLQ_CONNECTION_URL_ERROR = 77;
	public static final String TLQ_CONNECTION_URL_ERROR_INFO = "与TONGLINGQUEUE建立连接失败，url错误";
	
	public static final int TLQ_CLOSE_CONNECTION_ERROR = 78;
	public static final String TLQ_CLOSE_CONNECTION_ERROR_INFO = "TLQ关闭连接失败";
	
	public static final int TLQ_RECEIVER_MESSAGE_ERROR = 79;
	public static final String TLQ_RECEIVER_MESSAGE_ERROR_INFO = "TLQ的消息接收端连接异常";
	
	public static final int TLQ_SENDER_CONNECTION_ERROR = 80;
	public static final String TLQ_SENDER_CONNECTION_ERROR_INFO = "TLQ的消息发送端连接异常";
	
	public static final int PUT_MESSAGE_ERROR = 81;
	public static final String PUT_MESSAGE_ERROR_INFO = "发送一条信息失败";
	
	public static final int CREATE_TLQ_MESSAGE_ERROR = 82;
	public static final String CREATE_TLQ_MESSAGE_ERROR_INFO = "创建一条TLQ信息失败";

	public static final int TLQ_ACKNOWLEDGE_ERROR = 83;
	public static final String TLQ_ACKNOWLEDGE_ERROR_INFO = "TLQ确认接收消息异常";

	public static final int TLQ_INITIAL_CONTEXT_ERROR = 84;
	public static final String TLQ_INITIAL_CONTEXT_ERROR_INFO = "TLQ上下文初始化异常";

	public static final int TLQ_LOOKUP_ERROR = 85;
	public static final String TLQ_LOOKUP_ERROR_INFO = "TLQ寻找连接工厂和队列异常";

	public static final int TLQ_CREATE_CONNECTION_ERROR = 86;
	public static final String TLQ_CREATE_CONNECTION_ERROR_INFO = "TLQ创建连接异常";

	public static final int TLQ_CREATE_SENDER_ERROR = 87;
	public static final String TLQ_CREATE_SENDER_ERROR_INFO ="TLQ创建消息发送端异常";

	public static final int TLQ_CREATE_RECEIVER_ERROR = 88;
	public static final String TLQ_CREATE_RECEIVER_ERROR_INFO ="TLQ创建消息接收端异常";

	public static final int TASK_QUEUE_CREATE_CONNECTION_ERROR = 89;
	public static final String TASK_QUEUE_CREATE_CONNECTION_ERROR_INFO ="任务记录队列连接创建失败";
}
