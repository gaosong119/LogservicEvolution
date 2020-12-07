package com.aerotop.logserviceevolution.messageparse;

import com.aerotop.enums.FrameTypeEnum;
import com.aerotop.enums.LogLevelEnum;
import com.aerotop.message.Message;
import com.aerotop.pack.ByteConvertUtils;

/**
 * @ClassName: ParseForLog
 * @Description: 按照协议解析日志
 * @Author: gaosong
 * @Date 2020/7/16 16:35
 */
public class ParseForLog {
    /**
    * @Description 解析日志协议数据
    * @param bytes:二进制数据
    * @Return Message 消息对象
    * @Author gaosong
    * @Date 2020/7/16 16:38
    */
    public static Message unPack(byte[] bytes){
        if(CheckMessageHeader.messageHeaderParse(bytes)){//满足解析要求
            //返回对象
            Message message = new Message();
            //获取帧类型
            int frameType = ByteConvertUtils.byte1ToInt(bytes,6);
            if(0==frameType){//数据帧，进入解析
                message.setFrameType(FrameTypeEnum.DATAFRAME);
                //解析软件名称
                int sendNamePreconditions = ByteConvertUtils.byte4ToInt(bytes,7);//软件名称字符串长度
                String sendName = ByteConvertUtils.bytesToString(bytes, 11,
                        11 + sendNamePreconditions);
                message.setSourceName(sendName);
                //解析日志级别
                message.setLoglevel(LogLevelEnum.valueOf(SwitchConvertUtil.getLogLevelForString
                        (bytes[11 + sendNamePreconditions])));
                //解析发送时间
                message.setSendTime(ByteConvertUtils.byte8ToLong(bytes,12+sendNamePreconditions));

                //解析流程
                message.setProcess(bytes[20 + sendNamePreconditions]);

                //解析事件字符串
                int eventPreconditions = ByteConvertUtils.byte4ToInt(bytes,21+sendNamePreconditions);//长度描述
                String event = ByteConvertUtils.bytesToString(bytes,25+sendNamePreconditions,
                        25+sendNamePreconditions+eventPreconditions);
                message.setEvent(event);

                //解析事件内容字符串
                int eventContentPreconditions = ByteConvertUtils.byte4ToInt(bytes,
                        25+sendNamePreconditions+eventPreconditions);//事件字符串长度
                String eventContent = ByteConvertUtils.bytesToString(bytes,
                        29+sendNamePreconditions+eventPreconditions,
                        29+sendNamePreconditions+eventPreconditions+eventContentPreconditions);
                message.setEventCount(eventContent);

                //解析备注字符串
                int reservedPreconditions = ByteConvertUtils.byte4ToInt(bytes,
                        29+sendNamePreconditions+eventPreconditions+eventContentPreconditions);
                String reserved = ByteConvertUtils.bytesToString(bytes,
                        33+sendNamePreconditions+eventPreconditions+eventContentPreconditions,
                        33+sendNamePreconditions+eventPreconditions+eventContentPreconditions+reservedPreconditions);
                message.setReserved(reserved);
                return message;
            }
        }
        return null;
    }
}
