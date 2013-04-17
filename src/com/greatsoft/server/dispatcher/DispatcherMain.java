package com.greatsoft.server.dispatcher;

import org.apache.log4j.Logger;
import com.greatsoft.transq.core.CommandProcessor;
import com.greatsoft.transq.core.HiepController;
import com.greatsoft.transq.utils.Config;
import com.greatsoft.transq.utils.ConfigHelper;
import com.greatsoft.transq.utils.ConstantValue;
import com.greatsoft.transq.utils.ErrorCode;
import com.greatsoft.transq.utils.FileHelper;
import com.greatsoft.transq.utils.Log;

public class DispatcherMain implements HiepController {
	private static Logger log = Log.getLog(DispatcherMain.class);
	private static Logger userLog = Logger
			.getLogger(ConstantValue.DISPATCHER_USER_LOG);

	private static Object mainThreadLock;

	private static DispatcherCommandProcessor commandProcessor;
	private static DispatcherServerProcess dispatcherServerProcess;
	private static DispatcherServerRoute dispatcherServerRoute;
	private static DeleteFileWorker deleteFileWorker;
	/** 线程组 */
	private static ThreadGroup threadGroup;
	/** dispatcherServerProcess线程 */
	private static Thread dispatcherServerProcessThread;
	/** dispatcherServerRoute线程 */
	private static Thread dispatcherServerRouteThread;
	/** commandProcessor线程 */
	private static Thread commandProcessorThread;
	/** deleteFileWorker线程 */
	private static Thread deleteFileWorkerThread;
	/** monitorSocket端口 */
	private static int monitorSocketPort;

	private static boolean isStopMethodRun;

	public static void main(String[] args) {
		/** 加载配置信息 */
		if (!loadHiepConfig()) {
			System.exit(-1);
		}
		userLog.info("消息转发模块配置信息加载成功");
		/** 初始化运行环境 */
		if (!initEnvironment()) {
			System.exit(-1);
		}
		userLog.info("消息转发模块环境初始化成功");
		/** 启动线程组 */
		if (!startHiep()) {
			System.exit(-1);
		}
		userLog.info("消息转发模块启动成功");
		synchronized (mainThreadLock) {
			/** 如果是被stop()方法的notify()唤醒，则跳出循环 */
			isStopMethodRun = false;
			while (!isStopMethodRun) {
				try {
					mainThreadLock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			destory();
			userLog.info("消息转发模块关闭成功");
		}
		System.exit(0);
	}

	private static void destory() {
		deletePidFile();
	}

	/** 加载所有配置文件，并对信息的有效性进行检测 */
	private static boolean loadHiepConfig() {
		/** 获取注册表中HIEP_HOME_PATH的值 */
		if (!ConfigHelper.loadHiepHomePath()) {
			return false;
		}
		/** 检查主要的必备文件目录和配置文件是否存在 */
		if (!ConfigHelper.checkMainDirectory()) {
			return false;
		}
		/** 如果在Log启动之前就初始化失败，打印屏幕和写到当前工程的文件。Log静态类的初始化,尽早准备日志环境 */
		if (!ConfigHelper.createLog(Config.HIEP_HOME_PATH
				+ ConstantValue.CONFIG_DIRECTORY
				+ ConstantValue.DISPARTER_LOG4J_PROPERTIES_FILE_NAME)) {
			return false;
		}
		userLog.info("HIEP_HOME_PATH获取成功，主要目录和配置文件存在，日志初始化成功");
		/** 加载config目录下的config.ini文件内容和config_custom.ini文件内容 */
		if (!ConfigHelper.loadConfigFile()) {
			return false;
		}
		userLog.info("config.ini文件和config_custom.ini文件加载成功");

		/** 加载config目录下的HIEP_addressMap.ini */
		if (!ConfigHelper.loadAddressMapConfig()) {
			return false;
		}
		userLog.info("HIEP_addressMap.ini文件加载成功");

		/** 加载config目录下的HIEP_processorMap.ini */
		if (!ConfigHelper.loadProcessorMapConfig()) {
			return false;
		}
		userLog.info("HIEP_processorMap.ini文件加载成功");
		/** 加载数据库 */
		if (!ConfigHelper.loadDataBase()) {
			return false;
		}
		userLog.info("任务记录队列管理容器加载成功");
		/** EXT目录下的所有JAR */
		if (!ConfigHelper.loadThirdJar()) {
			return false;
		}
		userLog.info("EXT目录下的所有JAR加载成功");
		/** 检测硬盘空间是否足够 */
		if (!ConfigHelper.checkEnvironment()) {
			return false;
		}
		return true;
	}

	private static boolean initEnvironment() {
		monitorSocketPort = Config.DISPATCHER_PORT;
		mainThreadLock = new Object();
		/** 防止主进程重复启动，判断dispatcher.pid文件是否存在,如果dispatcher.pid文件存在就不启动系统 */
		if (isExistPidFile()) {
			log.error(ErrorCode.getErrorMessage(ErrorCode.PID_FILE_EXIST_ERROR));
			return false;
		}
		/** 创建PDI文件 */
		if (!createPidFile()) {
			log.error(ErrorCode
					.getErrorMessage(ErrorCode.CREATE_PID_FILE_ERROR));
			return false;
		}
		return true;
	}

	private static boolean startHiep() {
		/**
		 * 起两个进程router,dispatcher，然后dispatcher再起两个线程dispatcherServerProcess，
		 * dispatcherServerRoute
		 */
		threadGroup = new ThreadGroup(ConstantValue.threadGroupName);

		dispatcherServerProcess = new DispatcherServerProcess(
				Config.TASK_QUEUE_LOCAL_NAME);
		dispatcherServerRoute = new DispatcherServerRoute(
				Config.TASK_QUEUE_REMOTE_NAME);
		commandProcessor = new DispatcherCommandProcessor(monitorSocketPort,
				new DispatcherMain());
		deleteFileWorker = new DeleteFileWorker(true);

		dispatcherServerProcessThread = new Thread(threadGroup,
				dispatcherServerProcess,
				ConstantValue.DISPATCHER_SERVER_PROCESS_NAME);
		dispatcherServerRouteThread = new Thread(threadGroup,
				dispatcherServerRoute,
				ConstantValue.DISPATCHER_SERVER_ROUTE_NAME);
		commandProcessorThread = new Thread(threadGroup, commandProcessor,
				ConstantValue.DISPATCHER_COMMAND_PROCESSOR_NAME);
		deleteFileWorkerThread = new Thread(threadGroup, deleteFileWorker,
				ConstantValue.DELETE_FILE_WORKER_NAME);

		dispatcherServerProcessThread.start();
		dispatcherServerRouteThread.start();
		commandProcessorThread.start();
		//deleteFileWorkerThread.start();

		return true;
	}

	@Override
	public int stop(int timeout) {
		log.info("开始关闭消息转发模块，请等待");
		dispatcherServerProcess.setRunningFlag(false);
		dispatcherServerRoute.setRunningFlag(false);
		deleteFileWorker.setRunning(false);

		isStopMethodRun = true;
		dispatcherServerProcess.pool.shutdown();
		/** 关闭与数据库的连接 */
		ConfigHelper.closeDataBase();
		dispatcherServerProcess.getTaskQueue().closeTaskQueue();
		dispatcherServerRoute.getTaskQueue().closeTaskQueue();
		/** 超时判断,超时到了不管什么情况都停下来,如果负数，死等。 */
		if (timeout < 0) {
			log.info("无限超时关闭消息转发模块，请等待");
			/** 无限等待 */
			sleep();
			while (!dispatcherServerProcess.pool.isTerminated()) {
				sleep();
			}
			if (threadGroup.activeCount() != 0) {
				/** 子线程没有停掉 */
				log.info("线程集合中还有线程在运行");
				try {
					dispatcherServerProcessThread.join(timeout);
					dispatcherServerRouteThread.join(timeout);
					commandProcessorThread.join(timeout);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				synchronized (mainThreadLock) {
					mainThreadLock.notifyAll();
				}
				/** 通过返回值来表明命令处理的结果 */
				return CommandProcessor.FORCE_QUIT;
			} else {
				/** 子线程已经全部死掉了 */
				log.info("子线程全部死掉了，关闭主进程");

				synchronized (mainThreadLock) {
					mainThreadLock.notifyAll();
				}
				return CommandProcessor.NORMAL_QUIT;
			}

		} else {
			/** 在规定时间内关闭HIEP系统 */
			log.info("在" + (timeout / (1000)) + "秒内关闭disPatcher进程");
			sleep(timeout);
			log.info("规定时间到，开始关闭HIEP系统");
			killPoolThread();
			if (threadGroup.activeCount() != 0) {
				/** 子线程没有停掉 */
				log.info("线程集合中还有" + threadGroup.activeCount() + "线程正在运行");
				synchronized (mainThreadLock) {
					mainThreadLock.notifyAll();
				}
				/** 通过返回值来表明命令处理的结果 */
				return CommandProcessor.FORCE_QUIT;
			} else {
				/** 子线程已经全部死掉了 */
				log.info("线程集合中没有线程正在运行，开始关闭主进程");
				synchronized (mainThreadLock) {
					mainThreadLock.notifyAll();
				}
				return CommandProcessor.FORCE_QUIT;
			}
		}
	}

	private static void killPoolThread() {
		if (!dispatcherServerProcess.pool.isTerminated()) {
			log.info("超时时间到，线程池中子进程还有线程在运行,强制关闭线程池中所有子进程");
			dispatcherServerProcess.pool.shutdownNow();
			sleep();
			if (!dispatcherServerProcess.pool.isTerminated()) {
				log.info("线程池中子进程已经关闭");
			} else {
				while (!dispatcherServerProcess.pool.isTerminated()) {
					sleep();
					dispatcherServerProcess.pool.shutdownNow();
					sleep();
				}
			}
		} else {
			log.info("超时时间到，线程池中所有子进程已经关闭");
		}
	}

	private static void sleep() {
		try {
			Thread.sleep(Config.DISPARTER_PROCESSING_INTERVAL * 10);
		} catch (InterruptedException e) {
			log.error(ErrorCode
					.getErrorMessage(ErrorCode.THREAD_SLEEP_INTERRUPT_ERROR)
					+ e.getMessage());
		}
	}

	private static void sleep(int timeout) {
		try {
			Thread.sleep(timeout);
		} catch (InterruptedException e) {
			log.error(ErrorCode
					.getErrorMessage(ErrorCode.THREAD_SLEEP_INTERRUPT_ERROR)
					+ e.getMessage());
		}
	}

	/** 判断进程的PID文件是否存在 */
	private static boolean isExistPidFile() {
		return FileHelper.checkFile(Config.HIEP_HOME_PATH
				+ ConstantValue.CONFIG_DIRECTORY
				+ ConstantValue.DISPATCHER_PID_FILE_NAME);
	}

	/** 创建PDI文件 */
	private static boolean createPidFile() {
		return FileHelper.createFile(Config.HIEP_HOME_PATH
				+ ConstantValue.CONFIG_DIRECTORY
				+ ConstantValue.DISPATCHER_PID_FILE_NAME);
	}

	/** 删除hiep.pid文件 */
	private static boolean deletePidFile() {
		return FileHelper.deleteOneFile(Config.HIEP_HOME_PATH
				+ ConstantValue.CONFIG_DIRECTORY
				+ ConstantValue.DISPATCHER_PID_FILE_NAME);
	}

	@Override
	public String viewThreads() {
		StringBuilder result = new StringBuilder(ConstantValue.NULL_STRING);
		Thread threads[] = new Thread[threadGroup.activeCount()];
		threadGroup.enumerate(threads);
		for (Thread t : threads)
			result.append("路由处理模块活着的线程:" + t.getName() + ConstantValue.ENTER);

		return result.toString();
	}
}
