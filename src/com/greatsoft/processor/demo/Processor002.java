package com.greatsoft.processor.demo;

import com.greatsoft.processor.thirdjar.demo.ThirdJarDemo;
import com.greatsoft.transq.core.message.AbstractMessage;
import com.greatsoft.transq.core.message.ResultImp;
import com.greatsoft.transq.exception.ProcessorException;
import com.greatsoft.transq.processor.api.Processor;
import com.greatsoft.transq.utils.ByteBufferHelper;
import com.greatsoft.transq.utils.FileHelper;
import java.io.File;

public class Processor002 implements Processor
{

  public boolean init()
  {
    System.out.println(super.getClass().getName() + "��ʼ���ɹ�");
    return true;
  }

  public boolean destory()
  {
    System.out.println(super.getClass().getName() + "Processer�������ɹ�");
    return true;
  }

@Override
public ResultImp process(AbstractMessage message, Object addtionParam)
		throws ProcessorException {

	ThirdJarDemo thirdJar = new ThirdJarDemo();
    thirdJar.thirdJar();
    System.out.println("������JAR������������");
    String subAddress = message.getEnvelope().getTargetAddress().split("@")[0];
    String sendID = message.getEnvelope().getSendIdentify();

    if ((subAddress == null) || (subAddress.equals(""))) {
      System.out.println("��Ҫ�������Ϣ��Ŀ�ĵ�ַ����");
      return new ResultImp(1, "��Ϣ����ʧ��", null);
    }

    File subAddressDir = new File("D:/processor/" + subAddress + File.separator);
    if ((subAddressDir.isDirectory()) && (subAddressDir.exists())) {
      boolean result = ByteBufferHelper.putByteBuffer(message.getData(), subAddressDir + message.getEnvelope().getSourceDataName());
      if (result) {
          System.out.println(subAddress + "Ŀ¼����,����Ϣ" + sendID + "��Я�����������Ƶ�" + subAddress + "Ŀ¼����ɹ�");
          return new ResultImp(0, "��Ϣ����cheng gong", null);
        }else{
      	  System.out.println(subAddress + "Ŀ¼����,��ϢЯ���������Ƶ�" + subAddress + "Ŀ¼����ʧ��");
            return new ResultImp(1, "��Ϣ����ʧ��", null);
        }
    }

    System.out.println(subAddress + "Ŀ¼�����ڣ�������Ŀ¼");
    boolean mkdirOK = FileHelper.mkdir("D:/processor/" + subAddress + File.separator);
    if (mkdirOK) {
      System.out.println("����" + subAddress + "Ŀ¼�ɹ�");
      boolean result = ByteBufferHelper.putByteBuffer(message.getData(), subAddressDir + message.getEnvelope().getSourceDataName());
      if (result) {
        System.out.println(subAddress + "Ŀ¼����,����Ϣ" + sendID + "��Я�����������Ƶ�" + subAddress + "Ŀ¼����ɹ�");
        return new ResultImp(0, "��Ϣ���� cheng gong", null);
      }else{
    	  System.out.println(subAddress + "Ŀ¼����,��ϢЯ���������Ƶ�" + subAddress + "Ŀ¼����ʧ��");
          return new ResultImp(1, "��Ϣ����ʧ��", null);
      }
      
    }

    System.out.println("Processer��������Ϣ���");
    return new ResultImp(0, "��Ϣ���ͳɹ�", null);
}
}