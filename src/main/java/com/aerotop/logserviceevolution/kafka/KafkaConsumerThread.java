package com.aerotop.logserviceevolution.kafka;

import com.aerotop.logserviceevolution.messagehandler.MessageHandler;
import com.aerotop.logserviceevolution.messagehandler.MessageModel;
import com.aerotop.logserviceevolution.messageparse.ParseForLog;
import com.aerotop.transfer.WriterSingle;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;

/**
 * @ClassName: KafkaConsumerThread
 * @Description: kafkaConsumer线程类，用来启动KafkaConsumer
 * @Author: gaosong
 * @Date 2020/7/20 8:44
 */
public class KafkaConsumerThread extends Thread {
    //声明 kafkaConsumer
    public static KafkaConsumer<String, byte[]> kafkaConsumer;

    /**
     * @Description:构造kafkaConsumer对象
     * @Author: gaosong
     * @Date: 2020/7/27 9:47
     * @param: props, topic
     * @return: null
     **/
    public KafkaConsumerThread(Properties props, String topic) {
        try {
            kafkaConsumer = new KafkaConsumer<>(props);
            kafkaConsumer.subscribe(Collections.singletonList(topic), new ConsumerRebalanceListener() {
                @Override
                public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                    //此方法会在消费者停止消费消费后，在重平衡开始前调用
                    WriterSingle.getInstance().loggerInfo((byte) 10, "kafka即将执行重平衡机制", "已收到重平衡信号,即将进入重平衡阶段", "");
                    //同步提交消费位移(防止重复消费)
                    kafkaConsumer.commitSync();
                }

                @Override
                public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                    //此方法在分区分配给消费者后，在消费者开始读取消息前调用
                    WriterSingle.getInstance().loggerInfo((byte) 10, "kafka执行重平衡机制完毕", "分区已重新分配消费者,即将消费消息", "");
                    //打印订阅信息
                    for (TopicPartition topicPartition : partitions) {
                        WriterSingle.getInstance().loggerInfo((byte) 10, "记录分区重平衡之后订阅主题信息", "已订阅" + topic + "主题下" + topicPartition + "分区", "");
                    }
                    //设置从分区末尾开始消费
                    kafkaConsumer.seekToEnd(partitions);
                }
            });
        }catch (Exception e){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            WriterSingle.getInstance().loggerError((byte)10,"错误捕捉",baos.toString(),"");
        }
    }

    @Override
    public void run() {
        boolean flag=false;
        try{
            while (true) {
                ConsumerRecords<String, byte[]> records = kafkaConsumer.poll(Duration.ofMillis(100));
                if(records.isEmpty()){
                    if(!flag){
                        for(MessageModel messageModel : MessageHandler.sendMapping.values()){
                            messageModel.getBufferedOutputStream().flush();
                        }
                        flag = true;
                    }
                }else{
                    flag=false;
                    for (ConsumerRecord<String, byte[]> record : records) {
                        //调用消息处理模块
                        MessageHandler.messageProcess(ParseForLog.unPack(record.value()));
                    }
                }
            }
        }catch (Exception e){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            WriterSingle.getInstance().loggerError((byte)10,"错误捕捉",baos.toString(),"");
        }
    }
}
