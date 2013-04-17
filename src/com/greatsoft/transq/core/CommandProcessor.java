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
	// ����һ���źż�����
	private Selector selector;
	// ��������
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
			/**����һ��ServerScoketͨ����ʵ�����������������ܷ�����IO�¼�
			 * */ 
			ServerSocketChannel serverSocketChannel = ServerSocketChannel
					.open();
			// ����ͨ������Ϊ�첽��ʽ
			serverSocketChannel.configureBlocking(false);
			// �󶨵�һ��ָ���Ķ˿�
			serverSocketChannel.socket().bind(new InetSocketAddress(port));
			// ע���ض����͵��¼����źż�������
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("RouterServer���ģ������...");
			while (runningFlag) {
				// ��������ִ�У�ֱ�����¼�����
				selector.select();
				Iterator<SelectionKey> it = selector.selectedKeys().iterator();
				while (it.hasNext()) {
					SelectionKey key = it.next();
					// key���������ֲ�ͬ��ʽ�Ĳ���
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
					/**����������Ƴ���ǰ�¼��������ظ�����*/
					it.remove();
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**����������ӵ��¼�*/
	private void dealwithAccept(SelectionKey key) {
		try {
			System.out.println("�µĿͻ�����������...");
			ServerSocketChannel server = (ServerSocketChannel) key.channel();
			SocketChannel socketChannel = server.accept();
			socketChannel.configureBlocking(false);
			/**ע����¼�*/
			socketChannel.register(selector, SelectionKey.OP_READ);
			System.out.println("�ͻ������ӳɹ�...");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**����ͻ��˷�������Ϣ��������¼�*/
	private void dealwithRead(SelectionKey key) {
		/**�յ�message*/
		try {
			SocketChannel socketChannel = (SocketChannel) key.channel();
			System.out.println("��������");
			readByteBuffer.clear();
			/**���ֽ����дӴ�ͨ���ж�������Ļ�����r_bBuf*/

			socketChannel.read(readByteBuffer);

			readByteBuffer.flip();
			String msg = Charset.forName("UTF-8").decode(readByteBuffer)
					.toString();

			/**����������message*/
			switch (parseCommand(msg)) {
			case HIEP_STOP:
				/**�ȹص�����*/
				socketChannel.write(ByteBuffer.wrap("�Ѿ���ͻ��˶Ͽ�����"
						.getBytes("UTF-8")));
				socketChannel.socket().close();
				this.runningFlag = false;	/**�ص���ǰ�߳�*/
				hiepController.stop(ConstantValue.HIEP_STOP_TIMEOUT);

				break;
			case HIEP_DISCONNECT:
				/**����ͻ��˷���bye��������������ͻ��˶Ͽ�����*/
				socketChannel.write(ByteBuffer.wrap("�Ѿ���ͻ��˶Ͽ�����"
						.getBytes("UTF-8")));
				socketChannel.socket().close();
				break;
			case HIEP_CURRENT_TIME:
				/**����ͻ��˷���time�����������˷��ص�ǰʱ����ͻ���*/
				writeByteBuffer = ByteBuffer.wrap(getCurrentTime().getBytes(
						"UTF-8"));
				socketChannel.write(writeByteBuffer);
				writeByteBuffer.clear();
				break;
			default:
				/**���������д���ͻ���*/
				msg +="\n��������";
				socketChannel.write(ByteBuffer.wrap(msg.getBytes("UTF-8")));
				break;
			}
			System.out.println(msg);
			System.out.println("�������...");
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
		String time = "��������ǰʱ�䣺" + date.get(Calendar.YEAR) + "-"
				+ date.get(Calendar.MONTH) + 1 + "-" + date.get(Calendar.DATE)
				+ " " + date.get(Calendar.HOUR) + ":"
				+ date.get(Calendar.MINUTE) + ":" + date.get(Calendar.SECOND);
		return time;
	}

	private int parseCommand(String msg) {
		// TODO Auto-generated method stub
		if (msg.equalsIgnoreCase("stop")) {
			/**�����ֹͣ������*/
			System.out
					.println("CommandProcess->parCommand receive command stop");
			return HIEP_STOP;
			/**�����߳�ȫ���˳�֮�󣬲��ܰ����߳�ɱ����
			 * Main.quit = true;
			 * */

		}
		if (msg.equalsIgnoreCase("time")) {
			/**�����ѯ�ʷ�������ǰʱ�������*/
			System.out
					.println("CommandProcess->parCommand receive command time");
			return HIEP_CURRENT_TIME;
		}
		if (msg.equalsIgnoreCase("bye")) {
			/**�����ֹͣ������*/
			System.out
					.println("CommandProcess->parCommand receive command bye");
			return HIEP_DISCONNECT;
		}
		return HIEP_ERROR_COMMAND;
	}

}
