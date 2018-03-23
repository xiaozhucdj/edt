package com.yougy.common.utils;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;

/**
 * Created by jiangliang on 2016/12/23.
 */

public class FtpUtil {
    private static final String TAG = "FtpUtil";
    private static FTPClient ftpClient = null;
    //FTP服务器ip地址
    private final static String FTP_URL = "192.168.12.2";
    //FTP服务器端口号
    private final static int FTP_PORT = 21;
    //登陆FTP服务器的账号
    private final static String USER_NAME = "liuyang";
    //登陆FTP服务器的密码
    private final static String USER_PASSWORD = "123123";


    /**
     * 设置FTP服务器
     */
    private static boolean initFTPSetting() {
        int reply;
        try {
            //1.要连接的FTP服务器Url,Port
            ftpClient.connect(FTP_URL, FTP_PORT);
            //2.登陆FTP服务器
            ftpClient.login(USER_NAME, USER_PASSWORD);
            ftpClient.setControlEncoding("UTF-8");
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            //3.看返回的值是不是230，如果是，表示登陆成功
            reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                //断开
                ftpClient.disconnect();
                return false;
            }
            return true;
        } catch (SocketException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 上传文件
     *
     * @param filePath 要上传文件所在SDCard的路径
     * @param fileName 要上传的文件的文件名(如：Sim唯一标识码)
     * @return true为成功，false为失败
     */
    public static boolean uploadFile(String filePath, String fileName) {
        ftpClient = new FTPClient();
        if (!ftpClient.isConnected()) {
            if (!initFTPSetting()) {
                return false;
            }
        }
        FileInputStream fileInputStream = null;
        try {
            //设置存储路径
            ftpClient.makeDirectory("/data/" + SpUtils.getAccountId());
            ftpClient.changeWorkingDirectory("/data/" + SpUtils.getAccountId());

            //设置上传文件需要的一些基本信息
            ftpClient.setBufferSize(1024);
            ftpClient.setControlEncoding("UTF-8");
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            //文件上传吧～
            fileInputStream = new FileInputStream(filePath);
            ftpClient.storeFile(fileName, fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 下载文件
     *
     * @param filePath 要存放的文件的路径
     * @param fileName 远程FTP服务器上的那个文件的名字
     * @return true为成功，false为失败
     */
    public static boolean downLoadFile(String filePath, String fileName) {
        LogUtils.e(TAG, "file path is : " + filePath + ",file name is : " + fileName);
        ftpClient = new FTPClient();
        if (!ftpClient.isConnected()) {
            if (!initFTPSetting()) {
                return false;
            }
        }
        try {
            ftpClient.enterLocalPassiveMode();
            // 转到指定下载目录
            ftpClient.changeWorkingDirectory("/data/" + SpUtils.getAccountId());

            // 列出该目录下所有文件
            FTPFile[] files = ftpClient.listFiles("/data/" + SpUtils.getAccountId());
            LogUtils.e(TAG, "account id is : " + SpUtils.getAccountId() + ",files' size is : " + files.length);
            if (files.length == 0) {
                return false;
            }
            // 遍历所有文件，找到指定的文件
            for (FTPFile file : files) {
                LogUtils.e(TAG, "file name is : " + file.getName());
                if (file.getName().equals(fileName)) {
                    //根据绝对路径初始化文件
                    File localFile = new File(filePath);
                    // 输出流
                    OutputStream outputStream = new FileOutputStream(localFile);
                    // 下载文件
                    ftpClient.retrieveFile(file.getName(), outputStream);
                    //关闭流
                    outputStream.close();
                }
            }
            //退出登陆FTP，关闭ftpCLient的连接
            ftpClient.logout();
            ftpClient.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
