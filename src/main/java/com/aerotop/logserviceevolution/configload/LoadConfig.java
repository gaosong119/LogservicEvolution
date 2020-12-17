package com.aerotop.logserviceevolution.configload;

import com.aerotop.enums.FrameTypeEnum;
import com.aerotop.enums.LogLevelEnum;
import com.aerotop.logserviceevolution.LogServiceEvolution;
import com.aerotop.message.Message;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Properties;

/**
 * @ClassName: LoadConfig
 * @Description: 加载application.properties配置文件所有内容到单例对象中
 * @Author: gaosong
 * @Date 2020/9/22 17:08
 */
@Component
public class LoadConfig {
    //写入对象
    private Message message = new Message(FrameTypeEnum.DATAFRAME,"日志服务",LogLevelEnum.info,
            System.currentTimeMillis(),(byte)10,"","","",null);

    private static LoadConfig instance = new LoadConfig();//获取实例对象

    private String kafka_bootstrap_servers;//consumer连接kafka集群所需的broker地址清单

    private String kafka_consumer_group_id;//消费者组id

    private String kafka_consumer_client_id;//消费者client-id

    private String kafka_consumer_topic;//日志主题

    private String receivePort;//接收方端口

    private String feedbackPort;//自检软件接收端口

    private String sourceSystemType;//信源-系统类型

    private String sourceSystemCode;//信源-系统编码

    private String sourceNodeCode;//信源-节点编码

    private String sourceSoftID;//信源-软件标识

    private String receiveSystemType;//信宿-系统类型

    private String receiveSystemCode;//信宿-系统编码

    private String receiveNodeCode;//信宿-节点编码

    private String receiveSoftID;//信宿-软件标识

    private Properties props;//用来初始化props

    private LoadConfig() {}
    /**
     * @Description 获取LoadConfig实例对象
     * @Return instance
     * @Author gaosong
     * @Date 2020/7/16 14:09
     */
    public static LoadConfig getInstance(){
        return instance;
    }

    public String getReceiveSoftID() {
        return receiveSoftID;
    }
    @Value("${source_soft_ID}")
    public void setReceiveSoftID(String receiveSoftID) {
        instance.receiveSoftID = receiveSoftID;
    }

    public String getSourceSoftID() {
        return sourceSoftID;
    }
    @Value("${source_soft_ID}")
    public void setSourceSoftID(String sourceSoftID) {
        instance.sourceSoftID = sourceSoftID;
    }

    public String getReceivePort() {
        return receivePort;
    }
    @Value("${receivePort}")
    public void setReceivePort(String receivePort) {
        instance.receivePort = receivePort;
        message.setReserved("加载接收方UDP监听端口:"+receivePort);
        LogServiceEvolution.writerServiceImpl.logger(message);
    }

    public String getFeedbackPort() {
        return feedbackPort;
    }
    @Value("${feedbackPort}")
    public void setFeedbackPort(String feedbackPort) {
        instance.feedbackPort = feedbackPort;
        message.setReserved("加载自检软件接收端口:"+feedbackPort);
        LogServiceEvolution.writerServiceImpl.logger(message);
    }

    public String getSourceSystemType() {
        return sourceSystemType;
    }
    @Value("${source_system_type}")
    public void setSourceSystemType(String sourceSystemType) {
        instance.sourceSystemType = sourceSystemType;
        message.setReserved("加载信源-系统类型配置:"+sourceSystemType);
        LogServiceEvolution.writerServiceImpl.logger(message);
    }

    public String getSourceSystemCode() {
        return sourceSystemCode;
    }
    @Value("${source_system_code}")
    public void setSourceSystemCode(String sourceSystemCode) {
        instance.sourceSystemCode = sourceSystemCode;
        message.setReserved("加载信源-系统编码配置:"+sourceSystemCode);
        LogServiceEvolution.writerServiceImpl.logger(message);
    }

    public String getSourceNodeCode() {
        return sourceNodeCode;
    }
    @Value("${source_node_code}")
    public void setSourceNodeCode(String sourceNodeCode) {
        instance.sourceNodeCode = sourceNodeCode;
        message.setReserved("加载信源-节点编码配置:"+sourceNodeCode);
        LogServiceEvolution.writerServiceImpl.logger(message);
    }

    public String getReceiveSystemType() {
        return receiveSystemType;
    }
    @Value("${receive_system_type}")
    public void setReceiveSystemType(String receiveSystemType) {
        instance.receiveSystemType = receiveSystemType;
        message.setReserved("加载信宿-系统类型配置:"+receiveSystemType);
        LogServiceEvolution.writerServiceImpl.logger(message);
    }

    public String getReceiveSystemCode() {
        return receiveSystemCode;
    }
    @Value("${receive_system_code}")
    public void setReceiveSystemCode(String receiveSystemCode) {
        instance.receiveSystemCode = receiveSystemCode;
        message.setReserved("加载信宿-系统编码配置:"+receiveSystemCode);
        LogServiceEvolution.writerServiceImpl.logger(message);
    }

    public String getReceiveNodeCode() {
        return receiveNodeCode;
    }
    @Value("${receive_node_code}")
    public void setReceiveNodeCode(String receiveNodeCode) {
        instance.receiveNodeCode = receiveNodeCode;
        message.setReserved("加载信宿-节点编码配置:"+receiveNodeCode);
        LogServiceEvolution.writerServiceImpl.logger(message);
    }

    @Value("${kafka.bootstrap-servers}")
    public void setKafka_bootstrap_servers(String kafka_bootstrap_servers) {
        instance.kafka_bootstrap_servers = kafka_bootstrap_servers;
        message.setReserved("加载kafka集群配置:"+kafka_bootstrap_servers);
        LogServiceEvolution.writerServiceImpl.logger(message);
    }
    @Value("${kafka.consumer.group-id}")
    public void setKafka_consumer_group_id(String kafka_consumer_group_id) {
        instance.kafka_consumer_group_id = kafka_consumer_group_id;
        message.setReserved("加载consumer.group-id:"+kafka_consumer_group_id);
        LogServiceEvolution.writerServiceImpl.logger(message);
    }
    @Value("${kafka.consumer.client-id}")
    public void setKafka_consumer_client_id(String kafka_consumer_client_id) {
        instance.kafka_consumer_client_id = kafka_consumer_client_id;
        message.setReserved("加载kafka_consumer_client_id:"+kafka_consumer_client_id);
        LogServiceEvolution.writerServiceImpl.logger(message);
    }

    @Value("${kafka.consumer.topic}")
    public void setKafka_consumer_topic(String kafka_consumer_topic) {
        instance.kafka_consumer_topic = kafka_consumer_topic;
        message.setReserved("加载kafka_consumer_topic:"+kafka_consumer_topic);
        LogServiceEvolution.writerServiceImpl.logger(message);
    }

    /**
     * @Description:初始化 Properties对象
     * @Author: gaosong
     * @Date: 2020/7/24 16:27
     * @param: null
     * @return: null
     **/
    @PostConstruct
    private void initConfig(){
        //Properties类,用来封装initConfig()方法
        Properties properties = new Properties();
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,instance.kafka_bootstrap_servers);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG,instance.kafka_consumer_group_id);
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG,instance.kafka_consumer_client_id);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,"true");
        properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,"1000");
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG,2000);
        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG,"30000");
        properties.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG,"10000");
        properties.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG,"2097512");
        properties.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG,"1048576");
        properties.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
                "org.apache.kafka.clients.consumer.StickyAssignor");
        instance.props = properties;
        //刷新缓存通道
        LogServiceEvolution.writerServiceImpl.flushChannel();
    }

    public Properties getProps() {
        return props;
    }

    public String getKafka_consumer_topic() {
        return kafka_consumer_topic;
    }
}
