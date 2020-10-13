package com.aerotop.logserviceevolution.messageparse;

import com.aerotop.transfer.WriterSingle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: CheckMessageHeader
 * @Description: 检查消息是否满足解析要求
 * @Author: gaosong
 * @Date 2020/7/16 16:51
 */
public class CheckMessageHeader {
    //private static final Logger log = LoggerFactory.getLogger(CheckMessageHeader.class);//日志生成对象
    /**
     * @Description 判断消息是否满足解析要求，满足返回true,否则返回false
     * @parm bytes:二进制数据
     * @Return boolean
     * @Author gaosong
     * @Date 2020/7/16 16:42
     */
    public static boolean messageHeaderParse(byte[] bytes) {
        //校验帧标识
        return checkFrameMark(bytes) && calcChecksum(bytes) && checkFrameLength(bytes);
    }
    /**
    * @Description 校验通信帧标识
    * @parm bytes:二进制数据
    * @Return boolean
    * @Author gaosong
    * @Date 2020/7/16 16:55
    */
    public static Boolean checkFrameMark(byte[] bytes){
        boolean flag = true;
        if(bytes!=null && bytes.length>0){
            if(190 != (bytes[0]&255)){//0xBE开头
                flag = false;
                //log.error("校验通信帧标识错误,必须0xBE开头!");
                WriterSingle.getInstance().loggerError((byte)10,"通信帧完整性校验","校验通信帧标识错误,必须0xBE开头!","","");
            }
        }
        return flag;
    }
    /**
     * @Description 计算校验和
     * @parm bytes:二进制数据
     * @Return boolean
     * @Author gaosong
     * @Date 2020/7/16 16:55
     */
    public static Boolean calcChecksum(byte[] bytes){
        boolean flag =false;
        if(bytes!=null && bytes.length>2){
            int total = 0 ;
            for(int i=0;i<bytes.length;i++){
                if(i!=1){//校验和在第二个字节，不计算在内
                    total+=bytes[i];
                }
            }
            if((total & 255)==(bytes[1] & 255)){//与校验和一致
                flag = true;
            }else {
                //log.error("校验和计算错误!");
                WriterSingle.getInstance().loggerError((byte)10,"通信帧完整性校验","校验和计算错误!","","");
            }
        }
        return flag;
    }
    /**
     * @Description 计算帧长
     * @parm bytes:二进制数据
     * @Return boolean
     * @Author gaosong
     * @Date 2020/7/16 16:55
     */
    public static Boolean checkFrameLength(byte[] bytes){
        boolean flag =false;
        if(bytes!=null){
            //计算出帧长
            int frameLength = ByteConvertUtils.byte4ToInt(bytes,2);
            //计算发送方软件字符串长度具体数值
            int sendNum = ByteConvertUtils.byte4ToInt(bytes,7);
            //计算事件字符串长度具体数值
            int eventNum = ByteConvertUtils.byte4ToInt(bytes,21+sendNum);
            //计算事件内容字符串长度具体数值
            int eventContentNum = ByteConvertUtils.byte4ToInt(bytes,25+sendNum+eventNum);
            //计算备用字符串长度具体数值
            int reservedNum = ByteConvertUtils.byte4ToInt(bytes,29+sendNum+eventNum+eventContentNum);
            //计算总帧长
            if((33+sendNum+eventNum+eventContentNum+reservedNum)==frameLength){//等于帧长
                flag = true;
            }else{
                //log.error("计算帧长错误!");
                WriterSingle.getInstance().loggerError((byte)10,"通信帧完整性校验","计算帧长错误!","","");
            }
        }
        return flag;
    }
}
