package com.aerotop.logserviceevolution;

import com.aerotop.logserviceevolution.monitor.MonitorServiceImpl;
import com.aerotop.logserviceevolution.selfinspection.HandlerUtilForUDP;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.bind.DatatypeConverter;

@SpringBootTest
class LogserviceframeupApplicationTests {

    @Test
    void contextLoads() {
        /*MonitorInfoBean monitorInfoBean = new MonitorServiceImpl().getMonitorInfoBean();
        System.out.println("Cpu占用率:"+monitorInfoBean.getCpuRatio());
        System.out.println("内存占用:"+monitorInfoBean.getUsedMemory()+"MB");
        byte[] bytes = HandlerUtilForUDP.selfInspectionPack(monitorInfoBean);
        System.out.println("字节数组长度:---->"+bytes.length);
        System.out.println("原数组内容:---->"+bytes);
        System.out.println("toString后内容:"+ Arrays.toString(bytes));*/
        MonitorServiceImpl monitorService = new MonitorServiceImpl();
        byte[] bytes = HandlerUtilForUDP.selfInspectionPack(monitorService.getMonitorInfoBean());

        System.out.println("字节数组长度:"+bytes.length);
        System.out.println("十六进制字符串:");
        System.out.println(DatatypeConverter.printHexBinary(bytes));
    }
}
