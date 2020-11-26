package com.aerotop.logserviceevolution.configload;

import com.aerotop.transfer.WriterSingle;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Properties;

/**
 * @ClassName: LoadConfig
 * @Description: 加载application.properties配置文件所有内容到单例对象中
 * @Author: gaosong
 * @Date 2020/9/22 17:08
 */
@Component
public class LoadConfig {

    private static LoadConfig instance = new LoadConfig();//获取实例对象

    private SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");//日期时间格式化对象

    private SimpleDateFormat todayDateFormat =new SimpleDateFormat("yyyy-MM-dd");//日期格式化对象

    private SimpleDateFormat todayTimeFormat =new SimpleDateFormat("yyyyMMdd_HHmmss");//时间格式化对象

    private String fileRootPath;//文件生成根目录

    private int maxFileSize;//单个日志文件大小(单位:MB)

    private String kafka_bootstrap_servers;//consumer连接kafka集群所需的broker地址清单

    private String kafka_consumer_group_id;//消费者组

    private String kafka_consumer_client_id;//消费者client-id

    private String kafka_consumer_key_deserializer;//consumer-key反序列化器

    private String kafka_consumer_value_deserializer;//consumer-value反序列化器

    private String kafka_consumer_enable_auto_commit;//offset是否自动提交

    private int kafka_consumer_auto_commit_interval_ms;//offset自动提交间隔，当enable-auto-commit=true时才生效

    private String kafka_consumer_topic;//日志主题

    private String sourceIP;//发送方IP

    private String sourcePort;//发送方端口

    private String receiveIP;//接收方IP

    private String receivePort;//接收方端口

    private String sourceSystemType;//信源-系统类型

    private String sourceSystemCode;//信源-系统编码

    private String sourceNodeCode;//信源-节点编码

    private String receiveSystemType;//信宿-系统类型

    private String receiveSystemCode;//信宿-系统编码

    private String receiveNodeCode;//信宿-节点编码

    private Properties props;//用来初始化props

    private LoadConfig() {}
    /**
     * @Description 获取FileContentRuleSingle实例对象
     * @Return instance
     * @Author gaosong
     * @Date 2020/7/16 14:09
     */
    public static LoadConfig getInstance(){
        return instance;
    }

    public String getSourceIP() {
        return sourceIP;
    }
    @Value("${sourceIP}")
    public void setSourceIP(String sourceIP) {
        instance.sourceIP = sourceIP;
        WriterSingle.getInstance().loggerInfo((byte)10,"记录日志","加载发送方IP配置:"+sourceIP,"");
    }

    public String getSourcePort() {
        return sourcePort;
    }
    @Value("${sourcePort}")
    public void setSourcePort(String sourcePort) {
        instance.sourcePort = sourcePort;
        WriterSingle.getInstance().loggerInfo((byte)10,"记录日志","加载发送方端口配置:"+sourcePort,"");
    }

    public String getReceiveIP() {
        return receiveIP;
    }
    @Value("${receiveIP}")
    public void setReceiveIP(String receiveIP) {
        instance.receiveIP = receiveIP;
        WriterSingle.getInstance().loggerInfo((byte)10,"记录日志","加载接收方IP配置:"+receiveIP,"");
    }

    public String getReceivePort() {
        return receivePort;
    }
    @Value("${receivePort}")
    public void setReceivePort(String receivePort) {
        instance.receivePort = receivePort;
        WriterSingle.getInstance().loggerInfo((byte)10,"记录日志","加载接收方端口配置:"+receivePort,"");
    }

    public String getSourceSystemType() {
        return sourceSystemType;
    }
    @Value("${source_system_type}")
    public void setSourceSystemType(String sourceSystemType) {
        instance.sourceSystemType = sourceSystemType;
        WriterSingle.getInstance().loggerInfo((byte)10,"记录日志","加载信源-系统类型配置:"+sourceSystemType,"");
    }

    public String getSourceSystemCode() {
        return sourceSystemCode;
    }
    @Value("${source_system_code}")
    public void setSourceSystemCode(String sourceSystemCode) {
        instance.sourceSystemCode = sourceSystemCode;
        WriterSingle.getInstance().loggerInfo((byte)10,"记录日志","加载信源-系统编码配置:"+sourceSystemCode,"");
    }

    public String getSourceNodeCode() {
        return sourceNodeCode;
    }
    @Value("${source_node_code}")
    public void setSourceNodeCode(String sourceNodeCode) {
        instance.sourceNodeCode = sourceNodeCode;
        WriterSingle.getInstance().loggerInfo((byte)10,"记录日志","加载信源-节点编码配置:"+sourceNodeCode,"");
    }

    public String getReceiveSystemType() {
        return receiveSystemType;
    }
    @Value("${receive_system_type}")
    public void setReceiveSystemType(String receiveSystemType) {
        instance.receiveSystemType = receiveSystemType;
        WriterSingle.getInstance().loggerInfo((byte)10,"记录日志","加载信宿-系统类型配置:"+sourceSystemType,"");
    }

    public String getReceiveSystemCode() {
        return receiveSystemCode;
    }
    @Value("${receive_system_code}")
    public void setReceiveSystemCode(String receiveSystemCode) {
        instance.receiveSystemCode = receiveSystemCode;
        WriterSingle.getInstance().loggerInfo((byte)10,"记录日志","加载信宿-系统编码配置:"+receiveSystemCode,"");
    }

    public String getReceiveNodeCode() {
        return receiveNodeCode;
    }
    @Value("${receive_node_code}")
    public void setReceiveNodeCode(String receiveNodeCode) {
        instance.receiveNodeCode = receiveNodeCode;
        WriterSingle.getInstance().loggerInfo((byte)10,"记录日志","加载信宿-节点编码配置:"+receiveNodeCode,"");
    }

    @Value("${fileRootPath}")
    public void setFileNamePattern(String fileRootPath) {
        instance.fileRootPath = fileRootPath;
        WriterSingle.getInstance().loggerInfo((byte)10,"记录日志","加载文件创建根目录配置:"+fileRootPath,"");
    }
    @Value("${maxFileSize}")
    public void setMaxFileSize(int maxFileSize) {
        instance.maxFileSize = maxFileSize;
        WriterSingle.getInstance().loggerInfo((byte)10,"记录日志","加载maxFileSize单个日志文件大小配置:"+maxFileSize,"");
    }
    public int getMaxFileSize() {
        return maxFileSize*1024*1024;
    }

    public SimpleDateFormat getSimpleDateFormat() {
        return simpleDateFormat;
    }

    public String getFileRootPath() {
        return fileRootPath;
    }

    public SimpleDateFormat getTodayDateFormat() {
        return todayDateFormat;
    }

    public SimpleDateFormat getTodayTimeFormat() {
        return todayTimeFormat;
    }
    @Value("${kafka.bootstrap-servers}")
    public void setKafka_bootstrap_servers(String kafka_bootstrap_servers) {
        instance.kafka_bootstrap_servers = kafka_bootstrap_servers;
        WriterSingle.getInstance().loggerInfo((byte)10,"记录日志","加载 application.properties中kafka.bootstrap-servers:"+kafka_bootstrap_servers,"");
    }
    @Value("${kafka.consumer.group-id}")
    public void setKafka_consumer_group_id(String kafka_consumer_group_id) {
        instance.kafka_consumer_group_id = kafka_consumer_group_id;
        WriterSingle.getInstance().loggerInfo((byte)10,"记录日志","加载 application.properties中kafka.consumer.group-id:"+kafka_consumer_group_id,"");
    }
    @Value("${kafka.consumer.client-id}")
    public void setKafka_consumer_client_id(String kafka_consumer_client_id) {
        instance.kafka_consumer_client_id = kafka_consumer_client_id;
        WriterSingle.getInstance().loggerInfo((byte)10,"记录日志","加载 application.properties中kafka.consumer.client-id:"+kafka_consumer_client_id,"");
    }

    @Value("${kafka.consumer.key-deserializer}")
    public void setKafka_consumer_key_deserializer(String kafka_consumer_key_deserializer) {
        instance.kafka_consumer_key_deserializer = kafka_consumer_key_deserializer;
        WriterSingle.getInstance().loggerInfo((byte)10,"记录日志","加载 application.properties中kafka.consumer.key-deserializer:"+kafka_consumer_key_deserializer,"");
    }
    @Value("${kafka.consumer.value-deserializer}")
    public void setKafka_consumer_value_deserializer(String kafka_consumer_value_deserializer) {
        instance.kafka_consumer_value_deserializer = kafka_consumer_value_deserializer;
        WriterSingle.getInstance().loggerInfo((byte)10,"记录日志","加载 application.properties中kafka.consumer.value-deserializer:"+kafka_consumer_value_deserializer,"");
    }
    @Value("${kafka.consumer.enable-auto-commit}")
    public void setKafka_consumer_enable_auto_commit(String kafka_consumer_enable_auto_commit) {
        instance.kafka_consumer_enable_auto_commit = kafka_consumer_enable_auto_commit;
        WriterSingle.getInstance().loggerInfo((byte)10,"记录日志","加载 application.properties中kafka.consumer.enable-auto-commit:"+kafka_consumer_enable_auto_commit,"");
    }
    @Value("${kafka.consumer.auto-commit-interval.ms}")
    public void setKafka_consumer_auto_commit_interval_ms(int kafka_consumer_auto_commit_interval_ms) {
        instance.kafka_consumer_auto_commit_interval_ms = kafka_consumer_auto_commit_interval_ms;
        WriterSingle.getInstance().loggerInfo((byte)10,"记录日志","加载 application.properties中kafka.consumer.auto-commit-interval.ms:"+kafka_consumer_auto_commit_interval_ms,"");
    }
    @Value("${kafka.consumer.topic}")
    public void setKafka_consumer_topic(String kafka_consumer_topic) {
        instance.kafka_consumer_topic = kafka_consumer_topic;
        WriterSingle.getInstance().loggerInfo((byte)10,"记录日志","加载 application.properties中kafka_consumer_topic:"+kafka_consumer_topic,"");
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
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,instance.kafka_consumer_key_deserializer);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,instance.kafka_consumer_value_deserializer);
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,instance.kafka_bootstrap_servers);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG,instance.kafka_consumer_group_id);
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG,instance.kafka_consumer_client_id);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,instance.kafka_consumer_enable_auto_commit);
        properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,instance.kafka_consumer_auto_commit_interval_ms);
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG,2000);
        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG,"30000");
        properties.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG,"10000");
        properties.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG,"2097512");
        properties.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG,"1048576");
        properties.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,"org.apache.kafka.clients.consumer.StickyAssignor");
        instance.props = properties;
    }

    public Properties getProps() {
        return props;
    }

    public String getKafka_consumer_topic() {
        return kafka_consumer_topic;
    }
}
