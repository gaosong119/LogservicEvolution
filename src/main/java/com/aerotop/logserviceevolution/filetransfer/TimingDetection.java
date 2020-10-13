package com.aerotop.logserviceevolution.filetransfer;

import com.aerotop.logserviceevolution.configload.LoadConfig;
import com.aerotop.logserviceevolution.kafka.KafkaConsumerThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * @ClassName: TimingDetection
 * @Description: 定时任务类
 * @Author: gaosong
 * @Date 2020/9/24 15:40
 */
/*@EnableScheduling
@ConditionalOnProperty(name ="transferValve",havingValue = "true",matchIfMissing = false)
@Configuration*/
public class TimingDetection {
    private static final Logger logger = LoggerFactory.getLogger(TimingDetection.class);//日志生成对象
    @Scheduled(fixedDelayString = "${cronFormula}")
    public void diskUsageStatistics(){
        try {
            double diskUsage = DiskUseageInfo.getDiskUsage();
            if(diskUsage>LoadConfig.getInstance().getDiskThreshold()){//磁盘占用率超出配置阈值
                //判断根目录是否存在并且根目录下是否存在文件未被占用的待压缩文件
                if(new File(LoadConfig.getInstance().getFileRootPath()).exists() && ZipUtils.compressTarget(LoadConfig.getInstance().getFileRootPath())){
                    logger.info("磁盘占用率超出配置阈值,系统在:"+LoadConfig.getInstance().getSimpleDateFormat().format(System.currentTimeMillis())+"----开始执行转存----");
                    File file = new File(LoadConfig.getInstance().getArchiveCacheDir());
                    if(!file.exists()){
                        file.mkdirs();
                    }
                    //执行压缩打包
                    File zipFile = new File(LoadConfig.getInstance().getArchiveCacheDir() + "/" + System.currentTimeMillis() + ".zip");
                    FileOutputStream fos =new FileOutputStream(zipFile);
                    ZipUtils.toZip(LoadConfig.getInstance().getFileRootPath(), fos,true);
                    logger.info("系统在:"+ LoadConfig.getInstance().getSimpleDateFormat().format(System.currentTimeMillis())+" 执行压缩完毕!生成压缩包:"+zipFile.getAbsolutePath());
                    //通过scp发送到远程
                    ScpUtils.ScpRemoteFromLocation(zipFile.getAbsolutePath());
                    //删除压缩包
                    zipFile.delete();
                    logger.info("系统在:"+ LoadConfig.getInstance().getSimpleDateFormat().format(System.currentTimeMillis())+" 删除压缩包:"+zipFile.getAbsolutePath()+",转存执行结束!");
                }
            }
        } catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
    }

/*    @PostConstruct
    public void startKafkaConsumer(){
        //启动consumer
        new KafkaConsumerThread(LoadConfig.getInstance().getProps(), LoadConfig.getInstance().getKafka_consumer_topic()).start();
    }*/
}
