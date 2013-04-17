package com.greatsoft.transq.message.webservice;

import javax.jws.WebService;

import com.greatsoft.transq.core.message.ResultImp;

@WebService
public interface IProcessor {
	ResultImp process(WSMessage message, Object addtionParam);
}
