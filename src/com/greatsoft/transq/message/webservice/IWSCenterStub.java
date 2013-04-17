package com.greatsoft.transq.message.webservice;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import com.greatsoft.transq.core.message.ResultImp;

@WebService
@SOAPBinding(style=Style.DOCUMENT,use=Use.LITERAL)
public interface IWSCenterStub {
	@WebMethod ResultImp exchange(WSMessage message);
}
