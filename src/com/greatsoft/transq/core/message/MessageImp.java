package com.greatsoft.transq.core.message;

import java.io.Serializable;
import java.nio.ByteBuffer;

import javax.activation.DataHandler;

public class MessageImp extends AbstractMessage implements Serializable {

	private static final long serialVersionUID = 7117588590977298301L;
	private EnvelopeImp envelope;
	private byte[] byteData;
	private ByteBuffer byteBufferData;
	private String dataFileName = null;

	public MessageImp() {

	}

	public MessageImp(Envelope envelope, byte[] data) {
		super();
		this.envelope = (EnvelopeImp) envelope;
		this.byteData = data;
		byteBufferData = null;
		this.envelope.setOriginalFileLength(byteData.length);
	}

	public MessageImp(Envelope envelope, ByteBuffer data) {
		super();
		this.envelope = (EnvelopeImp) envelope;
		this.byteData = null;
		this.byteBufferData = data;
		if (this.envelope.getOriginalFileLength() == 0) {
			this.envelope.setOriginalFileLength(this.byteBufferData.capacity());
		}
	}

	public MessageImp(Envelope envelope, DataHandler data) {
		super();
		this.envelope = (EnvelopeImp) envelope;
		this.envelope.setOriginalFileLength(1000);
	}

	public MessageImp(AbstractMessage message) {
		this.envelope = new EnvelopeImp(message.getEnvelope());
		this.byteBufferData = message.getData();
		this.byteData = message.getDataBytes();
	}

	public MessageImp(Envelope envelope, String dataFileName) {
		this.envelope = (EnvelopeImp) envelope;
		this.dataFileName = dataFileName;
	}

	@Override
	public EnvelopeImp getEnvelope() {
		return this.envelope;
	}

	@Override
	public ByteBuffer getData() {
		return this.byteBufferData;
	}

	@Override
	public byte[] getDataBytes() throws UnsupportedOperationException {
		return this.byteData;
	}

	public String getDataFileName() {
		return dataFileName;
	}

	public void setDataFileName(String dataFileName) {
		this.dataFileName = dataFileName;
	}

	@Override
	public void setEnvelope(EnvelopeImp envelope) {
		this.envelope = envelope;
	}

	@Override
	public String toString() {
		if (this.byteData == null) {
			return "MessageImp [\n数据格式：byteBufferData\nenvelope="
					+ envelope.toString() + "\n]";
		} else {
			return "MessageImp [\n数据格式：byteData\nenvelope="
					+ envelope.toString() + "\n]";
		}
	}

	/***
	 * 一下两个函数无需实现
	 */
	@Override
	public DataHandler getDataHandler() {
		return null;
	}

	@Override
	public void setDataHandler(DataHandler data) {
	}
}
