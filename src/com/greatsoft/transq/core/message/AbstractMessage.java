package com.greatsoft.transq.core.message;

import java.nio.ByteBuffer;

import javax.activation.DataHandler;

public abstract class AbstractMessage {
	public abstract DataHandler getDataHandler();

	public abstract void setDataHandler(DataHandler data);

	public abstract EnvelopeImp getEnvelope();

	public abstract void setEnvelope(EnvelopeImp envelope);

	public abstract ByteBuffer getData();

	public abstract byte[] getDataBytes();

/*	public abstract String getDataFileName();

	public abstract void setDataFileName(String dataFileName);*/

}
