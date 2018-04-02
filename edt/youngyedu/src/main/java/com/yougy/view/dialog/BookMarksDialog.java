package com.yougy.view.dialog;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yougy.common.dialog.BaseDialog;
import com.yougy.ui.activity.R;


/**
 * Created by Administrator on 2016/7/11.
 * <p/>
 * 书签对话框
 */
public class BookMarksDialog extends BaseDialog {

    private TextView tvtitle;
    private EditText etmarks;
    private Button btnaddmarks;
    private Button btnchange;
    private Button btndelete;
    private RelativeLayout llChangeOrDelete;
    private FrameLayout fmConfirmOrCancel;
    private ImageButton imgBtn_close;

    private DialogClickFinsihListener mListener;
    private String mTag;

    public BookMarksDialog(Context context) {
        super(context);
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public BookMarksDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public void setTag(String tag){
        mTag = tag;
    }

    @Override
    protected void init() {
        // 用户不可以点击外部消失对话框
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }
    @Override
    protected void initLayout() {
        setContentView(R.layout.dialog_book_marks);
        this.fmConfirmOrCancel = (FrameLayout) findViewById(R.id.fm_ConfirmOrCancel);
        this.llChangeOrDelete = (RelativeLayout) findViewById(R.id.ll_ChangeOrDelete);
        this.btndelete = (Button) findViewById(R.id.btn_delete);
        this.btnchange = (Button) findViewById(R.id.btn_change);
        this.btnaddmarks = (Button) findViewById(R.id.btn_add_marks);
        this.etmarks = (EditText) findViewById(R.id.et_marks);
        this.tvtitle = (TextView) findViewById(R.id.tv_title);

        this.imgBtn_close = (ImageButton) findViewById(R.id.imgBtn_close);
        imgBtn_close.setOnClickListener(mClickListener);
        btndelete.setOnClickListener(mClickListener);
        btnchange.setOnClickListener(mClickListener);
        btnaddmarks.setOnClickListener(mClickListener);

    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (mListener != null) {
                ClickState clickState = null;
                switch (v.getId()) {

                    case R.id.btn_delete:
                        clickState = ClickState.BTN_DELETE_CLICKED;
                        break;

                    case R.id.btn_change:
                        clickState = ClickState.BTN_CHANGE_CLICKED;
                        break;

                    case R.id.btn_add_marks:
                        clickState = ClickState.BTN_ADD_CLICKED;
                        break;

                    case R.id.imgBtn_close:
                        clickState = ClickState.IMG_BTN_CLOSE_CLICKED;
                        break;
                }
                mListener.onDialogConfirmCancleClick(BookMarksDialog.this, clickState);
            }
        }
    };


    /**
     * 点击事情的监听函数
     */
    public interface DialogClickFinsihListener {
        public void onDialogConfirmCancleClick(BookMarksDialog dialog, ClickState clickState);
    }

    /**
     * 点击 按钮
     */
    public enum ClickState {
        BTN_CHANGE_CLICKED,

        BTN_DELETE_CLICKED,

        BTN_ADD_CLICKED,

        IMG_BTN_CLOSE_CLICKED
    }

    /**
     * 显示 按钮 类型
     */
    public enum DialogMode {

        CHANGE_OR_DELETE,

        ADD
    }

    public void setListener(DialogClickFinsihListener listener) {
        this.mListener = listener;
    }

    /**
     * 设置 标签内容
     *
     * @param content
     */
    public void setEditBookMarksContent(String content) {
        etmarks.setText(content);
    }

    /**
     * 显示 按钮 类型
     *
     * @param mode
     */
    public void setDialogMode(DialogMode mode) {
        if (mode == DialogMode.CHANGE_OR_DELETE) {
            btnaddmarks.setVisibility(View.GONE);
            llChangeOrDelete.setVisibility(View.VISIBLE);
            tvtitle.setText("修改书签");
        } else if (mode == DialogMode.ADD){
            btnaddmarks.setVisibility(View.VISIBLE);
            llChangeOrDelete.setVisibility(View.GONE);
            tvtitle.setText("添加书签");
        }

    }

    /**获取书签 内容*/
    public String getEditBookMarksContent() {
        return etmarks.getText().toString().trim();
    }


/*    @Override
    public void show() {
        super.show();
        Window window = getWindow();
        Display display = window.getWindowManager().getDefaultDisplay();
        int width = (int) (display.getWidth() * getWidthScale());
        window.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
        // window.setGravity(Gravity.CENTER_VERTICAL);
    }*/
}
