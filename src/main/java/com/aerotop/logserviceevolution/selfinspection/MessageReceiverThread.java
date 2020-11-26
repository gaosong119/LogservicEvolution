package com.aerotop.logserviceevolution.selfinspection;

import com.aerotop.logserviceevolution.configload.LoadConfig;
import com.aerotop.logserviceevolution.monitor.MonitorServiceImpl;
import com.aerotop.transfer.WriterSingle;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * @ClassName: MessageReceiverThread
 * @Description: 启动udp监听线程类
 * @Author: gaosong
 * @Date 2020/11/24 16:58
 */
public class MessageReceiverThread extends Thread {
    //自检对象
    private MonitorServiceImpl monitorServiceImpl = new MonitorServiceImpl();
    /**
     * @Description: 开启udp端口，接收客户端发送数据并将自检结果返回到发送方
     * @Author: gaosong
     * @Date: 2020/11/24 18:05
     * @return: void
     **/
    @Override
    public void run() {
        try {
            while(true){
                //接收发送方数据
                // 1.创建服务器端DatagramSocket，绑定发送方配置端口
                DatagramSocket socket = new DatagramSocket(Integer.parseInt(LoadConfig.getInstance().getSourcePort()));
                // 2.创建数据报，用于接收客户端发送的数据
                byte[] data = new byte[1024];// 创建字节数组，指定接收的数据包的大小
                DatagramPacket packet = new DatagramPacket(data, data.length);
                // 3.接收客户端发送的数据
                WriterSingle.getInstance().loggerInfo((byte)10,"监听udp端口","服务器端已经启动，等待客户端" +
                        "发送数据","监听端口号:"+Integer.parseInt(LoadConfig.getInstance().getSourcePort()));
                socket.receive(packet);// 此方法在接收到数据报之前会一直阻塞
                WriterSingle.getInstance().loggerInfo((byte)10,"服务端收到数据","即将校验数据合法性" ,
                        "若通过校验则返回自检结果");
                // 4.读取并解析数据
                boolean checkResult =HandlerUtilForUDP.legalVerification(data);
                if(!checkResult){//未通过校验
                    WriterSingle.getInstance().loggerInfo((byte)10,"校验数据是否符合规则","未通过校验",
                            "丢弃此数据:"+ Arrays.toString(data));
                    continue;
                }
                //通过校验则向客户端响应数据
                // 1.定义客户端的地址、端口号、数据
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                //执行组包
                byte[] selfInspection = HandlerUtilForUDP.selfInspectionPack(monitorServiceImpl.getMonitorInfoBean());
                // 2.创建数据报，包含响应的数据信息
                DatagramPacket packetSend = new DatagramPacket(selfInspection, selfInspection.length, address, port);
                // 3.响应客户端
                socket.send(packetSend);
            }
        } catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            WriterSingle.getInstance().loggerError((byte)10,"错误捕捉",baos.toString(),"自检模块UDP通信错误");
        }
    }
}
