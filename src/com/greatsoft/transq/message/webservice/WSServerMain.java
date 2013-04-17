package com.greatsoft.transq.message.webservice;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.greatsoft.transq.utils.ConstantValue;
import com.greatsoft.transq.utils.ErrorCode;

public class WSServerMain {
	private static Logger log = Logger.getLogger(WSServerMain.class);

	private static Object lock = new Object();
	private static String homePath = "";
	protected static Map<String, String> addressmap = new HashMap<String, String>();

	private static final int port = 56781;
	public static String ADDRESS = "";

	public WSServerMain() {

	}

	public static void main(String args[]) {
		if (!init()) {
			return;
		}
		System.out.println("WSServer已经启动");

		new Thread(new WSCommandProcessor(port)).start();

		WSServerMain server = new WSServerMain();
		server.deployService();

		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println("WSServer 安全退出");
		System.exit(0);
	}

	private static boolean init() {
		boolean flag = false;
		homePath = WSHelper.getHomePath();
		System.out.println("homePath=" + homePath);

		initLog(homePath + ConstantValue.WS_LOG_FILE);

		String configFilePath = homePath + ConstantValue.WS_CONFIG_FILE;
		flag = WSHelper.initProperties(configFilePath);

		String processorConfigFilePath = homePath
				+ ConstantValue.PROCESSOR_MAP_FILE;
		addressmap = WSHelper
				.loadProcessorAddressConfig(processorConfigFilePath);

		if (addressmap != null && (flag == true)) {
			return true;
		} else {
			return false;
		}

	}

	private static void initLog(String filePath) {
		File file = new File(filePath);
		if (!file.exists() || file.isDirectory()) {
			log.error(ErrorCode.getErrorMessage(ErrorCode.FILE_NOT_FOUND_ERROR)
					+ ConstantValue.PATH + filePath);
		}
		PropertyConfigurator.configure(filePath);
	}

	public void deployService() {
		WSCenterStub service = new WSCenterStub();
		log.info("部署地址：" + ADDRESS);
		/* Endpoint.publish(ADDRESS, service); */
		Map<String, Object> props = new HashMap<String, Object>();
		props.put("mtom-enabled", Boolean.TRUE);

		// 发布服务
		JaxWsServerFactoryBean server = new JaxWsServerFactoryBean();
		server.setServiceClass(service.getClass());
		server.setAddress(ADDRESS);
		server.setProperties(props);
		server.create();
	}

	public static void stop() {
		synchronized (lock) {
			lock.notifyAll();
		}
	}
}
