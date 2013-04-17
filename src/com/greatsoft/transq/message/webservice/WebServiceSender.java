package com.greatsoft.transq.message.webservice;

import com.greatsoft.transq.core.Sender;
import com.greatsoft.transq.core.message.AbstractMessage;
import com.greatsoft.transq.core.message.ResultImp;
import com.greatsoft.transq.exception.CommunicationException;

public class WebServiceSender implements Sender {

	@Override
	public ResultImp put(AbstractMessage message, String filePath)
			throws CommunicationException {
		return null;
	}

	@Override
	public ResultImp put(AbstractMessage message) throws CommunicationException {
		return null;
	}

}
