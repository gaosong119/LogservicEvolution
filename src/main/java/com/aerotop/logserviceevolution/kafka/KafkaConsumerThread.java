package com.aerotop.logserviceevolution.kafka;

import com.aerotop.logserviceevolution.messagehandler.MessageHandler;
import com.aerotop.logserviceevolution.messagehandler.MessageModel;
import com.aerotop.logserviceevolution.messageparse.ParseForLog;
import com.aerotop.transfer.WriterSingle;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.Duration;
import java.util.*;

/**
 * @ClassName: KafkaConsumerThread
 * @Description: kafkaConsumer线程类，用来启动KafkaConsumer
 * @Author: gaosong
 * @Date 2020/7/20 8:44
 */
public class KafkaConsumerThread extends Thread {
    //日志生成对象
    //private static final Logger log = LoggerFactory.getLogger(KafkaConsumerThread.class);
    //声明 kafkaConsumer
    private KafkaConsumer<String, byte[]> kafkaConsumer;

    /**
     * @Description:构造kafkaConsumer对象
     * @Author: gaosong
     * @Date: 2020/7/27 9:47
     * @param: props, topic
     * @return: null
     **/
    public KafkaConsumerThread(Properties props, String topic) {
        try {
            this.kafkaConsumer = new KafkaConsumer<>(props);
            this.kafkaConsumer.subscribe(Collections.singletonList(topic));
            //设置从分区末尾开始消费
            Set<TopicPartition> assignment = new HashSet<>();
            while (assignment.size() == 0) {
                this.kafkaConsumer.poll(Duration.ofMillis(100));
                assignment = this.kafkaConsumer.assignment();
            }
            this.kafkaConsumer.poll(Duration.ofMillis(100));
            this.kafkaConsumer.seekToEnd(assignment);
            //打印订阅信息
            for (TopicPartition topicPartition : assignment) {
                //log.info("已订阅"+topic+"主题下"+iterator.next()+"分区");
                WriterSingle.getInstance().loggerInfo((byte) 10, "记录订阅主题信息", "已订阅" + topic + "主题下" + topicPartition + "分区", "", "");
            }
        }catch (Exception e){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            //log.error(baos.toString());
            WriterSingle.getInstance().loggerError((byte)10,"错误捕捉",baos.toString(),"","");
        }
    }

    @Override
    public void run() {
        boolean flag=false;
        while (true) {
            ConsumerRecords<String, byte[]> records = kafkaConsumer.poll(Duration.ofMillis(10));
            if(records.isEmpty()){
                if(!flag){
                    for(MessageModel messageModel : MessageHandler.sendMapping.values()){
                        try {
                            messageModel.getBufferedOutputStream().flush();
                           //log.debug("程序将缓存数据刷新到文件!");
                        } catch (IOException e) {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            e.printStackTrace(new PrintStream(baos));
                            //log.error(baos.toString());
                            WriterSingle.getInstance().loggerError((byte)10,"错误捕捉",baos.toString(),"","");
                        }
                    }
                    flag = true;
                }
            }else{
                flag=false;
                try {
                    for (ConsumerRecord<String, byte[]> record : records) {
                        //打印日志
                        //log.debug("收到kafka消息-->主题:"+record.topic()+",分区编号:"+record.partition()+"数量:"+records.count());
                        //调用消息处理模块
                        MessageHandler.messageProcess(ParseForLog.unPack(record.value()));
                    }
                }catch (Exception e){
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    e.printStackTrace(new PrintStream(baos));
                    //log.error(baos.toString());
                    WriterSingle.getInstance().loggerError((byte)10,"错误捕捉",baos.toString(),"","");
                }

            }
        }
    }
}
