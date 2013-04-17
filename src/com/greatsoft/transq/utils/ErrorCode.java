package com.greatsoft.transq.utils;

public class ErrorCode {
	public static String getErrorMessage(int errorCode){
		if(errorCode>=0 && errorCode<ErrorInfo.length){ 
			return ErrorInfo[errorCode];
		}
		return ErrorInfo[UNKNOWN_ERROR];
	}
	public static final String[] ErrorInfo={
		/**NO_ERROR*/"û�д���",
		/**UNKNOWN_ERROR*/"δ���������Ϣ",
		/**LOAD_HIEP_HOME_PATH_ERROR*/"��ȡHIEP_HOME_PATHʧ��",
		/**HIEP_HOME_PATH_ERROR */"HIEP_HOME_PATH·������",
		/**GET_WINDOWS_HIEP_PATH_ERROR*/"��ȡHIEP_HOME_PATH�쳣",
		/**HIEP_HOME_PATH_DIRECTORY_ERROR*/"HIEP_HOME_PATHĿ¼������",
		/**CONFIG_DIRECTORY_ERROR*/"configĿ¼������",
		/**EXT_DIRECTORY_ERROR*/"extĿ¼������",
		/**HIEP_CONFIG_FILE_ERROR*/"cinfigĿ¼��ȱ�������ļ�: HIEP_config.ini",
		/**HIEP_CONFIG_CUSTOM_FILE_ERROR*/"cinfigĿ¼��ȱ�������ļ�: HIEP_config_custom.ini",
		/**DISPATCHER_LOG_4J_FILE_ERROR*/"cinfigĿ¼��ȱ�������ļ�: dispatcherlog4j.properties",
		/**ROUTER_LOG_4J_FILE_ERROR*/"cinfigĿ¼��ȱ�������ļ�: routerlog4j.properties",
		/**ADDRESS_MAP_FILE_ERROR*/"cinfigĿ¼��ȱ�������ļ�:HIEP_addressMap.ini",
		/**PROCESSOR_MAP_FILE_ERROR */"cinfigĿ¼��ȱ�������ļ�:HIEP_processorMap.ini",
		/**ROUTER_TABLE_FILE_ERROR*/"cinfigĿ¼��ȱ�������ļ�:HIEP_routerTable.ini",
		/**CREATE_LOG_ERROR*/"��־��ʼ��ʧ��",
		/**HIEP_CONFIG_FILE_CHECK_ERROR*/"HIEP�����ļ�������",
		/**FILE_NOT_FOUND_ERROR*/"û���ҵ�ָ��·�����ļ�",
		/**LOAD_HIEP_CONFIG_INI_ERRO*/"HIEP_config.ini�ļ�����ʧ��",
		/**LOAD_HIEP_CONFIG_CUSTOM_INI_ERROR*/"HIEP_config_custom.ini�ļ�����ʧ��",
		/**LINE_INFORMATION_NOT_STARDAND_ERROR*/"�����ļ�����Ϣ���ò���׼",
		/**LOCAL_ADDRESS_ERROR*/"LOCAL_ADDRESS������������",
		/**LOCAL_SERVER_ADDRESS_NUM_ERROR*/"LOCAL_SERVER_ADDRESS_NUM��������",
		/**ADDRESS_PARSE_ERROR*/"�ַ���ת��Ϊ��ַʧ��",
		/**LOCAL_SERVER_ADDRESS_PARSE_ERROR*/"LOCAL_SERVER_ADDRESS������������",
		/**LOCAL_SERVER_ADDRESS_EQUAL_ERROR*/"LOCAL_SERVER_ADDRESS���õĽ�����Ϣ��SERVER���ֵĸ�����LOCAL_SERVER_ADDRESS_NUM�����",
		/**TASK_QUEUE_CONTAINER_NAME_ERROR*/"TASK_QUEUE_CONTAINER_NAME��������",
		/**TASK_QUEUE_COUNT_ERROR*/"TASK_QUEUE_TABLE_COUNTֻ������Ϊ����1��������2",
		/**TASK_QUEUE_NAME_ERROR*/"HIEP_config.ini�����ļ�TASK_QUEUE_NAME��������",
		/**TASK_QUEUE_LOCAL_NAME_ERROR*/"HIEP_config.ini�����ļ�TASK_QUEUE_LOCAL_NAME��������",
		/**TASK_QUEUE_REMOTE_NAME_ERROR*/"HIEP_config.ini�����ļ�TASK_QUEUE_REMOTE_NAME��������",
		/**RECEIVED_MESSAGE_FILE_DIRECTORY_ERROR*/"RECEIVED_MESSAGE_FILE_DIRECTORY�����ļ�Ŀ¼��·������",
		/**NEW_MESSAGE_FILE_DIRECTORY_ERROR*/"NEW_MESSAGE_FILE_DIRECTORY_ERROR�����ļ�Ŀ¼��·������",
		/**EXPIRED_TIME_MESSAGE_FILE_DIRECTORY_ERROR*/"EXPIRED_TIME_MESSAGE_FILE_DIRECTORY�����ļ�Ŀ¼��·������",
		/**ERROR_MESSAGE_FILE_DIRECTORY_ERROR*/"ERROR_MESSAGE_FILE_DIRECTORY�����ļ�Ŀ¼��·������",
		/**SEND_MESSAGE_FILE_DIRECTORY_ERROR*/"SEND_MESSAGE_FILE_DIRECTORY�����ļ�Ŀ¼��·������",
		/**MIN_DISK_SPACE_CONFIG_ERROR*/"MIN_DISK_SPACE��������Ϊ����1����������",
		/**MIN_DISK_SPACE_ERROR*/"����ϵͳ����СӲ�̿ռ䲻�㹻",
		/**ROUTER_PORT_ERROR*/"ROUTER_PORTֻ������Ϊ1025��65535֮�����������",
		/**DISPATCHER_PORT_ERROR*/"DISPATCHER_PORTֻ������Ϊ1025��65535֮�����������",
		/**DISPATCHER_PORT_EQUAL_ROUTER_PORT_ERROR*/"ROUTER_PORT��DISPATCHER_PORT��������Ϊ��ͬ������",
		/**THREAD_NUMBER_ERROR*/"THREAD_NUMBER��������Ϊ5��20����������",
		/**ROUTER_PROCESSING_INTERVAL_ERROR*/"ROUTER_PROCESSING_INTERVAL��������Ϊ1��65535����������",
		/**DISPARTER_PROCESSING_INTERVAL_ERROR*/"DISPARTER_PROCESSING_INTERVAL��������Ϊ1��65535����������",
		/**RECEIVE_MESSAGE_PRIORITY_MODE_ERROR*/"RECEIVE_MESSAGE_PRIORITY_MODE��������Ϊ1��3����������",
		/**LOCAL_MESSAGE_PROCESS_PRIORITY_MODE_ERROR*/"LOCAL_MESSAGE_PROCESS_PRIORITY_MODE��������Ϊ1��3����������",
		/**REMOTE_MESSAGE_PROCESS_PRIORITY_MODE_ERROR*/"REMOTE_MESSAGE_PROCESS_PRIORITY_MODE��������Ϊ1��3����������",
		/**RELATIVE_EXPIRED_TIME_ERROR*/"RELATIVE_EXPIRED_TIME��������Ϊ�Ǹ�����",
		/**EXCHANGE_TRACE_MODE_ERROR*/"EXCHANGE_TRACE_MODEֻ����������Ϊ1����2",
		/**DUPLICATE_TARGET*/"·�ɼ�¼��Ŀ�ĵ�ַ�Ѿ���֮ǰ�ļ�¼�г���",
		/**LOAD_ROUTER_TABLE_CONFIG_ERROR*/"HIEP_routerTable.ini�ļ�����ʧ��",
		/**LOAD_ADDRESS_MAP_CONFIG_ERROR*/"HIEP_addressMap.ini�ļ�����ʧ��",
		/**LOAD_PROCESSOR_MAP_CONFIG_ERROR*/"HIEP_processorMap.ini�ļ�����ʧ��",
		/**ROUTER_NEXT_ADDRESS_MISSING_ADDRESS_ERROR*/"·�ɼ�¼�е���һ����ַû����HIEP_addressMap.ini�ļ������ö�Ӧ�ĵ�ַ��¼",
		/**CREATE_DATABASE_ERROR*/"�����¼����������ʼ��ʧ��",
		/**CONNECT_DATABASE_ERROR*/"�����¼������������ʧ��",
		/**PID_FILE_EXIST_ERROR*/"PID�ļ�����",
		/**CREATE_PID_FILE_ERROR*/"PID�ļ�����ʧ��",
		/**DUPLICATE_PROCESSOR_NAME*/"��ͬ�����ݴ����ź��������͵ļ�¼�Ѿ���֮ǰ�ļ�¼�г���",
		/**LOAD_THIRD_JAR_ERROR*/"EXTĿ¼�µ�JAR������ʧ��",
		/**FILE_BYTEBUFFER_READ_IO_ERROR*/"�ļ��ڶ�ȡ�����г���",
		/**FILE_BYTEBUFFER_CLOSE_ERROR*/"�ļ���BYTEBUFFER���ر�ʧ�ܣ�",
		/**THREAD_SLEEP_INTERRUPT_ERROR*/"�߳�˯�߱��쳣�ж�",
		/**TASK_QUEUE_GET_ERROR*/"�����¼���л�ȡʧ��",
		/**TASK_QUEUE_EXCEPTION_ERROR*/"�����¼���з����쳣",
		/**CONNECTION_ERROR*/"����Ϣ�м����������ʧ��",
		/**PUT_MESSAGE_COMMUNICATION_ERROR*/"Զ����Ϣ����ģ�鷢����Ϣʧ��",
		/**FAILED_PUT_TASK*/"�������¼���������¼����ʧ��",
		/**EXIST_SAME_ID_TASK_ERROR*/"��ͬID�������¼����",
		/**OPEN_TASK_QUEUE_ERROR*/"�����¼�������Ӵ���ʧ��",
		/**OPEN_TASK_QUEUE_SQLEXCEPTION_ERROR*/"�����¼���д�ʧ��",
		/**PARSE_TARGRT_ADDRESS_ERROR*/"��Ϣ��Ŀ�ĵ�ַ��������",
		/**ROUTER_GROUP_ERROR*/"��Ϣ��Ŀ�ĵ�ַ·�ɽ���ʧ��",
		/**PUT_ONE_TASK_INTO_TASK_QUEUE_ERROR*/"�Ž�һ�������¼���������ʧ��",
		/**GET_DATA_ERROR*/"�Ӵ���Ϣ�м����ȡ��Ϣʧ��",
		/**MESSAGE_FORMAT_ERROR*/"��Ϣ���������г��ָ�ʽ����",
		/**FILE_IO_ERROR*/"�ļ���д�����г���",
		/**TLQ_CONNECTION_URL_ERROR*/"��TONGLINGQUEUE��������ʧ�ܣ�url����",
		/**CLOSE_TLQ_CONNECTION_ERROR*/"�ر���TLQ������ʧ��",
		/**TLQ_RECEIVER_MESSAGE_ERROR*/"TLQ����Ϣ���ն������쳣",
		/**TLQ_SENDER_CONNECTION_ERROR*/"TLQ����Ϣ���Ͷ������쳣",
		/**PUT_MESSAGE_ERROR*/"������Ϣʧ��",
		/**CREATE_TLQ_MESSAGE_ERROR*/"����TLQ��Ϣʧ��",
		/**TLQ_ACKNOWLEDGE_ERROR*/"TLQȷ�Ͻ�����Ϣ�쳣",
		/**TLQ_INITIAL_CONTEXT_ERROR*/"TLQ�����ĳ�ʼ���쳣",
		/**TLQ_LOOKUP_ERROR*/"TLQѰ�����ӹ����Ͷ����쳣",
		/**TLQ_CREATE_CONNECTION_ERROR*/"TLQ���������쳣",
		/**TLQ_CREATE_SENDER_ERROR*/"TLQ������Ϣ���Ͷ��쳣",
		/**TLQ_CREATE_RECEIVER_ERROR*/"TLQ������Ϣ���ն��쳣",
	};
	
	public static final int NO_ERROR = 0;
	public static final String NO_ERROR_MESSAGE = "û�д���";
	
	public static final int UNKNOWN_ERROR = 1;
	public static final String UNKNOWN_ERROR_MESSAGE = "δ���������Ϣ";
	
	public static final int LOAD_HIEP_HOME_PATH_ERROR = 2;
	public static final String LOAD_HIEP_HOME_PATH_ERROR_INFO = "��ȡHIEP_HOME_PATHʧ��";
	
	public static final int HIEP_HOME_PATH_ERROR = 3;
	public static final String HIEP_HOME_PATH_ERROR_INFO = "HIEP_HOME_PATH·������";
	
	public static final int GET_WINDOWS_HIEP_PATH_ERROR = 4;
	public static final String GET_WINDOWS_HIEP_PATH_ERROR_INFO="��ȡHIEP_HOME_PATH�쳣";
	
	public static final int HIEP_HOME_PATH_DIRECTORY_ERROR = 5;
	public static final String HIEP_HOME_PATH_DIRECTORY_ERROR_INFO = "HIEP_HOME_PATHĿ¼������";
	
	public static final int CONFIG_DIRECTORY_ERROR = 6;
	public static final String CONFIG_DIRECTORY_ERROR_INFO = "configĿ¼������";
	
	public static final int EXT_DIRECTORY_ERROR = 7;
	public static final String EXT_DIRECTORY_ERROR_INFO = "extĿ¼������";
	
	public static final int HIEP_CONFIG_FILE_ERROR = 8;
	public static final String HIEP_CONFIG_FILE_ERROR_INFO = "cinfigĿ¼��ȱ�������ļ�: HIEP_config.ini";
	
	public static final int HIEP_CONFIG_CUSTOM_FILE_ERROR = 9;
	public static final String HIEP_CONFIG_CUSTOM_FILE_ERROR_INFO = "cinfigĿ¼��ȱ�������ļ�: HIEP_config_custom.ini";
	
	public static final int DISPATCHER_LOG_4J_FILE_ERROR = 10;
	public static final String DISPATCHER_LOG_4J_FILE_ERROR_INFO = "cinfigĿ¼��ȱ�������ļ�: dispatcherlog4j.properties";
	
	public static final int ROUTER_LOG_4J_FILE_ERROR = 11;
	public static final String ROUTER_LOG_4J_FILE_ERROR_INFO = "cinfigĿ¼��ȱ�������ļ�: routerlog4j.properties";
	
	public static final int ADDRESS_MAP_FILE_ERROR = 12;
	public static final String ADDRESS_MAP_FILE_ERROR_INFO = "cinfigĿ¼��ȱ�������ļ�:HIEP_addressMap.ini";

	public static final int PROCESSOR_MAP_FILE_ERROR = 13;
	public static final String PROCESSOR_MAP_FILE_ERROR_INFO = "cinfigĿ¼��ȱ�������ļ�:HIEP_processorMap.ini";

	public static final int ROUTER_TABLE_FILE_ERROR = 14;
	public static final String ROUTER_TABLE_FILE_ERROR_INFO = "cinfigĿ¼��ȱ�������ļ�:HIEP_routerTable.ini";
	
	public static final int CREATE_LOG_ERROR = 15;
	public static final String CREATE_LOG_ERROR_INFO = "��־��ʼ��ʧ��";
	
	public static final int HIEP_CONFIG_FILE_CHECK_ERROR = 16;
	public static final String HIEP_CONFIG_FILE_CHECK_ERROR_MESSAGE="HIEP�����ļ�������";
	
	public static final int FILE_NOT_FOUND_ERROR = 17;
	public static final String FILE_NOT_FOUND_ERROR_MESSAGE="û���ҵ�ָ��·�����ļ�";
	
	public static final int LOAD_HIEP_CONFIG_INI_ERROR = 18;
	public static final String LOAD_HIEP_CONFIG_INI_ERROR_INFO = "HIEP_config.ini�ļ�����ʧ��";
	
	public static final int LOAD_HIEP_CONFIG_CUSTOM_INI_ERROR = 19;
	public static final String LOAD_HIEP_CONFIG_CUSTOM_INI_ERROR_INFO = "HIEP_config_custom.ini�ļ�����ʧ��";
	
	public static final int LINE_INFORMATION_NOT_STARDAND_ERROR = 20;
	public static final String LINE_INFORMATION_NOT_STARDAND_ERROR_INFO = "�����ļ�����Ϣ���ò���׼";
	
	public static final int LOCAL_ADDRESS_ERROR =21;
	public static final String LOCAL_ADDRESS_ERROR_INFO = "LOCAL_ADDRESS������������";
	
	public static final int LOCAL_SERVER_ADDRESS_NUM_ERROR =22;
	public static final String LOCAL_SERVER_ADDRESS_NUM_ERROR_INFO = "LOCAL_SERVER_ADDRESS_NUM��������";
	
	public static final int LOCAL_SERVER_ADDRESS_NUM_ZERO =90;
	public static final String LOCAL_SERVER_ADDRESS_NUM_ZERO_INFO = "LOCAL_SERVER_ADDRESS_NUM����Ϊ0��û��������Ϣ�м��";
	
	
	public static final int ADDRESS_PARSE_ERROR =23;
	public static final String ADDRESS_PARSE_ERROR_INFO = "�ַ���ת��Ϊ��ַʧ��";
	
	public static final int LOCAL_SERVER_ADDRESS_PARSE_ERROR = 24;
	public static final String LOCAL_SERVER_ADDRESS_PARSE_ERROR_INFO = "LOCAL_SERVER_ADDRESS������������";
	
	public static final int LOCAL_SERVER_ADDRESS_EQUAL_ERROR = 25;
	public static final String LOCAL_SERVER_ADDRESS_EQUAL_ERROR_INFO = "LOCAL_SERVER_ADDRESS���õĽ�����Ϣ��SERVER���ֵĸ�����LOCAL_SERVER_ADDRESS_NUM�����";
	
	public static final int TASK_QUEUE_CONTAINER_NAME_ERROR = 26;
	public static final String TASK_QUEUE_CONTAINER_NAME_ERROR_INFO ="TASK_QUEUE_CONTAINER_NAME��������";
	
	public static final int TASK_QUEUE_COUNT_ERROR = 27;
	public static final String TASK_QUEUE_COUNT_ERROR_INFO="TASK_QUEUE_TABLE_COUNTֻ������Ϊ����1��������2";
	
	public static final int TASK_QUEUE_NAME_ERROR = 28;
	public static final String TASK_QUEUE_NAME_ERROR_INFO="HIEP_config.ini�����ļ�TASK_QUEUE_NAME��������";
	
	public static final int TASK_QUEUE_LOCAL_NAME_ERROR = 29;
	public static final String TASK_QUEUE_LOCAL_NAME_ERROR_INFO="HIEP_config.ini�����ļ�TASK_QUEUE_LOCAL_NAME��������";
	
	public static final int TASK_QUEUE_REMOTE_NAME_ERROR = 30;
	public static final String TASK_QUEUE_REMOTE_NAME_ERROR_INFO="HIEP_config.ini�����ļ�TASK_QUEUE_REMOTE_NAME��������";
	
	public static final int RECEIVED_MESSAGE_FILE_DIRECTORY_ERROR = 31;
	public static final String RECEIVED_MESSAGE_FILE_DIRECTORY_ERROR_INFO = "RECEIVED_MESSAGE_FILE_DIRECTORY�����ļ�Ŀ¼��·������";
	
	public static final int NEW_MESSAGE_FILE_DIRECTORY_ERROR = 32;
	public static final String NEW_MESSAGE_FILE_DIRECTORY_ERROR_INFO = "NEW_MESSAGE_FILE_DIRECTORY_ERROR�����ļ�Ŀ¼��·������";
	
	public static final int EXPIRED_TIME_MESSAGE_FILE_DIRECTORY_ERROR = 33;
	public static final String EXPIRED_TIME_MESSAGE_FILE_DIRECTORY_ERROR_INFO = "EXPIRED_TIME_MESSAGE_FILE_DIRECTORY�����ļ�Ŀ¼��·������";
	
	public static final int ERROR_MESSAGE_FILE_DIRECTORY_ERROR = 34;
	public static final String ERROR_MESSAGE_FILE_DIRECTORY_ERROR_INFO = "ERROR_MESSAGE_FILE_DIRECTORY�����ļ�Ŀ¼��·������";
	
	public static final int SEND_MESSAGE_FILE_DIRECTORY_ERROR = 35;
	public static final String SEND_MESSAGE_FILE_DIRECTORY_ERROR_INFO = "SEND_MESSAGE_FILE_DIRECTORY�����ļ�Ŀ¼��·������";
	
	public static final int MIN_DISK_SPACE_CONFIG_ERROR = 36;
	public static final String MIN_DISK_SPACE_CONFIG_ERROR_INFO = "MIN_DISK_SPACE��������Ϊ����1����������";
	
	public static final int MIN_DISK_SPACE_ERROR = 37;
	public static final String MIN_DISK_SPACE_ERROR_INFO = "����ϵͳ����СӲ�̿ռ䲻�㹻";
	
	public static final int ROUTER_PORT_ERROR = 38;
	public static final String ROUTER_PORT_ERROR_INFO ="ROUTER_PORTֻ������Ϊ1025��65535֮�����������";

	public static final int DISPATCHER_PORT_ERROR = 39;
	public static final String DISPATCHER_PORT_ERROR_INFO ="DISPATCHER_PORTֻ������Ϊ1025��65535֮�����������";

	public static final int DISPATCHER_PORT_EQUAL_ROUTER_PORT_ERROR =40;
	public static final String DISPATCHER_PORT_EQUAL_ROUTER_PORT_ERROR_INFO ="ROUTER_PORT��DISPATCHER_PORT��������Ϊ��ͬ������";
	
	public static final int THREAD_NUMBER_ERROR = 41;
	public static final String THREAD_NUMBER_ERROR_INFO ="THREAD_NUMBER��������Ϊ5��20����������";
	
	public static final int ROUTER_PROCESSING_INTERVAL_ERROR = 42;
	public static final String ROUTER_PROCESSING_INTERVAL_ERROR_INFO ="ROUTER_PROCESSING_INTERVAL��������Ϊ1��65535����������";

	public static final int DISPARTER_PROCESSING_INTERVAL_ERROR = 43;
	public static final String DISPARTER_PROCESSING_INTERVAL_ERROR_INFO ="DISPARTER_PROCESSING_INTERVAL��������Ϊ1��65535����������";
	
	public static final int RECEIVE_MESSAGE_PRIORITY_MODE_ERROR = 44;
	public static final String RECEIVE_MESSAGE_PRIORITY_MODE_ERROR_INFO ="RECEIVE_MESSAGE_PRIORITY_MODE��������Ϊ1��3����������";
	
	public static final int LOCAL_MESSAGE_PROCESS_PRIORITY_MODE_ERROR = 45;
	public static final String LOCAL_MESSAGE_PROCESS_PRIORITY_MODE_ERROR_INFO ="LOCAL_MESSAGE_PROCESS_PRIORITY_MODE��������Ϊ1��3����������";
	
	public static final int REMOTE_MESSAGE_PROCESS_PRIORITY_MODE_ERROR = 46;
	public static final String REMOTE_MESSAGE_PROCESS_PRIORITY_MODE_ERROR_INFO ="REMOTE_MESSAGE_PROCESS_PRIORITY_MODE��������Ϊ1��3����������";
	
	public static final int RELATIVE_EXPIRED_TIME_ERROR = 47;
	public static final String RELATIVE_EXPIRED_TIME_ERROR_INFO ="RELATIVE_EXPIRED_TIME��������Ϊ�Ǹ�����";
	
	public static final int EXCHANGE_TRACE_MODE_ERROR = 48;
	public static final String EXCHANGE_TRACE_MODE_ERROR_INFO ="EXCHANGE_TRACE_MODEֻ����������Ϊ1����2";
	
	public static final int DUPLICATE_TARGET=49;
	public static final String DUPLICATE_TARGET_INFO= "·�ɼ�¼��Ŀ�ĵ�ַ�Ѿ���֮ǰ�ļ�¼�г���";
	
	public static final int LOAD_ROUTER_TABLE_CONFIG_ERROR = 50;
	public static final String LOAD_ROUTER_TABLE_CONFIG_ERROR_INFO = "HIEP_routerTable.ini�ļ�����ʧ��";
	
	public static final int LOAD_ADDRESS_MAP_CONFIG_ERROR = 51;
	public static final String LOAD_ADDRESS_MAP_CONFIG_ERROR_INFO = "HIEP_addressMap.ini�ļ�����ʧ��";
	
	public static final int LOAD_PROCESSOR_MAP_CONFIG_ERROR = 52;
	public static final String LOAD_PROCESSOR_MAP_CONFIG_ERROR_INFO = "HIEP_processorMap.ini�ļ�����ʧ��";
	
	public static final int ROUTER_NEXT_ADDRESS_MISSING_ADDRESS_ERROR = 53;
	public static final String ROUTER_NEXT_ADDRESS_MISSING_ADDRESS_ERROR_INFO = "·�ɼ�¼�е���һ����ַû����HIEP_addressMap.ini�ļ������ö�Ӧ�ĵ�ַ��¼";
	
	public static final int CREATE_DATABASE_ERROR = 54;
	public static final String CREATE_DATABASE_ERROR_INFO ="�����¼����������ʼ��ʧ��";
	
	public static final int CONNECT_DATABASE_ERROR = 55;
	public static final String CONNECT_DATABASE_ERROR_INFO ="�����¼������������ʧ��";
	
	public static final int PID_FILE_EXIST_ERROR = 56;
	public static final String PID_FILE_EXIST_ERROR_INFO = "PID�ļ�����";
	
	public static final int CREATE_PID_FILE_ERROR = 57;
	public static final String CREATE_PID_FILE_ERROR_INFO = "PID�ļ�����ʧ��";
	
	public static final int DUPLICATE_PROCESSOR_NAME = 58;
	public static final String DUPLICATE_PROCESSOR_NAME_INFO = "��ͬ�����ݴ����ź��������͵ļ�¼�Ѿ���֮ǰ�ļ�¼�г���";
	
	public static final int LOAD_THIRD_JAR_ERROR = 59;
	public static final String LOAD_THIRD_JAR_ERROR_INFO = "EXTĿ¼�µ�JAR������ʧ��";
	
	public static final int FILE_BYTEBUFFER_READ_IO_ERROR = 60;
	public static final String FILE_BYTEBUFFER_READ_IO_ERROR_MESSAGE="�ļ��ڶ�ȡ�����г���";
	
	public static final int FILE_BYTEBUFFER_CLOSE_ERROR = 61;
	public static final String FILE_BYTEBUFFER_CLOSE_ERROR_INFO = "�ļ���BYTEBUFFER���ر�ʧ�ܣ�";
	
	public static final int THREAD_SLEEP_INTERRUPT_ERROR = 62;
	public static final String THREAD_SLEEP_INTERRUPT_ERROR_INFO = "�߳�˯�߱��쳣�ж�";
	
	public static final int TASK_QUEUE_GET_ERROR = 63;
	public static final String TASK_QUEUE_GET_ERROR_INFO = "�����¼���л�ȡʧ��";
	
	public static final int TASK_QUEUE_EXCEPTION_ERROR = 64;
	public static final String TASK_QUEUE_EXCEPTION_ERROR_INFO = "�����¼���з����쳣";
	
	public static final int CONNECTION_ERROR = 65; 
	public static final String CONNECTION_ERROR_INFO = "����Ϣ�м����������ʧ��";
	
	public static final int PUT_MESSAGE_COMMUNICATION_ERROR = 66;
	public static final String PUT_MESSAGE_COMMUNICATION_ERROR_INFO = "Զ����Ϣ����ģ�鷢����Ϣʧ��";
	
	public static final int FAILED_PUT_TASK = 67;
	public static final String FAILED_PUT_TASK_INFO="�������¼���������¼����ʧ��";
	
	public static final int EXIST_SAME_ID_TASK_ERROR = 68;
	public static final String EXIST_SAME_ID_TASK_ERROR_INFO="��ͬID�������¼����";
	
	public static final int OPEN_TASK_QUEUE_ERROR = 69;
	public static final String OPEN_TASK_QUEUE_ERROR_INFO="�����¼�������Ӵ���ʧ��";
	
	public static final int OPEN_TASK_QUEUE_SQLEXCEPTION_ERROR = 70;
	public static final String OPEN_TASK_QUEUE_SQLEXCEPTION_ERROR_INFO="�����¼���д�ʧ��";
	
	public static final int PARSE_TARGRT_ADDRESS_ERROR = 71;
	public static final String PARSE_TARGRT_ADDRESS_ERROR_INFO = "��Ϣ��Ŀ�ĵ�ַ��������";
	
	public static final int ROUTER_GROUP_ERROR = 72;
	public static final String ROUTER_GROUP_ERROR_INFO = "��Ϣ��Ŀ�ĵ�ַ·�ɽ���ʧ��";
	
	public static final int PUT_ONE_TASK_INTO_TASK_QUEUE_ERROR = 73;
	public static final String PUT_ONE_TASK_INTO_TASK_QUEUE_ERROR_INFO = "�Ž�һ�������¼���������ʧ��";
	
	public static final int GET_DATA_ERROR = 74;
	public static final String GET_DATA_ERROR_INFO = "�Ӵ���Ϣ�м����ȡ��Ϣʧ��";
	
	public static final int MESSAGE_FORMAT_ERROR = 75;
	public static final String MESSAGE_FORMAT_ERROR_INFO = "��Ϣ���������г��ָ�ʽ����";
	
	public static final int FILE_IO_ERROR = 76;
	public static final String FILE_IO_ERROR_INFO = "�ļ���д�����г���";
	
	public static final int TLQ_CONNECTION_URL_ERROR = 77;
	public static final String TLQ_CONNECTION_URL_ERROR_INFO = "��TONGLINGQUEUE��������ʧ�ܣ�url����";
	
	public static final int TLQ_CLOSE_CONNECTION_ERROR = 78;
	public static final String TLQ_CLOSE_CONNECTION_ERROR_INFO = "TLQ�ر�����ʧ��";
	
	public static final int TLQ_RECEIVER_MESSAGE_ERROR = 79;
	public static final String TLQ_RECEIVER_MESSAGE_ERROR_INFO = "TLQ����Ϣ���ն������쳣";
	
	public static final int TLQ_SENDER_CONNECTION_ERROR = 80;
	public static final String TLQ_SENDER_CONNECTION_ERROR_INFO = "TLQ����Ϣ���Ͷ������쳣";
	
	public static final int PUT_MESSAGE_ERROR = 81;
	public static final String PUT_MESSAGE_ERROR_INFO = "����һ����Ϣʧ��";
	
	public static final int CREATE_TLQ_MESSAGE_ERROR = 82;
	public static final String CREATE_TLQ_MESSAGE_ERROR_INFO = "����һ��TLQ��Ϣʧ��";

	public static final int TLQ_ACKNOWLEDGE_ERROR = 83;
	public static final String TLQ_ACKNOWLEDGE_ERROR_INFO = "TLQȷ�Ͻ�����Ϣ�쳣";

	public static final int TLQ_INITIAL_CONTEXT_ERROR = 84;
	public static final String TLQ_INITIAL_CONTEXT_ERROR_INFO = "TLQ�����ĳ�ʼ���쳣";

	public static final int TLQ_LOOKUP_ERROR = 85;
	public static final String TLQ_LOOKUP_ERROR_INFO = "TLQѰ�����ӹ����Ͷ����쳣";

	public static final int TLQ_CREATE_CONNECTION_ERROR = 86;
	public static final String TLQ_CREATE_CONNECTION_ERROR_INFO = "TLQ���������쳣";

	public static final int TLQ_CREATE_SENDER_ERROR = 87;
	public static final String TLQ_CREATE_SENDER_ERROR_INFO ="TLQ������Ϣ���Ͷ��쳣";

	public static final int TLQ_CREATE_RECEIVER_ERROR = 88;
	public static final String TLQ_CREATE_RECEIVER_ERROR_INFO ="TLQ������Ϣ���ն��쳣";

	public static final int TASK_QUEUE_CREATE_CONNECTION_ERROR = 89;
	public static final String TASK_QUEUE_CREATE_CONNECTION_ERROR_INFO ="�����¼�������Ӵ���ʧ��";
}
