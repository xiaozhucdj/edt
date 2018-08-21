package com.yougy.view.dialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.yougy.common.dialog.BaseDialog;
import com.yougy.common.utils.LogUtils;
import com.yougy.ui.activity.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

/**
 * Created by FH on 2017/02/17.
 */
public class QRCodeDialog extends BaseDialog {

    @BindView(R.id.qr_code_dialog_img)
    ImageView qrImg;
    @BindView(R.id.qr_code_dialog_finish_pay_btn)
    TextView finishPayBtn;
    @BindView(R.id.qr_code_dialog_qr_layout)
    LinearLayout qrLayout;
    @BindView(R.id.qr_code_dialog_hint_tv)
    TextView hintTv;
    @BindView(R.id.qr_code_dialog_retry_btn)
    TextView retryBtn;
    @BindView(R.id.qr_code_dialog_title_tv)
    TextView titleTv;
    @BindView(R.id.qr_code_dialog_cancle_btn)
    TextView cancleBtn;


    private OnBtnClickListener mOnBtnClickListener;
    private String mQRStr, mTitle;

    public enum FUNCTION{
        OK , HAS_FINISH_PAY , CANCLE , RETRY
    }

    public QRCodeDialog(Context context , String qrStr , String title , OnBtnClickListener onBtnClickListener) {
        super(context);
        mQRStr = qrStr;
        mTitle = title;
        mOnBtnClickListener = onBtnClickListener;
    }

    @Override
    protected void init() {
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.qr_dialog_layout);
        ButterKnife.bind(this);

        qrImg.setImageBitmap(encodeStrAsQRBitmap(mQRStr));
        titleTv.setText(mTitle);
    }

    @OnClick({R.id.qr_code_dialog_finish_pay_btn , R.id.qr_code_dialog_retry_btn, R.id.qr_code_dialog_cancle_btn})
    public void onClick(View view) {
        if (mOnBtnClickListener != null) {
            switch (view.getId()){
                case R.id.qr_code_dialog_finish_pay_btn:
                    mOnBtnClickListener.onBtnClick(FUNCTION.HAS_FINISH_PAY);
                    break;
                case R.id.qr_code_dialog_retry_btn:
                    mOnBtnClickListener.onBtnClick(FUNCTION.RETRY);
                    break;
                case R.id.qr_code_dialog_cancle_btn:
                    mOnBtnClickListener.onBtnClick(retryBtn.getVisibility() == View.VISIBLE ? FUNCTION.CANCLE : FUNCTION.OK);
                    break;
            }

        }
    }

    public void showHint(String hintText){
        hintTv.setVisibility(View.VISIBLE);
        retryBtn.setVisibility(View.GONE);
        qrLayout.setVisibility(View.GONE);
        cancleBtn.setVisibility(View.GONE);
        hintTv.setText(hintText);
    }
    public void showHintAndOK(String hintText , String okBtnText){
        hintTv.setVisibility(View.VISIBLE);
        retryBtn.setVisibility(View.GONE);
        qrLayout.setVisibility(View.GONE);
        cancleBtn.setVisibility(View.VISIBLE);
        cancleBtn.setText(okBtnText);
        hintTv.setText(hintText);
    }
    public void showHintAndRetry(String hintText , String retryBtnText){
        hintTv.setVisibility(View.VISIBLE);
        retryBtn.setVisibility(View.VISIBLE);
        qrLayout.setVisibility(View.GONE);
        cancleBtn.setVisibility(View.VISIBLE);
        hintTv.setText(hintText);
        retryBtn.setText(retryBtnText);
        cancleBtn.setText("取消");
    }
    /**
     * 根据给定的str生成一个二维码bitmap
     *
     * @param str 要生成二维码的字符串,如果为null或""会返回null,空格没有关系.即使是" "也会生成对应的字符串.
     * @return 二维码bitmap
     */
    public static Bitmap encodeStrAsQRBitmap(String str) {
        try {
            if (str == null || str.equals("")) {
                return null;
            }
            BitMatrix result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, 600, 600);
            int w = result.getWidth();
            int h = result.getHeight();
            int[] pixels = new int[w * h];
            for (int y = 0; y < h; y++) {
                int offset = y * w;
                for (int x = 0; x < w; x++) {
                    pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    public interface OnBtnClickListener {
        void onBtnClick(FUNCTION function);
    }
}
