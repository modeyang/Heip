package com.greatsoft.transq.message.webservice;

import java.net.ConnectException;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.log4j.Logger;

import com.greatsoft.transq.core.Address;
import com.greatsoft.transq.core.Connector;
import com.greatsoft.transq.core.Receiver;
import com.greatsoft.transq.core.Sender;
import com.greatsoft.transq.utils.ConstantValue;

public class WSConnector implements Connector {
	private static Logger log = Logger.getLogger(WSConnector.class);

	private String addressString = "";
	private static IWSCenterStub stub = null;

	public WSConnector(WSAddress address, Object options) {
		addressString = address.toString();
	}

	@Override
	public void connect() throws ConnectException {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(IWSCenterStub.class);
		factory.setAddress(addressString);
		log.info("WS连接的地址为：" + addressString);

		stub = (IWSCenterStub) factory.create();

		Client client = ClientProxy.getClient(stub);
		HTTPConduit conduit = (HTTPConduit) client.getConduit();
		HTTPClientPolicy policy = new HTTPClientPolicy();
		policy.setConnectionTimeout(Integer
				.parseInt(ConstantValue.CONNECTION_TIME));
		policy.setReceiveTimeout(Integer
				.parseInt(ConstantValue.WSSERVER_WAITINT_TIME));
		conduit.setClient(policy);
	}

	public static IWSCenterStub getStub() {
		return stub;
	}

	@Override
	public Boolean isConnected() {
		return true;
	}

	public Sender createSender() {
		return null;
	}

	public Receiver createReceiver() {
		return null;
	}

	public Address getAddress() {
		return null;
	}

	@Override
	public void disConnect() {

	}

}
