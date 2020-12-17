package com.aerotop.logserviceevolution;

import com.aerotop.enums.FrameTypeEnum;
import com.aerotop.enums.LogLevelEnum;
import com.aerotop.logserviceevolution.configload.LoadConfig;
import com.aerotop.logserviceevolution.kafka.KafkaConsumerThread;
import com.aerotop.logserviceevolution.selfinspection.MessageReceiverThread;
import com.aerotop.message.Message;
import com.aerotop.service.impl.WriterServiceImpl;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.io.*;

@SpringBootApplication
public class LogServiceEvolution {
    // 版本文件生成路径
    @Value("${versionPath}")
    private String versionPath;
    //版本文件写入内容
    public static final String versionCount = "v2.12-20201212-1600";
    // 创建全局写入对象
    public static final WriterServiceImpl writerServiceImpl = new WriterServiceImpl();
    //日志记录对象
    private static Message message = new Message(FrameTypeEnum.DATAFRAME,"日志服务",LogLevelEnum.error,
            System.currentTimeMillis(),(byte)10,"","","",null);
    public static void main(String[] args) {
        //开启主线程
        SpringApplication.run(LogServiceEvolution.class, args);

        // 注册关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                // 退出kafka consumer
                KafkaConsumerThread.kafkaConsumer.wakeup();
            }catch (WakeupException e){
                ByteArrayOutputStream baoS = new ByteArrayOutputStream();
                e.printStackTrace(new PrintStream(baoS));
                message.setLoglevel(LogLevelEnum.error);
                message.setReserved("kafka consumer执行退出函数,此异常为正常现象!"+baoS.toString());
                writerServiceImpl.logger(message);
            }
            try {
                message.setLoglevel(LogLevelEnum.info);
                message.setReserved("程序关闭执行通道刷新!");
                writerServiceImpl.logger(message);
            } finally {
                // 关闭时自动执行刷新
                writerServiceImpl.closeChannel();
            }
        }));

        //启动consumer线程
        new KafkaConsumerThread(LoadConfig.getInstance().getProps(), LoadConfig.getInstance().getKafka_consumer_topic())
                .start();
        //启动UDP监听线程
        new MessageReceiverThread().start();
    }
     /**
      * @Description:根据配置路径创建版本文件
      * @Author: gaosong
      * @Date: 2020/8/26 13:52
      * @param: * @param null: 
      * @return: * @return: null
      **/
    @PostConstruct
    private void createVersionFile() {
        try {
            File directory = new File(versionPath);
            File file;
            if(!directory.exists()){
                directory.mkdirs();
            }
            file = new File(versionPath + "/version-RZFW.ini");
            //if(!file.exists()){
            //每次都创建并覆盖
            file.createNewFile();
            //}
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(versionCount);
            fileWriter.close();
            message.setLoglevel(LogLevelEnum.info);
            message.setReserved("创建版本文件version.ini,生成路径:" + versionPath + ",文件内容:"+versionCount);
            writerServiceImpl.logger(message);
            writerServiceImpl.flushChannel();
        } catch (IOException e) {
            ByteArrayOutputStream baoS = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baoS));
            message.setLoglevel(LogLevelEnum.error);
            message.setReserved("创建版本文件异常!"+baoS.toString());
            writerServiceImpl.logger(message);
            writerServiceImpl.flushChannel();
        }
    }
}
