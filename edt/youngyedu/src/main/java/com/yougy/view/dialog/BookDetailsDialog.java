package com.yougy.view.dialog;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.yougy.common.dialog.BaseDialog;
import com.yougy.ui.activity.R;

/**
 * Created by Administrator on 2018/5/16.
 */

public class BookDetailsDialog extends BaseDialog implements View.OnClickListener {


    private ImageView mImgClosae;
    private Button mBtnCancel;
    private Button mBtnConfirm;

    public BookDetailsDialog(Context context) {
        super(context);
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public BookDetailsDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    protected void init() {
        // 用户不可以点击外部消失对话框
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.dialog_book_details);
        mImgClosae = this.findViewById(R.id.img_close);
        mImgClosae.setOnClickListener(this);

        mBtnCancel = this.findViewById(R.id.btn_cancel);
        mBtnCancel.setOnClickListener(this);

        mBtnConfirm = this.findViewById(R.id.btn_confirm);
        mBtnConfirm.setOnClickListener(this);

    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_close:
                dismiss();
                break;
            case R.id.btn_cancel:
                if (mBookDetailsListener!=null){
                    mBookDetailsListener.onCancelListener();
                }
                break;
            case R.id.btn_confirm:
                if (mBookDetailsListener!=null){
                    mBookDetailsListener.onConfirmListener();
                }
                break;
        }
    }

    private BookDetailsListener mBookDetailsListener ;

    public  void setBookDetailsListener(BookDetailsListener bookDetailsListener){
        mBookDetailsListener = bookDetailsListener ;
    }

    public interface BookDetailsListener {
        void onCancelListener();
        void onConfirmListener();
    }
}
