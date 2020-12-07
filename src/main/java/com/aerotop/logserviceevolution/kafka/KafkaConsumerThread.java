package com.aerotop.logserviceevolution.kafka;

import com.aerotop.enums.FrameTypeEnum;
import com.aerotop.enums.LogLevelEnum;
import com.aerotop.logserviceevolution.LogServiceEvolution;
import com.aerotop.logserviceevolution.configload.LoadConfig;
import com.aerotop.logserviceevolution.messageparse.ParseForLog;
import com.aerotop.message.Message;
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
    //日志记录对象
    private Message message = new Message(FrameTypeEnum.DATAFRAME,"日志服务",LogLevelEnum.info,
            System.currentTimeMillis(),(byte)10,"","","",LoadConfig.getInstance().
            getKafka_consumer_topic());
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
                /**
                 * @Description: 此方法会在消费者停止消费消费后，在重平衡开始前调用
                 * @Author: gaosong
                 * @Date: 2020/12/3 15:58
                 * @param partitions: 主题分区对象集合
                 * @return: void
                 **/
                @Override
                public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                    //同步提交消费位移(防止重复消费),提交完毕再切换分区
                    kafkaConsumer.commitSync();

                    message.setReserved("kafka即将执行重平衡机制,已执行同步提交操作");
                    LogServiceEvolution.writerServiceImpl.logger(message);
                    LogServiceEvolution.writerServiceImpl.flushChannel();
                }
                /**
                 * @Description: 此方法在分区分配给消费者后，在消费者开始读取消息前调用
                 * @Author: gaosong
                 * @Date: 2020/12/3 15:58
                 * @param partitions: 主题分区对象集合
                 * @return: void
                 **/
                @Override
                public void onPartitionsAssigned(Collection<TopicPartition> partitions) {

                    //打印订阅信息
                    for (TopicPartition topicPartition : partitions) {
                        message.setReserved("kafka执行重平衡机制完毕,已订阅" + topic + "主题下" + topicPartition + "分区");
                        LogServiceEvolution.writerServiceImpl.logger(message);
                        LogServiceEvolution.writerServiceImpl.flushChannel();
                    }
                    //设置从分区末尾开始消费
                    kafkaConsumer.seekToEnd(partitions);
                    message.setReserved("已设置从分区末尾开始消费");
                    LogServiceEvolution.writerServiceImpl.logger(message);
                    LogServiceEvolution.writerServiceImpl.flushChannel();
                }
            });
        }catch (Exception e){
            ByteArrayOutputStream baoS = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baoS));
            message.setLoglevel(LogLevelEnum.error);
            message.setReserved(baoS.toString());
            LogServiceEvolution.writerServiceImpl.logger(message);
        }
    }

    @Override
    public void run() {
        //是否刷新通道缓存标志
        boolean flag=false;
        try{
            while (true) {
                ConsumerRecords<String, byte[]> records = kafkaConsumer.poll(Duration.ofMillis(100));
                if(records.isEmpty()){
                    if(!flag){
                        //实现刷新通道
                        LogServiceEvolution.writerServiceImpl.flushChannel();
                        flag = true;
                    }
                }else{
                    flag=false;
                    for (ConsumerRecord<String, byte[]> record : records) {
                        //调用消息处理模块
                        LogServiceEvolution.writerServiceImpl.logger(ParseForLog.unPack(record.value()));
                    }
                }
            }
        }catch (Exception e){
            ByteArrayOutputStream baoS = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baoS));
            message.setLoglevel(LogLevelEnum.error);
            message.setReserved(baoS.toString());
            LogServiceEvolution.writerServiceImpl.logger(message);
            //将日志内容刷新到文件
            LogServiceEvolution.writerServiceImpl.flushChannel();
        }
    }
}
