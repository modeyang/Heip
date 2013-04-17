package com.greatsoft.transq.utils;

import java.util.List;
import java.util.Map;

import com.greatsoft.transq.core.Address;
import com.greatsoft.transq.core.RouterRecord;

/** ������ */
public class Config {
	/**��ȡ·�ɼ�¼*/
	public static List<RouterRecord> routerRecordList = null;
	/** �洢ҵ���ַ�������ϸ��ַ����� */
	public static Map<String, Address> addressMap = null;
	/**�洢ҵ����ϵͳӳ���ϵ*/
	public static Map<String, String> processorNameMap = null;
	/**���ڵ��HIEP�����˽�����Ϣ��SERVER��ַ*/
	public static Address[] localServerAddress = null;
	
/**********************HIEP��ַ��Ϣ����С��********************************/	
	public static String HIEP_HOME_PATH = null;
	/** ���ڵ��HIEP�����˵�ַ���� */
	public static String LOCAL_ADDRESS = null;
	/**���ڵ��HIEP�����˽�����Ϣ��SERVER�ĸ�����������Ϊ1��10֮�����������*/
	public static int LOCAL_SERVER_ADDRESS_NUM=-1;
	/**���ڵ��HIEP�����˽�����Ϣ��SERVER�����֣����������Ϣ��SERVER�ĵ�ַ֮���ԡ�;������*/
	public static String LOCAL_SERVER_ADDRESS = null;
	
/**********************�����¼��������С��********************/
	/** Ϊ�����¼���й�������������,��Ҫ��װ���ݿ�Sqliteʱ�ɰ�װ��Ա����*/
	public static String TASK_QUEUE_CONTAINER_NAME="TaskQueueContainer";
	/**Ϊrouterʹ�õĴ�������¼���еĸ���*/
	public static int TASK_QUEUE_COUNT = 2;
	/**Ϊ��TASK_QUEUE_TABLE_COUNT=1ʱ,�����¼���е�����*/
	public static String TASK_QUEUE_NAME = "taskQueue";
	/**Ϊ��TASK_QUEUE_TABLE_COUNT=1ʱ,���������¼���е�����*/
	public static String TASK_QUEUE_LOCAL_NAME = "taskQueueLocal";
	/**Ϊ��TASK_QUEUE_TABLE_COUNT=1ʱ,Զ�������¼���е�����*/
	public static String TASK_QUEUE_REMOTE_NAME = "taskQueueRemote";
	/** Ϊ�о������¼�ĸ�������Ϊ��ҳ��������޿���*/
	public static int MAX_TASK_LIST_COUNT = 1000;
	
/**********************��Ϣ����Ŀ¼����С��********************/
	/**Ϊ������Ϣ���е�Ŀ¼*/
	public static String RECEIVED_MESSAGE_FILE_DIRECTORY = null;
	/**Ϊ����Ϣ���е�Ŀ¼*/
	public static String NEW_MESSAGE_FILE_DIRECTORY= null;
	/**Ϊ��ʱ��Ϣ���е�Ŀ¼*/
	public static String EXPIRED_TIME_MESSAGE_FILE_DIRECTORY= null;
	/**������Ϣ���е�Ŀ¼*/
	public static String ERROR_MESSAGE_FILE_DIRECTORY= null;
	/**Ϊ������Ϣ���е�Ŀ¼*/
	public static String SEND_MESSAGE_FILE_DIRECTORY= null;
	
/**********************ϵͳ����Ӳ����������С��********************/
	/**Ϊ��Ŀ����ʱ��С�Ĵ��̿ռ䣨��λΪGB��*/
	public static long MIN_DISK_SPACE = 5* 1024 * 1024*1024;
	/** ��Ŀ����ʱ��Сʣ����̿ռ�*/
	public static long MIN_FREE_DISK_SPACE = 100 * 1024 * 1024;
	/**�̲߳������޵ĸ���*/
	public static int THREAD_NUMBER = 10;
	/**ΪԶ�̹ر�HIEP�����˹�����Routerģ��socket�����˿ڣ���������Ϊ1025��65535֮�����������*/
	public static int ROUTER_PORT = 56789;
	/**ΪԶ�̹ر�HIEP�����˹�����Dispatcherģ��socket�����˿ڣ���������Ϊ1025��65535֮�����������*/
	public static int DISPATCHER_PORT = 56780;
	
/**********************ϵͳ��ѭʱ������С��********************/
	/** ΪROUTER�̵߳���ѭʱ��������λΪ���룩 */
	public static int ROUTER_PROCESSING_INTERVAL = 50;
	/** ΪDISPARTER�̵߳���ѭʱ��������λΪ���룩 */
	public static int DISPARTER_PROCESSING_INTERVAL = 50;
	
/**********************��Ϣ��������С��********************/
	/**Ϊ������Ϣ�����ȼ�ģʽ��1�����ս���������е�˳������Ϣ��2��������Ϣ���ȼ�˳������Ϣ ��3������ʧЧʱ����̵�˳������Ϣ*/
	public static int RECEIVE_MESSAGE_PRIORITY_MODE=1;
	/**Ϊ��������Ϣ�����ȼ�ģʽ,1�����ս���������е�˳������Ϣ��2��������Ϣ���ȼ�˳������Ϣ ��3������ʧЧʱ����̵�˳������Ϣ*/
	public static int LOCAL_MESSAGE_PROCESS_PRIORITY_MODE=1;
	/**Ϊ����Զ����Ϣ�����ȼ�ģʽ,1�����ս���������е�˳������Ϣ��2��������Ϣ���ȼ�˳������Ϣ ��3������ʧЧʱ����̵�˳������Ϣ*/
	public static int REMOTE_MESSAGE_PROCESS_PRIORITY_MODE=1;
	/**Ϊȱʡ����Ϣ�����ʧЧʱ�䣨��λΪ���ӣ�*/
	public static int RELATIVE_EXPIRED_TIME=60;
	/**ΪHIEP�Ľ�����ִ���أ�1�����޻�ִ��2�����л�ִ*/
	public static int EXCHANGE_TRACE_MODE = 1;
	
	public static String DELETE_MESSAGE_FILE_DIRECTORY = null;
}
