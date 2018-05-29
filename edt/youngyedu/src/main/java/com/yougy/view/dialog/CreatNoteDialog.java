package com.yougy.view.dialog;

import android.app.Service;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yougy.common.dialog.BaseDialog;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.StringUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.adapter.DialogNoteSubjectAdapter;
import com.yougy.home.adapter.OnRecyclerItemClickListener;
import com.yougy.home.bean.DialogNoteSubjectInfo;
import com.yougy.home.bean.NoteInfo;
import com.yougy.ui.activity.R;
import com.yougy.view.CustomGridLayoutManager;
import com.yougy.view.DividerGridItemDecoration;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016/7/29.
 * 添加笔记
 */
public class CreatNoteDialog extends BaseDialog implements View.OnClickListener {
    private ImageView mBtnCancel;
    private Button mBtnCreat;
    private EditText mEditName;
    private ImageButton mImgBtnWhite;
    private ImageButton mImgBtnBiJi;
    private ImageButton mImgBtnTianZi;
    private String mStrSubject;

    private NoteInfo.NoteStyleOption mOption = NoteInfo.NoteStyleOption.NOTE_TYPE_LINE;
    private RecyclerView mRecyclerView;
    private Context mContex;
    private DialogNoteSubjectAdapter mAdaptet;
    private List<DialogNoteSubjectInfo> mInfos = new ArrayList<>();
    private DialogNoteSubjectInfo mSelectInfo;
    private TextView mTvNoteType;
    private LinearLayout mLLNoteType;
    private TextView mTvTitle;
    private TextView mTvSubject;

    public CreatNoteDialog(Context context) {

        super(context);
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mContex = context;
    }

    public CreatNoteDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mContex = context;
    }

    @Override
    protected void init() {
        // 用户不可以点击外部消失对话框
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.dialog_fragment_notes);
        mBtnCancel = (ImageView) this.findViewById(R.id.img_close);
        mBtnCancel.setOnClickListener(this);

        mBtnCreat = (Button) this.findViewById(R.id.btn_creat);
        mBtnCreat.setOnClickListener(this);

        mEditName = (EditText) this.findViewById(R.id.et_note_name);
        mEditName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP){//进行自己操作
                    popupInputMethodWindow();
                    return true;
                }
                return false;
            }
        });


        mImgBtnWhite = (ImageButton) this.findViewById(R.id.img_btn_style_white);
        mImgBtnWhite.setOnClickListener(this);

        mImgBtnBiJi = (ImageButton) this.findViewById(R.id.img_btn_style_biji);
        mImgBtnBiJi.setOnClickListener(this);

        mImgBtnTianZi = (ImageButton) this.findViewById(R.id.img_btn_style_tianzi);
        mImgBtnTianZi.setOnClickListener(this);
        setNoteStyleStates();


        mTvNoteType = (TextView) this.findViewById(R.id.tv_noteType);
        mLLNoteType = (LinearLayout) this.findViewById(R.id.ll_noteType);

        mTvSubject = (TextView) this.findViewById(R.id.tv_subject);


        mTvTitle = (TextView) this.findViewById(R.id.tv_title);


        mRecyclerView = (RecyclerView) this.findViewById(R.id.recycler_subject);
        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(UIUtils.getContext()));
        CustomGridLayoutManager layout = new CustomGridLayoutManager(mContex, 4);
        layout.setScrollEnabled(true);
        mRecyclerView.setLayoutManager(layout);

        //TODO:暂时去掉无
        DialogNoteSubjectInfo fist = new DialogNoteSubjectInfo();
        fist.setSubject("无");
         mInfos.add(fist);

        if (!StringUtils.isEmpty(SpUtils.getSubjectNames())){
            String[] subs = SpUtils.getSubjectNames().split(",");
            for (String str : subs) {
                DialogNoteSubjectInfo info = new DialogNoteSubjectInfo();
                info.setSubject(str);
                mInfos.add(info);
            }
        }


        mAdaptet = new DialogNoteSubjectAdapter(mInfos);
        mRecyclerView.setAdapter(mAdaptet);
        mRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(mRecyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                noteSubjectItemClick(vh.getAdapterPosition());
            }
        });
        mAdaptet.notifyDataSetChanged();
    }

    private void noteSubjectItemClick(int position) {
        if (mSelectInfo != null) {
            mSelectInfo.setSelect(false);
        }
        mSelectInfo = mInfos.get(position);
        UIUtils.showToastSafe(mSelectInfo.getSubject());
        mStrSubject = mSelectInfo.getSubject();
        mSelectInfo.setSelect(true);
        mAdaptet.notifyDataSetChanged();
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.img_close:
                if (mClickListener != null) {
                    mClickListener.onCancelListener();
                }

                break;
            case R.id.btn_creat:
                if (mClickListener != null) {
                    mClickListener.onCreatListener();
                }
                break;

            case R.id.img_btn_style_white:
                mOption = NoteInfo.NoteStyleOption.NOTE_TYPE_BLANK;
                setNoteStyleStates();
                break;

            case R.id.img_btn_style_biji:
                mOption = NoteInfo.NoteStyleOption.NOTE_TYPE_LINE;
                setNoteStyleStates();
                break;
            case R.id.img_btn_style_tianzi:
                mOption = NoteInfo.NoteStyleOption.NOTE_TYPE_GRID;
                setNoteStyleStates();
                break;
        }
    }

    public NoteInfo.NoteStyleOption getNoteOptionStyle() {
        return mOption;
    }

    public void setClickListener(NoteFragmentDialogClickListener lstener) {
        mClickListener = lstener;
    }

    private NoteFragmentDialogClickListener mClickListener;

    public interface NoteFragmentDialogClickListener {

        void onCreatListener();

        void onCancelListener();
    }

    public String getEditName() {
        return mEditName.getText().toString().trim();
    }


    /**
     * 设置按钮状态
     */
    private void setNoteStyleStates() {
        mImgBtnWhite.setSelected(mOption == NoteInfo.NoteStyleOption.NOTE_TYPE_BLANK);
        mImgBtnBiJi.setSelected(mOption == NoteInfo.NoteStyleOption.NOTE_TYPE_LINE);
        mImgBtnTianZi.setSelected(mOption == NoteInfo.NoteStyleOption.NOTE_TYPE_GRID);
    }

    /***
     * 获取学科
     *
     * @return
     */
    public String getStrSubject() {
        return mStrSubject;
    }


    public void setNoteTypeGone() {
        mTvNoteType.setVisibility(View.GONE);
        mLLNoteType.setVisibility(View.GONE);
    }

    public void setBtnName(String name) {
        mBtnCreat.setText(name);
    }

    public void setTitle(String name) {
        mTvTitle.setText(name);
    }

    public void setEditNameEnable(boolean enable) {
        mEditName.setEnabled(enable);
    }

    public void setNoteName(String str) {
        mEditName.setText(str);
    }

    public void setRecyclerViewGone( ) {
        mRecyclerView.setVisibility(View.GONE);
        mTvSubject.setVisibility(View.GONE);

    }

    private InputMethodManager imm;

    private void popupInputMethodWindow() {
        UIUtils.getMainThreadHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                imm = (InputMethodManager) mContex.getSystemService(Service.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 0);
    }
}
