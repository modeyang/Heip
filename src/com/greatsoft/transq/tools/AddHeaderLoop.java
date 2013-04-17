package com.greatsoft.transq.tools;

/**
 * @param args
 *            [0]:信封配置文件存放的目录:envelopeConfigFileDir:
 *            HiepHomePath:C:/Users/mojia/
 *            Desktop/Hiep/hiepHome/data/hiepTest/envelopeConfigDir/
 * @param args
 *            [1]:测试数据存放的目录
 *            testDataDir:C:/Users/mojia/Desktop/Hiep/hiepHome/data
 *            /hiepTest/testDataDir/
 * @param args
 *            [2]:生成新的HiepMessage存放的目录
 *            newHiepMessageDir:C:/Users/mojia/Desktop/Hiep
 *            /hiepHome/data/hiepTest/tempHiepMessage/
 * @author mojia
 * 
 */
public class AddHeaderLoop {
	// private static int count = 2000;
	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("传进来的参数不对");
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
