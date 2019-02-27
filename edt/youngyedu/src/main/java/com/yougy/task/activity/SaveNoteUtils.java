package com.yougy.task.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.yougy.common.utils.DataCacheUtils;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.StringUtils;
import com.yougy.view.NoteBookView;
import com.yougy.view.NoteBookView2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SaveNoteUtils {

    private String TASK_FILE_DIR ;

    private static Context mContext;
    private SaveNoteUtils (){
        TASK_FILE_DIR = FileUtils.getAppFilesDir() + "task";
    }

    public String getTaskFileDir () {
        return TASK_FILE_DIR;
    }
    public static SaveNoteUtils getInstance (Context context) {
        mContext = context;
        return Inner.instance;
    }
    public static class Inner {
        public static SaveNoteUtils instance = new SaveNoteUtils();
    }

    /**
     * 保存写的内容到本地
     * @param noteBookView2
     * @param cacheKey
     * @param bitmapKey
     */
    public void saveNoteViewData (NoteBookView2 noteBookView2, String fileDir, String cacheKey, String bitmapKey, String taskID
                , int stageId) {

        if (!NetUtils.isNetConnected()) {
            LogUtils.w("NoteView Net is not connected, saveNoteViewData return.");
            return;
        }
        if (noteBookView2 == null) {
            throw new NullPointerException("NoteView is NullPoint Exception, please init.");
        }
        if (StringUtils.isEmpty(cacheKey)) {
            throw new NullPointerException("NoteView cacheKey is NullPoint Exception, please init.");
        }
        LogUtils.d("NoteView saveNoteViewData cacheKey = " + cacheKey + "   bitmapKey : " + bitmapKey);
        DataCacheUtils.putObject(mContext,  cacheKey, noteBookView2.bitmap2Bytes());
//        DataCacheUtils.putObject(mContext, bitmapKey, pathLists);

        Bitmap bitmap = noteBookView2.getBitmap();
        String path = saveBitmapToFile (bitmap ,bitmapKey, fileDir + "/" + taskID + "/" + stageId);
        LogUtils.d("TaskTest NoteView Save Path = " + path);
        /*if (isSaveBitmap && !StringUtils.isEmpty(bitmapKey) && questionIndex < pathLists.size()) {
            if (isMultiPage) {
                if (pathLists.get(questionIndex) != null && !pathLists.get(questionIndex).contains(path)) {
                    path = pathLists.get(questionIndex) + "#" + path;
                }
            }
            pathLists.set(questionIndex, path);
        }*/
    }

    public void saveNoteViewData (NoteBookView noteBookView2, String fileDir, String cacheKey, String bitmapKey, String taskID
            , int stageId) {

        if (!NetUtils.isNetConnected()) {
            LogUtils.w("NoteView Net is not connected, saveNoteViewData return.");
            return;
        }
        if (noteBookView2 == null) {
            throw new NullPointerException("NoteView is NullPoint Exception, please init.");
        }
        if (StringUtils.isEmpty(cacheKey)) {
            throw new NullPointerException("NoteView cacheKey is NullPoint Exception, please init.");
        }
        LogUtils.d("NoteView saveNoteViewData cacheKey = " + cacheKey + "   bitmapKey : " + bitmapKey);
        DataCacheUtils.putObject(mContext,  cacheKey, noteBookView2.bitmap2Bytes());
//        DataCacheUtils.putObject(mContext, bitmapKey, pathLists);

        Bitmap bitmap = noteBookView2.getBitmap();
        String path = saveBitmapToFile (bitmap ,bitmapKey, fileDir + "/" + taskID + "/" + stageId);
        LogUtils.d("NoteView Save Path = " + path);
        /*if (isSaveBitmap && !StringUtils.isEmpty(bitmapKey) && questionIndex < pathLists.size()) {
            if (isMultiPage) {
                if (pathLists.get(questionIndex) != null && !pathLists.get(questionIndex).contains(path)) {
                    path = pathLists.get(questionIndex) + "#" + path;
                }
            }
            pathLists.set(questionIndex, path);
        }*/
    }


    /**
     * 保存bitmap到本地  返回其路径
     * @param bitmap
     * @param bitName
     * @return
     */
    public String saveBitmapToFile(Bitmap bitmap, String bitName, String fileDir) {
        FileUtils.createDirs(fileDir);
        File f = new File(fileDir, URLEncoder.encode(bitName + ".png"));
        if (f.exists()) f.delete();
        FileOutputStream fOut = null;
        try {
            f.createNewFile();
            fOut = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f.getAbsolutePath();
    }

    public void resetNoteView (NoteBookView2 noteBookView2, String cacheKey, String bitmapKey, String fileDir) {
        if (noteBookView2 == null) {
            throw new NullPointerException("NoteView is NullPoint Exception, please init.");
        }
        if (StringUtils.isEmpty(cacheKey)) {
            throw new NullPointerException("NoteView cacheKey is NullPoint Exception, please init.");
        }
        LogUtils.d("NoteView resetNoteView cacheKey = " + cacheKey);
        byte[] buffer = (byte[]) DataCacheUtils.getObject(mContext, cacheKey);
        if (buffer != null && buffer.length > 0) {
            LogUtils.d("NoteView resetNoteView cacheKey = " + cacheKey);
            noteBookView2.drawBitmap(BitmapFactory.decodeByteArray(buffer, 0, buffer.length));
            noteBookView2.setVisibility(View.VISIBLE);
        } else {
            LogUtils.d("NoteView buffer null or length is zero, check local file.");
            File f = new File(fileDir, URLEncoder.encode(bitmapKey + ".png"));
            if (f.exists()) {
                loadLocalNoteViewBitmap(noteBookView2, f);
            }
        }
    }

    public boolean isExists (String fileDir, String fileName) {
        File f = new File(fileDir, URLEncoder.encode(fileName));
        if (f.exists()) {
            return true;
        }
        return false;
    }

    public void resetNoteView (NoteBookView noteBookView2, String cacheKey, String bitmapKey, String fileDir) {
        if (noteBookView2 == null) {
            throw new NullPointerException("NoteView is NullPoint Exception, please init.");
        }
        if (StringUtils.isEmpty(cacheKey)) {
            throw new NullPointerException("NoteView cacheKey is NullPoint Exception, please init.");
        }
        LogUtils.d("NoteView resetNoteView cacheKey = " + cacheKey);
        byte[] buffer = (byte[]) DataCacheUtils.getObject(mContext, cacheKey);
        if (buffer != null && buffer.length > 0) {
            LogUtils.d("NoteView resetNoteView cacheKey = " + cacheKey);
            noteBookView2.drawBitmap(BitmapFactory.decodeByteArray(buffer, 0, buffer.length));
            noteBookView2.setVisibility(View.VISIBLE);
        } else {
            LogUtils.d("NoteView buffer null or length is zero, check local file.");
            File f = new File(fileDir, URLEncoder.encode(bitmapKey + ".png"));
            if (f.exists()) {
                loadLocalNoteViewBitmap(noteBookView2, f);
            }
        }
    }

    /**
     * 上传服务器成功后，删除缓存和文件
     * @param cacheKey
     * @param bitmapKey
     * @param fileDir
     * @return
     */
    public boolean deleteNoteViewCache (String cacheKey, String bitmapKey, String fileDir) {
        DataCacheUtils.reomve(mContext, cacheKey);
        File f = new File(fileDir, URLEncoder.encode(bitmapKey + ".png"));
        if (f.exists()) {
            return f.delete();
        }
        return false;
    }


    private synchronized void loadLocalNoteViewBitmap (NoteBookView2 noteBookView2, File file) {
        SimpleTarget<Bitmap> mSimpleTarget = new SimpleTarget<Bitmap>() {

            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                if (resource != null)
                    noteBookView2.drawBitmap(resource);
                else
                    LogUtils.d("NoteView resource is Null.");
            }
        };
        SimpleTarget<Bitmap> into = Glide.with(mContext)
                .load(file)
                .asBitmap()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mSimpleTarget);
    }

    private synchronized void loadLocalNoteViewBitmap (NoteBookView noteBookView2, File file) {
        SimpleTarget<Bitmap> mSimpleTarget = new SimpleTarget<Bitmap>() {

            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                if (resource != null)
                    noteBookView2.drawBitmap(resource);
                else
                    LogUtils.d("NoteView resource is Null.");
            }
        };
        SimpleTarget<Bitmap> into = Glide.with(mContext)
                .load(file)
                .asBitmap()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mSimpleTarget);
    }


}
