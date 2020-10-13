package com.aerotop.logserviceevolution;

import com.aerotop.enums.FrameTypeEnum;
import com.aerotop.logserviceevolution.configload.LoadConfig;
import com.aerotop.logserviceevolution.kafka.KafkaConsumerThread;
import com.aerotop.logserviceevolution.messagehandler.MessageHandler;
import com.aerotop.logserviceevolution.messagehandler.MessageModel;
import com.aerotop.transfer.WriterSingle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.io.*;

@SpringBootApplication
public class LogServiceEvolution {
    //日志生成对象
    //private static final Logger log = LoggerFactory.getLogger(LogServiceEvolution.class);
    @Value("${versionPath}")
    private String versionPath;//版本文件生成路径

    public static void main(String[] args) {
        // 注册关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // 执行收尾工作
            for (MessageModel messageModel : MessageHandler.sendMapping.values()) {
                try {
                    //关闭时自动执行刷新
                    messageModel.getBufferedOutputStream().close();
                } catch (IOException e) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    e.printStackTrace(new PrintStream(baos));
                    WriterSingle.getInstance().loggerError((byte)10,"错误捕捉",baos.toString(),"","");
                    //log.error(baos.toString());
                }
            }
            //log.info("程序退出时执行缓存刷新!");
            //System.out.println("----------------日志服务已退出----------------");
            WriterSingle.getInstance().loggerInfo((byte)10,"刷新缓存","程序退出时执行缓存刷新!","","");
            //WriterSingle.getInstance().close();
        }));
        //初始化本地库记录对象
        WriterSingle.getInstance().setFrameType(FrameTypeEnum.DATAFRAME);
        WriterSingle.getInstance().setSourceName("日志服务");
        SpringApplication.run(LogServiceEvolution.class, args);
        new KafkaConsumerThread(LoadConfig.getInstance().getProps(), LoadConfig.getInstance().getKafka_consumer_topic()).start();
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
            FileWriter fileWriter = new FileWriter(new File(versionPath + "/version.ini"));
            fileWriter.write("v2.0-20200812-1600");
            fileWriter.close();
            //log.info("version.ini已生成!生成路径:" + versionPath + "/version.ini");
            WriterSingle.getInstance().loggerInfo((byte)10,"版本文件创建信息","version.ini已生成!生成路径:" + versionPath + "/version.ini","","");
        } catch (IOException e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            //log.error(baos.toString());
            WriterSingle.getInstance().loggerError((byte)10,"错误捕捉",baos.toString(),"","");

        }
    }
}
