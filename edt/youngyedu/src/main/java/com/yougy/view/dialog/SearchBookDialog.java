package com.yougy.view.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.yougy.common.dialog.BaseDialog;
import com.yougy.ui.activity.R;

/**
 * Created by Administrator on 2016/11/8.
 * 搜索课外书对话框
 */
public class SearchBookDialog extends BaseDialog implements View.OnClickListener {

    private ImageView mImgClose;
    private EditText mEtKey;
    private Button mBtnSearch;

    public SearchBookDialog(Context context) {
        super(context);
//        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
//        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public SearchBookDialog(Context context, int themeResId) {
        super(context, themeResId);
//        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
//        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    protected void init() {
        // 用户不可以点击外部消失对话框
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.dialog_search_book);
        mImgClose = this.findViewById(R.id.img_close);
        mEtKey = this.findViewById(R.id.et_key);
        mBtnSearch = this.findViewById(R.id.btn_search);
        mImgClose.setOnClickListener(this);
        mBtnSearch.setOnClickListener(this);
    }


/*    @Override
    public void show() {
        super.show();
        Window window = getWindow();
        Display display = window.getWindowManager().getDefaultDisplay();
        int width = (int) (display.getWidth() * getWidthScale());
        window.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
    }*/

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.img_close:
                if (this.isShowing()){
                    this.dismiss();
                }
                break;

            case R.id.btn_search:
                if (mListener!=null)
                    mListener.searClick();
                break;
        }
    }

    public  String  getSearchKey(){
       return mEtKey.getText().toString();
    }

    public  void setSearchListener(SearchListener listener){
        mListener = listener ;
    }
    private  SearchListener mListener ;
    public interface   SearchListener{
        void searClick() ;
    }
}
