package com.aerotop.logserviceevolution.filetransfer;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import com.aerotop.logserviceevolution.configload.LoadConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;

/**
 * @ClassName: ScpUtils
 * @Description: 将本地压缩完毕的包上传到指定位置
 * @Author: gaosong
 * @Date 2020/9/22 15:40
 */
public class ScpUtils {
/*    private static final Logger log = LoggerFactory.getLogger(ScpUtils.class);//日志生成对象

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//日期格式化

    public static void ScpRemoteFromLocation(String localZipPath){
        //文件scp到数据服务器
        Connection conn = new Connection(LoadConfig.getInstance().getDataServerIp());
        try {
            conn.connect();
            boolean isAuthenticated = conn.authenticateWithPassword(LoadConfig.getInstance().getDataServerUsername(), LoadConfig.getInstance().getDataServerPassword());
            if (!isAuthenticated){
                log.error("获取远程连接失败,用户名或密码错误或网络通信异常!");
                return;
            }
            SCPClient client = new SCPClient(conn);
            client.put(localZipPath, LoadConfig.getInstance().getDataServerDestDir()); //本地文件scp到远程目录
            conn.close();
            log.info("系统在:"+ LoadConfig.getInstance().getSimpleDateFormat().format(System.currentTimeMillis())+" 将--->"+localZipPath+"转存至:"+LoadConfig.getInstance().getDataServerIp()+":"+LoadConfig.getInstance().getDataServerDestDir());
        } catch (IOException e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            log.error(baos.toString());
        }
    }*/
}
