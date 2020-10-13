package com.aerotop.logserviceevolution.messageparse;

import java.nio.charset.StandardCharsets;

/**
 * @ClassName: ByteConvertUtils
 * @Description: 字节转换工具类
 * @Author: gaosong
 * @Date 2020/7/16 15:42
 */
public class ByteConvertUtils {
    /**
    * @Description 将一个字节byte类型转换为int类型
    * @param :bytes 字节数组
     * @param :index 取值下标
    * @Return int
    * @Author gaosong
    * @Date 2020/7/16 15:48
    */
    public static int byte1ToInt(byte[] bytes,int index){
        return bytes[index] & 255;
    }
    /**
     * @Description 将四个字节byte类型转换为int类型
     * @param :bytes 字节数组
     * @param :index 取值下标
     * @Return int
     * @Author gaosong
     * @Date 2020/7/16 15:48
     */
    public static int byte4ToInt(byte[] bytes,int index) {
        if (bytes.length > 3) {
            int b0 = bytes[index] & 0xFF;
            int b1 = bytes[index + 1] & 0xFF;
            int b2 = bytes[index + 2] & 0xFF;
            int b3 = bytes[index + 3] & 0xFF;
            return (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
        }
        return 0;
    }
    /**
     * @Description 将8个字节byte类型转换为long类型
     * @param :bytes 字节数组
     * @param :index 取值下标
     * @Return int
     * @Author gaosong
     * @Date 2020/7/16 15:48
     */
    public static long byte8ToLong(byte[] bytes,int index){
        long temp;
        long res = 0;
        if(bytes.length>7){
            int j=0;
            for (int i = index; i < index+8; i++) {
                temp = bytes[i] & 0xff;
                temp <<= 8 * j;
                res |= temp;
                j++;
            }
        }
        return res;
    }
    /**
     * @Description 将N个字节byte类型转换为String类型
     * @param :bytes 字节数组
     * @param :startIndex 取值开始下标
     * @param :endIndex 取值结束下标
     * @Return int
     * @Author gaosong
     * @Date 2020/7/16 15:48
     */
    public static String bytesToString(byte[] bytes,int startIndex,int endIndex){
        if(bytes!=null && endIndex>0 && startIndex!=endIndex){
            byte[] byteModel = new byte[endIndex-startIndex];
            System.arraycopy(bytes, startIndex, byteModel, 0, endIndex-startIndex);
            return new String(byteModel, StandardCharsets.UTF_8);
        }
        return "";
    }
     /**
      * @Description:int 转 byte[]
      * @Author: gaosong
      * @Date: 2020/7/22 16:58
      * @param: * @param null:
      * @return: * @return: null
      **/
    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        //由高位到低位
        result[0] = (byte)((i >> 24) & 0xFF);
        result[1] = (byte)((i >> 16) & 0xFF);
        result[2] = (byte)((i >> 8) & 0xFF);
        result[3] = (byte)(i & 0xFF);
        return result;
    }
    //long类型转成byte数组
    public static byte[] longToByteArray(long number){
        long temp = number;
        byte[] b =new byte[8];
        for(int i =0; i < b.length; i++){
            b[i]=new Long(temp & 0xff).byteValue();//
            //将最低位保存在最低位
            temp = temp >>8;// 向右移8位
        }
        return b;
    }
     /**
      * @Description:16进制字符串转bytes
      * @Author: gaosong
      * @Date: 2020/9/9 10:52
      * @param: * @param null:
      * @return: * @return: null
      **/
    public static byte[] hexStrToBytes(String src){
        int l =src.length()/2;
        byte[] ret =new byte[l];
        for(int i=0;i<l;i++){
            ret[i]=Integer.valueOf(src.substring(i*2,i*2+2),16).byteValue();
        }
        return ret;
    }
}
