package com.aerotop.logserviceevolution.selfinspection;

import com.aerotop.logserviceevolution.configload.LoadConfig;
import com.aerotop.pack.ByteConvertUtils;

/**
 * @ClassName: CommunicationPackage
 * @Description: 消息通信库组包类
 * @Author: gaosong
 * @Date 2020/12/11 8:57
 */
public class CommunicationPackage {
    /**
     * @Description: 根据消息通信协议组包,将自检结果发送到消息通信库
     * @Author: gaosong
     * @Date: 2020/12/11 9:02
     * @param bytes: 自检结果
     * @return: byte[]
     **/
    public static byte[] resultPackage(byte[] bytes){

        //消息通信帧长48+自检数据帧长142=190
        byte[] resultBytes = new byte[190];
        //设置帧同步-1字节
        resultBytes[0] = (byte)0xBE;

        //帧长-2字节
        byte[] frameLengthBytes = ByteConvertUtils.short2byteLittle((short) 190);
        System.arraycopy(frameLengthBytes,0,resultBytes,1,2);

        //帧类型-1字节
        resultBytes[3] = (byte)0;

        //信源(日志服务)-系统类型 -1字节
        byte receiveSystemTypeBytes = Byte.parseByte(LoadConfig.getInstance().getReceiveSystemType());
        resultBytes[4] = receiveSystemTypeBytes;

        //信源(日志服务)-系统编码 -2字节
        byte[] receiveSystemCodeBytes = ByteConvertUtils.short2byteLittle(Short.parseShort(LoadConfig.getInstance().getReceiveSystemCode()));
        System.arraycopy(receiveSystemCodeBytes,0,resultBytes,5,2);

        //信源(日志服务)-节点编码 -1字节
        byte receiveNodeCodeBytes = Byte.parseByte(LoadConfig.getInstance().getReceiveNodeCode());
        resultBytes[7] = receiveNodeCodeBytes;

        //信宿(自检服务)-系统类型 -1字节
        byte sourceSystemTypeBytes = Byte.parseByte(LoadConfig.getInstance().getSourceSystemType());
        resultBytes[8] = sourceSystemTypeBytes;

        //信宿(自检服务)-系统编码 -2字节
        byte[] sourceSystemCodeBytes = ByteConvertUtils.short2byteLittle(Short.parseShort(LoadConfig.getInstance().getSourceSystemCode()));
        System.arraycopy(sourceSystemCodeBytes,0,resultBytes,9,2);

        //信宿(自检服务)-节点编码 -1字节
        byte sourceNodeCodeBytes = Byte.parseByte(LoadConfig.getInstance().getSourceNodeCode());
        resultBytes[11] = sourceNodeCodeBytes;

        //日期-年-2字节
        byte[] yearBytes = ByteConvertUtils.short2byteLittle(Short.parseShort(HandlerUtilForUDP.getSysYear()));
        System.arraycopy(yearBytes,0,resultBytes,12,2);

        //日期-月-1字节
        byte monthBytes = Byte.parseByte(HandlerUtilForUDP.getSysMonth());
        resultBytes[14] = monthBytes;

        //日期-日-1字节
        byte dayBytes = Byte.parseByte(HandlerUtilForUDP.getSysDay());
        resultBytes[15] = dayBytes;

        //日期-时间-4字节
        byte[] timeBytes = ByteConvertUtils.intToBytesLittle(HandlerUtilForUDP.getSysTime());
        System.arraycopy(timeBytes,0,resultBytes,16,4);

        //帧序号/确认帧序号 -4字节
        byte[] frameNum = ByteConvertUtils.intToBytesLittle(1);
        System.arraycopy(frameNum,0,resultBytes,20,4);

        //确认标志-1字节
        byte confirmMark = (byte)0x00;
        resultBytes[24] = confirmMark;

        //重传次数-1字节
        byte retransmissionTimes =(byte) 0x00;
        resultBytes[25] = retransmissionTimes;

        //信息字类型-1字节
        byte informationWord = (byte)1;
        resultBytes[26] = informationWord;

        //信息字格式-2字节
        byte[] informationWordNum = ByteConvertUtils.short2byteLittle((short)256);
        System.arraycopy(informationWordNum,0,resultBytes,27,2);

        //备用字节-4字节
        byte[] reserved = ByteConvertUtils.intToBytesLittle(0);
        System.arraycopy(reserved,0,resultBytes,29,4);

        //--------组织消息通信信息头---------
        //消息发送方软件标识字符串长度
        resultBytes[34] = (byte)1;
        //消息发送方软件标识字符串
        resultBytes[35] = (byte)1;
        //消息接收方软件标识字符串长度
        resultBytes[36] = (byte)1;
        //消息接收方软件标识字符串
        resultBytes[37] = (byte)1;
        //消息通信信息字类型
        resultBytes[38] = (byte)1;
        //消息通信信息字个数
        byte[] informationWordNums = ByteConvertUtils.short2byteLittle((short) 1);
        System.arraycopy(informationWordNums,0,resultBytes,39,2);
        //表号
        byte[] tableNum = ByteConvertUtils.short2byteLittle((short) 4270);
        System.arraycopy(tableNum,0,resultBytes,41,2);
        //优先级
        resultBytes[43] = (byte)1;
        //备用字符串长度
        resultBytes[44] = (byte)1;
        //备用字符串
        resultBytes[45] = (byte)1;

        //--------组织消息通信信息字---------
        //由于采取自定义信息字,字符串数据长度
        byte[] userDefineLength = ByteConvertUtils.short2byteLittle((short) 142);
        System.arraycopy(userDefineLength,0,resultBytes,46,2);
        //将自检结果放入数据字段
        System.arraycopy(bytes,0,resultBytes,48,142);
        //计算校验和
        byte checkSum = HandlerUtilForUDP.checkSum(resultBytes);
        resultBytes[33] = checkSum;
        //返回结果
        return resultBytes;
    }
}
