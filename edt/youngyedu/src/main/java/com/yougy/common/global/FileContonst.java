package com.yougy.common.global;

import com.yougy.common.utils.SystemUtils;

/**
 * Created by Administrator on 2016/8/9.
 */
public class FileContonst {

    ////////////////////////////////书城搜索//////////////////////////////////////
    /**
     * 搜索书城 图书 key
     */
    public final static String FILE_SEARCH_TEXT_KEY = "FILE_SEARCH_TEXT_KEY";

    public final static String USER_ID = "userId";

    ///////////////////////////图书笔记点击跳转需要传递的参数/////////////////////////////////////////
    /**
     * 笔记创建者
     */
    public final static String NOTE_CREATOR = "NOTE_CREATOR";
    /**
     * 图书id
     */
    public final static String BOOK_ID = "bookId";

    /***
     * 图书对应的笔记ID
     */
    public final static String NOTE_ID = "noteId";

    /***
     * 作业id
     */
    public final static String HOME_WROK_ID = "homewrokId";
    /***
     * 关联分类编码
     */
    public final static String CATEGORY_ID = "CATEGORY_ID";

    /***
     * 笔记标题
     */
    public final static String NOTE_TITLE = "noteTitle";

    /***
     * 笔记样式
     */
    public final static String NOTE_Style = "noteStyleStyle";

    public final static String NOTE_SUBJECT_ID = "noteFitSubjectId";


    public final static String NOTE_CONTENT = "noteContent";
    /***
     * 笔记样式
     */
    public final static String NOTE_SUBJECT_NAME = "noteFitSubjectName";

    public final static String NOTE_OBJECT = "NOTE_INFO";

    public final static String NOTE_BOOK_ID = "noteFitBookId";

    ///////////////////////////作业 笔记 课本 跳转//////////////////////////////////////////
    /**
     * 跳转key
     */
    public final static String JUMP_FRAGMENT = "JUMP_FRAGMENT";

    /**
     * 跳转课本
     */
    public final static String JUMP_TEXT_BOOK = "JUMP_TEXT_BOOK";

    /**
     * 跳转笔记
     */
    public final static String JUMP_NOTE = "JUMP_NOTE";

    /**
     * 跳转作业
     */
    public final static String JUMP_HOMEWROK = "JUMP_HOMEWROK";

    /**
     * 判断是否添加了笔记需要在 当前笔记设置状态，全部笔记使用判断
     */
    public static boolean globeIsAdd = false;

    /**
     * 笔记标记
     */
    public final static String NOTE_MARK = "noteMark";

    public final static boolean OPEN_ONYX_READER = true;


    public final static int PAGE_COUNTS = 9;

    public final static int PAGE_LINES = 3;

    public static final String DOWN_LOAD_BOOKS_KEY = "down_load_books_key";
    public final static int SEARCH_PAGE_COUNTS = 20;

    public final static int SMALL_PAGE_LINES = 5;

    public final static int SMALL_PAGE_COUNTS = 15;

    public final static int MIN_ALL_ITEM_SUBJECT = 200;


    public final static String LOAD_APP_STUDENT = "1";

    public final static String LOAD_APP_RESET = "0";

    public final static String IS_REFERENCE_BOOK = "is_reference_book";

    public final static String IS_OPEN_VOICE = "is_open_voice";

    public final static String IS_OPEN_SELF_ADAPTER = "is_open_self_adapter";

    public static int withS = 150;
    public static int withL = 200;

    public static int heightS = 200;

    public static int heightL = 266;

    public static final String DEVICE_TYPE_EDU = "EDU";
    public static final String DEVICE_TYPE_PL107 = "PL107";
    public static final String DEVICE_TYPE_N96 = "N96";
    public static String serverDevice = "PL107";

    /***
     * 消息锁屏
     */
    public final static String LOCK_SCREEN = "lock_screen";

    public final static String NO_LOCK_SCREEN = "unlocking_screen";

    static {
        if (SystemUtils.getDeviceModel().contains(DEVICE_TYPE_EDU)) {
            withS = 150;
            heightS = 200;
            withL = 200;
            heightL = 266;
            serverDevice = DEVICE_TYPE_EDU;
        } else if (SystemUtils.getDeviceModel().contains(DEVICE_TYPE_PL107)) {
            withS = 150;
            heightS = 200;
            withL = 200;
            heightL = 266;
            serverDevice = DEVICE_TYPE_PL107;
        } else if (SystemUtils.getDeviceModel().contains(DEVICE_TYPE_N96)) {
            withS = 150;
            heightS = 200;
            withL = 200;
            heightL = 266;
            serverDevice = DEVICE_TYPE_N96;
        }
    }

    public static final String  BOOK_STATU_SCODE = "BA01,BA02,BA03,BA04,BA11,BA12";
}
