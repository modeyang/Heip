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
 * 系统中永远都只有一个TaskQueueManger。 用单例模式来实现这个功能，维护DB
 * */
public class TaskQueueManagerImp implements TaskQueueManager {

	private static Logger log = Log.getLog(TaskQueueManagerImp.class);

	private static Connection connection=null;;
	public static String dbName;
	/** 测试数据库是否可以使用 */
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
 * 检查数据库文件是否存在，是否可用
 * @param dbName
 * @return
 */
	public boolean init(String dbName) {
		/**检查数据库文件是否存在，建库，建表。如果文件毁坏，报错。 连接TaskQueue数据库*/
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
		/**此处只需要准备环境，一个taskqueue提供一个连接，若只有一个对象，则连接会出现锁
		// 直接给数据库的名字或者创建一个新的对象，线程安全和多线程环境意识*/
		if (isExist(queueName)) {
			/**如果表存在*/
			log.info("任务记录队列存在：name="+queueName);
			TaskQueue taskQueue = new TaskQueueImp(queueName, null);
			try {
				if (!taskQueue.openTaskQueue(dbName)) {
					return null;
				}
			} catch (TaskQueueException e) {
				log.error("任务记录队列连接失败：name="+queueName+e.getMessage());
				return null;
			}
			return taskQueue;
		} else {
			log.info("任务记录队列不存在：name="+queueName);
			TaskQueue taskQueue = createTaskQueue(dbName, queueName, null);
			if (taskQueue == null) {
				log.error("任务记录队列创建失败：name="+queueName);
				return null;
			}
			try {
				if (!taskQueue.openTaskQueue(dbName)) {
					log.error("任务记录队列连接失败：name="+queueName);
					return null;
				}
			} catch (TaskQueueException e) {
				log.error("任务记录队列连接失败：name="+queueName+e.getMessage());
				return null;
			}
			return taskQueue;
		}
	}

	@Override
	public TaskQueue createTaskQueue(String dbName, String queueName,
			TaskQueueProperties options) {
		/**如果对应的表不存在就创建一个表（还有数据库存在的判断）*/
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
			log.error("任务记录队列删除失败：name="+queueName+e1.getMessage());
			return false;
		}
		try {
			fileLock = fos.getChannel().tryLock();
		} catch (IOException e) {
			log.error("任务记录队列删除失败：name="+queueName+e.getMessage());
			return false;
		}
		if (fileLock != null) {
			/**判断table是否存在*/
			if (isExist(queueName)) {
				/**如果存在，删除对应的table，及同名的所有实例对象*/
				String sql = "drop table if exists " + queueName;
				PreparedStatement preparedStatement = null;
				try {
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.executeUpdate();
					preparedStatement.close();
				} catch (SQLException e) {
					log.error("任务记录队列删除失败：name="+queueName+e.getMessage());
					return false;
				} finally {
					try {
						fileLock.release();
						fileLock=null;
						fos.close();
						fos=null;
					} catch (IOException e) {
						log.error("任务记录队列锁关闭失败：name="+queueName+e.getMessage());
					}
				}
			} else {/**否则不存在*/
				log.info("待删除的任务记录队列不存在：name="+queueName);
				return false;
			}
		}
		return true;
	}

	@Override
	public Boolean isExist(String queueName) {
		/**TODO 单独实现，此时这种操作会每次都遍历数据库，实际交给数据库找对应的表，
		// 判断返回是否为1（count效率更高），会提升很多效率

		// 判断对应的数据库，进一步判断表是否存在*/
		List<String> tableNameList = getTableNames();
		if (tableNameList == null) {
			log.error("任务记录队列已经损坏：name="+queueName);
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
			log.error("任务记录队列创建失败：name="+queueName+e.getMessage());
			return false;
		}finally{
			try {
				statement.close();
			} catch (SQLException e) {
				log.error("任务记录队列关闭失败");
			}
		}
		log.info("任务记录队列创建成功：name="+queueName);
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

			while (resultset.next()) {/**next不是第一次出错的话，返回值不为空，考虑*/
				String tableName = resultset.getString("name");
				tableNames.add(tableName);
			}
		} catch (SQLException e) {
			// 考虑出错处理,数据库文件被破坏或者不存在
			log.error("数据库文件损坏,请修复数据库,放到原始目录下面");
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
					log.error("任务记录队列连接关闭失败"+e.getMessage());
					isClosed = false;
					continue;
				}
				isClosed = true;
			}
			connection = null;
		}

	}
}
