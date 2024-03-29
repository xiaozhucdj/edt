package com.yougy.common.utils;

import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.zip.ZipException;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
/**
 * Created by Administrator on 2016/7/13.
 * 文件操作
 */
public class FileUtils {
    public static final String APP_CACHE_DIR_NAME = "student";
    public static final String TEXT_BOOK = "text_book";
    public static final String TEXT_BOOK_ICON = "text_book_icon";
    public static final String TEXT_BOOK_PROBATION = "text_book_probation";
    public static final String MEDIA_FILES = "voice";
    public static String mAnsweringFileAbsPath = FileUtils.getAppFilesDir();

    private static final String MEDIA_JSON = "json";
    private static final String MEDIA_MP3 = "mp3";

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

    public static File ifNotExistCreateFile(String filePath) throws IOException {
        if (TextUtils.isEmpty(filePath)) {
            throw new IOException("filePath is empty");
        }
        String saveFileDir = filePath.substring(0, filePath.lastIndexOf("/"));
        if (TextUtils.isEmpty(saveFileDir) || !createDirs(saveFileDir)) {
            throw new IOException("create saveFileDir fail");
        }
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
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
            return targetParent.isDirectory();
        }
        return true;
    }


    public static String getLogFilesDir() {
        if (isSDcardExist()) {
            return getSDCardPath() + APP_CACHE_DIR_NAME + "/" + "error" + "/" + "log" + "/";
        } else {
            return getAppFilesDirByData() + APP_CACHE_DIR_NAME + "/" + "error" + "/" + "log" + "/";
        }
    }

    /**
     * 根据值读取
     */
    public static void readProperties(String filePath, String value) {
        if (StringUtils.isEmpty(filePath)) {
            return;
        }
        FileInputStream inputStream = null ;
        File f = new File(filePath);
        try {
            if (!f.exists() || !f.isFile()) {
                f.createNewFile();
            }
            inputStream = new FileInputStream(f);
            byte temp[] = new byte[1024];
            StringBuilder sb = new StringBuilder("");
            int len = 0;
            while ((len = inputStream.read(temp)) > 0){
                sb.append(new String(temp, 0, len));
            }
            LogUtils.e("msg", "readSaveFile: \n" + sb.toString());
            inputStream.close();
        } catch (IOException e) {
//            LogUtils.e(e);
        } finally {
            IOUtils.close(inputStream);
        }
    }


    /**
     * 根据值读取
     */
    public static void writeProperties(String filePath, String value) {
        FileOutputStream fos = null;
        File f = new File(filePath);
        try {
            if (!f.exists() || !f.isFile()) {
                f.createNewFile();
            }
            fos = new FileOutputStream(f);
            fos.write(value.getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
//            LogUtils.e(e);
        } finally {
            IOUtils.close(fos);
        }
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

    public static final String pdf = ".pdf";
    public static final String epub = ".epub";
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


    public static String getMediaFilesDir() {
        return getAppFilesDir() + MEDIA_FILES + "/";
    }

    public static String getMediaJsonPath() {
        return getMediaFilesDir() + MEDIA_JSON + "/";
    }

    public static String getMediaMp3Path() {
        return getMediaFilesDir() + MEDIA_MP3 + "/";
    }





    public static boolean unZip1(String strZipPath, String strOutputFolderPath) {
        ZipFile zf = null;
        try {
            zf = new ZipFile(strZipPath, "GBK");
            for (@SuppressWarnings("unchecked")
                 Enumeration entries = zf.getEntries();
                 entries.hasMoreElements(); ) {
                final ZipEntry entry = (ZipEntry) entries.nextElement();
                final File file = new File(strOutputFolderPath + File.separator + entry.getName());
                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(file);
                    } catch (Exception e) {
                        File fileParent = file.getParentFile();
                        if (!fileParent.exists()) {
                            if (fileParent.mkdirs()) {
                                fos = new FileOutputStream(file);
                            }
                        }
                    }

                    if (fos != null) {
                        InputStream is = null;
                        try {
                            is = zf.getInputStream(entry);
                            int nReadLength = -1;
                            final byte buffer[] = new byte[4096];
                            while ((nReadLength = is.read(buffer)) != -1) {
                                fos.write(buffer, 0, nReadLength);
                            }
                        } finally {
                            fos.close();
                            is.close();
                        }
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (zf != null) {
                try {
                    zf.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 解压缩zip文件
     * @param srcPath  需要被解压的文件路径
     * @param destPath 被解压后的文件存放的目录路径
     * @return 是否解压成功
     */
    public static boolean unZip2(String srcPath, String destPath) {
        return unZip2(new File(srcPath), new File(destPath));
    }

    /**
     * 解压缩zip文件
     * @param srcFile  需要被解压的文件
     * @param destFile 被解压后的文件存放的目录
     * @return 是否解压成功
     */
    @SuppressWarnings("resource")
    public static boolean unZip2(File srcFile, File destFile) {
        boolean res = false;
        if (null == srcFile || null == destFile || !srcFile.exists() || !srcFile.canRead()) {
            return false;
        }
        FileOutputStream fos = null;
        InputStream is = null;
        try {
            ZipFile  zipFile= new ZipFile(srcFile, "GBK");
            Enumeration<? extends java.util.zip.ZipEntry> entries = zipFile.getEntries();
            byte[] buffer = new byte[1024 * 8];
            int len = 0;
            while (entries.hasMoreElements()) {
                final ZipEntry zipEntry = (ZipEntry) entries.nextElement();
                String entryName = zipEntry.getName();
                String unZipFileName = destFile.getAbsolutePath() + File.separator + entryName;
                if (zipEntry.isDirectory()) { // 没有执行此代码
                    new File(unZipFileName).mkdirs();
                } else { // 总是执行该代码.因为压缩的时候是对每个文件进行压缩的.
                    new File(unZipFileName).getParentFile().mkdirs();
                }
                File unZipedFile = new File(unZipFileName);
                if (unZipedFile.isDirectory()) {
                    File[] files = unZipedFile.listFiles();
                    for (File file : files) {
                        fos = new FileOutputStream(file);
                        is = zipFile.getInputStream(zipEntry);
                        while ((len = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }
                    }
                } else {
                    fos = new FileOutputStream(unZipedFile);
                    is = zipFile.getInputStream(zipEntry);
                    while ((len = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }
                }
            }
            fos.flush();
            res = true;
        } catch (ZipException e) {
            LogUtils.e(e);
        } catch (IOException e) {
            LogUtils.e(e);
        } finally {
            IOUtils.close(fos);
            IOUtils.close(is);
        }
        return res;
    }


    /**
     * 解压缩zip文件
     * @param srcPath  需要被解压的文件路径
     * @param destPath 被解压后的文件存放的目录路径
     * @return 是否解压成功
     */
    public static boolean unZip3(String srcPath, String destPath) {
        return unZip3(new File(srcPath), new File(destPath));
    }

    /**
     * 解压缩zip文件
     * @param srcFile  需要被解压的文件
     * @param destFile 被解压后的文件存放的目录
     * @return 是否解压成功
     */
    @SuppressWarnings("resource")
    public static boolean unZip3(File srcFile, File destFile) {
        boolean res = false;
        if (null == srcFile || null == destFile || !srcFile.exists() || !srcFile.canRead()) {
            return false;
        }
        FileOutputStream fos = null;
        InputStream is = null;
        try {
            java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(srcFile);
            Enumeration<? extends java.util.zip.ZipEntry> entries = zipFile.entries();
            byte[] buffer = new byte[1024 * 8];
            int len = 0;
            while (entries.hasMoreElements()) {
                java.util.zip.ZipEntry zipEntry = entries.nextElement();
                String entryName = zipEntry.getName();
                String unZipFileName = destFile.getAbsolutePath() + File.separator + entryName;
                if (zipEntry.isDirectory()) { // 没有执行此代码
                    new File(unZipFileName).mkdirs();
                } else { // 总是执行该代码.因为压缩的时候是对每个文件进行压缩的.
                    new File(unZipFileName).getParentFile().mkdirs();
                }
                File unZipedFile = new File(unZipFileName);
                if (unZipedFile.isDirectory()) {
                    File[] files = unZipedFile.listFiles();
                    for (File file : files) {
                        fos = new FileOutputStream(file);
                        is = zipFile.getInputStream(zipEntry);
                        while ((len = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }
                    }
                } else {
                    fos = new FileOutputStream(unZipedFile);
                    is = zipFile.getInputStream(zipEntry);
                    while ((len = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }
                }
            }
            fos.flush();
            res = true;
        } catch (ZipException e) {
            LogUtils.e(e);
        } catch (IOException e) {
            LogUtils.e(e);
        } finally {
            IOUtils.close(fos);
            IOUtils.close(is);
        }
        return res;
    }

    public static String readerFile(String filePath) {
        String result = "";
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(filePath), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            br.close();
            isr.close();
            result = builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
