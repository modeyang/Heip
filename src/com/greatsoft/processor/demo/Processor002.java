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
    System.out.println(super.getClass().getName() + "初始化成功");
    return true;
  }

  public boolean destory()
  {
    System.out.println(super.getClass().getName() + "Processer清理工作成功");
    return true;
  }

@Override
public ResultImp process(AbstractMessage message, Object addtionParam)
		throws ProcessorException {

	ThirdJarDemo thirdJar = new ThirdJarDemo();
    thirdJar.thirdJar();
    System.out.println("第三方JAR辅助类调用完毕");
    String subAddress = message.getEnvelope().getTargetAddress().split("@")[0];
    String sendID = message.getEnvelope().getSendIdentify();

    if ((subAddress == null) || (subAddress.equals(""))) {
      System.out.println("将要处理的消息的目的地址有误");
      return new ResultImp(1, "消息发送失败", null);
    }

    File subAddressDir = new File("D:/processor/" + subAddress + File.separator);
    if ((subAddressDir.isDirectory()) && (subAddressDir.exists())) {
      boolean result = ByteBufferHelper.putByteBuffer(message.getData(), subAddressDir + message.getEnvelope().getSourceDataName());
      if (result) {
          System.out.println(subAddress + "目录存在,将消息" + sendID + "中携带的数据移移到" + subAddress + "目录下面成功");
          return new ResultImp(0, "消息发送cheng gong", null);
        }else{
      	  System.out.println(subAddress + "目录存在,消息携带的数据移到" + subAddress + "目录下面失败");
            return new ResultImp(1, "消息发送失败", null);
        }
    }

    System.out.println(subAddress + "目录不存在，创建此目录");
    boolean mkdirOK = FileHelper.mkdir("D:/processor/" + subAddress + File.separator);
    if (mkdirOK) {
      System.out.println("创建" + subAddress + "目录成功");
      boolean result = ByteBufferHelper.putByteBuffer(message.getData(), subAddressDir + message.getEnvelope().getSourceDataName());
      if (result) {
        System.out.println(subAddress + "目录存在,将消息" + sendID + "中携带的数据移移到" + subAddress + "目录下面成功");
        return new ResultImp(0, "消息发送 cheng gong", null);
      }else{
    	  System.out.println(subAddress + "目录存在,消息携带的数据移到" + subAddress + "目录下面失败");
          return new ResultImp(1, "消息发送失败", null);
      }
      
    }

    System.out.println("Processer处理本地消息完毕");
    return new ResultImp(0, "消息发送成功", null);
}
}