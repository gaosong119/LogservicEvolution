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
    //private static final Logger log = LoggerFactory.getLogger(LoadConfig.class);//日志生成对象

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

    private String transferValve;//是否启用日志转存

    private String diskThreshold;//磁盘占用率阈值

    private String dataServerIp;//数据服务器的ip地址

    private String dataServerUsername;//数据服务器的用户名

    private String dataServerPassword;//数据服务器的密码

    private String dataServerDestDir;//数据服务器的目的文件夹

    private String archiveCacheDir;//压缩包临时存储目录

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

    public String getArchiveCacheDir() {
        return archiveCacheDir;
    }
    @Value("${archiveCacheDir}")
    public void setArchiveCacheDir(String archiveCacheDir) {
        instance.archiveCacheDir = archiveCacheDir;
        //log.info("加载压缩包临时存储目录配置:"+archiveCacheDir);
        WriterSingle.getInstance().loggerInfo((byte)10,"记录日志","加载压缩包临时存储目录配置"+archiveCacheDir,"","");
    }

    public String getDataServerIp() {
        return dataServerIp;
    }

    @Value("${transferValve}")
    public void setTransferValve(String transferValve) {
        instance.transferValve = transferValve;
        //log.info("加载是否启用日志转存配置:"+transferValve);
        WriterSingle.getInstance().loggerInfo((byte)10,"记录日志","加载是否启用日志转存配置:"+transferValve,"","");
    }

    public Double getDiskThreshold() {
        return Double.valueOf(diskThreshold);
    }
    @Value("${diskThreshold}")
    public void setDiskThreshold(String diskThreshold) {
        instance.diskThreshold = diskThreshold;
        //log.info("加载磁盘占用率阈值配置:"+diskThreshold);
        WriterSingle.getInstance().loggerInfo((byte)10,"记录日志","加载磁盘占用率阈值配置:"+diskThreshold,"","");
    }

    @Value("${dataServerIp}")
    public void setDataServerIp(String dataServerIp) {
        instance.dataServerIp = dataServerIp;
        //log.info("加载数据服务器的ip地址配置:"+dataServerIp);
        WriterSingle.getInstance().loggerInfo((byte)10,"记录日志","加载数据服务器的ip地址配置:"+dataServerIp,"","");
    }

    public String getDataServerUsername() {
        return dataServerUsername;
    }
    @Value("${dataServerUsername}")
    public void setDataServerUsername(String dataServerUsername) {
        instance.dataServerUsername = dataServerUsername;
        //log.info("加载数据服务器的用户名配置:"+dataServerUsername);
        WriterSingle.getInstance().loggerInfo((byte)10,"记录日志","加载数据服务器的用户名配置:"+dataServerUsername,"","");
    }

    public String getDataServerPassword() {
        return dataServerPassword;
    }
    @Value("${dataServerPassword}")
    public void setDataServerPassword(String dataServerPassword) {
        instance.dataServerPassword = dataServerPassword;
        //log.info("加载数据服务器的密码配置:"+dataServerPassword);
        WriterSingle.getInstance().loggerInfo((byte)10,"记录日志","加载数据服务器的密码配置:"+dataServerPassword,"","");
    }

    public String getDataServerDestDir() {
        return dataServerDestDir;
    }
    @Value("${dataServerDestDir}")
    public void setDataServerDestDir(String dataServerDestDir) {
        instance.dataServerDestDir = dataServerDestDir;
        //log.info("加载数据服务器的目的文件夹配置:"+dataServerDestDir);
        WriterSingle.getInstance().loggerInfo((byte)10,"记录日志","加载数据服务器的目的文件夹配置:"+dataServerDestDir,"","");
    }

    @Value("${fileRootPath}")
    public void setFileNamePattern(String fileRootPath) {
        instance.fileRootPath = fileRootPath;
        //log.info("加载文件创建根目录配置:"+fileRootPath);
        WriterSingle.getInstance().loggerInfo((byte)10,"记录日志","加载文件创建根目录配置:"+fileRootPath,"","");
    }
    @Value("${maxFileSize}")
    public void setMaxFileSize(int maxFileSize) {
        instance.maxFileSize = maxFileSize;
        //log.info("加载maxFileSize单个日志文件大小配置:"+maxFileSize);
        WriterSingle.getInstance().loggerInfo((byte)10,"记录日志","加载maxFileSize单个日志文件大小配置:"+maxFileSize,"","");
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
        //log.info("加载 application.properties中kafka.bootstrap-servers:"+kafka_bootstrap_servers);
        WriterSingle.getInstance().loggerInfo((byte)10,"记录日志","加载 application.properties中kafka.bootstrap-servers:"+kafka_bootstrap_servers,"","");
    }
    @Value("${kafka.consumer.group-id}")
    public void setKafka_consumer_group_id(String kafka_consumer_group_id) {
        instance.kafka_consumer_group_id = kafka_consumer_group_id;
        //log.info("加载 application.properties中kafka.consumer.group-id:"+kafka_consumer_group_id);
        WriterSingle.getInstance().loggerInfo((byte)10,"记录日志","加载 application.properties中kafka.consumer.group-id:"+kafka_consumer_group_id,"","");
    }
    @Value("${kafka.consumer.client-id}")
    public void setKafka_consumer_client_id(String kafka_consumer_client_id) {
        instance.kafka_consumer_client_id = kafka_consumer_client_id;
        //log.info("加载 application.properties中kafka.consumer.client-id:"+kafka_consumer_client_id);
        WriterSingle.getInstance().loggerInfo((byte)10,"记录日志","加载 application.properties中kafka.consumer.client-id:"+kafka_consumer_client_id,"","");
    }

    @Value("${kafka.consumer.key-deserializer}")
    public void setKafka_consumer_key_deserializer(String kafka_consumer_key_deserializer) {
        instance.kafka_consumer_key_deserializer = kafka_consumer_key_deserializer;
        //log.info("加载 application.properties中kafka.consumer.key-deserializer:"+kafka_consumer_key_deserializer);
        WriterSingle.getInstance().loggerInfo((byte)10,"记录日志","加载 application.properties中kafka.consumer.key-deserializer:"+kafka_consumer_key_deserializer,"","");
    }
    @Value("${kafka.consumer.value-deserializer}")
    public void setKafka_consumer_value_deserializer(String kafka_consumer_value_deserializer) {
        instance.kafka_consumer_value_deserializer = kafka_consumer_value_deserializer;
        //log.info("加载 application.properties中kafka.consumer.value-deserializer:"+kafka_consumer_value_deserializer);
        WriterSingle.getInstance().loggerInfo((byte)10,"记录日志","加载 application.properties中kafka.consumer.value-deserializer:"+kafka_consumer_value_deserializer,"","");
    }
    @Value("${kafka.consumer.enable-auto-commit}")
    public void setKafka_consumer_enable_auto_commit(String kafka_consumer_enable_auto_commit) {
        instance.kafka_consumer_enable_auto_commit = kafka_consumer_enable_auto_commit;
        //log.info("加载 application.properties中kafka.consumer.enable-auto-commit:"+kafka_consumer_enable_auto_commit);
        WriterSingle.getInstance().loggerInfo((byte)10,"记录日志","加载 application.properties中kafka.consumer.enable-auto-commit:"+kafka_consumer_enable_auto_commit,"","");
    }
    @Value("${kafka.consumer.auto-commit-interval.ms}")
    public void setKafka_consumer_auto_commit_interval_ms(int kafka_consumer_auto_commit_interval_ms) {
        instance.kafka_consumer_auto_commit_interval_ms = kafka_consumer_auto_commit_interval_ms;
        //log.info("加载 application.properties中kafka.consumer.auto-commit-interval.ms:"+kafka_consumer_auto_commit_interval_ms);
        WriterSingle.getInstance().loggerInfo((byte)10,"记录日志","加载 application.properties中kafka.consumer.auto-commit-interval.ms:"+kafka_consumer_auto_commit_interval_ms,"","");
    }
    @Value("${kafka.consumer.topic}")
    public void setKafka_consumer_topic(String kafka_consumer_topic) {
        instance.kafka_consumer_topic = kafka_consumer_topic;
        //log.info("加载 application.properties中kafka_consumer_topic:"+kafka_consumer_topic);
        WriterSingle.getInstance().loggerInfo((byte)10,"记录日志","加载 application.properties中kafka_consumer_topic:"+kafka_consumer_topic,"","");
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
        instance.props = properties;
    }

    public Properties getProps() {
        return props;
    }

    public String getKafka_consumer_topic() {
        return kafka_consumer_topic;
    }
}
