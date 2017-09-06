package com.yougy.answer.activity;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.yougy.common.activity.BaseActivity;
import com.yougy.ui.activity.R;
import com.yougy.view.NoteBookView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/5.
 */

public class Answer1Activity extends BaseActivity {

    private NoteBookView mNbvAnswerBoard;
    private RelativeLayout rl;
    private TextView tvTitle;
//    private ImageView ivResult;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_answer1);
    }

    @Override
    protected void init() {


    }

    @Override
    protected void initLayout() {

        findViewById(R.id.btn_left).setVisibility(View.GONE);
        findViewById(R.id.img_btn_right).setVisibility(View.GONE);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText("问答中");

        rl = (RelativeLayout) findViewById(R.id.rl);
//        ivResult = (ImageView) findViewById(R.id.iv_result);

        mNbvAnswerBoard = new NoteBookView(this);
        rl.addView(mNbvAnswerBoard);


    }

    @Override
    protected void loadData() {

    }

    @Override
    protected void refreshView() {

    }


    public void onClick(View view) {
        EpdController.leaveScribbleMode(mNbvAnswerBoard);
        switch (view.getId()) {
            case R.id.tv_submit_answer:
                saveResultBitmap();
                break;
        }

    }

    private void saveResultBitmap() {
        rl.setDrawingCacheEnabled(true);
        Bitmap tBitmap = rl.getDrawingCache();
        // 拷贝图片，否则在setDrawingCacheEnabled(false)以后该图片会被释放掉
        tBitmap = tBitmap.createBitmap(tBitmap);
        rl.setDrawingCacheEnabled(false);
        if (tBitmap != null) {
//            ivResult.setImageBitmap(tBitmap);
            saveBitmapToFile(tBitmap, "adsf");
            Toast.makeText(this, "获取成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "获取失败", Toast.LENGTH_SHORT).show();
        }
    }


    public void saveBitmapToFile(Bitmap bitmap, String bitName) {
        File f = new File("/sdcard/" + bitName + ".png");
        FileOutputStream fOut = null;
        try {
            f.createNewFile();
            fOut = new FileOutputStream(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
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
    }

}
