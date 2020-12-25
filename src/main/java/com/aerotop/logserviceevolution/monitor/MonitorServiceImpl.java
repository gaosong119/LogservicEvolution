package com.aerotop.logserviceevolution.monitor;

import com.aerotop.enums.FrameTypeEnum;
import com.aerotop.enums.LogLevelEnum;
import com.aerotop.logserviceevolution.LogServiceEvolution;
import com.aerotop.logserviceevolution.configload.LoadConfig;
import com.aerotop.message.Message;
import com.aerotop.pack.ByteConvertUtils;
import com.sun.management.OperatingSystemMXBean;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * @ClassName: MonitorServiceImpl
 * @Description: 自检功能实现类
 * @Author: gaosong
 * @Date 2020/8/14 9:57
 */
public class MonitorServiceImpl implements IMonitorService{
    //日志记录对象
    private Message message = new Message(FrameTypeEnum.DATAFRAME,"日志服务", LogLevelEnum.info,
            System.currentTimeMillis(),(byte)10,"","","", LoadConfig.getInstance().
            getKafka_consumer_topic());
    //获取CPU使用率间隔时间
    private static final int CPUTIME = 30;
    //百分比换算数据
    private static final BigDecimal PERCENT = BigDecimal.valueOf(1024);
    //错误信息长度
    private static final int FAULTLENGTH = 10;
    /**
     * @Description: 获得当前的监控对象自检结果
     * @Author: gaosong
     * @Date: 2020/11/24 9:26
     * @return: com.aerotop.logserviceevolution.monitor.MonitorInfoBean
     **/
    @Override
    public  MonitorInfoBean getMonitorInfoBean() {
        //换算单位
        int kb = 1024;
        // 可使用内存
        long totalMemory = Runtime.getRuntime().totalMemory() / kb;
        // 剩余内存
        long freeMemory = Runtime.getRuntime().freeMemory() / kb;
        // 最大可使用内存
        long maxMemory = Runtime.getRuntime().maxMemory() / kb;
        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        // 操作系统
        String osName = System.getProperty("os.name");
        // 总的物理内存
        long totalMemorySize = osmxb.getTotalPhysicalMemorySize() / kb /kb;
        // 剩余的物理内存
        long freePhysicalMemorySize = osmxb.getFreePhysicalMemorySize() / kb /kb;
        // 已使用的物理内存
        float usedMemory = 0;
        // 获得线程总数
        ThreadGroup parentThread;
        for (parentThread = Thread.currentThread().getThreadGroup(); parentThread.getParent() != null; parentThread = parentThread.getParent());
        int totalThread = parentThread.activeCount();
        float cpuRatio = 0;
        if (osName.toLowerCase().startsWith("windows")) {
            //usedMemory = (osmxb.getTotalPhysicalMemorySize() - osmxb.getFreePhysicalMemorySize()) / kb /kb;
            usedMemory = totalMemorySize-freePhysicalMemorySize;
            cpuRatio = this.getCpuRatioForWindows();
        } else {
            //cpuRatio = getCpuRateForLinux();
            BufferedReader bufferedReader=null;
            try {
                Runtime runtime = Runtime.getRuntime();
                int pid = getPid();
                String[] cmd={"/bin/sh","-c","top -b -n 1 | grep "+pid};
                Process process=runtime.exec(cmd);
                bufferedReader=new BufferedReader(new InputStreamReader(process.getInputStream()));
                String string;
                String[] strArray;
                while ((string=bufferedReader.readLine())!=null) {
                    int m = 0;
                    strArray = string.split(" ");
                    for (String info : strArray) {
                        if (info.trim().length() == 0) {
                            continue;
                        }
                        if (m == 5) {//第六列为进程占用的物理内存值(转换成单位:MB)
                            usedMemory = new BigDecimal(info).divide(PERCENT).floatValue();
                            message.setReserved("获取Linux环境下当前进程占用" +"的物理内存值:" + usedMemory+" MB");
                            LogServiceEvolution.writerServiceImpl.logger(message);
                            //将日志内容刷新到文件
                            LogServiceEvolution.writerServiceImpl.flushChannel();
                        }
                        if (m == 8) {//第九列为CPU的占用百分比
                            cpuRatio = new BigDecimal(info).divide(BigDecimal.valueOf(100)).floatValue();
                            message.setReserved("获取Linux环境下当前进程占用CPU信息:" + cpuRatio);
                            LogServiceEvolution.writerServiceImpl.logger(message);
                            //将日志内容刷新到文件
                            LogServiceEvolution.writerServiceImpl.flushChannel();
                        }
                        m++;
                    }
                }
            } catch (Exception e) {
                ByteArrayOutputStream baoS = new ByteArrayOutputStream();
                e.printStackTrace(new PrintStream(baoS));
                message.setLoglevel(LogLevelEnum.error);
                message.setReserved(baoS.toString());
                LogServiceEvolution.writerServiceImpl.logger(message);
                //将日志内容刷新到文件
                LogServiceEvolution.writerServiceImpl.flushChannel();
            }finally {
                try {
                    if(bufferedReader!=null){
                        bufferedReader.close();
                    }
                } catch (IOException e) {
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
        //将日志内容刷新到文件
        LogServiceEvolution.writerServiceImpl.flushChannel();
        // 构造返回对象
        MonitorInfoBean infoBean = new MonitorInfoBean();
        infoBean.setFreeMemory(freeMemory);
        infoBean.setFreePhysicalMemorySize(freePhysicalMemorySize);
        infoBean.setMaxMemory(maxMemory);
        infoBean.setOsName(osName);
        infoBean.setTotalMemory(totalMemory);
        infoBean.setTotalMemorySize(totalMemorySize);
        infoBean.setTotalThread(totalThread);
        infoBean.setUsedMemory(usedMemory);
        infoBean.setCpuRatio(cpuRatio);
        return infoBean;
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

    /**
     * @Description: 获取windows环境下CPU使用率
     * @Author: gaosong
     * @Date: 2020/11/24 9:40
     * @return: float
     **/
    private float getCpuRatioForWindows() {
        try {
            String procCmd = System.getenv("windir") + "//system32//wbem//wmic.exe process get Caption,CommandLine,KernelModeTime,ReadOperationCount,ThreadCount,UserModeTime,WriteOperationCount";
            // 取进程信息
            long[] c0 = readCpu(Runtime.getRuntime().exec(procCmd));
            Thread.sleep(CPUTIME);
            long[] c1 = readCpu(Runtime.getRuntime().exec(procCmd));
            if (c0 != null && c1 != null) {
                //CPU空闲时间
                long idleTime = c1[0] - c0[0];
                //CPU繁忙时间
                long busyTime = c1[1] - c0[1];
                //返回CPU占用率
                //return (float)(Math.round((busyTime) / (busyTime + idleTime)* PERCENT ))/ PERCENT;
                return (((float) busyTime) / ((float) busyTime + (float) idleTime));
            } else {
                return 0.0f;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ByteArrayOutputStream baoS = new ByteArrayOutputStream();
            ex.printStackTrace(new PrintStream(baoS));
            message.setLoglevel(LogLevelEnum.error);
            message.setReserved(baoS.toString());
            LogServiceEvolution.writerServiceImpl.logger(message);
            return 0.0f;
        }
    }
    /**
     * @Description: 获取CPU信息
     * @Author: gaosong
     * @Date: 2020/11/24 9:41
     * @param proc: 进程对象
     * @return: long[]
     **/
    private long[] readCpu(final Process proc) {
        //数字格式校验
        String pattern = "^-?\\d+\\.?\\d*$";
        long[] retn = new long[2];
        try {
            proc.getOutputStream().close();
            InputStreamReader ir = new InputStreamReader(proc.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String line = input.readLine();
            if (line == null || line.length() < FAULTLENGTH) {
                return null;
            }
            int capidx = line.indexOf("Caption");
            int cmdidx = line.indexOf("CommandLine");
            int rocidx = line.indexOf("ReadOperationCount");
            int umtidx = line.indexOf("UserModeTime");
            int kmtidx = line.indexOf("KernelModeTime");
            int wocidx = line.indexOf("WriteOperationCount");
            long idletime = 0;
            long kneltime = 0;
            long usertime = 0;
            while ((line = input.readLine()) != null) {
                if (line.length() < wocidx) {
                    continue;
                }
                // 字段出现顺序：Caption,CommandLine,KernelModeTime,ReadOperationCount,
                // ThreadCount,UserModeTime,WriteOperation
                String caption = ByteConvertUtils.substring(line, capidx, cmdidx - 1).trim();
                String cmd = ByteConvertUtils.substring(line, cmdidx, kmtidx - 1).trim();
                if (cmd.contains("wmic.exe")) {
                    continue;
                }
                String s1 = ByteConvertUtils.substring(line, kmtidx, rocidx - 1).replaceAll("\\s*", "");
                String s2 = ByteConvertUtils.substring(line, umtidx, wocidx - 1).replaceAll("\\s*", "");
                if (caption.equals("System Idle Process") || caption.equals("System")) {
                    if (s1.length() > 0 && Pattern.matches(pattern, s1))
                        idletime += Long.parseLong(s1);
                    if (s2.length() > 0 && Pattern.matches(pattern, s2))
                        idletime += Long.parseLong(s2);
                    continue;
                }
                if (s1.length() > 0 && Pattern.matches(pattern, s1))
                    kneltime += Long.parseLong(s1);
                if (s2.length() > 0 && Pattern.matches(pattern, s2))
                    usertime += Long.parseLong(s2);
            }
            retn[0] = idletime;
            retn[1] = kneltime + usertime;
            return retn;
        } catch (Exception ex) {
            ex.printStackTrace();
            ByteArrayOutputStream baoS = new ByteArrayOutputStream();
            ex.printStackTrace(new PrintStream(baoS));
            message.setLoglevel(LogLevelEnum.error);
            message.setReserved(baoS.toString());
            LogServiceEvolution.writerServiceImpl.logger(message);
        } finally {
            try {
                proc.getInputStream().close();
            } catch (Exception e) {
                e.printStackTrace();
                ByteArrayOutputStream baoS = new ByteArrayOutputStream();
                e.printStackTrace(new PrintStream(baoS));
                message.setLoglevel(LogLevelEnum.error);
                message.setReserved(baoS.toString());
                LogServiceEvolution.writerServiceImpl.logger(message);
                //将日志内容刷新到文件
                LogServiceEvolution.writerServiceImpl.flushChannel();
            }
        }
        return null;
    }
}
