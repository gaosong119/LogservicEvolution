package com.aerotop.logserviceevolution.selfinspection;

import com.aerotop.logserviceevolution.configload.LoadConfig;
import com.aerotop.logserviceevolution.monitor.MonitorInfoBean;
import com.aerotop.pack.ByteConvertUtils;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;

/**
 * @ClassName: HandlerUtilForUDP
 * @Description: udp通信处理工具类
 * @Author: gaosong
 * @Date 2020/11/25 9:44
 */
public class HandlerUtilForUDP {
    /**
     * @Description: 数据解析并判断是否为自检指令
     * @Author: gaosong
     * @Date: 2020/11/25 9:45
     * @param data: 端口接收数据
     * @return: boolean
     **/
/*    public static boolean legalVerification(byte[] data) {
        boolean result = false;
        //检查帧同步标识
        Boolean frameMarkResult = CheckMessageHeader.checkFrameMark(data);
        //检查信源是否与配置文件相同
        Boolean sourceVerificationResult = sourceVerification(data);
        if(frameMarkResult && sourceVerificationResult){
            result = true;
        }
        return result;
    }*/
    /**
     * @Description: 检查信源 系统类型、系统编码、节点编码是否与配置文件相同
     * @Author: gaosong
     * @Date: 2020/11/25 10:06
     * @param dataS: 数据包
     * @return: java.lang.Boolean
     **/
/*    private static Boolean sourceVerification(byte[] dataS) {
        boolean flag = false;
        //获取系统类型-第5个字节
        String sourceSystemType = ByteConvertUtils.bytesToString(dataS,4,5);
        //获取系统编码-第6、7两个字节
        String sourceSystemCode = ByteConvertUtils.bytesToString(dataS,5,7);
        //获取节点编码-第8个字节
        String sourceNodeCode = ByteConvertUtils.bytesToString(dataS,7,8);
        //获取配置文件 信源-系统类型
        String sourceSystemTypeConfig = LoadConfig.getInstance().getSourceSystemType();
        //获取配置文件 信源-系统编码
        String sourceSystemCodeConfig = LoadConfig.getInstance().getSourceSystemCode();
        //获取配置文件 信源-节点编码
        String sourceNodeCodeConfig = LoadConfig.getInstance().getSourceNodeCode();
        if(sourceSystemType.equalsIgnoreCase(sourceSystemTypeConfig) &&
                sourceSystemCode.equalsIgnoreCase(sourceSystemCodeConfig) &&
                sourceNodeCode.equalsIgnoreCase(sourceNodeCodeConfig)){
            flag = true;
        }
        return flag;
    }*/
    /**
     * @Description: 消息头组包方法
     * @Author: gaosong
     * @Date: 2020/12/11 10:10
     * @param messageBytes: 组包对象
     * @return: void
     **/
/*    public static void messageHeaderPackage(byte[] messageBytes){
        //设置帧同步-1字节
        messageBytes[0] = (byte)0xBE;

        //帧长-2字节
        byte[] frameLengthBytes = ByteConvertUtils.short2byteLittle((short) 142);
        System.arraycopy(frameLengthBytes,0,messageBytes,1,2);

        //帧类型-1字节
        messageBytes[3] = (byte)0;

        //信源(日志服务)-系统类型 -1字节
        byte receiveSystemTypeBytes = Byte.parseByte(LoadConfig.getInstance().getReceiveSystemType());
        messageBytes[4] = receiveSystemTypeBytes;

        //信源(日志服务)-系统编码 -2字节
        byte[] receiveSystemCodeBytes = ByteConvertUtils.short2byteLittle(Short.parseShort(LoadConfig.getInstance().getReceiveSystemCode()));
        System.arraycopy(receiveSystemCodeBytes,0,messageBytes,5,2);

        //信源(日志服务)-节点编码 -1字节
        byte receiveNodeCodeBytes = Byte.parseByte(LoadConfig.getInstance().getReceiveNodeCode());
        messageBytes[7] = receiveNodeCodeBytes;

        //信宿(自检服务)-系统类型 -1字节
        byte sourceSystemTypeBytes = Byte.parseByte(LoadConfig.getInstance().getSourceSystemType());
        messageBytes[8] = sourceSystemTypeBytes;

        //信宿(自检服务)-系统编码 -2字节
        byte[] sourceSystemCodeBytes = ByteConvertUtils.short2byteLittle(Short.parseShort(LoadConfig.getInstance().getSourceSystemCode()));
        System.arraycopy(sourceSystemCodeBytes,0,messageBytes,9,2);

        //信宿(自检服务)-节点编码 -1字节
        byte sourceNodeCodeBytes = Byte.parseByte(LoadConfig.getInstance().getSourceNodeCode());
        messageBytes[11] = sourceNodeCodeBytes;

        //日期-年-2字节
        byte[] yearBytes = ByteConvertUtils.short2byteLittle(Short.parseShort(getSysYear()));
        System.arraycopy(yearBytes,0,messageBytes,12,2);

        //日期-月-1字节
        byte monthBytes = Byte.parseByte(getSysMonth());
        messageBytes[14] = monthBytes;

        //日期-日-1字节
        byte dayBytes = Byte.parseByte(getSysDay());
        messageBytes[15] = dayBytes;

        //日期-时间-4字节
        byte[] timeBytes = ByteConvertUtils.intToBytesLittle(getSysTime());
        System.arraycopy(timeBytes,0,messageBytes,16,4);

        //帧序号/确认帧序号 -4字节
        byte[] frameNum = ByteConvertUtils.intToBytesLittle(1);
        System.arraycopy(frameNum,0,messageBytes,20,4);

        //确认标志-1字节
        byte confirmMark = (byte)0x00;
        messageBytes[24] = confirmMark;

        //重传次数-1字节
        byte retransmissionTimes =(byte) 0x00;
        messageBytes[25] = retransmissionTimes;

        //信息字类型-1字节
        byte informationWord = (byte)0;
        messageBytes[26] = informationWord;

        //信息字个数-2字节-固定位9个信息字
        byte[] informationWordNum = ByteConvertUtils.short2byteLittle((short)9);
        System.arraycopy(informationWordNum,0,messageBytes,27,2);

        //备用字节-4字节
        byte[] reserved = ByteConvertUtils.intToBytesLittle(0);
        System.arraycopy(reserved,0,messageBytes,29,4);
    }*/
    /**
     * @Description: 执行自检程序并将自检结果按照协议组包
     * @Author: gaosong
     * @Date: 2020/11/25 18:49
     * @param monitorInfoBean: 自检结果对象
     * @return: byte[]
     **/
    public static byte[] selfInspectionPack(MonitorInfoBean monitorInfoBean) {
        //消息通信自字节数组 下标:45+14*9=171,长度为172
        byte[] resultBytes = new byte[172];
        //设置帧同步-1字节
        resultBytes[0] = (byte)0xBE;

        //帧长-2字节
        byte[] frameLengthBytes = ByteConvertUtils.short2byteLittle((short) 172);
        System.arraycopy(frameLengthBytes,0,resultBytes,1,2);

        //帧类型-1字节
        resultBytes[3] = (byte)0;

        //信源(日志服务)-系统类型 -1字节
        byte receiveSystemTypeBytes = Byte.parseByte(LoadConfig.getInstance().getReceiveSystemType());
        resultBytes[4] = receiveSystemTypeBytes;

        //信源(日志服务)-系统编码 -2字节
        byte[] receiveSystemCodeBytes = ByteConvertUtils.short2byteLittle(Short.parseShort(
                LoadConfig.getInstance().getReceiveSystemCode()));
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
        byte[] yearBytes = ByteConvertUtils.short2byteLittle(Short.parseShort(getSysYear()));
        System.arraycopy(yearBytes,0,resultBytes,12,2);

        //日期-月-1字节
        byte monthBytes = Byte.parseByte(getSysMonth());
        resultBytes[14] = monthBytes;

        //日期-日-1字节
        byte dayBytes = Byte.parseByte(getSysDay());
        resultBytes[15] = dayBytes;

        //日期-时间-4字节
        byte[] timeBytes = ByteConvertUtils.intToBytesLittle(getSysTime());
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
        byte informationWord = (byte)3;
        resultBytes[26] = informationWord;

        //信息字格式-2字节
        byte[] informationWordNum = ByteConvertUtils.short2byteLittle((short)256);
        System.arraycopy(informationWordNum,0,resultBytes,27,2);

        //备用字节-4字节
        byte[] reserved = ByteConvertUtils.intToBytesLittle(0);
        System.arraycopy(reserved,0,resultBytes,29,4);

        //--------组织消息通信信息头---------
        //消息发送方软件标识字符串长度
        resultBytes[34] = (byte)0;
        //消息发送方软件标识字符串
        //resultBytes[35] = (byte)0;
        //消息接收方软件标识字符串长度
        resultBytes[35] = (byte)0;
        //消息接收方软件标识字符串
        //resultBytes[37] = (byte)0;
        //消息通信信息字类型
        resultBytes[36] = (byte)0;
        //消息通信信息字个数
        byte[] informationWordNums = ByteConvertUtils.short2byteLittle((short) 9);
        System.arraycopy(informationWordNums,0,resultBytes,37,2);
        //表号
        byte[] tableNum = ByteConvertUtils.short2byteLittle((short) 4270);
        System.arraycopy(tableNum,0,resultBytes,39,2);
        //优先级
        resultBytes[41] = (byte)1;
        //备用字符串长度
        resultBytes[42] = (byte)1;
        //备用字符串
        resultBytes[43] = (byte)1;

        //--------组织消息通信信息字---------
        //第1-3个信息字,前三个信息字固定为14字节
        for(int i=1;i<=9;i++){
            //第i个信息字-格式-1字节
            resultBytes[44+14*(i-1)] = (byte)0;
            //第i个信息字-编码-2字节
            byte[] code_one = ByteConvertUtils.short2byteLittle((short) i);
            System.arraycopy(code_one,0,resultBytes,45+14*(i-1),2);
            //第i个信息字-数据类型-1字节
            if(i==1||i==2||i==3||i==7){
                resultBytes[47+14*(i-1)] = (byte)1;//int类型
            }else if(i==4||i==5||i==6){
                resultBytes[47+14*(i-1)] = (byte)4;//String类型
            }else {
                resultBytes[47+14*(i-1)] = (byte)2;//float类型
            }
            //第i个信息字-超差标记-1字节
            resultBytes[48+14*(i-1)] = (byte)1;
            //第i个信息字-弹编号-2字节
            byte[] missileCode_one = ByteConvertUtils.short2byteLittle((short) 1);
            System.arraycopy(missileCode_one,0,resultBytes,49+14*(i-1),2);
            //第i个信息字-字符串数据长度-1字节
            resultBytes[51+14*(i-1)] = (byte)4;
            //第i个信息字-数据-4字节
            if(i==1){//获取系统类型
                byte[] systemType_one = ByteConvertUtils.intToBytesLittle(Integer.parseInt(
                        LoadConfig.getInstance().getReceiveSystemType()));
                System.arraycopy(systemType_one,0,resultBytes,52+14*(i-1),4);
            }else if(i==2){//获取系统编码
                byte[] systemCode_one = ByteConvertUtils.intToBytesLittle(Integer.parseInt(
                        LoadConfig.getInstance().getReceiveSystemCode()));
                System.arraycopy(systemCode_one,0,resultBytes,52+14*(i-1),4);
            }else if(i==3){//获取节点编码
                byte[] nodeCode_one = ByteConvertUtils.intToBytesLittle(Integer.parseInt(
                        LoadConfig.getInstance().getReceiveNodeCode()));
                System.arraycopy(nodeCode_one,0,resultBytes,52+14*(i-1),4);
            }else if(i==4||i==5||i==6){//第4、5、6个信息字为字符串类型,由于为用到统一写为V2.12
                byte[] frame_string  = "2.12".getBytes(StandardCharsets.UTF_8);
                System.arraycopy(frame_string,0,resultBytes,52+14*(i-1),4);
            }else if(i==7){//软件自检结果,固定写1
                byte[] selfInspectionResult  = ByteConvertUtils.intToBytesLittle(1);
                System.arraycopy(selfInspectionResult,0,resultBytes,52+14*(i-1),4);
            }else if(i==8){//软件CPU占用情况
                byte[] CPURatio = ByteConvertUtils.float2byte(monitorInfoBean.getCpuRatio());
                System.arraycopy(CPURatio,0,resultBytes,52+14*(i-1),4);
            }else{//第九个信息字,软件内存占用情况
                byte[] usedMemory = ByteConvertUtils.float2byte(monitorInfoBean.getUsedMemory());
                System.arraycopy(usedMemory,0,resultBytes,52+14*(i-1),4);
            }
            //第i个信息字-备用字符串长度-1字节
            resultBytes[56+14*(i-1)] = (byte)1;
            //第i个信息字-备用字符串-1字节
            resultBytes[56+14*(i-1)] = (byte)1;
        }

        //计算校验和
        byte checkSum = checkSum(resultBytes);
        resultBytes[33] = checkSum;
        //返回结果
        return resultBytes;

    }
    /**
     * @Description: 计算校验和-下标为33不计算在内
     * @Author: gaosong
     * @Date: 2020/11/25 15:58
     * @param messageBytes: 字节数组
     * @return: byte
     **/
    public static byte checkSum(byte[] messageBytes) {
        if(messageBytes!=null && messageBytes.length>2){
            int total = 0 ;
            for(int i=0;i<messageBytes.length;i++){
                if(i!=33){//校验和在第33个字节，不计算在内
                    total+=messageBytes[i];
                }
            }
            return (byte) (total & 255);
        }
        return (byte)0;
    }

    /**
     * @Description: 获取当前年
     * @Author: gaosong
     * @Date: 2020/11/25 14:58
     * @return: java.lang.String
     **/
    public static String getSysYear() {
        Calendar date = Calendar.getInstance();
        return String.valueOf(date.get(Calendar.YEAR));
    }
    /**
     * @Description: 获取当前月
     * @Author: gaosong
     * @Date: 2020/11/25 14:58
     * @return: java.lang.String
     **/
    public static String getSysMonth() {
        Calendar date = Calendar.getInstance();
        return String.valueOf(date.get(Calendar.MONTH)+1);
    }
    /**
     * @Description: 获取当前日
     * @Author: gaosong
     * @Date: 2020/11/25 14:58
     * @return: java.lang.String
     **/
    public static String getSysDay() {
        Calendar date = Calendar.getInstance();
        return String.valueOf(date.get(Calendar.DAY_OF_MONTH));
    }
    /**
     * @Description: 获取系统时间毫秒 4字节
     * @Author: gaosong
     * @Date: 2020/11/25 15:03
     * @return: int
     **/
    public static int getSysTime(){
        Calendar date = Calendar.getInstance();
        int hour = date.get(Calendar.HOUR_OF_DAY);//获取当前小时
        int min = date.get(Calendar.MINUTE);//获取当前分钟
        int second = date.get(Calendar.SECOND);//获取当前秒
        int millisecond = date.get(Calendar.MILLISECOND);//获取当前毫秒
        return ((hour*60+min)*60+second)*1000+millisecond;
    }

}
