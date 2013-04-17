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
			log.warn("���ݿ��Ѿ��ر�");
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
			log.error("��ȡ���ݿ���ָ����Ϣ��¼ʧ�ܣ�dataIdentfy=" + dataIdentfy + "��\n"+ e.getMessage());
			return null;
		} finally {
			try {
				resultset.close();
				preparedStatement.close();
			} catch (SQLException e) {
				log.error("preparedStatement�ر�ʧ�ܡ�" + e.getMessage());
				return null;
			}
		}
		return messageData;
	}

	public synchronized boolean putOneMessageData(MessageData messageData){
		PreparedStatement preparedStatement = null;
		String dataIdentify=messageData.getDataIdentfy();
		if (isMessageDataExist(messageDataDBName, dataIdentify)) {
			log.error("�����Ϣ��¼ʧ�ܣ���ͬdataIdentify����Ϣ�Ѿ��ڿ��д��ڣ�dataIdentify=" + dataIdentify);
			return false;
		}
		if (connection == null) {
			log.warn("���ݿ��Ѿ��ر�");
			return false;
		}
		String sql = "insert into '" + messageDataDBName+ "' values(?,?,?,?,?)";
		try {
			preparedStatement = connection.prepareStatement(sql);
		} catch (SQLException e) {
			log.error("preparedStatement��ȡʧ�ܣ���Ϣ��¼û�зŽ���Ϣ����");
			try {
				preparedStatement.close();
			} catch (SQLException e1) {
				log.error("preparedStatement�ر�ʧ��"+e1.getMessage());
			}
			return false;
		}
		int idx = 1;
		try {
			preparedStatement.setString(idx++, dataIdentify);
		} catch (SQLException e) {
			log.error("preparedStatement����dataIdentifyʧ�ܣ���Ϣ��¼û�зŽ���Ϣ����");
			try {
				preparedStatement.close();
			} catch (SQLException e1) {
				log.error("preparedStatement�ر�ʧ��"+e1.getMessage());
			}
			return false;
		}
		try {
			preparedStatement.setLong(idx++, messageData.getLength());
		} catch (SQLException e) {
			log.error("preparedStatement����lengthʧ�ܣ���Ϣ��¼û�зŽ���Ϣ����");
			try {
				preparedStatement.close();
			} catch (SQLException e1) {
				log.error("preparedStatement�ر�ʧ��"+e1.getMessage());
			}
			return false;
		}
		try {
			preparedStatement.setString(idx++, messageData.getDataType());
		} catch (SQLException e) {
			log.error("preparedStatement����dataTypeʧ�ܣ���Ϣ��¼û�зŽ���Ϣ����");
			try {
				preparedStatement.close();
			} catch (SQLException e1) {
				log.error("preparedStatement�ر�ʧ��"+e1.getMessage());
			}
			return false;
		}
		try {
			preparedStatement.setString(idx++, messageData.getSourceDataName());
		} catch (SQLException e) {
			log.error("preparedStatement����sourceDataNameʧ�ܣ���Ϣ��¼û�зŽ���Ϣ����");
			try {
				preparedStatement.close();
			} catch (SQLException e1) {
				log.error("preparedStatement�ر�ʧ��"+e1.getMessage());
			}
			return false;
		}
		try {
			preparedStatement.setBytes(idx++, messageData.getSourceData());
		} catch (SQLException e) {
			log.error("preparedStatement����sourceDataʧ�ܣ���Ϣ��¼û�зŽ���Ϣ����");
			try {
				preparedStatement.close();
			} catch (SQLException e1) {
				log.error("preparedStatement�ر�ʧ��"+e1.getMessage());
			}
			return false;
		}
			try {
				preparedStatement.close();
			} catch (SQLException e) {
				log.error("preparedStatement�ر�ʧ��");
				return false;
			}
		log.info("�ɹ�����һ����Ϣ��¼����Ϣ���У�dataIdentify=" + dataIdentify);
		return true;
	}
/**��һ����Ϣ��¼��ӵ����ݿ���*/
	public boolean putTask(MessageData[] messageDataArray){
		boolean flag = true;
		int length=messageDataArray.length;
		for (int index=0;index<length;index++) {
			if (putOneMessageData(messageDataArray[index])){
				flag = false;
				log.error("����"+(index+1)+"����Ϣ��¼��ӵ����ݿ�Ĺ����г���:"+messageDataArray[index].toString());
				break;
			}
		}
		return flag;
	}
/**�޸���Ϣ��¼*/
	public synchronized boolean setMessageData(MessageData messageData) {
		if (connection == null) {
			log.warn("�޸���Ϣ��¼ʧ�ܣ����ݿ��Ѿ��ر�");
			return false;
		}
		String dataIdentfy = messageData.getDataIdentfy();
		if (!(isMessageDataExist(messageDataDBName, dataIdentfy))) {
			log.error("�޸���Ϣ��¼ʧ�ܣ���Ϣ��¼�����ڣ�DataIdentfy=" + dataIdentfy);
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
			log.info("�ɹ��޸���Ϣ��¼��DataIdentfy=" + dataIdentfy);
		} catch (SQLException e) {
			log.error("�޸���Ϣ��¼ʧ�ܣ���Ϣ��¼�����ڣ�DataIdentfy=" + dataIdentfy
					+ e.getMessage());
			return false;
		} finally {
			try {
				preparedStatement.close();
			} catch (SQLException e) {
				log.error("preparedStatement�ر�ʧ��");
				return false;
			}
		}
		return true;
	}
	/**ɾ����Ϣ��¼*/
	public synchronized boolean deleteMessageData(String dataIdentfy) {
		if (!(isMessageDataExist(messageDataDBName, dataIdentfy))) {
			log.warn("������dataIdentfy=��" + dataIdentfy + "���ļ�¼");
			return true;
		}
		if (connection == null) {
			log.warn("���ݿ��Ѿ��ر�");
			return false;
		}
		String sql = "delete from " + messageDataDBName
				+ " where dataIdentfy='" + dataIdentfy + "';";
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			log.error("messageData��¼ɾ��ʧ��:dataIdentfy=��" + dataIdentfy + "����"
					+ e.getMessage());
			return false;
		} finally {
			try {
				preparedStatement.close();
			} catch (SQLException e) {
				log.error("�ر����ݿ�����ʧ��" + e.getMessage());
				return false;
			}
		}
		log.info("�ɹ�ɾ��һ��messageData��¼");
		return true;
	}
/**��ȡ������Ϣ��¼*/
	public MessageData[] selectAllMessageData(String MessageDataDBName) {
		if (connection == null) {
			log.warn("���ݿ��Ѿ��ر�");
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
			log.error("��ȡ��Ϣ��¼�����г���:"+e.getMessage());
			return null;
		} finally {
			try {
				resultset.close();
				preparedStatement.close();
			} catch (SQLException e) {
				log.error("�ر�preparedStatementʧ��");
				return null;
			}
		}
		return messageDataArray;
	}

	private boolean isMessageDataExist(String MessageDataDBName, String dataIdentify) {
		if (connection == null) {
			log.warn("���ݿ��Ѿ��ر�");
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
			log.error("�ر�preparedStatementʧ��"+e.getMessage());
		}
		return flag;
	}

	/**
	 * �������ݿ�
	 */
	public int openMessageDataDB(String MessageDataDBName) {
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager
					.getConnection("jdbc:sqlite:" + MessageDataDBName);
			if (connection == null) {
				log.error("���ݿ����Ӵ���ʧ��");
				return -1;
			}
		} catch (ClassNotFoundException e) {
			log.error("���ݿ��ʧ�ܣ�");
			return -1;
		} catch (SQLException e) {
			log.error("���ݿ��ʧ�ܣ�");
			return -1;
		}
		return ErrorCode.NO_ERROR;
	}

	public boolean closeMessageDataDB() {
		if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					log.error("���ݿ����ӹر�ʧ��");
					return false;
				}
				log.info("���ݿ����ӹرճɹ�");
				return true;
		}
		log.warn("���ݿ������Ѿ��رգ�����Ҫ�ٴιر�");
		return true;
	}
}
