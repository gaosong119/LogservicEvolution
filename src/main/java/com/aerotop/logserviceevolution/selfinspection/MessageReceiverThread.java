package com.aerotop.logserviceevolution.selfinspection;

import com.aerotop.enums.FrameTypeEnum;
import com.aerotop.enums.LogLevelEnum;
import com.aerotop.logserviceevolution.LogServiceEvolution;
import com.aerotop.logserviceevolution.configload.LoadConfig;
import com.aerotop.logserviceevolution.monitor.MonitorServiceImpl;
import com.aerotop.message.Message;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * @ClassName: MessageReceiverThread
 * @Description: 启动udp监听线程类
 * @Author: gaosong
 * @Date 2020/11/24 16:58
 */
public class MessageReceiverThread extends Thread {
    //日志记录对象
    private Message message = new Message(FrameTypeEnum.DATAFRAME,"日志服务", LogLevelEnum.info,
            System.currentTimeMillis(),(byte)10,"","","",LoadConfig.getInstance().
            getKafka_consumer_topic());
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
            // 1.创建服务器端DatagramSocket，绑定接收方配置端口
            DatagramSocket socket = new DatagramSocket(Integer.parseInt(LoadConfig.getInstance().getReceivePort()));
            // 2.创建数据报，用于接收客户端发送的数据
            byte[] data = new byte[1024];// 创建字节数组，指定接收的数据包的大小
            DatagramPacket packet = new DatagramPacket(data, data.length);
            // 3.接收客户端发送的数据
            message.setReserved("服务器端已经启动,监听本机端口号:"+Integer.parseInt(LoadConfig.getInstance().getReceivePort()));
            LogServiceEvolution.writerServiceImpl.logger(message);
            //将日志内容刷新到文件
            LogServiceEvolution.writerServiceImpl.flushChannel();
            while(true){
                socket.receive(packet);// 此方法在接收到数据报之前会一直阻塞
                message.setReserved("服务端收到数据");
                LogServiceEvolution.writerServiceImpl.logger(message);
                // 4.读取并解析数据
                //boolean checkResult =HandlerUtilForUDP.legalVerification(data);
                /*if(!checkResult){//未通过校验
                    message.setLoglevel(LogLevelEnum.error);
                    message.setReserved("未通过校验丢弃此数据:"+ DatatypeConverter.printHexBinary(data));
                    LogServiceEvolution.writerServiceImpl.logger(message);
                    //将日志内容刷新到文件
                    LogServiceEvolution.writerServiceImpl.flushChannel();
                    continue;
                }*/
                //通过校验则向客户端响应数据
                // 1.定义客户端的地址、端口号、数据
                InetAddress address = packet.getAddress();
                //int port = packet.getPort();
                int port = Integer.parseInt(LoadConfig.getInstance().getFeedbackPort());
                //执行组包
                byte[] selfInspection = CommunicationPackage.resultPackage(
                        HandlerUtilForUDP.selfInspectionPack(monitorServiceImpl.getMonitorInfoBean())
                );
                // 2.创建数据报，包含响应的数据信息
                DatagramPacket packetSend = new DatagramPacket(selfInspection, selfInspection.length, address, port);
                // 3.响应客户端
                socket.send(packetSend);
                message.setLoglevel(LogLevelEnum.info);
                message.setReserved("向"+address.toString()+":"+port+"发送自检数据:"+ DatatypeConverter.printHexBinary(selfInspection));
                LogServiceEvolution.writerServiceImpl.logger(message);
                //将日志内容刷新到文件
                LogServiceEvolution.writerServiceImpl.flushChannel();
            }
        } catch (Exception e) {
            ByteArrayOutputStream baoS = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baoS));
            message.setLoglevel(LogLevelEnum.error);
            message.setReserved(baoS.toString());
            LogServiceEvolution.writerServiceImpl.logger(message);
        }
    }
}
