package com.greatsoft.server.dispatcher;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.greatsoft.transq.core.HiepController;
import com.greatsoft.transq.utils.ConstantValue;

/**
 * Dispatcher模块的命令监视器
 * */
public class DispatcherCommandProcessor implements Runnable {
	private Logger log = Logger.getLogger(DispatcherCommandProcessor.class);
	private int port;
	// 生成一个信号监视器
	private Selector selector;
	// 读缓冲区
	private ByteBuffer readByteBuffer = ByteBuffer.allocate(1024);
	private ByteBuffer writeByteBuffer;

	private static final int HIEP_STOP = 0;
	private static final int HIEP_CURRENT_TIME = 1;
	private static final int HIEP_DISCONNECT = 2;
	private static final int HIEP_VIEW_THREADS = 3;
	private static final int HIEP_ERROR_COMMAND = 99999;

	public static final int NORMAL_QUIT = 0;
	public static final int FORCE_QUIT = -1;

	private HiepController hiepController;

	private boolean runningFlag;

	public DispatcherCommandProcessor(int port, HiepController controller) {
		this.port = port;
		this.hiepController = controller;
		this.runningFlag = true;

		try {
			selector = Selector.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		ServerSocketChannel serverSocketChannel = null;
		try {
			/**
			 * 生成一个ServerScoket通道的实例对象，用于侦听可能发生的IO事件
			 * */
			serverSocketChannel = ServerSocketChannel.open();
			// 将该通道设置为异步方式
			serverSocketChannel.configureBlocking(false);
			// 绑定到一个指定的端口
			serverSocketChannel.socket().bind(new InetSocketAddress(port));
			// 注册特定类型的事件到信号监视器上
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			log.info("Dispatcher监控模块启动...");
			while (runningFlag && selector.select() > 0) {
				// 将会阻塞执行，直到有事件发生
				selector.select();
				Iterator<SelectionKey> it = selector.selectedKeys().iterator();
				while (runningFlag && it.hasNext()) {
					SelectionKey key = it.next();
					// key定义了四种不同形式的操作
					switch (key.readyOps()) {
					case SelectionKey.OP_ACCEPT:
						try {
							dealwithAccept(key);
						} catch (Exception e) {
							log.warn("新客户端建立连接失败");
							break;
						}
						break;
					case SelectionKey.OP_CONNECT:
						break;
					case SelectionKey.OP_READ:
						dealwithRead(key);
						break;
					case SelectionKey.OP_WRITE:
						break;
					}
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 处理接收连接的事件
	 * 
	 * @throws Exception
	 */
	private void dealwithAccept(SelectionKey key) throws Exception {
		try {
			/** 移除本次事件 */
			selector.selectedKeys().remove(key);
			log.info("新的客户端请求连接");
			ServerSocketChannel server = (ServerSocketChannel) key.channel();
			SocketChannel socketChannel = server.accept();
			socketChannel.configureBlocking(false);
			/** 注册读事件 */
			socketChannel.register(selector, SelectionKey.OP_READ);
			log.info("客户端连接成功");
		} catch (IOException e) {
			throw new Exception();
		}
	}

	/** 处理客户端发来的消息，处理读事件 */
	private void dealwithRead(SelectionKey key) {
		/** 移除本次事件 */
		selector.selectedKeys().remove(key);
		/** 收到message */
		SocketChannel socketChannel = (SocketChannel) key.channel();
		log.info("读入数据");
		readByteBuffer.clear();
		/** 将字节序列从此通道中读入给定的缓冲区r_bBuf */
		if (socketChannel != null && socketChannel.isConnected()) {
			try {
				socketChannel.read(readByteBuffer);
			} catch (IOException e) {
				try {
					socketChannel.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				socketChannel = null;
				return;
			}

			readByteBuffer.flip();
			String msg = Charset.forName("UTF-8").decode(readByteBuffer)
					.toString();

			/** 解析，处理message */
			switch (parseCommand(msg)) {
			case HIEP_STOP:
				/** 先关掉连接 */
				try {
					socketChannel.write(ByteBuffer.wrap("已经与客户端断开连接"
							.getBytes("UTF-8")));
				} catch (UnsupportedEncodingException e) {
					log.error("socket编码出错" + e.getMessage());
					break;
				} catch (IOException e) {
					log.error("socket读取出错" + e.getMessage());
					break;
				}

				try {
					socketChannel.socket().close();
					socketChannel.close();
					socketChannel = null;
				} catch (IOException e) {
					log.error("socketChannel关闭失败");
					return;
				}

				this.runningFlag = false;
				/** 关掉当前线程 */
				hiepController.stop(ConstantValue.HIEP_STOP_TIMEOUT);

				break;
			case HIEP_DISCONNECT:
				/** 如果客户端发出bye命令，则服务器端与客户端断开连接 */
				try {
					socketChannel.write(ByteBuffer.wrap("已经与客户端断开连接"
							.getBytes("UTF-8")));
				} catch (UnsupportedEncodingException e) {
					log.error("socket编码出错" + e.getMessage());
					break;
				} catch (IOException e) {
					log.warn("socketChannel写出错" + e.getMessage());
					return;
				}
				try {
					socketChannel.socket().close();
					socketChannel.close();
					socketChannel = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case HIEP_CURRENT_TIME:
				/** 如果客户端发出time命令，则服务器端返回当前时间给客户端 */
				try {
					writeByteBuffer = ByteBuffer.wrap(getCurrentTime()
							.getBytes("UTF-8"));
				} catch (UnsupportedEncodingException e) {
					log.error("socket编码出错" + e.getMessage());
					break;
				}
				try {
					socketChannel.write(writeByteBuffer);
				} catch (IOException e) {
					log.warn("socketChannel写出错" + e.getMessage());
					return;
				}
				writeByteBuffer.clear();
				break;
			case HIEP_VIEW_THREADS:
				/** 如果客户端发出time命令，则服务器端返回当前时间给客户端 */
				try {
					writeByteBuffer = ByteBuffer.wrap(getCurrentTime()
							.getBytes("UTF-8"));
				} catch (UnsupportedEncodingException e) {
					log.error("socket编码出错" + e.getMessage());
					break;
				}
				try {
					socketChannel.write(writeByteBuffer);
				} catch (IOException e) {
					log.warn("socketChannel写出错" + e.getMessage());
					return;
				}
				writeByteBuffer.clear();
				break;
			default:
				/** 错误命令，回写给客户端 */
				msg += "\n错误命令";
				try {
					socketChannel.write(ByteBuffer.wrap(msg.getBytes("UTF-8")));
				} catch (UnsupportedEncodingException e) {
					log.error("socket编码出错" + e.getMessage());
					break;
				} catch (IOException e) {
					log.warn("socketChannel写出错" + e.getMessage());
					return;
				}
				break;
			}
			log.info("收到命令:" + msg);
			readByteBuffer.clear();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				log.error("睡眠中断");
				return;
			}
		}
	}

	private String getCurrentTime() {
		Calendar date = Calendar.getInstance();
		String time = "服务器当前时间：" + date.get(Calendar.YEAR) + "-"
				+ date.get(Calendar.MONTH) + 1 + "-" + date.get(Calendar.DATE)
				+ " " + date.get(Calendar.HOUR) + ":"
				+ date.get(Calendar.MINUTE) + ":" + date.get(Calendar.SECOND);
		return time;
	}

	private int parseCommand(String msg) {
		if (msg.equalsIgnoreCase("stop")) {
			/** 如果是停止的命令 */
			log.info("收到stop命令");
			return HIEP_STOP;
		}
		if (msg.equalsIgnoreCase("time")) {
			/** 如果是询问服务器当前时间的命令 */
			log.info("收到询问时间time命令");
			return HIEP_CURRENT_TIME;
		}
		if (msg.equalsIgnoreCase("bye")) {
			/** 如果是停止的命令 */
			log.info("收到bye命令");
			return HIEP_DISCONNECT;
		}
		if (msg.equalsIgnoreCase("view")) {
			/** 如果是查看线程的命令 */
			log.info("收到view命令");
			return HIEP_VIEW_THREADS;
		}
		return HIEP_ERROR_COMMAND;
	}
}
