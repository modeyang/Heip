package com.greatsoft.transq.core;

import com.greatsoft.transq.message.queue.TLQAddress;
import com.greatsoft.transq.message.queue.TLQConnector;
import com.greatsoft.transq.message.webservice.WSAddress;
import com.greatsoft.transq.message.webservice.WSConnector;

public class ConnectorAdapterImp implements ConnectorAdapter {
	
	private static ConnectorAdapterImp connectorAdapterImp=null;
	
	private ConnectorAdapterImp() {
	}

	public static ConnectorAdapterImp getInstance() {
		if(connectorAdapterImp==null){
			connectorAdapterImp=new ConnectorAdapterImp();
		}
		return connectorAdapterImp;
	}

	@Override
	public Connector getConnector(Address address, Object options) {
		if(address instanceof TLQAddress){
			return new TLQConnector((TLQAddress) address,options);
		}else{
			return new WSConnector((WSAddress) address,options);
		}
	}
}
