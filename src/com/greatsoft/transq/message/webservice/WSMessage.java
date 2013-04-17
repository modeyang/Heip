package com.greatsoft.transq.message.webservice;

import java.nio.ByteBuffer;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlMimeType;

import com.greatsoft.transq.core.message.AbstractMessage;
import com.greatsoft.transq.core.message.EnvelopeImp;

@XmlAccessorType(XmlAccessType.FIELD)
public class WSMessage extends AbstractMessage {
	@XmlMimeType("application/octet-stream")
	private DataHandler data;
	private EnvelopeImp envelope;

	public WSMessage() {
	}

	public WSMessage(DataHandler data, EnvelopeImp envelope) {
		super();
		this.data = data;
		this.envelope = envelope;
	}

	public DataHandler getDataHandler() {
		return data;
	}

	public void setDataHandler(DataHandler data) {
		this.data = data;
	}

	public EnvelopeImp getEnvelope() {
		return envelope;
	}

	@Override
	public void setEnvelope(EnvelopeImp envelope) {
		this.envelope = (EnvelopeImp) envelope;
	}

	/**
	 * 下面函数无需实现
	 * */
	@Override
	public ByteBuffer getData() {
		return null;
	}

	@Override
	public byte[] getDataBytes() {
		return null;
	}
}
