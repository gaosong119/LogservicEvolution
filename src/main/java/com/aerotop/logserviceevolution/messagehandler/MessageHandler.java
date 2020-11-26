package com.aerotop.logserviceevolution.messagehandler;

import com.aerotop.logserviceevolution.configload.LoadConfig;
import com.aerotop.transfer.WriterSingle;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @ClassName: MessageHandler
 * @Description: 消息处理类，决定消息的存储目标及执行存储过程
 * @Author: gaosong
 * @Date 2020/7/21 14:54
 */
public class MessageHandler {
    //软件发送方与MessageModel对应的集合
    public static Map<String, MessageModel> sendMapping = new HashMap<>();

    /**
     * @Description:处理解析后的消息集合
     * @Author: gaosong
     * @Date: 2020/7/21 15:55
     * @param: messageMap: 已解析的一条消息结果集
     * @return: * @return: null
     **/
    public static void messageProcess(Map<String, String> messageMap) {
        if(messageMap!=null){
            //获取消息发送方名称
            String sourceName = messageMap.get("sourceName");
            if (sourceName != null && !"".equals(sourceName)) {
                //获取MessageModel对象
                MessageModel messageModel = sendMapping.get(sourceName);
                //根据存储规则组织存储内容
                String messageContent = assemblyMessage(messageMap);
                //传入消息写入判定函数
                determineWriter(sourceName,messageContent,messageModel);
            }
        }
    }
    /**
     * @Description:决定是否追加写入文件并维护messageModel对象
     * @Author: gaosong
     * @Date: 2020/7/22 10:49
     * @param: sourceName 发送方名称,messageContent 消息内容 ,messageModel 消息模型对象
     * @return: * @return: null
     **/
    private static void determineWriter(String sourceName, String messageContent, MessageModel messageModel) {
        if (messageModel == null) {//此发送方第一次存储
            messageModel = new MessageModel();
            //初始化messageModel，准备写入
            initMessageModel(sourceName,messageModel);
            //将初始化完毕的messageModel存入映射集合
            sendMapping.put(sourceName, messageModel);
        }
        byte[] bytes = messageContent.getBytes(StandardCharsets.UTF_8);
        //判断messageModel中file大小是否满足追加写入
        if(LoadConfig.getInstance().getMaxFileSize()<bytes.length+messageModel.getFile().length()){//超出最大值
            //先执行刷新并关闭原来文件的输出流
            try {
                messageModel.getBufferedOutputStream().close();//强制关闭输出流，关闭时自动执行刷新
            } catch (IOException e) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                e.printStackTrace(new PrintStream(baos));
                //log.error(baos.toString());
                WriterSingle.getInstance().loggerError((byte)10,"错误捕捉",baos.toString(),"");
            }
            //创建新文件
            initMessageModel(sourceName,messageModel);
        }
        //执行写入
        writeMessage(bytes,messageModel);
    }
    /**
     * @Description:执行写入的函数
     * @Author: gaosong
     * @Date: 2020/7/22 14:04
     * @param: messageContentForBytes 消息内容，messageModel 写入对象
     * @return:  null
     **/
    private static void writeMessage(byte[] messageContentForBytes, MessageModel messageModel) {
        try {
            messageModel.getBufferedOutputStream().write(messageContentForBytes);
        } catch (IOException e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            WriterSingle.getInstance().loggerError((byte)10,"错误捕捉",baos.toString(),"");
        }
    }

    /**
     * @Description:将messageMap中数据根据配置规则组织成需要存储到文件的字符串
     * @Author: gaosong
     * @Date: 2020/7/22 10:03
     * @param: messageMap
     * @return: String
     **/
    private static String assemblyMessage(Map<String, String> messageMap) {
        //返回的字符串
        String messageContent = messageMap.get("logLevel")+"||"+messageMap.get("sendTime")+"||"+LoadConfig.getInstance().getSimpleDateFormat().format(System.currentTimeMillis())
                +"||"+messageMap.get("process")+"||"+messageMap.get("event")+"||"+messageMap.get("content")+"||"+messageMap.get("reserved");
        //自动添加换行符
        return messageContent+System.lineSeparator();
    }

    /**
     * @Description:根据软件名称初始化消息对象
     * @Author: gaosong
     * @Date: 2020/7/21 16:43
     * @param: sourceName 软件名称,messageModel 消息存储对象
     * @return: null
     **/
    private static void initMessageModel(String sourceName, MessageModel messageModel) {
        try {
            //根据sourceName获取存储目录
            String directoryPath = LoadConfig.getInstance().getFileRootPath()+"/"+sourceName+"/"+LoadConfig.getInstance().getTodayDateFormat().format(System.currentTimeMillis());
            //创建目录
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            //在目录创建完成后更新下标index
            int fileIndex = Objects.requireNonNull(directory.listFiles()).length;
            messageModel.setIndex(fileIndex);
            //获取file绝对路径
            String filePath = LoadConfig.getInstance().getFileRootPath()+"/"+sourceName+"/"+LoadConfig.getInstance().getTodayDateFormat().format(System.currentTimeMillis())+
                    "/"+sourceName+"_"+LoadConfig.getInstance().getTodayTimeFormat().format(System.currentTimeMillis())+"_"+fileIndex+".log";
            //更新File对象
            File file = new File(filePath);
            if(!file.exists()){
                boolean createResult = file.createNewFile();
                if(createResult){
                    //为messageModel赋值
                    messageModel.setFile(file);
                    messageModel.setFileOutputStream(new FileOutputStream(file,true));
                    messageModel.setBufferedOutputStream(new BufferedOutputStream(messageModel.getFileOutputStream(),messageModel.getBUFFER_SIZE()));
                }
            }
        } catch (IOException e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            WriterSingle.getInstance().loggerError((byte)10,"错误捕捉",baos.toString(),"");
        }
    }
}
