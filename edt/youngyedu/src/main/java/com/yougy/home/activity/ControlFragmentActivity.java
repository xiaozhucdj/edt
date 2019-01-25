package com.yougy.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import com.yougy.common.activity.BaseActivity;
import com.yougy.common.fragment.BFragment;
import com.yougy.common.global.FileContonst;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.home.bean.NoteInfo;
import com.yougy.home.fragment.showFragment.BaseFragment;
import com.yougy.home.fragment.showFragment.ExerciseBookFragment;
import com.yougy.home.fragment.showFragment.HandleOnyxReaderFragment;
import com.yougy.home.fragment.showFragment.NoteBookFragment;
import com.yougy.home.fragment.showFragment.TextBookFragment;
import com.yougy.ui.activity.R;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Administrator on 2016/8/24.
 * <p/>
 * 控制 笔记 绘制 课本 绘制 图形的 操作Activity
 * <p>
 * <p>
 * <p>
 * 参数规则：
 * <p>
 * 1.当noteOwner参数与当前用户编码一致时，此笔记才是笔记原作。
 * 当前，需要使用不到  2016.11/9
 * (..........................................................)
 * 2.当noteCreator参数与当前用户编码一致时，才允许删除此笔记。
 * noteCreator == userid 表示自己创建的 ，可以删除
 * (..........................................................)
 * 3.bookId，categoryId参数都存在时，该笔记是与固定图书绑定的独立笔记。
 * 按照学科 有服务器创建的笔记在笔记列表可以获取（非在书上书写的笔记）
 * ((..........................................................)
 * 4.仅有bookId参数，该笔记是与固定图书绑定的内部笔记。
 * 在“书”上所使用的笔记
 * ((..........................................................)
 * 5.仅有categoryId参数，该笔记是与固定科目绑定的独立笔记。
 * categoryId代表学科 ，笔记未绑定图书
 * (..........................................................)
 * 6.termIndex参数为0,将追加用户本学期笔记。目前仅支持参数0。
 * 传-1 那么服务器将图书存放在全部课本里
 * (..........................................................)
 */
public class ControlFragmentActivity extends BaseActivity implements BaseFragment.OnSwitcherListener {
    ////////////////////////////////////三个跳转的fragment 公共使用的参数/////////////////////////////////////////
    /**
     * 笔记创建者
     */
    public int mNoteCreator;
    /**
     * 图书对应的笔记ID
     */
    public int mNoteId = -1;
    /**
     * 图书id
     */
    public int mBookId = -1;
    /**
     * 关联分类编码
     */
    public int mCategoryId;
    /**
     * 判断跳转到哪个fragment
     */
    private String mJump;

    /**
     * 笔记标题
     */
    public String mNotetitle;

    /**
     * 笔记学科
     */
    public String mNoteSubject;

    public int subjectId;
    /**
     * 笔记类型
     */
    public int mNoteStyle;

    private NoteInfo info;

    private Map<String, String> params = new HashMap<>();
    /**
     * 笔记内部ID
     */
    public long mNoteMark = -1;
    public int mHomewrokId;

    public boolean mIsReferenceBook;
    private boolean mIsOpenSelfAdapter;
    private boolean mIsOpenVoice;


    @Override
    protected void init() {
        mIsCheckStartNet = false;
        if (getIntent() != null && getIntent().getExtras() != null) {
            LogUtils.i("yuanye ..init");
            Bundle bundle = getIntent().getExtras();
            mNoteCreator = bundle.getInt(FileContonst.NOTE_CREATOR, -1);
            mBookId = bundle.getInt(FileContonst.BOOK_ID, -1);
            mNoteId = bundle.getInt(FileContonst.NOTE_ID, -1);
            mNoteMark = bundle.getLong(FileContonst.NOTE_MARK, -1);
            mCategoryId = bundle.getInt(FileContonst.CATEGORY_ID, -1);
            mJump = bundle.getString(FileContonst.JUMP_FRAGMENT, "");
            LogUtils.i("yuanye ....mJump  test1 ==" + mJump);
            mNoteStyle = bundle.getInt(FileContonst.NOTE_Style, 0);
            mNoteSubject = bundle.getString(FileContonst.NOTE_SUBJECT_NAME, "");
            mNotetitle = bundle.getString(FileContonst.NOTE_TITLE, "");
            subjectId = bundle.getInt(FileContonst.NOTE_SUBJECT_ID);
            info = (NoteInfo) bundle.getSerializable(FileContonst.NOTE_OBJECT);

            params.put(FileContonst.USER_ID, SpUtils.getAccountId() + "");
            params.put(FileContonst.NOTE_ID, mNoteId + "");
            params.put(FileContonst.NOTE_Style, mNoteStyle + "");
            params.put(FileContonst.NOTE_TITLE, mNotetitle);
            params.put(FileContonst.NOTE_SUBJECT_ID, subjectId + "");
            params.put(FileContonst.NOTE_SUBJECT_NAME, mNoteSubject);
            params.put(FileContonst.NOTE_BOOK_ID, mBookId + "");
            mHomewrokId = bundle.getInt(FileContonst.HOME_WROK_ID, -1);
            mIsReferenceBook = bundle.getBoolean(FileContonst.IS_REFERENCE_BOOK);
            mIsOpenSelfAdapter = bundle.getBoolean(FileContonst.IS_OPEN_SELF_ADAPTER);
            mIsOpenVoice = bundle.getBoolean(FileContonst.IS_OPEN_VOICE);
        }
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initLayout() {
    }

    @Override
    protected void loadData() {
        jump();
    }

    @Override
    protected void refreshView() {
    }

    /**
     * 跳转至课本界面
     */
    private BFragment mFragment;
    private TextBookFragment mTextBookFragment;
    private HandleOnyxReaderFragment mHandleOnyxReader;
    private ExerciseBookFragment mExerciseBookFragment;
    /**
     * 跳转至笔记本界面
     */
    private NoteBookFragment mNoteBookFragment;

    private void toTextBookFragment() {
        //跳转判断条件 根据服务器接口定义
        LogUtils.i("mBookId===" + mBookId);
        LogUtils.i("mNoteCreator===" + mNoteCreator);
        LogUtils.i("mCategoryId===" + mCategoryId);

        if (mBookId > 0 /***&& mNoteCreator != Integer.parseInt(SpUtils.getAccountId()) && mCategoryId > 0*/) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            if (FileContonst.OPEN_ONYX_READER) {
                if (null == mHandleOnyxReader) {
                    mHandleOnyxReader = new HandleOnyxReaderFragment();
                    Bundle args = new Bundle();

                    args.putBoolean(FileContonst.IS_REFERENCE_BOOK, mIsReferenceBook);
                    args.putBoolean(FileContonst.IS_OPEN_SELF_ADAPTER, mIsOpenSelfAdapter);
                    args.putBoolean(FileContonst.IS_OPEN_VOICE, mIsOpenVoice);

                    mHandleOnyxReader.setArguments(args);

                    if (params.size() > 0) {
                        mHandleOnyxReader.setParams(params);
                    }
                    mHandleOnyxReader.setOnSwitcherListener(this);
                    mHandleOnyxReader.setActivity(this);
                    ft.add(R.id.container, mHandleOnyxReader);
                } else {
                    ft.show(mHandleOnyxReader);
                }
                if (mFragment != null && !(mFragment instanceof HandleOnyxReaderFragment)) {
                    ft.hide(mFragment);
                }
                mFragment = mHandleOnyxReader;

            } else {
                if (null == mTextBookFragment) {
                    mTextBookFragment = new TextBookFragment();
                    if (params.size() > 0) {
                        mTextBookFragment.setParams(params);
                    }
                    mTextBookFragment.setOnSwitcherListener(this);
                    mTextBookFragment.setActivity(this);
                    ft.add(R.id.container, mTextBookFragment);
                } else {
                    ft.show(mTextBookFragment);
                }
                if (mFragment != null && !(mFragment instanceof TextBookFragment)) {
                    ft.hide(mFragment);
                }
                mFragment = mTextBookFragment;
            }

            ft.commit();
        }
    }

    private void toNoteBookFragment() {
        if (mNoteId > 0 || mNoteMark > 0) {
            File file;
            if (mNoteMark > 0) {
                file = new File(Environment.getExternalStorageDirectory() + "/android/data/" + mNoteMark);
            } else {
                file = new File(Environment.getExternalStorageDirectory() + "/android/data/" + mNoteId);
            }

            if (!file.exists()) {
                file.mkdir();
            }
            LogUtils.e("MainActivity", "file name is : " + mNoteId);

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            if (null == mNoteBookFragment) {
                mNoteBookFragment = new NoteBookFragment();
                if (params.size() > 0) {
                    mNoteBookFragment.setParams(params);
                }
                mNoteBookFragment.setNoteInfo(info);
                mNoteBookFragment.setActivity(this);
                mNoteBookFragment.setOnSwitcherListener(this);
                ft.add(R.id.container, mNoteBookFragment);
            } else {
                ft.show(mNoteBookFragment);
            }
            if (mFragment != null && !(mFragment instanceof NoteBookFragment)) {
                ft.hide(mFragment);
            }

//          if (mExerciseBookFragment != null) {
//                ft.hide(mExerciseBookFragment);
//            }
            mFragment = mNoteBookFragment;
            ft.commit();
        }
    }

    private void toExerciseBookFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (null == mExerciseBookFragment) {
            mExerciseBookFragment = new ExerciseBookFragment();
            mExerciseBookFragment.setActivity(this);
            ft.add(R.id.container, mExerciseBookFragment);
        } else {
            ft.show(mExerciseBookFragment);
        }
        if (mFragment != null && !(mFragment instanceof ExerciseBookFragment)) {
            ft.hide(mFragment);
        }
        mFragment = mExerciseBookFragment;
        ft.commit();
    }

    @Override
    public void switch2TextBookFragment() {
        toTextBookFragment();
    }

    @Override
    public void switch2NoteBookFragment() {
        toNoteBookFragment();
    }

    @Override
    public void switch2ExerciseFragment() {
        toExerciseBookFragment();
    }

    @Override
    public void onBackPressed() {
        LogUtils.e(tag, "onBackPressed");
        if (mTextBookFragment != null) {
            mTextBookFragment.leaveScribbleMode(false);
        }
        if (mHandleOnyxReader != null) {
            mHandleOnyxReader.leaveScribbleMode(false);
        }
        if (mNoteBookFragment != null) {
            mNoteBookFragment.leaveScribbleMode(false);
        }

        super.onBackPressed();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtils.i("yuanye ..mJump==" + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mFragment != null) {
                if (mFragment instanceof HandleOnyxReaderFragment) {
                    mHandleOnyxReader.onBackListener();
                } else if (mFragment instanceof NoteBookFragment) {
                    mNoteBookFragment.onBackListener();
                } else if (mFragment instanceof ExerciseBookFragment) {
                    mExerciseBookFragment.onBackListener();
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void jump() {
        LogUtils.i("yuanye ..mJump==" + mJump);
        switch (mJump) {
            case FileContonst.JUMP_TEXT_BOOK:
                toTextBookFragment();
                break;
            case FileContonst.JUMP_NOTE:
                toNoteBookFragment();
                break;
            case FileContonst.JUMP_HOMEWROK:
//               toExerciseBookFragment();
//                Bundle extras = new Bundle();
//                //图书ID
//                extras.putInt(FileContonst.BOOK_ID, this.mBookId);
//                //笔记ID
//                extras.putInt(FileContonst.NOTE_ID, this.mNoteId);
//                //作业ID
//                extras.putInt(FileContonst.HOME_WROK_ID, this.mHomewrokId);
//                //笔记名字
//                extras.putString(FileContonst.NOTE_TITLE, this.mNotetitle);
//                //笔记样式
//                extras.putInt(FileContonst.NOTE_Style, this.mNoteStyle);
//
//                Intent intent = new Intent(this, MainActivityScreen.class);
//                intent.putExtras(extras);
//                startActivity(intent);
//                finish();
                toExerciseBookFragment();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        LogUtils.i("yuanye ..onNewIntent");
        init();
        jump();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mNoteBookFragment = null;
        mTextBookFragment = null;
        mHandleOnyxReader = null;
        mExerciseBookFragment = null;
        Runtime.getRuntime().gc();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            LogUtils.e(tag, "onPause......");
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (mNoteBookFragment != null) {
                transaction.remove(mNoteBookFragment);
            }
            if (mTextBookFragment != null) {
                transaction.remove(mTextBookFragment);
            }

            if (mHandleOnyxReader != null) {
                transaction.remove(mHandleOnyxReader);
            }
            if (mExerciseBookFragment != null) {
                transaction.remove(mExerciseBookFragment);
            }
            transaction.commit();
        }
    }
}
