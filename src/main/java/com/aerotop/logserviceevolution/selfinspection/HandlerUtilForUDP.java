package com.aerotop.logserviceevolution.selfinspection;

import com.aerotop.logserviceevolution.configload.LoadConfig;
import com.aerotop.logserviceevolution.messageparse.CheckMessageHeader;
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
    public static boolean legalVerification(byte[] data) {
        boolean result = false;
        //检查帧同步标识
        Boolean frameMarkResult = CheckMessageHeader.checkFrameMark(data);
        //检查信源是否与配置文件相同
        Boolean sourceVerificationResult = sourceVerification(data);
        if(frameMarkResult && sourceVerificationResult){
            result = true;
        }
        return result;
    }
    /**
     * @Description: 检查信源 系统类型、系统编码、节点编码是否与配置文件相同
     * @Author: gaosong
     * @Date: 2020/11/25 10:06
     * @param dataS: 数据包
     * @return: java.lang.Boolean
     **/
    private static Boolean sourceVerification(byte[] dataS) {
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
    }
    /**
     * @Description: 执行自检程序并将自检结果按照协议组包
     * @Author: gaosong
     * @Date: 2020/11/25 18:49
     * @param monitorInfoBean: 自检结果对象
     * @return: byte[]
     **/
    public static byte[] selfInspectionPack(MonitorInfoBean monitorInfoBean) {
        //创建待返回字节数组(帧头+9*信息字0=34+9*12=142)
        byte[] messageBytes = new byte[142];

        //设置帧同步-1字节
        messageBytes[0] = (byte)0xBE;

        //帧长-2字节
        byte[] frameLengthBytes = ByteConvertUtils.short2byte((short) 142);
        System.arraycopy(frameLengthBytes,0,messageBytes,1,2);

        //帧类型-1字节
        messageBytes[3] = (byte)0;

        //信源(日志服务)-系统类型 -1字节
        byte receiveSystemTypeBytes = Byte.parseByte(LoadConfig.getInstance().getReceiveSystemType());
        messageBytes[4] = receiveSystemTypeBytes;

        //信源(日志服务)-系统编码 -2字节
        byte[] receiveSystemCodeBytes = ByteConvertUtils.short2byte(Short.parseShort(LoadConfig.getInstance().getReceiveSystemCode()));
        System.arraycopy(receiveSystemCodeBytes,0,messageBytes,5,2);

        //信源(日志服务)-节点编码 -1字节
        byte receiveNodeCodeBytes = Byte.parseByte(LoadConfig.getInstance().getReceiveNodeCode());
        messageBytes[7] = receiveNodeCodeBytes;

        //信宿(自检服务)-系统类型 -1字节
        byte sourceSystemTypeBytes = Byte.parseByte(LoadConfig.getInstance().getSourceSystemType());
        messageBytes[8] = sourceSystemTypeBytes;

        //信宿(自检服务)-系统编码 -2字节
        byte[] sourceSystemCodeBytes = ByteConvertUtils.short2byte(Short.parseShort(LoadConfig.getInstance().getSourceSystemCode()));
        System.arraycopy(sourceSystemCodeBytes,0,messageBytes,9,2);

        //信宿(自检服务)-节点编码 -1字节
        byte sourceNodeCodeBytes = Byte.parseByte(LoadConfig.getInstance().getSourceNodeCode());
        messageBytes[11] = sourceNodeCodeBytes;

        //日期-年-2字节
        byte[] yearBytes = ByteConvertUtils.short2byte(Short.parseShort(getSysYear()));
        System.arraycopy(yearBytes,0,messageBytes,12,2);

        //日期-月-1字节
        byte monthBytes = Byte.parseByte(getSysMonth());
        messageBytes[14] = monthBytes;

        //日期-日-1字节
        byte dayBytes = Byte.parseByte(getSysDay());
        messageBytes[15] = dayBytes;

        //日期-时间-4字节
        byte[] timeBytes = ByteConvertUtils.intToByteArray(getSysTime());
        System.arraycopy(timeBytes,0,messageBytes,16,4);

        //帧序号/确认帧序号 -4字节
        byte[] frameNum = ByteConvertUtils.intToByteArray(1);
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
        byte[] informationWordNum = ByteConvertUtils.short2byte((short)9);
        System.arraycopy(informationWordNum,0,messageBytes,27,2);

        //备用字节-4字节
        byte[] reserved = ByteConvertUtils.intToByteArray(0);
        System.arraycopy(reserved,0,messageBytes,29,4);

        //组包9个信息字
        //表号-2字节-信息字表号相同
        byte[] tableNum = ByteConvertUtils.short2byte((short)4270);
        for(int i=1;i<=9;i++){
            //存储第i个信息字-表号-2字节
            System.arraycopy(tableNum,0,messageBytes,34+12*(i-1),2);

            //存储第i个信息字-编码-2字节
            byte[] code = ByteConvertUtils.short2byte((short)i);
            System.arraycopy(code,0,messageBytes,34+12*(i-1)+2,2);

            if(i==1){
                //存储第1个信息字-数据-4字节,将配置文件日志服务(信宿-系统类型)按照4字节存储
                byte[] receiveSystemType = ByteConvertUtils.intToByteArray(Integer.parseInt(LoadConfig.getInstance()
                        .getReceiveSystemType()));
                System.arraycopy(receiveSystemType,0,messageBytes,34+12*(i-1)+4,4);
            }else if(i==2){
                //存储第2个信息字-数据-4字节,将配置文件日志服务(信宿-系统编码)按照4字节存储
                byte[] receiveSystemCode = ByteConvertUtils.intToByteArray(Integer.parseInt(LoadConfig.getInstance()
                        .getReceiveSystemCode()));
                System.arraycopy(receiveSystemCode,0,messageBytes,34+12*(i-1)+4,4);
            }else if(i==3){
                //存储第3个信息字-数据-4字节,将配置文件日志服务(信宿-节点编码)按照4字节存储
                byte[] receiveNodeCode = ByteConvertUtils.intToByteArray(Integer.parseInt(LoadConfig.getInstance()
                        .getReceiveNodeCode()));
                System.arraycopy(receiveNodeCode,0,messageBytes,34+12*(i-1)+4,4);
            }else if(i==4){
                //存储第4个信息字-数据-4字节,将配置文件日志服务(软件登录用户id)按照4字节存储
                byte[] receiveUserID = ByteConvertUtils.intToByteArray(0);
                System.arraycopy(receiveUserID,0,messageBytes,34+12*(i-1)+4,4);
            }else if(i==5){
                //存储第5个信息字-数据-4字节,将配置文件日志服务(软件唯一标识)按照4字节存储
                byte[] uniqueIdentification = ByteConvertUtils.intToByteArray(0);
                System.arraycopy(uniqueIdentification,0,messageBytes,34+12*(i-1)+4,4);
            }else if(i==6){
                //存储第6个信息字-数据-4字节,将配置文件日志服务(软件版本号)按照4字节存储
                byte[] versionNumber = "2.02".getBytes(StandardCharsets.UTF_8);
                System.arraycopy(versionNumber,0,messageBytes,34+12*(i-1)+4,4);
            }else if(i==7){
                //存储第7个信息字-数据-4字节,将配置文件日志服务(软件自检结果)按照4字节存储
                byte[] checkSelfResult = ByteConvertUtils.intToByteArray(1);
                System.arraycopy(checkSelfResult,0,messageBytes,34+12*(i-1)+4,4);
            }else if(i==8){
                //存储第8个信息字-数据-4字节,将配置文件日志服务(软件CPU占用情况)按照4字节存储
                byte[] CPURatio = ByteConvertUtils.float2byte(monitorInfoBean.getCpuRatio());
                System.arraycopy(CPURatio,0,messageBytes,34+12*(i-1)+4,4);
            }else {
                //存储第9个信息字-数据-4字节,将配置文件日志服务(软件内存占用情况)按照4字节存储
                byte[] usedMemory = ByteConvertUtils.float2byte(monitorInfoBean.getUsedMemory());
                System.arraycopy(usedMemory,0,messageBytes,34+12*(i-1)+4,4);
            }

            //存储第i个信息字-数据类型-1字节
            if(i!=8 && i!=9){
                byte dataType = (byte)2;
                messageBytes[34+12*(i-1)+8] = dataType;
            }else{
                byte dataType = (byte)1;
                messageBytes[34+12*(i-1)+8] = dataType;
            }

            //井编号-2字节
            byte[] wellNum = ByteConvertUtils.short2byte((short)0);
            System.arraycopy(wellNum,0,messageBytes,34+12*(i-1)+9,2);

            //备用-1字节 (不使用，填充为0)
            messageBytes[34+12*(i-1)+11] = (byte)0;
        }

        //校验和-1字节-最后计算
        byte checkSum = checkSum(messageBytes);
        messageBytes[33] = checkSum;
        return messageBytes;
    }
    /**
     * @Description: 计算校验和-下标为33不计算在内
     * @Author: gaosong
     * @Date: 2020/11/25 15:58
     * @param messageBytes: 字节数组
     * @return: byte
     **/
    private static byte checkSum(byte[] messageBytes) {
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
