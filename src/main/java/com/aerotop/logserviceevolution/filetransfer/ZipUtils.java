package com.aerotop.logserviceevolution.filetransfer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @ClassName: ZipUtils
 * @Description: 压缩工具类
 * @Author: gaosong
 * @Date 2020/9/22 14:41
 */
public class ZipUtils {
    private static final Logger log = LoggerFactory.getLogger(ZipUtils.class);//日志生成对象

    private static final int  BUFFER_SIZE = 2 * 1024;
    /**
     * 压缩成ZIP 方法1
     * @param srcDir 压缩文件夹路径
     * @param out    压缩文件输出流
     * @param KeepDirStructure  是否保留原来的目录结构,true:保留目录结构;
     * false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
    public static void toZip(String srcDir, OutputStream out, boolean KeepDirStructure)throws RuntimeException{
        ZipOutputStream zos = null ;
        try {
            zos = new ZipOutputStream(out);
            File sourceFile = new File(srcDir);
            compress(sourceFile,zos,sourceFile.getName(),KeepDirStructure);
            //递归删除未压缩的源文件
            deleteDir(sourceFile);
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils",e);
        }finally{
            if(zos != null){
                try {
                    zos.close();
                } catch (IOException e) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    e.printStackTrace(new PrintStream(baos));
                    log.error(baos.toString());
                }
            }
        }
    }
    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     * @param dir 将要删除的文件目录
     * @return boolean
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if(children!=null){
                //递归删除目录中的子目录下
                for (String child : children) {
                    boolean success = deleteDir(new File(dir, child));
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }
    /**
     * 压缩成ZIP 方法2
     * @param srcFiles 需要压缩的文件列表
     * @param out           压缩文件输出流
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
    public static void toZip(List<File> srcFiles , OutputStream out)throws RuntimeException {
        ZipOutputStream zos = null ;
        try {
            zos = new ZipOutputStream(out);
            for (File srcFile : srcFiles) {
                byte[] buf = new byte[BUFFER_SIZE];
                zos.putNextEntry(new ZipEntry(srcFile.getName()));
                int len;
                FileInputStream in = new FileInputStream(srcFile);
                while ((len = in.read(buf)) != -1){
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
                in.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils",e);
        }finally{
            if(zos != null){
                try {
                    zos.close();
                } catch (IOException e) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    e.printStackTrace(new PrintStream(baos));
                    log.error(baos.toString());
                }
            }
        }
    }
    /**
     * 递归压缩方法
     * @param sourceFile 源文件
     * @param zos        zip输出流
     * @param name       压缩后的名称
     * @param keepDirStructure  是否保留原来的目录结构,true:保留目录结构;
     * false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws Exception
     */
    private static void compress(File sourceFile, ZipOutputStream zos, String name, boolean keepDirStructure) throws Exception{
        byte[] buf = new byte[BUFFER_SIZE];
        if(sourceFile.isFile()){
            // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
            zos.putNextEntry(new ZipEntry(name));
            // copy文件到zip输出流中
            int len;
            FileInputStream in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1){
                zos.write(buf, 0, len);
            }
            // Complete the entry
            zos.closeEntry();
            in.close();
        } else {
            File[] listFiles = sourceFile.listFiles();
            if(listFiles == null || listFiles.length == 0){
                // 需要保留原来的文件结构时,需要对空文件夹进行处理
                if(keepDirStructure){
                    // 空文件夹的处理
                    zos.putNextEntry(new ZipEntry(name + "/"));
                    // 没有文件，不需要文件的copy
                    zos.closeEntry();
                }
            }else {
                for (File file : listFiles) {
                    // 判断是否需要保留原来的文件结构
                    if (keepDirStructure) {
                        // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                        // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                        if(file.isDirectory()){//判断是否为目录,若为目录则FileIsUsed方法会返回true,出现逻辑异常,所以只有file为文件时执行被占用判断
                            compress(file, zos, name + "/" + file.getName(),keepDirStructure);
                        }else if(file.isFile()){
                            if(!FileIsUsed(file)){
                                compress(file, zos, name + "/" + file.getName(),keepDirStructure);
                            }
                        }
                    } else {
                        if(file.isDirectory()){//判断是否为目录,若为目录则FileIsUsed方法会返回true,出现逻辑异常,所以只有file为文件时执行被占用判断
                            compress(file, zos, name + "/" + file.getName(),keepDirStructure);
                        }else if(file.isFile()){
                            if(!FileIsUsed(file)){
                                compress(file, zos, name + "/" + file.getName(),keepDirStructure);
                            }
                        }
                    }
                }
            }
        }
    }
     /**
      * @Description:判断文件是否被占用
      * @Author: gaosong
      * @Date: 2020/9/25 15:45
      * @param:  file 文件对象，例如：“C:\MyFile.txt”
      * @return: 如果文件已被其它程序使用，则为 true；否则为 false
      **/
    public static Boolean FileIsUsed(File file)
    {
        Boolean result = true;
        try {
            if(file.renameTo(file)){
                result=false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        //返回指示文件是否已被其它程序使用的值
        return result;
    }
     /**
      * @Description:判断某个目录下是否存在通道未被占用的文件
      * @Author: gaosong
      * @Date: 2020/9/27 10:38
      * @param: * @param null:
      * @return: * @return: null
      **/
    public static Boolean compressTarget(String fileRootPath){
        Boolean flag =false;
        File[] files=new File(fileRootPath).listFiles();   //列出所有的子文件
        if(files!=null){
            for(File file :files)
            {
                if(file.isFile())//如果是文件，则输出文件名字
                {
                    //判断此文件是否被占用
                    if(!FileIsUsed(file)){
                        //文件未被占用，则说明有可以压缩的文件。停止遍历
                        flag = true;
                        return flag;
                    }
                }else if(file.isDirectory()&&file.listFiles().length>0)//如果是文件夹，则输出文件夹的名字，并递归遍历该文件夹
                {
                    return compressTarget(file.getAbsolutePath());//递归遍历
                }
            }
        }
        return flag;
    }
}
