package com.greatsoft.transq.message.webservice;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;

import com.greatsoft.transq.core.message.ResultImp;
import com.greatsoft.transq.utils.ConstantValue;

public class ProcessorWorker implements Callable<ResultImp> {

	private String address;
	private WSMessage message;

	public ProcessorWorker(String address, WSMessage message) {
		this.address = address;
		this.message = message;
	}

	@Override
	public ResultImp call() throws Exception {

		Map<String, Object> props = new HashMap<String, Object>();
		props.put("mtom-enabled", Boolean.TRUE);

		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(IProcessor.class);
		factory.setAddress(address);
		factory.setProperties(props);

		IProcessor processor = (IProcessor) factory.create();

		Client client = ClientProxy.getClient(processor);
		HTTPConduit conduit = (HTTPConduit) client.getConduit();
		HTTPClientPolicy policy = new HTTPClientPolicy();

		int connectionTime = Integer.parseInt(ConstantValue.CONNECTION_TIME);
		policy.setConnectionTimeout(connectionTime);
		int waitingTime = Integer.parseInt(ConstantValue.WSSERVER_WAITINT_TIME);
		policy.setReceiveTimeout(waitingTime);
		conduit.setClient(policy);

		ResultImp result = new ResultImp();
		result = processor.process(message, null);

		return result;
	}
}
