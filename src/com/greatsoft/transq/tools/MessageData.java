package com.greatsoft.transq.tools;

public class MessageData {
	/**
	 * ����ID,Ψһ��ʶ��
	 */
	private String dataIdentfy;
	/**
	 * ���ݳ���
	 */
	private long length;
	/**
	 * �������
	 */
	private String dataType;
	/**
	 * ������ʾ����
	 */
	private String sourceDataName;
	/**
	 * ����
	 */
	private byte[] sourceData;

	public MessageData(String dataIdentfy, long length, String dataType,
			String sourceDataName, byte[] sourceData) {
		super();
		this.dataIdentfy = dataIdentfy;
		this.length = length;
		this.dataType = dataType;
		this.sourceDataName = sourceDataName;
		this.sourceData = sourceData;
	}

	public String getDataIdentfy() {
		return dataIdentfy;
	}

	public void setDataIdentfy(String dataIdentfy) {
		this.dataIdentfy = dataIdentfy;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getSourceDataName() {
		return sourceDataName;
	}

	public void setSourceDataName(String sourceDataName) {
		this.sourceDataName = sourceDataName;
	}

	public byte[] getSourceData() {
		return sourceData;
	}

	public void setSourceData(byte[] sourceData) {
		this.sourceData = sourceData;
	}

	@Override
	public String toString() {
		return "MessageData [dataIdentfy=" + dataIdentfy + ", length=" + length
				+ ", dataType=" + dataType + ", sourceDataName="
				+ sourceDataName + ", sourceData=" + sourceData + "]";
	}
}