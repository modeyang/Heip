package com.greatsoft.transq.core;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Iterator;

import com.greatsoft.transq.utils.ConstantValue;

public class CommandProcessor implements Runnable {

	private int port;
	// 生成一个信号监视器
	private Selector selector;
	// 读缓冲区
	private ByteBuffer readByteBuffer = ByteBuffer.allocate(1024);
	private ByteBuffer writeByteBuffer;

	private static final int HIEP_STOP = 0;
	private static final int HIEP_CURRENT_TIME = 1;
	private static final int HIEP_DISCONNECT = 2;
	private static final int HIEP_ERROR_COMMAND = 99999;

	public static final int NORMAL_QUIT = 0;
	public static final int FORCE_QUIT = -1;

	private HiepController hiepController;
	
	private boolean runningFlag;

	public CommandProcessor(int port, HiepController controller) {
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
		try {
			/**生成一个ServerScoket通道的实例对象，用于侦听可能发生的IO事件
			 * */ 
			ServerSocketChannel serverSocketChannel = ServerSocketChannel
					.open();
			// 将该通道设置为异步方式
			serverSocketChannel.configureBlocking(false);
			// 绑定到一个指定的端口
			serverSocketChannel.socket().bind(new InetSocketAddress(port));
			// 注册特定类型的事件到信号监视器上
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("RouterServer监控模块启动...");
			while (runningFlag) {
				// 将会阻塞执行，直到有事件发生
				selector.select();
				Iterator<SelectionKey> it = selector.selectedKeys().iterator();
				while (it.hasNext()) {
					SelectionKey key = it.next();
					// key定义了四种不同形式的操作
					switch (key.readyOps()) {
					case SelectionKey.OP_ACCEPT:
						dealwithAccept(key);
						break;
					case SelectionKey.OP_CONNECT:
						break;
					case SelectionKey.OP_READ:
						dealwithRead(key);
						break;
					case SelectionKey.OP_WRITE:
						break;
					}
					/**处理结束后移除当前事件，以免重复处理*/
					it.remove();
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**处理接收连接的事件*/
	private void dealwithAccept(SelectionKey key) {
		try {
			System.out.println("新的客户端请求连接...");
			ServerSocketChannel server = (ServerSocketChannel) key.channel();
			SocketChannel socketChannel = server.accept();
			socketChannel.configureBlocking(false);
			/**注册读事件*/
			socketChannel.register(selector, SelectionKey.OP_READ);
			System.out.println("客户端连接成功...");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**处理客户端发来的消息，处理读事件*/
	private void dealwithRead(SelectionKey key) {
		/**收到message*/
		try {
			SocketChannel socketChannel = (SocketChannel) key.channel();
			System.out.println("读入数据");
			readByteBuffer.clear();
			/**将字节序列从此通道中读入给定的缓冲区r_bBuf*/

			socketChannel.read(readByteBuffer);

			readByteBuffer.flip();
			String msg = Charset.forName("UTF-8").decode(readByteBuffer)
					.toString();

			/**解析，处理message*/
			switch (parseCommand(msg)) {
			case HIEP_STOP:
				/**先关掉连接*/
				socketChannel.write(ByteBuffer.wrap("已经与客户端断开连接"
						.getBytes("UTF-8")));
				socketChannel.socket().close();
				this.runningFlag = false;	/**关掉当前线程*/
				hiepController.stop(ConstantValue.HIEP_STOP_TIMEOUT);

				break;
			case HIEP_DISCONNECT:
				/**如果客户端发出bye命令，则服务器端与客户端断开连接*/
				socketChannel.write(ByteBuffer.wrap("已经与客户端断开连接"
						.getBytes("UTF-8")));
				socketChannel.socket().close();
				break;
			case HIEP_CURRENT_TIME:
				/**如果客户端发出time命令，则服务器端返回当前时间给客户端*/
				writeByteBuffer = ByteBuffer.wrap(getCurrentTime().getBytes(
						"UTF-8"));
				socketChannel.write(writeByteBuffer);
				writeByteBuffer.clear();
				break;
			default:
				/**错误命令，回写给客户端*/
				msg +="\n错误命令";
				socketChannel.write(ByteBuffer.wrap(msg.getBytes("UTF-8")));
				break;
			}
			System.out.println(msg);
			System.out.println("处理完毕...");
			readByteBuffer.clear();

			Thread.currentThread();

			Thread.sleep(100);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
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
		// TODO Auto-generated method stub
		if (msg.equalsIgnoreCase("stop")) {
			/**如果是停止的命令*/
			System.out
					.println("CommandProcess->parCommand receive command stop");
			return HIEP_STOP;
			/**等子线程全部退出之后，才能把主线程杀掉。
			 * Main.quit = true;
			 * */

		}
		if (msg.equalsIgnoreCase("time")) {
			/**如果是询问服务器当前时间的命令*/
			System.out
					.println("CommandProcess->parCommand receive command time");
			return HIEP_CURRENT_TIME;
		}
		if (msg.equalsIgnoreCase("bye")) {
			/**如果是停止的命令*/
			System.out
					.println("CommandProcess->parCommand receive command bye");
			return HIEP_DISCONNECT;
		}
		return HIEP_ERROR_COMMAND;
	}

}
