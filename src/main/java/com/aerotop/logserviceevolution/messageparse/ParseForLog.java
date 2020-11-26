package com.aerotop.logserviceevolution.messageparse;

import com.aerotop.logserviceevolution.configload.LoadConfig;
import com.aerotop.pack.ByteConvertUtils;

import java.util.HashMap;
import java.util.Map;

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
    public static Map<String,String> unPack(byte[] bytes){
        if(CheckMessageHeader.messageHeaderParse(bytes)){//满足解析要求
            //获取帧类型
            int frameType = ByteConvertUtils.byte1ToInt(bytes,6);
            if(0==frameType){//数据帧，进入解析
                Map<String,String> messageMap = new HashMap<>();
                //解析软件名称
                int sendNamePreconditions = ByteConvertUtils.byte4ToInt(bytes,7);//软件名称字符串长度
                String sendName = ByteConvertUtils.bytesToString(bytes, 11, 11 + sendNamePreconditions);
                messageMap.put("sourceName",sendName);
                //解析日志级别
                messageMap.put("logLevel",SwitchConvertUtil.getLogLevelForString(ByteConvertUtils.byte1ToInt(bytes,11 + sendNamePreconditions)));
                //解析发送时间
                messageMap.put("sendTime", LoadConfig.getInstance().getSimpleDateFormat().format(ByteConvertUtils.byte8ToLong(bytes,12+sendNamePreconditions)));
                //解析流程
                messageMap.put("process",SwitchConvertUtil.getProcessByCoder(ByteConvertUtils.byte1ToInt(bytes,20 + sendNamePreconditions)));
                //解析事件字符串
                int eventPreconditions = ByteConvertUtils.byte4ToInt(bytes,21+sendNamePreconditions);//事件字符串长度
                String event = ByteConvertUtils.bytesToString(bytes,25+sendNamePreconditions,25+sendNamePreconditions+eventPreconditions);
                messageMap.put("event",event);
                //解析事件内容字符串
                int eventContentPreconditions = ByteConvertUtils.byte4ToInt(bytes,25+sendNamePreconditions+eventPreconditions);//事件字符串长度
                String eventContent = ByteConvertUtils.bytesToString(bytes,29+sendNamePreconditions+eventPreconditions,29+sendNamePreconditions+eventPreconditions+eventContentPreconditions);
                messageMap.put("content",eventContent);
                //解析备注字符串
                int reservedPreconditions = ByteConvertUtils.byte4ToInt(bytes,29+sendNamePreconditions+eventPreconditions+eventContentPreconditions);
                String reserved = ByteConvertUtils.bytesToString(bytes,33+sendNamePreconditions+eventPreconditions+eventContentPreconditions,33+sendNamePreconditions+eventPreconditions+eventContentPreconditions+reservedPreconditions);
                messageMap.put("reserved",reserved);
                return messageMap;
            }
        }
        return null;
    }
}
