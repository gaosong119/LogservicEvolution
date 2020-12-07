package com.aerotop.logserviceevolution.messageparse;

import com.aerotop.enums.FrameTypeEnum;
import com.aerotop.enums.LogLevelEnum;
import com.aerotop.logserviceevolution.LogServiceEvolution;
import com.aerotop.logserviceevolution.configload.LoadConfig;
import com.aerotop.message.Message;
import com.aerotop.pack.ByteConvertUtils;

/**
 * @ClassName: CheckMessageHeader
 * @Description: 检查消息是否满足解析要求
 * @Author: gaosong
 * @Date 2020/7/16 16:51
 */
public class CheckMessageHeader {
    //日志记录对象
    private static Message message = new Message(FrameTypeEnum.DATAFRAME,"日志服务", LogLevelEnum.error,
            System.currentTimeMillis(),(byte)10,"","","", LoadConfig.getInstance().
            getKafka_consumer_topic());
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
                message.setReserved("校验通信帧标识错误,必须0xBE开头!");
                LogServiceEvolution.writerServiceImpl.logger(message);
                //将日志内容刷新到文件
                LogServiceEvolution.writerServiceImpl.flushChannel();
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
                message.setReserved("校验和计算错误!");
                LogServiceEvolution.writerServiceImpl.logger(message);
                //将日志内容刷新到文件
                LogServiceEvolution.writerServiceImpl.flushChannel();
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
            //计算总帧长
            if(bytes.length==frameLength){//等于帧长
                flag = true;
            }else{
                message.setReserved("计算帧长错误!");
                LogServiceEvolution.writerServiceImpl.logger(message);
                //将日志内容刷新到文件
                LogServiceEvolution.writerServiceImpl.flushChannel();
            }
        }
        return flag;
    }
}
