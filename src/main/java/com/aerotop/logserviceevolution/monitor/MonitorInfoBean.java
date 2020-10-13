package com.aerotop.logserviceevolution.monitor;

/**
 * @ClassName: MonitorIdfoBean
 * @Description: 创建一个Bean用来存贮要得到的信息
 * @Author: gaosong
 * @Date 2020/8/14 9:24
 */
public class MonitorInfoBean {
    //内存使用大小
    private float memUseSize;
    //cpu使用率
    private float cpuUsage;
    //内存使用率
    private double memUsage;

    public float getMemUseSize() {
        return memUseSize;
    }

    public void setMemUseSize(float memUseSize) {
        this.memUseSize = memUseSize;
    }

    public float getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(float cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public double getMemUsage() {
        return memUsage;
    }

    public void setMemUsage(double memUsage) {
        this.memUsage = memUsage;
    }
}
