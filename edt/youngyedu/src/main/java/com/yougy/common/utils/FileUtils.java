package com.yougy.common.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Administrator on 2016/7/13.
 * 文件操作
 */
public class FileUtils {
    public static final String APP_CACHE_DIR_NAME = "student";
    public static final String TEXT_BOOK = "text_book";
    public static final String TEXT_BOOK_ICON = "text_book_icon";
    public static final String TEXT_BOOK_PROBATION = "text_book_probation";


    /**
     * 判断是否有存储卡，有返回TRUE，否则FALSE
     */
    public static boolean isSDcardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 判断文件是否存在
     *
     * @param pathName 文件全路径名称
     * @return 有返回TRUE，否则FALSE
     */
    public static boolean exists(String pathName) {
        try {
            return !isEmpty(pathName) && new File(pathName).exists();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 创建目录
     *
     * @param dirPath 目录全路径名
     * @return 是否创建成功
     */
    public static boolean createDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists() || !file.isDirectory()) {
            return file.mkdir();
        }
        return true;
    }


    /**
     * 创建多级目录，如果上级目录不存在，会先创建上级目录
     *
     * @param dirPath 目录全路径名
     * @return 是否创建成功
     */
    public static boolean createDirs(String dirPath) {
        LogUtils.i("yuanye path == " + dirPath);
        File file = new File(dirPath);
        if (!file.exists() || !file.isDirectory()) {
            return file.mkdirs();
        }
        return true;
    }


    /**
     * Delete file or folder.
     *
     * @param file file.
     * @return is succeed.
     * @deprecated use {@link #delFileOrFolder(File)} instead.
     */
    @Deprecated
    public static boolean deleteFolder(File file) {
        return delFileOrFolder(file);
    }

    /**
     * Delete file or folder.
     *
     * @param path path.
     * @return is succeed.
     * @see #deleteFolder(File)
     */
    public static boolean delFileOrFolder(String path) {
        return delFileOrFolder(new File(path));
    }


    /**
     * Delete file or folder.
     *
     * @param file file.
     * @return is succeed.
     * @see #delFileOrFolder(String)
     */
    public static boolean delFileOrFolder(File file) {
        if (file == null || !file.exists()) {
            // do nothing
        } else if (file.isFile())
            file.delete();
        else if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null)
                for (File sonFile : files)
                    delFileOrFolder(sonFile);
            file.delete();
        }
        return true;
    }

    /**
     * 获取安装在用户手机上的sdcard/下的chianandroid目录
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
    }

    /**
     * 获取应用的路径，如果没有SD卡，则返回data/data目录下的应用目录，如果有，则返回SD卡上的应用目录
     */
    public static String getAppFilesDir() {
        if (isSDcardExist()) {
            return getSDCardPath() + APP_CACHE_DIR_NAME + "/";
        } else {
            return getAppFilesDirByData();
        }
    }


    /**
     * 获取课本的目录
     */
    public static String getTextBookFilesDir() {
        return getAppFilesDir() + TEXT_BOOK + "/";
    }


    /**
     * 获取课本图片目录
     */
    public static String getTextBookIconFilesDir() {
        return getAppFilesDir() + TEXT_BOOK_ICON + "/";
    }

    public static String getProbationBookFilesDir() {
        return getAppFilesDir() + TEXT_BOOK_PROBATION + "/";
    }


    /**
     * 获取文件data/data目录
     */
    public static String getAppFilesDirByData() {
        return UIUtils.getContext().getFilesDir().getAbsolutePath() + "/";
    }

    /**
     * 判断字符串是否为空
     */
    private static boolean isEmpty(String value) {
        return !(value != null && !"".equalsIgnoreCase(value.trim()) && !"null".equalsIgnoreCase(value.trim()));
    }

    public static boolean makeParentsDir(String filePath) {
        if (isEmpty(filePath)) {
            return false;
        }
        File targetFile = new File(filePath);
        File targetParent = targetFile.getParentFile();
        if (!targetParent.exists()) {
            makeParentsDir(targetParent.getAbsolutePath());
            targetParent.mkdir();
        } else {
            if (targetParent.isDirectory()) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }


    public static String getLogFilesDir() {
        if (isSDcardExist()) {
            return getSDCardPath() + "rongjie" + "/" + "edt" + "/" + "log" + "/";
        } else {
            return getAppFilesDirByData() + "rongjie" + "/" + "edt" + "/" + "log" + "/";
        }
    }

    /**
     * 根据值读取
     */
    public static String readProperties(String filePath, String key, String defaultValue) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(filePath)) {
            return null;
        }
        String value = null;
        FileInputStream fis = null;
        File f = new File(filePath);
        try {
            if (!f.exists() || !f.isFile()) {
                f.createNewFile();
            }
            fis = new FileInputStream(f);
            Properties p = new Properties();
            p.load(fis);
            value = p.getProperty(key, defaultValue);
        } catch (IOException e) {
//            LogUtils.e(e);
        } finally {
            IOUtils.close(fis);
        }
        return value;
    }

    /**
     * 删除文件
     *
     * @param path 被删除的文件路径
     * @return 是否删除成功
     */
    public static boolean deleteFile(String path) {
        return new File(path).delete();
    }


    /**
     * 获取到目录下面文件的大小。包含了子目录。
     */
    public static long getDirLength(File f) throws IOException {
        if (null == f) {
            return -1;
        }
        if (f.isFile()) return f.length();
        long size = 0;
        File[] files = f.listFiles();
        if (files != null) {
            for (File file : files) {
                long length = 0;
                if (file.isFile()) {
                    length = file.length();
                } else {
                    length = getDirLength(file);
                }
                size += length;
            }
        }
        return size;
    }

    private static final String pdf = ".pdf";
    private static final String epub = ".epub";
    public static final int bookDir = 1;
    public static final int bookProbation = 2;


    /**
     * 获取图书全路径 包含后缀
     *
     * @param booId
     * @param bookType bookDir
     *                 bookProbation
     * @return
     */
    public static String getBookFileName(int booId, int bookType) {
        String bookName = "";
        switch (bookType) {
            case bookDir:
                if (FileUtils.exists(FileUtils.getTextBookFilesDir() + booId + pdf)) {
                    bookName = FileUtils.getTextBookFilesDir() + booId + pdf;
                } else if (FileUtils.exists(FileUtils.getTextBookFilesDir() + booId + epub)) {
                    bookName = FileUtils.getTextBookFilesDir() + booId + epub;
                }
                break;
            case bookProbation:
                if (FileUtils.exists(FileUtils.getProbationBookFilesDir() + booId + pdf)) {
                    bookName = FileUtils.getProbationBookFilesDir() + booId + pdf;
                } else if (FileUtils.exists(FileUtils.getProbationBookFilesDir() + booId + epub)) {
                    bookName = FileUtils.getProbationBookFilesDir() + booId + epub;
                }
                break;
        }
        return bookName;
    }

    /**
     * 获取下载文件后缀
     *
     * @param filePath
     * @return .pdf
     */
    public static String getDownBookSuffix(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return "";
        }
        return filePath.substring(filePath.lastIndexOf("."));
    }
}
