package com.test;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.greatsoft.transq.utils.ConstantValue;

public class TestLog {

	public static void main(String[] args) {
		PropertyConfigurator
				.configure("C:/Users/mojia/Desktop/Hiep/hiepHome/config/"
						+ ConstantValue.ROUTER_LOG4J_PROPERTIES_FILE_NAME);

		Logger log = Logger.getLogger(TestLog.class);

		log.info("I do not want to on the screen");
		log.error("wuwu help me ");
	}

}
