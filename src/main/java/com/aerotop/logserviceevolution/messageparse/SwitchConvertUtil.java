package com.aerotop.logserviceevolution.messageparse;
/**
* @Description 日志级别、日志流程获取类，将数字转换成对应的字符串
* @Return
* @Author gaosong
* @Date 2020/7/17 10:22
*/
public class SwitchConvertUtil {
    /**
    * @Description 将数字转换为对应的日志级别字符串并返回
    * @param value
    * @Return String
    * @Author gaosong
    * @Date 2020/7/17 10:27
    */
    public static String getLogLevelForString(byte value){
        switch (value){
            case 0:
                return "debug";
            case 1:
                return "info";
            case 2:
                return "warn";
            case 3:
                return "error";
            default:
                return "";
        }
    }
}
