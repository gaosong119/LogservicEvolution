package com.aerotop.logserviceevolution.monitor;

import com.aerotop.logserviceevolution.selfinspection.HandlerUtilForUDP;
import com.aerotop.pack.ByteConvertUtils;
import com.aerotop.transfer.WriterSingle;
import com.sun.management.OperatingSystemMXBean;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * @ClassName: MonitorServiceImpl
 * @Description: TODO
 * @Author: gaosong
 * @Date 2020/8/14 9:57
 */
public class MonitorServiceImpl implements IMonitorService{
    //获取CPU使用率间隔时间
    private static final int CPUTIME = 30;
    //百分比换算数据
    private static final BigDecimal PERCENT = BigDecimal.valueOf(1024);
    //错误信息长度
    private static final int FAULTLENGTH = 10;
    //linux系统版本
    private static String linuxVersion = null;
    /**
     * @Description: 获得当前的监控对象
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
        long totalMemorySize = osmxb.getTotalPhysicalMemorySize() / kb;
        // 剩余的物理内存
        long freePhysicalMemorySize = osmxb.getFreePhysicalMemorySize() / kb;
        // 已使用的物理内存
        float usedMemory = 0;
        // 获得线程总数
        ThreadGroup parentThread;
        for (parentThread = Thread.currentThread().getThreadGroup(); parentThread.getParent() != null; parentThread = parentThread.getParent());
        int totalThread = parentThread.activeCount();
        float cpuRatio = 0;
        if (osName.toLowerCase().startsWith("windows")) {
            usedMemory = (osmxb.getTotalPhysicalMemorySize() - osmxb.getFreePhysicalMemorySize()) / kb /kb;
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
                            WriterSingle.getInstance().loggerInfo((byte) 10, "自检结果", "获取当前进程占用" +
                                    "的物理内存值:" + usedMemory, "单位:MB");
                        }
                        if (m == 8) {//第九列为CPU的占用百分比
                            cpuRatio = new BigDecimal(info).divide(BigDecimal.valueOf(100)).floatValue();
                            WriterSingle.getInstance().loggerInfo((byte) 10, "自检结果", "获取当前进程占用" +
                                    "CPU信息:" + cpuRatio, "将百分比形式/100所得值");
                        }
                        m++;
                    }
                }
            } catch (Exception e) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                e.printStackTrace(new PrintStream(baos));
                WriterSingle.getInstance().loggerError((byte)10,"获取当前进程占用信息错误",baos.toString()
                        ,"异常信息");
            }finally {
                try {
                    if(bufferedReader!=null){
                        bufferedReader.close();
                    }
                } catch (IOException e) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    e.printStackTrace(new PrintStream(baos));
                    WriterSingle.getInstance().loggerError((byte)10,"IO关闭异常",baos.toString()
                            ,"异常信息");
                }
            }
        }
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
     * @Description: 获取Linux环境下CPU使用率
     * @Author: gaosong
     * @Date: 2020/11/24 9:30
     * @return: double
     **/
    private static float getCpuRateForLinux() {
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader brStat = null;
        StringTokenizer tokenStat;
        try {
            //System.out.println("Get usage rate of CPU , linux version: " + linuxVersion);
            Process process = Runtime.getRuntime().exec("top -b -n 1");
            is = process.getInputStream();
            isr = new InputStreamReader(is);
            brStat = new BufferedReader(isr);
            if (linuxVersion.equals("2.4")) {
                brStat.readLine();
                brStat.readLine();
                brStat.readLine();
                brStat.readLine();
                tokenStat = new StringTokenizer(brStat.readLine());
                tokenStat.nextToken();
                tokenStat.nextToken();
                String user = tokenStat.nextToken();
                tokenStat.nextToken();
                String system = tokenStat.nextToken();
                tokenStat.nextToken();
                String nice = tokenStat.nextToken();
                //System.out.println(user + " , " + system + " , " + nice);
                user = user.substring(0, user.indexOf("%"));
                system = system.substring(0, system.indexOf("%"));
                nice = nice.substring(0, nice.indexOf("%"));
                float userUsage = Float.parseFloat(user);
                float systemUsage = Float.parseFloat(system);
                float niceUsage = Float.parseFloat(nice);
                //返回float类型结果
                return (userUsage + systemUsage + niceUsage);
                //return (userUsage + systemUsage + niceUsage) / 100;
            } else {
                brStat.readLine();
                brStat.readLine();
                tokenStat = new StringTokenizer(brStat.readLine());
                tokenStat.nextToken();
                tokenStat.nextToken();
                tokenStat.nextToken();
                tokenStat.nextToken();
                tokenStat.nextToken();
                tokenStat.nextToken();
                tokenStat.nextToken();
                String cpuUsage = tokenStat.nextToken();
                return new Float(cpuUsage.substring(0, cpuUsage.indexOf("%")));
            }
        } catch (IOException ioe) {
            //System.out.println(ioe.getMessage());
            freeResource(is, isr, brStat);
            return 1;
        } finally {
            freeResource(is, isr, brStat);
        }
    }
    /**
     * @Description: 释放资源
     * @Author: gaosong
     * @Date: 2020/11/24 9:40
     * @param is: InputStream对象
     * @param isr: InputStreamReader对象
     * @param br: BufferedReader 对象
     * @return: void
     **/
    private static void freeResource(InputStream is, InputStreamReader isr,
                                     BufferedReader br) {
        try {
            if (is != null)
                is.close();
            if (isr != null)
                isr.close();
            if (br != null)
                br.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    /**
     * @Description: 获取windows环境下CPU使用率
     * @Author: gaosong
     * @Date: 2020/11/24 9:40
     * @return: double
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
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ex.printStackTrace(new PrintStream(baos));
            WriterSingle.getInstance().loggerError((byte)10,"错误捕捉",baos.toString(),"获取windows环境下CPU使用率报错");
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
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ex.printStackTrace(new PrintStream(baos));
            WriterSingle.getInstance().loggerError((byte)10,"错误捕捉",baos.toString(),"执行readCpu方法读取CPU信息错误");
        } finally {
            try {
                proc.getInputStream().close();
            } catch (Exception e) {
                e.printStackTrace();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                e.printStackTrace(new PrintStream(baos));
                WriterSingle.getInstance().loggerError((byte)10,"错误捕捉",baos.toString(),"关闭Process对象异常");
            }
        }
        return null;
    }
}
