package com.greatsoft.transq.core.task;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.greatsoft.transq.exception.TaskQueueException;
import com.greatsoft.transq.exception.TaskQueueNotExistException;
import com.greatsoft.transq.utils.ConstantValue;
import com.greatsoft.transq.utils.ErrorCode;
import com.greatsoft.transq.utils.Log;

/**
 * ϵͳ����Զ��ֻ��һ��TaskQueueManger�� �õ���ģʽ��ʵ��������ܣ�ά��DB
 * */
public class TaskQueueManagerImp implements TaskQueueManager {

	private static Logger log = Log.getLog(TaskQueueManagerImp.class);

	private static Connection connection=null;;
	public static String dbName;
	/** �������ݿ��Ƿ����ʹ�� */
	private static final String TEST_TABLE_NAME = "test";
	
	private static TaskQueueManagerImp taskQueueManagerImp=null;
	
	private  TaskQueueManagerImp(String dbname) {
		dbName = dbname;
	}
	
	public static TaskQueueManagerImp getInstance(String dbname) {
		if(taskQueueManagerImp==null){
			taskQueueManagerImp=new TaskQueueManagerImp(dbname);
		}
		return taskQueueManagerImp;
	}

/**
 * ������ݿ��ļ��Ƿ���ڣ��Ƿ����
 * @param dbName
 * @return
 */
	public boolean init(String dbName) {
		/**������ݿ��ļ��Ƿ���ڣ����⣬��������ļ��ٻ������� ����TaskQueue���ݿ�*/
		try {
			Class.forName(ConstantValue.JDBC_URL);
		} catch (ClassNotFoundException e) {
			log.error(ConstantValue.CLASSNAME+ConstantValue.JDBC_URL+e.getMessage());
			return false;
		}
		try {
			connection = DriverManager.getConnection(ConstantValue.CONNECTION_URL+ConstantValue.DATANAME_CONNECTION+dbName);
		} catch (SQLException e) {
			log.error(ErrorCode.getErrorMessage(ErrorCode.CONNECT_DATABASE_ERROR)+e.getMessage());
			return false;
		}
		if (connection == null) {
			log.error(ErrorCode.getErrorMessage(ErrorCode.CONNECT_DATABASE_ERROR));
			return false;
		}
		if (!createTable(dbName, TEST_TABLE_NAME)) {
			return false;
		}
		return true;
	}

	@Override
	public TaskQueue getTaskQueue(String dbName, String queueName) {
		/**�˴�ֻ��Ҫ׼��������һ��taskqueue�ṩһ�����ӣ���ֻ��һ�����������ӻ������
		// ֱ�Ӹ����ݿ�����ֻ��ߴ���һ���µĶ����̰߳�ȫ�Ͷ��̻߳�����ʶ*/
		if (isExist(queueName)) {
			/**��������*/
			log.info("�����¼���д��ڣ�name="+queueName);
			TaskQueue taskQueue = new TaskQueueImp(queueName, null);
			try {
				if (!taskQueue.openTaskQueue(dbName)) {
					return null;
				}
			} catch (TaskQueueException e) {
				log.error("�����¼��������ʧ�ܣ�name="+queueName+e.getMessage());
				return null;
			}
			return taskQueue;
		} else {
			log.info("�����¼���в����ڣ�name="+queueName);
			TaskQueue taskQueue = createTaskQueue(dbName, queueName, null);
			if (taskQueue == null) {
				log.error("�����¼���д���ʧ�ܣ�name="+queueName);
				return null;
			}
			try {
				if (!taskQueue.openTaskQueue(dbName)) {
					log.error("�����¼��������ʧ�ܣ�name="+queueName);
					return null;
				}
			} catch (TaskQueueException e) {
				log.error("�����¼��������ʧ�ܣ�name="+queueName+e.getMessage());
				return null;
			}
			return taskQueue;
		}
	}

	@Override
	public TaskQueue createTaskQueue(String dbName, String queueName,
			TaskQueueProperties options) {
		/**�����Ӧ�ı����ھʹ���һ�����������ݿ���ڵ��жϣ�*/
		if (createTable(dbName, queueName)) {
			TaskQueue taskQueue = new TaskQueueImp(queueName, options);
			return taskQueue;
		} else {
			return null;
		}
	}

	@Override
	public boolean DeleteTaskQueue(String queueName)throws TaskQueueNotExistException {
		FileLock fileLock = null;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream("file.lck");
		} catch (FileNotFoundException e1) {
			log.error("�����¼����ɾ��ʧ�ܣ�name="+queueName+e1.getMessage());
			return false;
		}
		try {
			fileLock = fos.getChannel().tryLock();
		} catch (IOException e) {
			log.error("�����¼����ɾ��ʧ�ܣ�name="+queueName+e.getMessage());
			return false;
		}
		if (fileLock != null) {
			/**�ж�table�Ƿ����*/
			if (isExist(queueName)) {
				/**������ڣ�ɾ����Ӧ��table����ͬ��������ʵ������*/
				String sql = "drop table if exists " + queueName;
				PreparedStatement preparedStatement = null;
				try {
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.executeUpdate();
					preparedStatement.close();
				} catch (SQLException e) {
					log.error("�����¼����ɾ��ʧ�ܣ�name="+queueName+e.getMessage());
					return false;
				} finally {
					try {
						fileLock.release();
						fileLock=null;
						fos.close();
						fos=null;
					} catch (IOException e) {
						log.error("�����¼�������ر�ʧ�ܣ�name="+queueName+e.getMessage());
					}
				}
			} else {/**���򲻴���*/
				log.info("��ɾ���������¼���в����ڣ�name="+queueName);
				return false;
			}
		}
		return true;
	}

	@Override
	public Boolean isExist(String queueName) {
		/**TODO ����ʵ�֣���ʱ���ֲ�����ÿ�ζ��������ݿ⣬ʵ�ʽ������ݿ��Ҷ�Ӧ�ı�
		// �жϷ����Ƿ�Ϊ1��countЧ�ʸ��ߣ����������ܶ�Ч��

		// �ж϶�Ӧ�����ݿ⣬��һ���жϱ��Ƿ����*/
		List<String> tableNameList = getTableNames();
		if (tableNameList == null) {
			log.error("�����¼�����Ѿ��𻵣�name="+queueName);
			return false;
		}
		int length = tableNameList.size();
		for (int i = 0; i < length; i++) {
			if (queueName.equals(tableNameList.get(i))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String[] listQueue() {
		List<String> tableNameList = getTableNames();

		int length = tableNameList.size();
		String[] tables = new String[length];

		for (int i = 0; i < length; i++) {
			tables[i] = tableNameList.get(i);
		}
		return tables;
	}

	private boolean createTable(String dbName, String queueName) {
		Statement statement = null;
		String sql = "CREATE TABLE IF NOT EXISTS '"
				+ queueName
				+ "'(id TEXT primary key,priority Integer,enterTime TEXT,expiredTime TEXT,status Integer,dataType TEXT,toAddress TEXT,sourceDataName TEXT,dataIdentify TEXT,sendIdentify TEXT);";
		try {
			statement = connection.createStatement();
			statement.executeUpdate(sql);
			statement.close();
		} catch (SQLException e) {
			log.error("�����¼���д���ʧ�ܣ�name="+queueName+e.getMessage());
			return false;
		}finally{
			try {
				statement.close();
			} catch (SQLException e) {
				log.error("�����¼���йر�ʧ��");
			}
		}
		log.info("�����¼���д����ɹ���name="+queueName);
		return true;
	}

	private List<String> getTableNames() {
		String sql = "select * FROM Sqlite_master where type='table'";
		PreparedStatement preparedStatement = null;
		ResultSet resultset = null;
		List<String> tableNames = new ArrayList<String>();
		try {
			preparedStatement = connection.prepareStatement(sql);
			resultset = preparedStatement.executeQuery();

			while (resultset.next()) {/**next���ǵ�һ�γ���Ļ�������ֵ��Ϊ�գ�����*/
				String tableName = resultset.getString("name");
				tableNames.add(tableName);
			}
		} catch (SQLException e) {
			// ���ǳ�����,���ݿ��ļ����ƻ����߲�����
			log.error("���ݿ��ļ���,���޸����ݿ�,�ŵ�ԭʼĿ¼����");
			return null;
		}
		return tableNames;
	}

	public void closeConneciton() {
		boolean isClosed = false;
		if (connection != null) {
			while (!isClosed) {
				try {
					Thread.sleep(ConstantValue.THRED_GET_WRITE_DATABASE_INTERVAL);
				} catch (InterruptedException e1) {
					log.error(ErrorCode.getErrorMessage(ErrorCode.THREAD_SLEEP_INTERRUPT_ERROR)+e1.getMessage());
				}
				try {
					connection.close();
				} catch (SQLException e) {
					log.error("�����¼�������ӹر�ʧ��"+e.getMessage());
					isClosed = false;
					continue;
				}
				isClosed = true;
			}
			connection = null;
		}

	}
}
