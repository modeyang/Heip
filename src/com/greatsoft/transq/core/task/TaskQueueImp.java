package com.greatsoft.transq.core.task;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.greatsoft.transq.exception.TaskQueueException;
import com.greatsoft.transq.utils.ConstantValue;
import com.greatsoft.transq.utils.ErrorCode;
import com.greatsoft.transq.utils.Log;

public class TaskQueueImp implements TaskQueue {
	public static final String CONNECTION_CLOSED="���ݿ������Ѿ��ر�";
	
	private static Logger log = Log.getLog(TaskQueueImp.class);

	private static Connection connection = null;

	private String taskQueueName;

	private TaskQueueProperties taskQueueProperties;

	private int port = ConstantValue.DB_PORT;

	public TaskQueueImp(String taskQueueName,
			TaskQueueProperties taskQueueProperties) {
		this.taskQueueName = taskQueueName;
		this.taskQueueProperties = taskQueueProperties;
	}

	@Override
	public String getName() {
		return this.taskQueueName;
	}

	@Override
	public TaskQueueProperties getProperties() {
		return this.taskQueueProperties;
	}

	@Override
	public Task getFirstOne(String nextID, int status)
			throws TaskQueueException {
		if (connection == null) {
			log.warn(CONNECTION_CLOSED);
			return null;
		}
		Task task = null;
		ResultSet resultset = null;
		PreparedStatement preparedStatement = null;
		String sql = "select * from '" + taskQueueName + "' where status="+ status + " and toAddress='" + nextID + "';";
		try {
			preparedStatement = connection.prepareStatement(sql);
			resultset = preparedStatement.executeQuery();
			if (resultset.next()) {
				String id = resultset.getString("id");
				int priority = resultset.getInt("priority");
				String enterTime = resultset.getString("enterTime");
				String expiredTime = resultset.getString("expiredTime");
				String dataType = resultset.getString("dataType");
				String sourceDataName = resultset.getString("sourceDataName");
				String dataIdentify = resultset.getString("dataIdentify");
				String sendIdentify = resultset.getString("sendIdentify");

				task = new TaskImp(id, priority, enterTime, expiredTime,
						status, dataType, nextID, sourceDataName, dataIdentify,
						sendIdentify);
			}
		} catch (SQLException e) {
			log.error("��ȡָ�������������¼ʧ�ܣ�status=" + status + "toAddress=" + nextID+ e.getMessage());
		} finally {
			try {
				if (resultset != null) {
					resultset.close();
					resultset = null;
				}
				if (preparedStatement != null) {
					preparedStatement.close();
					preparedStatement = null;
				}
			} catch (SQLException e) {
				log.error(e.getMessage());
			}
		}
		return task;
	}

	@Override
	public Task getFirstOne(int status) throws TaskQueueException {
		PreparedStatement preparedStatement = null;
		ResultSet resultset = null;
		String sql = "select * from " + taskQueueName + " where status="
				+ status + ";";
		if (connection == null) {
			log.warn(CONNECTION_CLOSED);
			return null;
		}
		Task task = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			resultset = preparedStatement.executeQuery();
			if (resultset.next()) {
				String id = resultset.getString("id");
				int priority = resultset.getInt("priority");
				String enterTime = resultset.getString("enterTime");
				String expiredTime = resultset.getString("expiredTime");
				String dataType = resultset.getString("dataType");
				String toAddress = resultset.getString("toAddress");
				String sourceDataName = resultset.getString("sourceDataName");
				String dataIdentify = resultset.getString("dataIdentify");
				String sendIdentify = resultset.getString("sendIdentify");

				task = new TaskImp(id, priority, enterTime, expiredTime,
						status, dataType, toAddress, sourceDataName,
						dataIdentify, sendIdentify);
			}
		} catch (SQLException e) {
			log.error("��ȡָ�������������¼ʧ�ܣ�status=" + status + e.getMessage());
		} finally {
			try {
				if (resultset != null) {
					resultset.close();
				}
				resultset = null;
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				preparedStatement = null;
			} catch (SQLException e) {
				log.error("�����¼���йر�ʧ��" + e.getMessage());
			}
		}
		return task;
	}

	@Override
	public List<Task> getTask(String nextID, int status)
			throws TaskQueueException {
		PreparedStatement preparedStatement = null;
		ResultSet resultset = null;

		String sql = "select * from " + taskQueueName + " where status="
				+ status + ";";
		List<Task> taskList = new ArrayList<Task>();
		if (connection == null) {
			log.warn(CONNECTION_CLOSED);
			return null;
		}
		Task task = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			resultset = preparedStatement.executeQuery();
			while (resultset.next()) {
				String id = resultset.getString("id");
				int priority = resultset.getInt("priority");
				String enterTime = resultset.getString("enterTime");
				String expiredTime = resultset.getString("expiredTime");
				String dataType = resultset.getString("dataType");
				String toAddress = resultset.getString("toAddress");
				String sourceDataName = resultset.getString("sourceDataName");
				String dataIdentify = resultset.getString("dataIdentify");
				String sendIdentify = resultset.getString("sendIdentify");

				task = new TaskImp(id, priority, enterTime, expiredTime,
						status, dataType, toAddress, sourceDataName,
						dataIdentify, sendIdentify);
				taskList.add(task);
			}
		} catch (SQLException e) {
			log.error("��ȡָ�����������������¼ʧ�ܣ�status=" + status + "toAddress="
					+ nextID + e.getMessage());
			taskList.removeAll(taskList);
		} finally {
			try {
				if (resultset != null) {
					resultset.close();
				}
				resultset = null;
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				preparedStatement = null;
			} catch (SQLException e) {
				log.error("�����¼���йر�ʧ��" + e.getMessage());
			}
		}
		return taskList;
	}

	public synchronized int putOneTask(Task task) throws TaskQueueException {

		ServerSocket server = getServerSocket(this.port);
		while (server == null) {
			log.warn("û���������������¼���еĶ˿�");
			try {
				Thread.sleep(ConstantValue.THRED_GET_WRITE_DATABASE_INTERVAL);
			} catch (InterruptedException e) {
				log.error(ErrorCode
						.getErrorMessage(ErrorCode.THREAD_SLEEP_INTERRUPT_ERROR)
						+ e.getMessage());
				return ErrorCode.THREAD_SLEEP_INTERRUPT_ERROR;
			}
			server = getServerSocket(this.port);
		}
		log.info("�������������¼���еĶ˿�");

		PreparedStatement preparedStatement = null;
		String id = task.getId();
		if (isTaskExist(taskQueueName, id)) {
			log.error("�����¼���ʧ�ܣ���ͬID�����������Ѿ������������¼���У�ID=" + id);
			closeServerSocket(server);
			return ErrorCode.EXIST_SAME_ID_TASK_ERROR;
		}

		if (connection == null) {
			log.warn(CONNECTION_CLOSED);
			closeServerSocket(server);
			return ErrorCode.FAILED_PUT_TASK;
		}
		String sql = "insert into '" + taskQueueName
				+ "' values(?,?,?,?,?,?,?,?,?,?)";
		try {
			preparedStatement = connection.prepareStatement(sql);
		} catch (SQLException e) {
			log.error("preparedStatement��ȡʧ��" + e.getMessage());
			closeServerSocket(server);
			return ErrorCode.FAILED_PUT_TASK;
		}
		int idx = 1;
		try {
			preparedStatement.setString(idx++, task.getId());
			preparedStatement.setInt(idx++, task.getPriority());
			preparedStatement.setString(idx++, task.getEnterTime().toString());
			preparedStatement
					.setString(idx++, task.getExpiredTime().toString());
			preparedStatement.setInt(idx++, task.getStatus());
			preparedStatement.setString(idx++, task.getDataType());
			preparedStatement.setString(idx++, task.getToAddress());
			preparedStatement.setString(idx++, task.getSourceDataName());
			preparedStatement.setString(idx++, task.getDataIdentfy());
			preparedStatement.setString(idx++, task.getSendIdentify());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			log.error("���������¼ʧ��" + e.getMessage());
			return ErrorCode.FAILED_PUT_TASK;
		} finally {
			closeServerSocket(server);
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				preparedStatement = null;
			} catch (SQLException e) {
				log.error("�����¼���йر�ʧ��");
			}
		}
		log.info("�����¼���������¼���гɹ���ID=" + id);
		return ErrorCode.NO_ERROR;
	}

	@Override
	public boolean putTask(List<Task> tasks) throws TaskQueueException {
		boolean flag = true;
		for (Task task : tasks) {
			if ((putOneTask(task)) != ErrorCode.NO_ERROR) {
				flag = false;
				break;
			}
		}
		return flag;
	}

	@Override
	public synchronized boolean setTask(Task task) throws TaskQueueException {
		ServerSocket server = getServerSocket(port);
		while (server == null) {
			log.warn("û���������������¼���еĶ˿�");
			try {
				Thread.sleep(ConstantValue.THRED_GET_WRITE_DATABASE_INTERVAL);
			} catch (InterruptedException e) {
				log.error(ErrorCode
						.getErrorMessage(ErrorCode.THREAD_SLEEP_INTERRUPT_ERROR)
						+ e.getMessage());
				return false;
			}
			server = getServerSocket(this.port);
		}
		log.info("�������������¼���еĶ˿�");
		String id = task.getId();
		if (!(isTaskExist(taskQueueName, id))) {
			log.error("�޸������¼ʧ�ܣ������¼�����в����ڸ������¼��ID=" + id);
			closeServerSocket(server);
			return false;
		}
		if (connection == null) {
			log.warn(CONNECTION_CLOSED);
			closeServerSocket(server);
			return false;
		}
		String sql = "update "
				+ taskQueueName
				+ " set priority=?,enterTime=?,expiredTime=?,status=?,dataType=?,toAddress=?,sourceDataName=?,dataIdentify=?,sendIdentify=? where id = ?";
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			int idx = 1;
			preparedStatement.setInt(idx++, task.getPriority());
			preparedStatement.setString(idx++, task.getEnterTime().toString());
			preparedStatement
					.setString(idx++, task.getExpiredTime().toString());
			preparedStatement.setInt(idx++, task.getStatus());
			preparedStatement.setString(idx++, task.getDataType());
			preparedStatement.setString(idx++, task.getToAddress());
			preparedStatement.setString(idx++, task.getSourceDataName());
			preparedStatement.setString(idx++, task.getDataIdentfy());
			preparedStatement.setString(idx++, task.getSendIdentify());
			preparedStatement.setString(idx++, task.getId());
			preparedStatement.executeUpdate();
			log.info("�޸�һ�������¼�ɹ���ID=" + id);
			return true;
		} catch (SQLException e) {
			log.error("�޸�һ�������¼�ɹ���ID=" + id + e.getMessage());
			return false;
		} finally {
			closeServerSocket(server);
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				preparedStatement = null;
			} catch (SQLException e) {
				log.error("�����¼���йر�ʧ��");
			}
		}
	}

	@Override
	public synchronized boolean deleteTask(Task task) throws TaskQueueException {
		ServerSocket server = getServerSocket(port);
		while (server == null) {
			log.warn("û���������������¼���еĶ˿�");
			try {
				Thread.sleep(ConstantValue.THRED_GET_WRITE_DATABASE_INTERVAL);
			} catch (InterruptedException e) {
				log.error(ErrorCode
						.getErrorMessage(ErrorCode.THREAD_SLEEP_INTERRUPT_ERROR)
						+ e.getMessage());
				return false;
			}
			server = getServerSocket(this.port);
		}
		log.info("�������������¼���еĶ˿�");
		String id = task.getId();
		if (!(isTaskExist(taskQueueName, id))) {
			log.error("ɾ�������¼ʧ�ܣ������¼�����в����ڸ������¼��ID=" + id);
			closeServerSocket(server);
			return false;
		}
		String sql = "delete from " + taskQueueName + " where id='"
				+ task.getId() + "';";
		if (connection == null) {
			log.warn(CONNECTION_CLOSED);
			closeServerSocket(server);
			return false;
		}
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			log.error("ɾ�������¼ʧ�ܣ�ID=" + id + e.getMessage());
			return false;
		} finally {
			closeServerSocket(server);
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				preparedStatement = null;
			} catch (SQLException e) {
				log.error("�����¼���йر�ʧ��");
			}
		}
		log.info("ɾ�������¼�ɹ���ID=" + id);
		return true;
	}

	@Override
	public void list(String NextAddr, int count, List<Task> tasks)
			throws TaskQueueException {
		List<Task> getTasks = null;
		if (NextAddr == null || NextAddr.equals(ConstantValue.NULL_STRING)) {
			getTasks = selectAllFromTask(taskQueueName);
		} else {
			getTasks = selectAllFromTask(NextAddr);
		}
		int num = 0;
		Iterator<Task> getTasksIterator = getTasks.iterator();
		while (getTasksIterator.hasNext() && (num++) < count) {
			tasks.add(getTasksIterator.next());
		}
		if (num < count) {
			log.info("�о������¼������С��" + count + "����");
		} else {
			log.info("�о������¼������" + count + "����");
		}
		int index = 1;
		for (Task task : tasks) {
			log.info("�����¼" + (index++) + ":" + task);
		}
	}

	@Override
	public synchronized boolean resetAllTask() throws TaskQueueException {
		boolean flag = true;
		List<Task> taskList = selectAllFromTask(taskQueueName);
		Iterator<Task> taskItr = taskList.iterator();
		while (taskItr.hasNext()) {
			Task task = taskItr.next();
			task.setStatus(ConstantValue.TASK_UNPROCESSED);
			if (!(setTask(task))) {
				flag = false;
				log.error("�����¼��״̬����Ϊ��δ����ʧ�ܣ�ID=" + task.getId());
				break;
			}
		}
		return flag;
	}

	public List<Task> selectAllFromTask(String qName) {
		String sql = "select * from " + qName + ";";
		List<Task> taskList = new ArrayList<Task>();
		if (connection == null) {
			log.warn(CONNECTION_CLOSED);
			return null;
		}
		Task task = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultset = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			resultset = preparedStatement.executeQuery();
			while (resultset.next()) {
				String id = resultset.getString("id");
				int priority = resultset.getInt("priority");
				String enterTime = resultset.getString("enterTime");
				String expiredTime = resultset.getString("expiredTime");
				String dataType = resultset.getString("dataType");
				String toAddress = resultset.getString("toAddress");
				String sourceDataName = resultset.getString("sourceDataName");
				String dataIdentify = resultset.getString("dataIdentify");
				String sendIdentify = resultset.getString("sendIdentify");
				int status = resultset.getInt("status");
				task = new TaskImp(id, priority, enterTime, expiredTime,
						status, dataType, toAddress, sourceDataName,
						dataIdentify, sendIdentify);
				taskList.add(task);
			}
		} catch (SQLException e) {
			log.error("�о������¼�����е����������¼����ʧ��" + e.getMessage());
			taskList.removeAll(taskList);
		} finally {
			try {
				if (resultset != null) {
					resultset.close();
				}
				resultset = null;
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				preparedStatement = null;
			} catch (SQLException e) {
				log.error("�����¼���йر�ʧ��" + e.getMessage());
			}
		}
		return taskList;
	}

	private boolean isTaskExist(String qName, String id) {
		String sql = "select * from '" + qName + "' where id='" + id + "';";
		if (connection == null) {
			log.warn(CONNECTION_CLOSED);
			return false;
		}
		boolean flag = false;
		PreparedStatement preparedStatement = null;
		ResultSet resultset = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			resultset = preparedStatement.executeQuery();
			if (resultset.next()) {
				flag = true;
			}
		} catch (SQLException e) {
			log.equals("�ж������¼�Ƿ�����������¼����ʧ�ܣ�ID=" + id + e.getMessage());
		} finally {
			try {
				if (resultset != null) {
					resultset.close();
				}
				resultset = null;
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				preparedStatement = null;
			} catch (SQLException e) {
				log.error("�����¼���йر�ʧ��" + e.getMessage());
			}
		}
		return flag;
	}

	/**
	 * ����TaskQueue���ݿ�
	 */
	public boolean openTaskQueue(String dbName) throws TaskQueueException{
		try {
			Class.forName(ConstantValue.JDBC_URL);
			connection = DriverManager.getConnection(ConstantValue.CONNECTION_URL + dbName);
			if (connection == null) {
				log.error(ErrorCode.getErrorMessage(ErrorCode.TASK_QUEUE_CREATE_CONNECTION_ERROR));
				return false;
			}
		} catch (ClassNotFoundException e) {
			log.error(ConstantValue.JDBC_URL+e.getMessage());
			return false;
		} catch (SQLException e) {
			log.error(e.getMessage());
			return false;
		}
		return true;
	}

	public void closeTaskQueue() {
		if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					log.error(e.getMessage());
				}
				connection = null;
			}
	}

	private static void closeServerSocket(ServerSocket server) {
		if(server!=null){
			try {
				server.close();
			} catch (IOException e) {
				log.error(e.getMessage());
				server = null;
			}
		}
		log.info("���������¼���еĶ˿��ͷųɹ�");
	}

	private static ServerSocket getServerSocket(int port) {
		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			log.error(e.getMessage());
			return null;
		}
		return server;
	}

	/**
	 * ȡ�õ�һ��δ����������¼���������ȼ���ȡ�����ȼ�1--9��9������ȼ�
	 */
	@Override
	public Task getFirstOneByPriority(int status) throws TaskQueueException {
		PreparedStatement preparedStatement = null;
		ResultSet resultset = null;
		String sql = "select * from '" + taskQueueName
				+ "' where status="+ status +" order by priority desc";
		if (connection == null) {
			log.warn(CONNECTION_CLOSED);
			return null;
		}
		Task task = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, ConstantValue.TASK_UNPROCESSED);
			resultset = preparedStatement.executeQuery();
			if (resultset.next()) {
				String id = resultset.getString("id");
				int priority = resultset.getInt("priority");
				String enterTime = resultset.getString("enterTime");
				String expiredTime = resultset.getString("expiredTime");
				String dataType = resultset.getString("dataType");
				String toAddress = resultset.getString("toAddress");
				String sourceDataName = resultset.getString("sourceDataName");
				String dataIdentify = resultset.getString("dataIdentify");
				String sendIdentify = resultset.getString("sendIdentify");

				task = new TaskImp(id, priority, enterTime, expiredTime,
						status, dataType, toAddress, sourceDataName,
						dataIdentify, sendIdentify);
			}
		} catch (SQLException e) {
			log.error("�������ȼ��Ӹߵ��ͻ�ȡ�����¼ʧ��" + e.getMessage());
		} finally {
			try {
				if (resultset != null) {
					resultset.close();
				}
				resultset = null;
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				preparedStatement = null;
			} catch (SQLException e) {
				log.error("�����¼���йر�ʧ��" + e.getMessage());
			}
		}
		return task;
	}

	/**
	 * ����ʧЧʱ����̻�ȡһ��δ����������¼
	 */
	@Override
	public Task getFirstOneByDeadTime(int status) throws TaskQueueException{
		if (connection == null) {
			log.warn(CONNECTION_CLOSED);
			return null;
		}
		String sql = "select * from '" + taskQueueName+ "' where status="+ status +" order by expiredTime asc";
		PreparedStatement preparedStatement = null;
		ResultSet resultset = null;
		Task task = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, ConstantValue.TASK_UNPROCESSED);
			resultset = preparedStatement.executeQuery();
			if (resultset.next()) {
				String id = resultset.getString("id");
				int priority = resultset.getInt("priority");
				String enterTime = resultset.getString("enterTime");
				String expiredTime = resultset.getString("expiredTime");
				String dataType = resultset.getString("dataType");
				String toAddress = resultset.getString("toAddress");
				String sourceDataName = resultset.getString("sourceDataName");
				String dataIdentify = resultset.getString("dataIdentify");
				String sendIdentify = resultset.getString("sendIdentify");

				task = new TaskImp(id, priority, enterTime, expiredTime,
						status, dataType, toAddress, sourceDataName,
						dataIdentify, sendIdentify);
			}
		} catch (SQLException e) {
			log.error(e.getMessage());
			return null;
		} finally {
			try {
				if (resultset != null) {
					resultset.close();
					resultset = null;
				}
				if (preparedStatement != null) {
					preparedStatement.close();
					preparedStatement = null;
				}
			} catch (SQLException e) {
				log.error(e.getMessage());
			}
		}
		return task;
	}
}
