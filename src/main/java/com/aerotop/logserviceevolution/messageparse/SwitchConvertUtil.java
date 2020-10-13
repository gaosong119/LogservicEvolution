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
    public static String getLogLevelForString(int value){
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
    /**
     * @Description:通过流程编码获取流程字符串
     * @Author: gaosong
     * @Date: 2020/7/17 15:40
     * @param: coder:流程编码
     * @return: String 流程名称
     **/
    public static String getProcessByCoder(int coder){
        switch (coder){
            case 0:
                return "转入ljb测试";
            case 1:
                return "ljb";
            case 2:
                return "转入ejrjb测试";
            case 3:
                return "ejrjb";
            case 4:
                return "转入yjrjb测试";
            case 5:
                return "yjrjb";
            case 6:
                return "fs";
            case 7:
                return "tczb";
            case 8:
                return "紧急断电";
            case 9:
                return "空J";
            case 10:
                return "其他";
            default:
                return "";
        }
    }
}
