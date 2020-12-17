package com.aerotop.logserviceevolution.selfinspection;

import com.aerotop.logserviceevolution.LogServiceEvolution;
import com.aerotop.logserviceevolution.configload.LoadConfig;
import com.aerotop.logserviceevolution.monitor.MonitorInfoBean;
import com.aerotop.pack.ByteConvertUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * @ClassName: HandlerUtilForUDP
 * @Description: udp通信处理工具类
 * @Author: gaosong
 * @Date 2020/11/25 9:44
 */
public class HandlerUtilForUDP {
    /**
     * @Description: 将byte数组追加到List集合中
     * @Author: gaosong
     * @Date: 2020/12/17 10:40
     * @param bytes: 源数组
     * @param arrayList: 集合
     * @return: void
     **/
  public static void appendBytesToList(byte[] bytes,ArrayList<Byte> arrayList){
      if(bytes!=null && arrayList!=null){
          for (byte aByte : bytes) {
              arrayList.add(aByte);
          }
      }
  }
    /**
     * @Description: 执行自检程序并将自检结果按照协议组包
     * @Author: gaosong
     * @Date: 2020/11/25 18:49
     * @param monitorInfoBean: 自检结果对象
     * @return: byte[]
     **/
    public static byte[] selfInspectionPack(MonitorInfoBean monitorInfoBean) {
        //消息通信返回集合
        ArrayList sendMsg = new ArrayList<>();
        //设置帧同步-1字节
        sendMsg.add((byte)0xBE);

        //帧长-2字节
        byte[] frameLengthBytes = ByteConvertUtils.short2byteLittle((short) 0);
        appendBytesToList(frameLengthBytes,sendMsg);

        //帧类型-1字节
        sendMsg.add((byte)0);

        //信源(日志服务)-系统类型 -1字节
        byte receiveSystemTypeBytes = Byte.parseByte(LoadConfig.getInstance().getReceiveSystemType());
        sendMsg.add(receiveSystemTypeBytes);

        //信源(日志服务)-系统编码 -2字节
        byte[] receiveSystemCodeBytes = ByteConvertUtils.short2byteLittle(Short.parseShort(
                LoadConfig.getInstance().getReceiveSystemCode()));
        appendBytesToList(receiveSystemCodeBytes,sendMsg);

        //信源(日志服务)-节点编码 -1字节
        byte receiveNodeCodeBytes = Byte.parseByte(LoadConfig.getInstance().getReceiveNodeCode());
        sendMsg.add(receiveNodeCodeBytes);

        //信宿(自检服务)-系统类型 -1字节
        byte sourceSystemTypeBytes = Byte.parseByte(LoadConfig.getInstance().getSourceSystemType());
        sendMsg.add(sourceSystemTypeBytes);

        //信宿(自检服务)-系统编码 -2字节
        byte[] sourceSystemCodeBytes = ByteConvertUtils.short2byteLittle(Short.parseShort(LoadConfig.getInstance().getSourceSystemCode()));
        appendBytesToList(sourceSystemCodeBytes,sendMsg);

        //信宿(自检服务)-节点编码 -1字节
        byte sourceNodeCodeBytes = Byte.parseByte(LoadConfig.getInstance().getSourceNodeCode());
        sendMsg.add(sourceNodeCodeBytes);

        //日期-年-2字节
        byte[] yearBytes = ByteConvertUtils.short2byteLittle(Short.parseShort(getSysYear()));
        appendBytesToList(yearBytes,sendMsg);

        //日期-月-1字节
        byte monthBytes = Byte.parseByte(getSysMonth());
        sendMsg.add(monthBytes);

        //日期-日-1字节
        byte dayBytes = Byte.parseByte(getSysDay());
        sendMsg.add(dayBytes);

        //日期-时间-4字节
        byte[] timeBytes = ByteConvertUtils.intToBytesLittle(getSysTime());
        appendBytesToList(timeBytes,sendMsg);

        //帧序号/确认帧序号 -4字节
        byte[] frameNum = ByteConvertUtils.intToBytesLittle(1);
        appendBytesToList(frameNum,sendMsg);

        //确认标志-1字节
        byte confirmMark = (byte)0x00;
        sendMsg.add(confirmMark);

        //重传次数-1字节
        byte retransmissionTimes =(byte) 0x00;
        sendMsg.add(retransmissionTimes);

        //信息字类型-1字节
        byte informationWord = (byte)3;
        sendMsg.add(informationWord);

        //信息字格式-2字节
        byte[] informationWordNum = ByteConvertUtils.short2byteLittle((short)256);
        appendBytesToList(informationWordNum,sendMsg);

        //备用字节-4字节
        byte[] reserved = ByteConvertUtils.intToBytesLittle(0);
        appendBytesToList(reserved,sendMsg);

        //校验和-先用1字节占位
        sendMsg.add((byte)1);
        //--------组织消息通信信息头---------
        //消息发送方软件标识字符串长度
        int receiveSoftUniqueIDLength = LoadConfig.getInstance().getReceiveSoftUniqueID().
                getBytes(StandardCharsets.UTF_8).length;
        sendMsg.add((byte)receiveSoftUniqueIDLength);
        //消息发送方软件标识字符串
        String receiveSoftUniqueID = LoadConfig.getInstance().getReceiveSoftUniqueID();
        if(receiveSoftUniqueIDLength!=0){//若长度描述不为0,说明软件标识字段有值
            appendBytesToList(receiveSoftUniqueID.getBytes(StandardCharsets.UTF_8),sendMsg);
        }

        //消息接收方软件标识字符串长度
        int sourceSoftUniqueIDLength = LoadConfig.getInstance().getSourceSoftUniqueID().
                getBytes(StandardCharsets.UTF_8).length;
        sendMsg.add((byte)sourceSoftUniqueIDLength);
        //消息接收方软件标识字符串
        String sourceSoftUniqueID = LoadConfig.getInstance().getSourceSoftUniqueID();
        if(sourceSoftUniqueIDLength!=0){//若长度描述不为0,说明软件标识字段有值
            appendBytesToList(sourceSoftUniqueID.getBytes(StandardCharsets.UTF_8),sendMsg);
        }

        //消息通信信息字类型
        sendMsg.add((byte)0);
        //消息通信信息字个数
        byte[] informationWordNums = ByteConvertUtils.short2byteLittle((short) 9);
        appendBytesToList(informationWordNums,sendMsg);
        //表号
        byte[] tableNum = ByteConvertUtils.short2byteLittle((short) 4270);
        appendBytesToList(tableNum,sendMsg);
        //优先级
        sendMsg.add((byte)1);
        //备用字符串长度
        sendMsg.add((byte)1);
        //备用字符串
        sendMsg.add((byte)1);
        //--------组织消息通信信息字---------
        //第1-3个信息字,前三个信息字固定为14字节
        for(int i=1;i<=9;i++){
            //第i个信息字-格式-1字节
            sendMsg.add((byte)0);

            //第i个信息字-编码-2字节
            byte[] code_one = ByteConvertUtils.short2byteLittle((short) i);
            appendBytesToList(code_one,sendMsg);

            //第i个信息字-数据类型-1字节
            if(i==1||i==2||i==3||i==7){
                sendMsg.add((byte)1);//int类型
            }else if(i==4||i==5||i==6){
                sendMsg.add((byte)4);//String类型
            }else {
                sendMsg.add((byte)2);//float类型
            }
            //第i个信息字-超差标记-1字节
            sendMsg.add((byte)1);
            //第i个信息字-弹编号-2字节
            byte[] missileCode_one = ByteConvertUtils.short2byteLittle((short) 1);
            appendBytesToList(missileCode_one,sendMsg);

            //第i个信息字-数据-4字节
            if(i==1){//获取系统类型
                //第i个信息字-字符串数据长度-1字节,前三个信息字固定长度4字节
                sendMsg.add((byte)4);
                byte[] systemType_one = ByteConvertUtils.intToBytesLittle(Integer.parseInt(
                        LoadConfig.getInstance().getReceiveSystemType()));
                appendBytesToList(systemType_one,sendMsg);
            }else if(i==2){//获取系统编码
                //第i个信息字-字符串数据长度-1字节,前三个信息字固定长度4字节
                sendMsg.add((byte)4);
                byte[] systemCode_one = ByteConvertUtils.intToBytesLittle(Integer.parseInt(
                        LoadConfig.getInstance().getReceiveSystemCode()));
                appendBytesToList(systemCode_one,sendMsg);
            }else if(i==3){//获取节点编码
                //第i个信息字-字符串数据长度-1字节,前三个信息字固定长度4字节
                sendMsg.add((byte)4);
                byte[] nodeCode_one = ByteConvertUtils.intToBytesLittle(Integer.parseInt(
                        LoadConfig.getInstance().getReceiveNodeCode()));
                appendBytesToList(nodeCode_one,sendMsg);
            }else if(i==4){//第4个信息字为软件登录用户id
                byte[] userLoginIDBytes = LoadConfig.getInstance().getReceiveUserLoginID().
                        getBytes(StandardCharsets.UTF_8);
                sendMsg.add((byte)userLoginIDBytes.length);
                if(userLoginIDBytes.length > 0){
                    appendBytesToList(userLoginIDBytes,sendMsg);
                }
            }else if(i==5){//第5个信息字为软件唯一标识
                byte[] receiveSoftUniqueIDBytes = LoadConfig.getInstance().getReceiveSoftUniqueID().
                        getBytes(StandardCharsets.UTF_8);
                sendMsg.add((byte)receiveSoftUniqueIDBytes.length);
                if(receiveSoftUniqueIDBytes.length > 0){
                    appendBytesToList(receiveSoftUniqueIDBytes,sendMsg);
                }
            }else if(i==6){//第六个信息字为软件版本号
                byte[] versionBytes = LogServiceEvolution.versionCount.getBytes(StandardCharsets.UTF_8);
                sendMsg.add((byte)versionBytes.length);
                appendBytesToList(versionBytes,sendMsg);
            }else if(i==7){//软件自检结果,固定写1
                byte[] selfInspectionResult  = ByteConvertUtils.intToBytesLittle(1);
                sendMsg.add((byte)selfInspectionResult.length);
                appendBytesToList(selfInspectionResult,sendMsg);
            }else if(i==8){//软件CPU占用情况
                byte[] CPURatio = ByteConvertUtils.float2byte(monitorInfoBean.getCpuRatio());
                sendMsg.add((byte)CPURatio.length);
                appendBytesToList(CPURatio,sendMsg);
            }else{//第九个信息字,软件内存占用情况
                byte[] usedMemory = ByteConvertUtils.float2byte(monitorInfoBean.getUsedMemory());
                sendMsg.add((byte)usedMemory.length);
                appendBytesToList(usedMemory,sendMsg);
            }
            //第i个信息字-备用字符串长度-1字节
            sendMsg.add((byte)1);
            //第i个信息字-备用字符串-1字节
            sendMsg.add((byte)1);
        }
        byte[] resultBytes = ListToBytes(sendMsg);
        //重新设置帧长
        byte[] frameLength = ByteConvertUtils.short2byteLittle((short) resultBytes.length);
        System.arraycopy(frameLength,0,resultBytes,1,2);
        //重新计算校验和
        byte checkSum = checkSum(resultBytes);
        resultBytes[33] = checkSum;
        //返回结果
        return resultBytes;

    }
    /**
     * @Description: List集合所有元素转到byte数组
     * @Author: gaosong
     * @Date: 2020/12/17 13:36
     * @param arrayList: 源集合
     * @return: byte[]
     **/
    public static byte[] ListToBytes(ArrayList arrayList){

        if(arrayList!=null){
            int size = arrayList.size();
            byte[] bytes = new byte[size];
            for(int i=0;i<size;i++){
                bytes[i]= (byte) arrayList.get(i);
            }
            return bytes;
        }
        return null;
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
