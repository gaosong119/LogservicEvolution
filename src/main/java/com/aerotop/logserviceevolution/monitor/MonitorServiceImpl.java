package com.aerotop.logserviceevolution.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;

/**
 * @ClassName: MonitorServiceImpl
 * @Description: TODO
 * @Author: gaosong
 * @Date 2020/8/14 9:57
 */
public class MonitorServiceImpl implements IMonitorService{
    //获取系统类型
    String osName = System.getProperty("os.name");
    //换算单位
    final BigDecimal DIVISOR = BigDecimal.valueOf(1024);
    //日志生成对象
    private static final Logger log = LoggerFactory.getLogger(MonitorServiceImpl.class);

    @Override
    public MonitorInfoBean getMonitorInfoBean() {
        MonitorInfoBean monitorInfoBean = new MonitorInfoBean();
        if (osName.toLowerCase().startsWith("windows")) {//windows环境
            return monitorInfoBean;//由于部署在Linux环境所以windows暂时不做处理
        }
        else {//Linux环境
            BufferedReader bufferedReader=null;
            try {
                Runtime runtime = Runtime.getRuntime();
                int pid = getPid();
                String[] cmd={"/bin/sh","-c","top -b -n 1 | grep "+pid};
                Process process=runtime.exec(cmd);
                bufferedReader=new BufferedReader(new InputStreamReader(process.getInputStream()));
                String string  =null;
                String[] strArray=null;
                while ((string=bufferedReader.readLine())!=null) {
                    int m = 0;
                    strArray = string.split(" ");
                    for(int i=0;i<strArray.length;i++){
                        String info = strArray[i];
                        if(info.trim().length()==0){
                            continue;
                        }
                        if(m==5){//第六列为进程占用的物理内存值(转换成单位:MB)
                            String unit = info.substring(info.length()-1);
                            log.info("获取当前系统物理内存值单位:"+unit);
                            if(unit.equalsIgnoreCase("g")){
                                monitorInfoBean.setMemUseSize(new BigDecimal(info).multiply(DIVISOR).floatValue());
                            }else if(unit.equalsIgnoreCase("m")){
                                monitorInfoBean.setMemUseSize(Float.parseFloat(info));
                            }else{
                                monitorInfoBean.setMemUseSize(new BigDecimal(info).divide(DIVISOR).floatValue());
                            }
                        }
                        if(m==8){//第九列为CPU的占用百分比
                            monitorInfoBean.setCpuUsage(new BigDecimal(info).divide(BigDecimal.valueOf(100)).floatValue());
                        }
                        if(m==9){//第10列为内存使用的百分比
                            monitorInfoBean.setMemUsage(Double.parseDouble(info));
                        }
                        m++;
                    }
                }
                log.info("收到自检指令执行当前进程下各系统信息获取函数");
            } catch (IOException e) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                e.printStackTrace(new PrintStream(baos));
                log.error(baos.toString());
            }finally {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    e.printStackTrace(new PrintStream(baos));
                    log.error(baos.toString());
                }
            }
        }
        return monitorInfoBean;
    }
     /**
      * @Description:返回当前进程pid
      * @Author: gaosong
      * @Date: 2020/8/14 10:16
      * @param: * @param null:
      * @return: int
      **/
    public int getPid(){
        return Integer.parseInt(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
    }
}
