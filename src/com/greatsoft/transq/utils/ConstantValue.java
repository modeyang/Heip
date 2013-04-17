package com.greatsoft.transq.utils;

import java.nio.ByteOrder;

public class ConstantValue {

	/** Main�У��߳�������� */
	public static final String threadGroupName = "HiepThreadGroup";
	/** Dispatcher�û���־������ */
	public static final String DISPATCHER_USER_LOG = "userLogDis";
	
	/** Router�û���־������ */
	public static final String ROUTER_USER_LOG = "userLogRouter";

	/** ����ϵͳ��Ӧ�ı�ţ�δ֪����ϵͳ=0 */
	public static final int OS_UNKNOW = 0;
	/** ����ϵͳ��Ӧ�ı�ţ�WINDOWS ����ϵͳ=1 */
	public static final int OS_WINDOWS = 1;
	/** ����ϵͳ��Ӧ�ı�ţ�UNIX ����ϵͳ=2 */
	public static final int OS_UNIX = 2;

	public static final String COLON = ":";
	public static final String EQUAL_OPERATOR = "=";
	public static final String NULL_STRING = "";
	public static final String NUMBER_STRING = "[0-9]*";
	public static final String MORE_PATH_STRING = "//";
	public static final String TAB_OR_SPACE = "(" + (char) 32 + "|" + (char) 9
			+ ")+";

	/** ��Ϣ��ַ�ָ��� */
	public static final String ADDRESS_SEPARATOR = ";|��";
	/** ��Ϣ��ַ�ָ��� */
	public static final String ADDRESS_SEPARATOR_ONE = ";";
	/** һ����ַ�ڵ����ݲ��ŷָ��� */
	public static final String SINGLE_ADDRESS_SPLIT_STRING = ",|��";
	/** һ����ַ�ڵ����ݴ����ź�HIEP�����ڵ��ַ�ָ��� */
	public static final String PARSE_SINGLE_ADDRESS_STRING = "@";
	/** HIEP�ڵ���ص��������ݴ����� */
	public static final String ALL_DEPARTMENT_STRING = "*";

	public static final String UUID_SEPERATER = "_";

	public static final String TLQ_ADDRESS_BEGIN_STRING = "tlq://";
	public static final String TLQ = "tlq:";

	/** ��ȡע���HIEP_HOME_PATH������ֶ� */
	public static final String SOFTWARE = "SOFTWARE";
	public static final String HIEP = "HIEP";
	public static final String CURRENT_VERSION = "CurrentVersion";
	public static final String HIEP_HOME_PATH = "hiepHomePath";
	public static final String HIEP_HOME = "HIEP_HOME";

	/** ������Ϣ */
	public static final String LOCAL = "����";
	/** Զ����Ϣ */
	public static final String REMOTE = "Զ��";

	/** ��Ϣ�������ȼ���MESSAGE_PROCESS_NO_PRIORITY=�����Ƚ��ȳ����д��� */
	public static final int MESSAGE_PROCESS_NO_PRIORITY = 1;
	/** ��Ϣ�������ȼ���MESSAGE_PROCESS_NO_PRIORITY=������Ϣ���ȼ����д��� */
	public static final int MESSAGE_PROCESS_PRIORITY = 2;
	/** ��Ϣ�������ȼ���MESSAGE_PROCESS_NO_PRIORITY=�������ʧЧ��Ϣ���д��� */
	public static final int MESSAGE_PROCESS_TIME_PRIORITY = 3;

	/** δ����������¼ */
	public static final int TASK_UNPROCESSED = 0x00000001;
	/** ���ڴ���������¼ */
	public static final int TASK_PROCESSING = 0x00000002;
	/** �����쳣�жϵ������¼ */
	public static final int TASK_INTERRUPT = 0x00000003;
	/** ��ʱ�������¼ */
	public static final int TASK_EXPIRED_TIME = 0x00000004;

	/** ������Ϣ������룺���ͳɹ� */
	public static final int PROCESS_SUCCESS = 0x00000000;
	public static final int PROCESS_FAILED = 0x00000001;

	public static final String PROCESS_SUCCESS_INFO = "��Ϣ���ͳɹ�";
	public static final String PROCESS_FAILED_INFO = "��Ϣ����ʧ��";

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

	/** ���ݿ������ */
	public static final int DB_PORT = 5909;

	public static final String DISPATCHER_COMMAND_PROCESSOR_NAME = "dispatcherCommandProcessor";

	public static final String DELETE_FILE_WORKER_NAME = "deleteFileWorker";

	public static final String DISPATCHER_SERVER_PROCESS_NAME = "dispatcherProcess";

	public static final String DISPATCHER_SERVER_ROUTE_NAME = "dispatcherRoute";

	public static final String ROUTER_SERVER_NAME = "routerServerThread";

	public static final String ROUTER_COMMAND_PROCESS_NAME = "routerCommandProcessThread";
	// /** ��ʱɾ��Ŀ¼ */
	// public static final String DELETE_MESSAGE_DIR =
	// "data/message/deleteMessage/";

	/** HIEP_config.ini */
	public static final String HIEP_CONFIG_FILE_NAME = "HIEP_config.ini";
	/** HIEP_config_custom.ini */
	public static final String HIEP_CONFIG_CUSTOM_FILE_NAME = "HIEP_config_ad.ini";
	/** HIEP_routerTable.ini��Ϊ·�ɱ������ļ� */
	public static final String ROUTER_TABLE_FILE_NAME = "HIEP_routerTable.ini";
	/** Ϊ���ݴ�������ҵ��ϵͳӳ�����������ĵ� */
	public static final String PROCESSOR_MAP_FILE_NAME = "HIEP_processorMap_syn.ini";
	/** Ϊ���������ӳ����������ĵ� */
	public static final String ADDRESS_MAP_FILE_NAME = "HIEP_addressMap.ini";
	/** ΪRouterģ�����־�����ļ� */
	public static final String ROUTER_LOG4J_PROPERTIES_FILE_NAME = "routerlog4j.properties";
	/** ΪDispatcherģ�����־�����ļ� */
	public static final String DISPARTER_LOG4J_PROPERTIES_FILE_NAME = "dispatcherlog4j.properties";
	/** ΪRouterģ���PID�ļ� */
	public static final String ROUTER_PID_FILE_NAME = "router.pid";
	/** ΪDispatcherģ���PID�ļ� */
	public static final String DISPATCHER_PID_FILE_NAME = "dispatcher.pid";

	/** Ϊ�����ļ������Ŀ¼ */
	public static String CONFIG_DIRECTORY = "config/";
	/** ������JAR��������·�� */
	public static final String EXT_DIRECTORY = "ext/";
	/** ��¼��־�����Ŀ¼ */
	public static final String LOG_DIRECTORY = "data/log/";
	/** Ϊ������Ϣ���е����Ŀ¼ */
	public static final String RELATIVE_RECEIVED_MESSAGE_FILE_DIRECTORY = "data/message/received/";
	/** Ϊ����Ϣ���е����Ŀ¼ */
	public static final String RELATIVE_NEW_MESSAGE_FILE_DIRECTORY = "data/message/new/";
	/** Ϊ��ʱ��Ϣ���е����Ŀ¼ */
	public static final String RELATIVE_EXPIRED_TIME_MESSAGE_FILE_DIRECTORY = "data/message/expiredTime/";
	/** ������Ϣ���е����Ŀ¼ */
	public static final String RELATIVE_ERROR_MESSAGE_FILE_DIRECTORY = "data/message/error/";
	/** Ϊ������Ϣ���е����Ŀ¼ */
	public static final String RELATIVE_SEND_MESSAGE_FILE_DIRECTORY = "data/message/send/";
	/** Ϊɾ����Ϣ���е����Ŀ¼ */
	public static final String DELETE_MESSAGE_FILE_DIRECTORY = "data/message/delete/";

	public static final String ENTER = "\n";
	public static final String PATH = ":path=";
	public static final String LINE = ":line=";
	public static final String ADDURL = "addURL";
	public static final String ADDRESS = "��address=";
	public static final String CLASSNAME = "className=";
	public static final String DATANAME_CONNECTION = "./";
	public static final String TYPE = "��type=";
	public static final String SUBADRESS = "��subAddress=";
	public static final String JAR = ".jar";
	public static final String USE_DEFAULT_CONFIGURATION = "ʹ��Ĭ�����ã�";
	public static final String THREAD_NAME = "��routerServer�߳�";
	public static final String THREAD_GROUP_NAME = "·�ɴ���ģ���̼߳���";
	public static final long THRED_GET_WRITE_DATABASE_INTERVAL = 20;

	/** ������Ϣ������ɹ� */
	public static final int PUT_MESSAGE_SUCCESS = 0x00000000;
	/** ������Ϣ�����ʧ�� */
	public static final int PUT_MESSAGE_FAILED = 0x00000001;
	/** ����Ϣ�м����ȡ��Ϣ����Ч�ȴ�ʱ�� */
	public static final int WAIT_TIME = 30000;

	/** webservice�������� **/
	public static final String PROCESSOR_MAP_FILE = "config/HIEP_processorMap_ws.ini";

	public static final String WS_CONFIG_FILE = "config/HIEP_WebServiceConfig.ini";
	
	public static final String WS_LOG_FILE = "config/HIEP_WebServiceLog4j.properties";
	
	public static String CONNECTION_TIME = "";
	public static String WSSERVER_WAITINT_TIME = "";

}
