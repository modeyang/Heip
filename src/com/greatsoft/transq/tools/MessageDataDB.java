package com.greatsoft.transq.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.greatsoft.transq.utils.ErrorCode;
import com.greatsoft.transq.utils.Log;

public class MessageDataDB {
	private static Logger log = Log.getLog(MessageDataDB.class);
	private static Connection connection = null;
	private String messageDataDBName;
	private String databasePath;

	public MessageDataDB(String messageDataDBName, String databasePath) {
		this.messageDataDBName = messageDataDBName;
		this.databasePath = databasePath;
	}

	public String getMessageDataDBName() {
		return messageDataDBName;
	}

	public void setMessageDataDBName(String messageDataDBName) {
		this.messageDataDBName = messageDataDBName;
	}

	public String getDatabasePath() {
		return databasePath;
	}

	public void setDatabasePath(String databasePath) {
		this.databasePath = databasePath;
	}

	public MessageData getMessageData(String dataIdentfy) {
		if (connection == null) {
			log.warn("数据库已经关闭");
			return null;
		}
		String sql = "select * from '" + messageDataDBName
				+ "' where dataIdentfy='" + dataIdentfy + "';";
		PreparedStatement preparedStatement = null;
		ResultSet resultset = null;
		MessageData messageData = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			resultset = preparedStatement.executeQuery();
			if (resultset.next()) {
				long length = resultset.getLong("length");
				String dataType = resultset.getString("dataType");
				String sourceDataName = resultset.getString("sourceDataName");
				byte[] sourceData = resultset.getBytes("sourceData");
				messageData = new MessageData(dataIdentfy, length, dataType,
						sourceDataName, sourceData);
			}
		} catch (SQLException e) {
			log.error("获取数据库中指定消息记录失败：dataIdentfy=" + dataIdentfy + "。\n"+ e.getMessage());
			return null;
		} finally {
			try {
				resultset.close();
				preparedStatement.close();
			} catch (SQLException e) {
				log.error("preparedStatement关闭失败。" + e.getMessage());
				return null;
			}
		}
		return messageData;
	}

	public synchronized boolean putOneMessageData(MessageData messageData){
		PreparedStatement preparedStatement = null;
		String dataIdentify=messageData.getDataIdentfy();
		if (isMessageDataExist(messageDataDBName, dataIdentify)) {
			log.error("添加消息记录失败，相同dataIdentify的消息已经在库中存在：dataIdentify=" + dataIdentify);
			return false;
		}
		if (connection == null) {
			log.warn("数据库已经关闭");
			return false;
		}
		String sql = "insert into '" + messageDataDBName+ "' values(?,?,?,?,?)";
		try {
			preparedStatement = connection.prepareStatement(sql);
		} catch (SQLException e) {
			log.error("preparedStatement获取失败，信息记录没有放进信息库中");
			try {
				preparedStatement.close();
			} catch (SQLException e1) {
				log.error("preparedStatement关闭失败"+e1.getMessage());
			}
			return false;
		}
		int idx = 1;
		try {
			preparedStatement.setString(idx++, dataIdentify);
		} catch (SQLException e) {
			log.error("preparedStatement设置dataIdentify失败，信息记录没有放进信息库中");
			try {
				preparedStatement.close();
			} catch (SQLException e1) {
				log.error("preparedStatement关闭失败"+e1.getMessage());
			}
			return false;
		}
		try {
			preparedStatement.setLong(idx++, messageData.getLength());
		} catch (SQLException e) {
			log.error("preparedStatement设置length失败，信息记录没有放进信息库中");
			try {
				preparedStatement.close();
			} catch (SQLException e1) {
				log.error("preparedStatement关闭失败"+e1.getMessage());
			}
			return false;
		}
		try {
			preparedStatement.setString(idx++, messageData.getDataType());
		} catch (SQLException e) {
			log.error("preparedStatement设置dataType失败，信息记录没有放进信息库中");
			try {
				preparedStatement.close();
			} catch (SQLException e1) {
				log.error("preparedStatement关闭失败"+e1.getMessage());
			}
			return false;
		}
		try {
			preparedStatement.setString(idx++, messageData.getSourceDataName());
		} catch (SQLException e) {
			log.error("preparedStatement设置sourceDataName失败，信息记录没有放进信息库中");
			try {
				preparedStatement.close();
			} catch (SQLException e1) {
				log.error("preparedStatement关闭失败"+e1.getMessage());
			}
			return false;
		}
		try {
			preparedStatement.setBytes(idx++, messageData.getSourceData());
		} catch (SQLException e) {
			log.error("preparedStatement设置sourceData失败，信息记录没有放进信息库中");
			try {
				preparedStatement.close();
			} catch (SQLException e1) {
				log.error("preparedStatement关闭失败"+e1.getMessage());
			}
			return false;
		}
			try {
				preparedStatement.close();
			} catch (SQLException e) {
				log.error("preparedStatement关闭失败");
				return false;
			}
		log.info("成功放入一条信息记录到信息库中：dataIdentify=" + dataIdentify);
		return true;
	}
/**将一组信息记录添加到数据库中*/
	public boolean putTask(MessageData[] messageDataArray){
		boolean flag = true;
		int length=messageDataArray.length;
		for (int index=0;index<length;index++) {
			if (putOneMessageData(messageDataArray[index])){
				flag = false;
				log.error("将第"+(index+1)+"个信息记录添加到数据库的过程中出错:"+messageDataArray[index].toString());
				break;
			}
		}
		return flag;
	}
/**修改信息记录*/
	public synchronized boolean setMessageData(MessageData messageData) {
		if (connection == null) {
			log.warn("修改信息记录失败，数据库已经关闭");
			return false;
		}
		String dataIdentfy = messageData.getDataIdentfy();
		if (!(isMessageDataExist(messageDataDBName, dataIdentfy))) {
			log.error("修改信息记录失败，信息记录不存在：DataIdentfy=" + dataIdentfy);
			return false;
		}
		String sql = "update "
				+ messageDataDBName
				+ " set length=?,dataType=?,sourceDataName=?,sourceData=? where dataIdentfy = ?";
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			int idx = 1;
			preparedStatement.setLong(idx++, messageData.getLength());
			preparedStatement.setString(idx++, messageData.getDataType());
			preparedStatement.setString(idx++, messageData.getSourceDataName());
			preparedStatement.setBytes(idx++, messageData.getSourceData());
			preparedStatement.executeUpdate();
			log.info("成功修改信息记录：DataIdentfy=" + dataIdentfy);
		} catch (SQLException e) {
			log.error("修改信息记录失败，信息记录不存在：DataIdentfy=" + dataIdentfy
					+ e.getMessage());
			return false;
		} finally {
			try {
				preparedStatement.close();
			} catch (SQLException e) {
				log.error("preparedStatement关闭失败");
				return false;
			}
		}
		return true;
	}
	/**删除信息记录*/
	public synchronized boolean deleteMessageData(String dataIdentfy) {
		if (!(isMessageDataExist(messageDataDBName, dataIdentfy))) {
			log.warn("不存在dataIdentfy=“" + dataIdentfy + "”的记录");
			return true;
		}
		if (connection == null) {
			log.warn("数据库已经关闭");
			return false;
		}
		String sql = "delete from " + messageDataDBName
				+ " where dataIdentfy='" + dataIdentfy + "';";
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			log.error("messageData记录删除失败:dataIdentfy=“" + dataIdentfy + "”。"
					+ e.getMessage());
			return false;
		} finally {
			try {
				preparedStatement.close();
			} catch (SQLException e) {
				log.error("关闭数据库连接失败" + e.getMessage());
				return false;
			}
		}
		log.info("成功删除一条messageData记录");
		return true;
	}
/**提取所有信息记录*/
	public MessageData[] selectAllMessageData(String MessageDataDBName) {
		if (connection == null) {
			log.warn("数据库已经关闭");
			return null;
		}
		String sql = "select * from " + MessageDataDBName + ";";
		PreparedStatement preparedStatement = null;
		ResultSet resultset = null;
		MessageData[] messageDataArray=null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			resultset = preparedStatement.executeQuery();
			int number=resultset.getFetchSize();
			messageDataArray=new MessageData[number];//?
			int index=0;
			while (resultset.next()){
				String dataIdentify = resultset.getString("dataIdentify");
				long length = resultset.getLong("length");
				String dataType = resultset.getString("dataType");
				String sourceDataName = resultset.getString("sourceDataName");
				byte[]  sourceData= resultset.getBytes("sourceData");
				messageDataArray[index++]=new MessageData(dataIdentify,length,dataType,sourceDataName,sourceData);
			}
		} catch (SQLException e) {
			log.error("获取消息记录过程中出错:"+e.getMessage());
			return null;
		} finally {
			try {
				resultset.close();
				preparedStatement.close();
			} catch (SQLException e) {
				log.error("关闭preparedStatement失败");
				return null;
			}
		}
		return messageDataArray;
	}

	private boolean isMessageDataExist(String MessageDataDBName, String dataIdentify) {
		if (connection == null) {
			log.warn("数据库已经关闭");
			return false;
		}
		boolean flag = false;
		String sql = "select * from '" + MessageDataDBName + "' where dataIdentify='" + dataIdentify + "';";
		PreparedStatement preparedStatement = null;
		ResultSet resultset = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			resultset = preparedStatement.executeQuery();
			if (resultset.next()) {
				flag = true;
			}
			resultset.close();
			preparedStatement.close();
		} catch (SQLException e) {
			log.error("关闭preparedStatement失败"+e.getMessage());
		}
		return flag;
	}

	/**
	 * 连接数据库
	 */
	public int openMessageDataDB(String MessageDataDBName) {
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager
					.getConnection("jdbc:sqlite:" + MessageDataDBName);
			if (connection == null) {
				log.error("数据库连接创建失败");
				return -1;
			}
		} catch (ClassNotFoundException e) {
			log.error("数据库打开失败！");
			return -1;
		} catch (SQLException e) {
			log.error("数据库打开失败！");
			return -1;
		}
		return ErrorCode.NO_ERROR;
	}

	public boolean closeMessageDataDB() {
		if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					log.error("数据库连接关闭失败");
					return false;
				}
				log.info("数据库连接关闭成功");
				return true;
		}
		log.warn("数据库连接已经关闭，不需要再次关闭");
		return true;
	}
}
