package com.greatsoft.transq.tools;

/**
 * @param args
 *            [0]:�ŷ������ļ���ŵ�Ŀ¼:envelopeConfigFileDir:
 *            HiepHomePath:C:/Users/mojia/
 *            Desktop/Hiep/hiepHome/data/hiepTest/envelopeConfigDir/
 * @param args
 *            [1]:�������ݴ�ŵ�Ŀ¼
 *            testDataDir:C:/Users/mojia/Desktop/Hiep/hiepHome/data
 *            /hiepTest/testDataDir/
 * @param args
 *            [2]:�����µ�HiepMessage��ŵ�Ŀ¼
 *            newHiepMessageDir:C:/Users/mojia/Desktop/Hiep
 *            /hiepHome/data/hiepTest/tempHiepMessage/
 * @author mojia
 * 
 */
public class AddHeaderLoop {
	// private static int count = 2000;
	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("�������Ĳ�������");
			return;
		}
		String envelopeConfigFileDir = args[0];
		String testDataDir = args[1];
		String newHiepMessageDir = args[2];

		while (true) {
			new AddHeaderWorker(envelopeConfigFileDir, testDataDir,
					newHiepMessageDir).run();
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
