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
 * Dispatcherģ������������
 * */
public class DispatcherCommandProcessor implements Runnable {
	private Logger log = Logger.getLogger(DispatcherCommandProcessor.class);
	private int port;
	// ����һ���źż�����
	private Selector selector;
	// ��������
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
			 * ����һ��ServerScoketͨ����ʵ�����������������ܷ�����IO�¼�
			 * */
			serverSocketChannel = ServerSocketChannel.open();
			// ����ͨ������Ϊ�첽��ʽ
			serverSocketChannel.configureBlocking(false);
			// �󶨵�һ��ָ���Ķ˿�
			serverSocketChannel.socket().bind(new InetSocketAddress(port));
			// ע���ض����͵��¼����źż�������
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			log.info("Dispatcher���ģ������...");
			while (runningFlag && selector.select() > 0) {
				// ��������ִ�У�ֱ�����¼�����
				selector.select();
				Iterator<SelectionKey> it = selector.selectedKeys().iterator();
				while (runningFlag && it.hasNext()) {
					SelectionKey key = it.next();
					// key���������ֲ�ͬ��ʽ�Ĳ���
					switch (key.readyOps()) {
					case SelectionKey.OP_ACCEPT:
						try {
							dealwithAccept(key);
						} catch (Exception e) {
							log.warn("�¿ͻ��˽�������ʧ��");
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
	 * ����������ӵ��¼�
	 * 
	 * @throws Exception
	 */
	private void dealwithAccept(SelectionKey key) throws Exception {
		try {
			/** �Ƴ������¼� */
			selector.selectedKeys().remove(key);
			log.info("�µĿͻ�����������");
			ServerSocketChannel server = (ServerSocketChannel) key.channel();
			SocketChannel socketChannel = server.accept();
			socketChannel.configureBlocking(false);
			/** ע����¼� */
			socketChannel.register(selector, SelectionKey.OP_READ);
			log.info("�ͻ������ӳɹ�");
		} catch (IOException e) {
			throw new Exception();
		}
	}

	/** ����ͻ��˷�������Ϣ��������¼� */
	private void dealwithRead(SelectionKey key) {
		/** �Ƴ������¼� */
		selector.selectedKeys().remove(key);
		/** �յ�message */
		SocketChannel socketChannel = (SocketChannel) key.channel();
		log.info("��������");
		readByteBuffer.clear();
		/** ���ֽ����дӴ�ͨ���ж�������Ļ�����r_bBuf */
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

			/** ����������message */
			switch (parseCommand(msg)) {
			case HIEP_STOP:
				/** �ȹص����� */
				try {
					socketChannel.write(ByteBuffer.wrap("�Ѿ���ͻ��˶Ͽ�����"
							.getBytes("UTF-8")));
				} catch (UnsupportedEncodingException e) {
					log.error("socket�������" + e.getMessage());
					break;
				} catch (IOException e) {
					log.error("socket��ȡ����" + e.getMessage());
					break;
				}

				try {
					socketChannel.socket().close();
					socketChannel.close();
					socketChannel = null;
				} catch (IOException e) {
					log.error("socketChannel�ر�ʧ��");
					return;
				}

				this.runningFlag = false;
				/** �ص���ǰ�߳� */
				hiepController.stop(ConstantValue.HIEP_STOP_TIMEOUT);

				break;
			case HIEP_DISCONNECT:
				/** ����ͻ��˷���bye��������������ͻ��˶Ͽ����� */
				try {
					socketChannel.write(ByteBuffer.wrap("�Ѿ���ͻ��˶Ͽ�����"
							.getBytes("UTF-8")));
				} catch (UnsupportedEncodingException e) {
					log.error("socket�������" + e.getMessage());
					break;
				} catch (IOException e) {
					log.warn("socketChannelд����" + e.getMessage());
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
				/** ����ͻ��˷���time�����������˷��ص�ǰʱ����ͻ��� */
				try {
					writeByteBuffer = ByteBuffer.wrap(getCurrentTime()
							.getBytes("UTF-8"));
				} catch (UnsupportedEncodingException e) {
					log.error("socket�������" + e.getMessage());
					break;
				}
				try {
					socketChannel.write(writeByteBuffer);
				} catch (IOException e) {
					log.warn("socketChannelд����" + e.getMessage());
					return;
				}
				writeByteBuffer.clear();
				break;
			case HIEP_VIEW_THREADS:
				/** ����ͻ��˷���time�����������˷��ص�ǰʱ����ͻ��� */
				try {
					writeByteBuffer = ByteBuffer.wrap(getCurrentTime()
							.getBytes("UTF-8"));
				} catch (UnsupportedEncodingException e) {
					log.error("socket�������" + e.getMessage());
					break;
				}
				try {
					socketChannel.write(writeByteBuffer);
				} catch (IOException e) {
					log.warn("socketChannelд����" + e.getMessage());
					return;
				}
				writeByteBuffer.clear();
				break;
			default:
				/** ���������д���ͻ��� */
				msg += "\n��������";
				try {
					socketChannel.write(ByteBuffer.wrap(msg.getBytes("UTF-8")));
				} catch (UnsupportedEncodingException e) {
					log.error("socket�������" + e.getMessage());
					break;
				} catch (IOException e) {
					log.warn("socketChannelд����" + e.getMessage());
					return;
				}
				break;
			}
			log.info("�յ�����:" + msg);
			readByteBuffer.clear();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				log.error("˯���ж�");
				return;
			}
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
		if (msg.equalsIgnoreCase("stop")) {
			/** �����ֹͣ������ */
			log.info("�յ�stop����");
			return HIEP_STOP;
		}
		if (msg.equalsIgnoreCase("time")) {
			/** �����ѯ�ʷ�������ǰʱ������� */
			log.info("�յ�ѯ��ʱ��time����");
			return HIEP_CURRENT_TIME;
		}
		if (msg.equalsIgnoreCase("bye")) {
			/** �����ֹͣ������ */
			log.info("�յ�bye����");
			return HIEP_DISCONNECT;
		}
		if (msg.equalsIgnoreCase("view")) {
			/** ����ǲ鿴�̵߳����� */
			log.info("�յ�view����");
			return HIEP_VIEW_THREADS;
		}
		return HIEP_ERROR_COMMAND;
	}
}
