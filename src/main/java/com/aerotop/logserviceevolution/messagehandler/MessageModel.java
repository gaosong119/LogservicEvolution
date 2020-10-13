package com.aerotop.logserviceevolution.messagehandler;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * @ClassName: MessageModel
 * @Description: 每个发送方对应一个此类对象用来存储文件对象，存储着每个发送方对应的写入通道
 * @Author: gaosong
 * @Date 2020/7/21 14:57
 */
public class MessageModel {
    //发送方对应的文件句柄
    private File file;
    //数据输出通道对象
    private FileOutputStream fileOutputStream;
    //缓冲输出字节流对象
    private BufferedOutputStream bufferedOutputStream;
    //软件发送方存储下标(识别%i)
    private int index;

    public MessageModel() {
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public FileOutputStream getFileOutputStream() {
        return fileOutputStream;
    }

    public void setFileOutputStream(FileOutputStream fileOutputStream) {
        this.fileOutputStream = fileOutputStream;
    }

    public BufferedOutputStream getBufferedOutputStream() {
        return bufferedOutputStream;
    }

    public void setBufferedOutputStream(BufferedOutputStream bufferedOutputStream) {
        this.bufferedOutputStream = bufferedOutputStream;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getBUFFER_SIZE() {
        //输出流缓冲区(64KB)
        return 65536;
    }
}
